package com.kl.grooveo.boundedContext.member.controller;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kl.grooveo.base.email.service.EmailService;
import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.boundedContext.follow.service.FollowService;
import com.kl.grooveo.boundedContext.member.dto.MemberProfileDto;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class restMemberController {

	private final MemberService memberService;
	private final FollowService followService;
	private final EmailService emailService;
	private final Rq rq;

	@GetMapping("/member/getProfile")
	@ResponseBody
	public MemberProfileDto getProfile(@RequestParam String userNickName) throws Exception {
		Optional<Member> opFollowingUser = memberService.findByUserNickName(userNickName);

		if (opFollowingUser.isEmpty()) {
			throw new Exception("존재하지 않는 User 입니다.");
		}

		Member followUser = rq.getMember();
		Member followingUser = opFollowingUser.get();

		boolean isFollowing = followService.isFollowing(followUser, followingUser);

		return MemberProfileDto.builder()
			.profileImageUrl(followingUser.getProfileImageUrl())
			.username(followingUser.getUsername())
			.trackCount(followingUser.getFileInfos().size())
			.postCount(followingUser.getFreedomPosts().size())
			.followerPeopleCount(followingUser.getFollowerPeople().size())
			.followingPeopleCount(followingUser.getFollowingPeople().size())
			.nickName(followingUser.getNickName())
			.email(followingUser.getEmail())
			.isFollowing(isFollowing)
			.build();
	}

	@PostMapping("/sendCode")
	@ResponseBody
	public String sendVerificationCode(HttpServletRequest request, String userEmail) {
		HttpSession session = request.getSession();

		emailService.sendVerificationCode(session, userEmail);

		return "";
	}

	@PostMapping("/certification")
	@ResponseBody
	public boolean checkVerificationCode(HttpServletRequest request, String userEmail, String inputCode) {
		HttpSession session = request.getSession();

		return emailService.emailCertification(session, userEmail, inputCode);
	}

	@GetMapping("/isFollowing")
	@ResponseBody
	public boolean isFollowing(@RequestParam String userNickName) throws Exception {
		Optional<Member> opFollowingUser = memberService.findByUserNickName(userNickName);

		if (opFollowingUser.isEmpty()) {
			throw new Exception("존재하지 않는 User 입니다.");
		}

		Member followUser = rq.getMember();
		Member followingUser = opFollowingUser.get();

		return followService.isFollowing(followUser, followingUser);
	}
}