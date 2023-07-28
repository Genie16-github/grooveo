package com.kl.grooveo.boundedContext.comment.controller;

import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.boundedContext.comment.entity.SoundPostComment;
import com.kl.grooveo.boundedContext.comment.service.SoundPostCommentService;
import com.kl.grooveo.boundedContext.form.CommentForm;
import com.kl.grooveo.boundedContext.form.ReplyForm;
import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import com.kl.grooveo.boundedContext.library.service.SoundTrackService;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/library/comment")
@RequiredArgsConstructor
@Controller
public class SoundPostCommentController {
	private final SoundTrackService soundTrackService;
	private final SoundPostCommentService soundPostCommentService;
	private final MemberService memberService;
	private final Rq rq;

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/{id}")
	public String create(Model model, @PathVariable("id") Long id,
		@RequestParam(value = "commentPage", defaultValue = "0") int commentPage,
		@RequestParam(value = "so", defaultValue = "create") String so,
		@Valid CommentForm commentForm, BindingResult bindingResult, ReplyForm replyForm) {

		FileInfo fileInfo = soundTrackService.getSoundTrack(id);
		Member member = memberService.findByUsername(rq.getMember().getUsername()).orElseThrow();
		Page<SoundPostComment> commentPaging = getCommentPaging(fileInfo, commentPage, "create");

		if (bindingResult.hasErrors()) {
			model.addAttribute("fileInfo", fileInfo);
			model.addAttribute("commentPaging", commentPaging);
			return "usr/library/soundDetail";
		}

		SoundPostComment soundPostComment = createSoundPostComment(fileInfo, commentForm.getContent(), member);
		commentPaging = getCommentPaging(fileInfo, commentPage, so);

		if (so.equals("recent")) {
			return createRedirectUrl(soundPostComment.getFileInfo().getId(), 0, so, soundPostComment.getId());
		}

		return createRedirectUrl(soundPostComment.getFileInfo().getId(), commentPaging.getTotalPages() - 1, so,
			soundPostComment.getId());
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{id}")
	public String delete(@PathVariable("id") Long id) {
		SoundPostComment soundPostComment = soundPostCommentService.getComment(id);

		if (!soundPostComment.getAuthor().getUsername().equals(rq.getMember().getUsername())) {
			throw new AccessDeniedException("삭제권한이 없습니다.");
		}

		soundPostCommentService.delete(soundPostComment);
		return createDetailRedirectUrl(soundPostComment.getFileInfo().getId());
	}

	private Page<SoundPostComment> getCommentPaging(FileInfo fileInfo, int commentPage, String so) {
		return soundPostCommentService.getList(fileInfo, commentPage, so);
	}

	private SoundPostComment createSoundPostComment(FileInfo fileInfo, String content, Member member) {
		return soundPostCommentService.create(fileInfo, content, member);
	}

	private String createRedirectUrl(Long postId, int commentPage, String so, Long commentId) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/library/soundDetail/{postId}")
			.queryParam("commentPage", commentPage)
			.queryParam("so", so)
			.fragment("comment-" + commentId);

		return "redirect:" + builder.buildAndExpand(postId).toUriString();
	}

	private String createDetailRedirectUrl(Long postId) {
		return "redirect:/library/soundDetail/" + postId;
	}
}
