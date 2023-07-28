package com.kl.grooveo.boundedContext.library.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.boundedContext.comment.entity.SoundPostComment;
import com.kl.grooveo.boundedContext.comment.service.SoundPostCommentService;
import com.kl.grooveo.boundedContext.form.CommentForm;
import com.kl.grooveo.boundedContext.form.ReplyForm;
import com.kl.grooveo.boundedContext.form.SoundTrackForm;
import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import com.kl.grooveo.boundedContext.library.service.SoundTrackService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/library")
@RequiredArgsConstructor
public class SoundTrackController {
	private final SoundTrackService soundTrackService;
	private final SoundPostCommentService soundPostCommentService;
	private final Rq rq;
	private final AmazonS3 amazonS3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${cloud.aws.region.static}")
	private String region;

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/list/{sortCode}")
	public String showLibrary(Model model, @PathVariable("sortCode") int sortCode,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "kw", defaultValue = "") String kw) {

		List<FileInfo> soundTracks = soundTrackService.findAllForPrintByOrderByIdDesc(rq.getMember());

		Page<FileInfo> paging = this.soundTrackService.getList(kw, page, sortCode);
		model.addAttribute("sortCode", sortCode);
		model.addAttribute("paging", paging);
		model.addAttribute("kw", kw);
		model.addAttribute("soundTracks", soundTracks);

		return "usr/library/soundTrackList";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping(value = "/soundDetail/{id}")
	public String showMoreDetail(Model model, @PathVariable("id") Long id,
		@RequestParam(value = "so", defaultValue = "create") String so,
		@RequestParam(value = "commentPage", defaultValue = "0") int commentPage, CommentForm commentForm,
		ReplyForm replyForm,
		HttpServletRequest request, HttpServletResponse response) {
		FileInfo fileInfo = this.soundTrackService.getSoundTrack(id);

		// 조회수 관련 로직
		Cookie oldCookie = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {   // 쿠키가 null 인지 검사
			// null 이 아니라면 "postView" 라는 이름의 쿠키가 있는지 검사
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("soundPostView")) {
					oldCookie = cookie;
				}
			}
		}

		if (oldCookie != null) {
			// "postView" 가 존재한다면
			// value 가 현재 접근한 게시글의 id 를 포함하고 있는지 검사
			if (!oldCookie.getValue().contains("[" + id.toString() + "]")) {
				// 포함하고 있지 않으면 조회수 증가
				this.soundTrackService.updateView(id);
				oldCookie.setValue(oldCookie.getValue() + "_[" + id + "]");
				oldCookie.setPath("/");
				oldCookie.setMaxAge(60 * 60 * 24);                            // 쿠키 시간
				response.addCookie(oldCookie);
			}
		} else {
			// "soundPostView" 가 존재하지 않는다면
			// 게시글의 id 를 포함하는 쿠키를 만들고
			// 마찬가지로 조회수 증가
			this.soundTrackService.updateView(id);
			Cookie newCookie = new Cookie("soundPostView", "[" + id + "]");
			newCookie.setPath("/");
			newCookie.setMaxAge(60 * 60 * 24);                                // 쿠키 시간
			response.addCookie(newCookie);
		}

		Page<SoundPostComment> commentPaging = this.soundPostCommentService.getList(fileInfo, commentPage, so);

		model.addAttribute("commentPaging", commentPaging);
		model.addAttribute("fileInfo", fileInfo);
		model.addAttribute("so", so);

		return "usr/library/soundDetail";
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/soundTrack/{id}")
	public String soundTrackDelete(@PathVariable("id") Long id) {
		FileInfo fileInfo = this.soundTrackService.getSoundTrack(id);

		if (!fileInfo.getArtist().getUsername().equals(rq.getMember().getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
		}

		this.soundTrackService.delete(fileInfo);
		return "redirect:/library/list/1";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/soundTrack/modify/{id}")
	public String soundTrackModify(Model model, @PathVariable("id") Long id) {
		FileInfo fileInfo = this.soundTrackService.getSoundTrack(id);

		if (!fileInfo.getArtist().getUsername().equals(rq.getMember().getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}

		model.addAttribute("soundTrackForm",
			new SoundTrackForm(fileInfo.getTitle(), fileInfo.getDescription(), null, null));
		model.addAttribute("albumCoverUrl", fileInfo.getAlbumCoverUrl());
		model.addAttribute("soundUrl", fileInfo.getSoundUrl());

		return "usr/library/soundUpload";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/soundTrack/modify/{id}")
	public String soundTrackModify(@Valid SoundTrackForm soundTrackForm, BindingResult bindingResult,
		@PathVariable("id") Long id) {
		if (bindingResult.hasErrors()) {
			return "usr/library/soundUpload";
		}

		FileInfo fileInfo = this.soundTrackService.getSoundTrack(id);

		if (!fileInfo.getArtist().getUsername().equals(rq.getMember().getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}

		try {
			// 앨범 커버 수정 코드 추가
			String albumCoverExtension = FilenameUtils.getExtension(
				soundTrackForm.getAlbumCover().getOriginalFilename());
			String albumCoverName = UUID.randomUUID().toString() + "." + albumCoverExtension;
			String albumCoverUrl =
				"https://s3." + region + ".amazonaws.com/" + bucket + "/albumCover/" + albumCoverName;

			String title = URLEncoder.encode(soundTrackForm.getTitle(), StandardCharsets.UTF_8);
			String description = URLEncoder.encode(soundTrackForm.getDescription(), StandardCharsets.UTF_8);

			ObjectMetadata albumCoverMetadata = new ObjectMetadata();
			albumCoverMetadata.setContentType(soundTrackForm.getAlbumCover().getContentType());
			albumCoverMetadata.setContentLength(soundTrackForm.getAlbumCover().getSize());
			albumCoverMetadata.addUserMetadata("title", title);
			albumCoverMetadata.addUserMetadata("description", description);

			amazonS3Client.putObject(new PutObjectRequest(bucket, "albumCover/" + albumCoverName,
				soundTrackForm.getAlbumCover().getInputStream(), albumCoverMetadata));

			this.soundTrackService.modify(fileInfo, soundTrackForm.getTitle(), soundTrackForm.getDescription(),
				albumCoverUrl, fileInfo.getSoundUrl());
			return String.format("redirect:/library/soundDetail/%s", id);
		} catch (IOException e) {
			e.printStackTrace();
			return "redirect:/library/soundUpload";
		}
	}

	@GetMapping("/getView")
	@ResponseBody
	public String getViewCnt(@RequestParam("postId") Long postId) {
		return String.valueOf(soundTrackService.getViewCnt(postId));  // 조회수 String 처리
	}
}
