package com.kl.grooveo.boundedContext.follow.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.base.rsData.RsData;
import com.kl.grooveo.boundedContext.follow.service.FollowService;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FollowController {

	private final FollowService followService;
	private final MemberService memberService;
	private final Rq rq;

	@PostMapping("/follow")
	@ResponseBody
	public String follow(@RequestParam String username) throws Exception {
		if (rq.isLogout()) {
			return rq.redirectWithMsg("/usr/member/login", "로그인이 필요합니다.");
		}

		Member actor = rq.getMember();
		Member followingUser = memberService.findByUsername(username).orElseThrow();

		RsData followRsdata = followService.following(actor, followingUser);

		if (followRsdata.isFail()) {
			return rq.historyBack(followRsdata);
		}

		return "follow";
	}

	@PostMapping("/followNickName")
	@ResponseBody
	public String followNickName(@RequestParam String nickName) throws Exception {
		if (rq.isLogout()) {
			return rq.redirectWithMsg("/usr/member/login", "로그인이 필요합니다.");
		}

		Member actor = rq.getMember();
		Member followingUser = memberService.findByUserNickName(nickName).orElseThrow();

		RsData followRsdata = followService.following(actor, followingUser);

		if (followRsdata.isFail()) {
			return rq.historyBack(followRsdata);
		}

		return "follow";
	}

	@PostMapping("/unFollow")
	@ResponseBody
	public String unFollow(@RequestParam String username) {
		if (rq.isLogout()) {
			return rq.redirectWithMsg("/usr/member/login", "로그인이 필요합니다.");
		}

		Member actor = rq.getMember();
		Member followingUser = memberService.findByUsername(username).orElseThrow();

		RsData unFollowRsdata = followService.unFollowing(actor, followingUser);

		if (unFollowRsdata.isFail()) {
			return rq.historyBack(unFollowRsdata);
		}
		return "unFollow";
	}

	@PostMapping("/unFollowNickName")
	@ResponseBody
	public String unFollowNickName(@RequestParam String nickName) {
		if (rq.isLogout()) {
			return rq.redirectWithMsg("/usr/member/login", "로그인이 필요합니다.");
		}

		Member actor = rq.getMember();
		Member followingUser = memberService.findByUserNickName(nickName).orElseThrow();

		RsData unFollowRsdata = followService.unFollowing(actor, followingUser);

		if (unFollowRsdata.isFail()) {
			return rq.historyBack(unFollowRsdata);
		}
		return "unFollow";
	}

	@PostMapping("/deleteFollower")
	@ResponseBody
	public String deleteFollow(@RequestParam String username) {
		if (rq.isLogout()) {
			return rq.redirectWithMsg("/usr/member/login", "로그인이 필요합니다.");
		}

		Member actor = rq.getMember();
		Member followingUser = memberService.findByUsername(username).orElseThrow();

		RsData unFollowRsdata = followService.unFollowing(followingUser, actor);

		if (unFollowRsdata.isFail()) {
			return rq.historyBack(unFollowRsdata);
		}
		return "deleteFollower";
	}

	@GetMapping("/updateFollowerCount")
	public int updateFollowerCount() {
		Member actor = rq.getMember();

		return actor.getFollowingPeople().size();
	}

	@PostMapping("/deleteFollowing")
	@ResponseBody
	public String deleteFollowing(@RequestParam String username) {
		if (rq.isLogout()) {
			return rq.redirectWithMsg("/usr/member/login", "로그인이 필요합니다.");
		}

		Member actor = rq.getMember();
		Member followingUser = memberService.findByUsername(username).orElseThrow();

		RsData unFollowRsdata = followService.unFollowing(actor, followingUser);

		if (unFollowRsdata.isFail()) {
			return rq.historyBack(unFollowRsdata);
		}
		return "deleteFollowing";
	}

	@GetMapping("/updateFollowingCount")
	public int updateFollowingCount() {
		Member actor = rq.getMember();

		return actor.getFollowerPeople().size();
	}
}