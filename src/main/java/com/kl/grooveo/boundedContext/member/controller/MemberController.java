package com.kl.grooveo.boundedContext.member.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.base.rsData.RsData;
import com.kl.grooveo.boundedContext.comment.entity.FreedomPostComment;
import com.kl.grooveo.boundedContext.comment.service.FreedomPostCommentService;
import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.community.service.FreedomPostService;
import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import com.kl.grooveo.boundedContext.library.service.SoundTrackService;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.form.FindPasswordForm;
import com.kl.grooveo.boundedContext.member.form.FindUsernameForm;
import com.kl.grooveo.boundedContext.member.form.JoinForm;
import com.kl.grooveo.boundedContext.member.form.ModifyEmailForm;
import com.kl.grooveo.boundedContext.member.form.ModifyNickNameForm;
import com.kl.grooveo.boundedContext.member.form.ModifyPasswordForm;
import com.kl.grooveo.boundedContext.member.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/member")
public class MemberController {

	private final MemberService memberService;
	private final FreedomPostService freedomPostService;
	private final FreedomPostCommentService freedomPostCommentService;
	private final SoundTrackService soundTrackService;
	private final Rq rq;
	private final AmazonS3 amazonS3Client;
	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${cloud.aws.region.static}")
	private String region;

	@GetMapping("/join")
	public String showJoin() {
		return "usr/member/join";
	}

	@PostMapping("/join")
	public String join(@Valid JoinForm joinForm, HttpServletRequest request) {
		RsData emailVerified = memberService.isEmailVerified(request);

		if (emailVerified.isFail()) {
			return rq.historyBack(emailVerified);
		}

		RsData<Member> joinRs = memberService.join(joinForm.getUsername(), joinForm.getPassword(),
			joinForm.getName(), joinForm.getNickName(), joinForm.getEmail());

		if (joinRs.isFail()) {
			return rq.historyBack(joinRs);
		}

		return rq.redirectWithMsg("/usr/member/login", joinRs);
	}

	@GetMapping("/login")
	public String showLogin(@RequestParam(value = "error", required = false) String error,
		@RequestParam(value = "exception", required = false) String exception,
		Model model) {
		model.addAttribute("error", error);
		model.addAttribute("exception", exception);
		return "usr/member/login";
	}

	@GetMapping("/findUsername")
	public String showFindId() {
		return "usr/member/findUsername";
	}

	@PostMapping("/findUsername")
	public String findId(@Valid FindUsernameForm findUsernameForm) {
		RsData findIdRs = memberService.findUsername(findUsernameForm.getEmail());

		if (findIdRs.isFail()) {
			return rq.historyBack(findIdRs);
		}

		return rq.redirectWithMsg("/usr/member/login", findIdRs);
	}

	@GetMapping("/findPassword")
	public String showFindPassword() {
		return "usr/member/findPassword";
	}

	@PostMapping("/findPassword")
	public String findPassword(@Valid FindPasswordForm findPasswordForm) {
		RsData findPasswordRs = memberService.findUserPassword(findPasswordForm.getUsername(),
			findPasswordForm.getEmail());

		if (findPasswordRs.isFail()) {
			return rq.historyBack(findPasswordRs);
		}

		return rq.redirectWithMsg("/usr/member/login", findPasswordRs);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/myPage")
	public String showMyPage() {
		return "usr/member/myPage";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/myPage/post")
	public String showMyPost(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
		Member member = rq.getMember();
		List<FreedomPost> freedomPosts = member.getFreedomPosts();
		Page<FreedomPost> paging = freedomPostService.getList(member.getId(), page);
		model.addAttribute("paging", paging);
		model.addAttribute("freedomPosts", freedomPosts);
		return "usr/member/myPage/post";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/myPage/comment")
	public String showMyComment(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
		Member member = rq.getMember();
		List<FreedomPostComment> freedomPostComments = member.getFreedomPostComments();
		Page<FreedomPostComment> paging = freedomPostCommentService.getCommentList(member.getId(), page);
		model.addAttribute("paging", paging);
		model.addAttribute("freedomPostComments", freedomPostComments);
		return "usr/member/myPage/comment";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/myPage/library")
	public String showMyLibrary(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
		String username = rq.getMember().getUsername();

		Page<FileInfo> paging = this.soundTrackService.getMemberUploads(username, page);
		model.addAttribute("paging", paging);

		return "usr/member/myPage/library";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/myPage/modifyPassword")
	public String showModifyPassword() {
		return "usr/member/myPage/modifyPassword";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/myPage/modifyPassword")
	public String modifyPassword(@Valid ModifyPasswordForm modifyPasswordForm, HttpSession session) {
		RsData<Member> member = memberService.modifyPassword(rq.getMember(), modifyPasswordForm.getPreviousPassword(),
			modifyPasswordForm.getNewPassword(), modifyPasswordForm.getConfirmNewPassword());

		if (member.isFail()) {
			return rq.historyBack(member);
		}

		session.invalidate();

		return rq.redirectWithMsg("/usr/member/login", member);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/myPage/modifyNickName")
	public String showModifyNickName() {
		return "usr/member/myPage/modifyNickName";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/myPage/modifyNickName")
	public String modifyInfo(@Valid ModifyNickNameForm modifyInfoForm) {
		RsData<Member> member = memberService.modifyNickName(rq.getMember(), modifyInfoForm.getNickName());

		if (member.isFail()) {
			return rq.historyBack(member);
		}

		return rq.redirectWithMsg("/usr/member/myPage", member);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/myPage/modifyEmail")
	public String showModifyEmail() {
		return "usr/member/myPage/modifyEmail";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/myPage/modifyEmail")
	public String modifyEmail(@Valid ModifyEmailForm modifyEmailForm) {
		RsData<Member> member = memberService.modifyEmail(rq.getMember(), modifyEmailForm.getEmail());

		if (member.isFail()) {
			return rq.historyBack(member);
		}

		return rq.redirectWithMsg("/usr/member/myPage", member);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/myPage/modifyProfileImage")
	public String showModifyProfileImage() {
		return "usr/member/myPage/modifyProfileImage";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/myPage/modifyProfileImage")
	@ResponseBody
	public ResponseEntity<String> uploadFile(@RequestParam("profileImage") MultipartFile profileImage) {
		Member actor = rq.getMember();

		try {
			String fileName = "profileImage_userId_" + actor.getId();
			String profileUrl = "https://s3." + region + ".amazonaws.com/" + bucket + "/profileImages/" + fileName;

			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(profileImage.getContentType());
			metadata.setContentLength(profileImage.getSize());

			// 사용자로부터 받은 정보를 metadata에 추가
			amazonS3Client.putObject(
				new PutObjectRequest(bucket, "profileImages/" + fileName, profileImage.getInputStream(), metadata));

			// DB에 파일 정보를 저장
			memberService.saveProfileImage(actor, profileUrl);

			return ResponseEntity.ok().body("프로필 사진이 변경되었습니다.");
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("문제가 발생했습니다. 관리자에게 문의하세요");
		}
	}
}