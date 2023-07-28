package com.kl.grooveo.boundedContext.thumbsUp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.boundedContext.response.ThumbsUpDTO;
import com.kl.grooveo.boundedContext.thumbsUp.service.ThumbsUpService;
import com.kl.grooveo.boundedContext.thumbsUp.service.ThumbsUp_summaryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class ThumbsUpController {
	private final ThumbsUpService thumbsUpService;
	private final ThumbsUp_summaryService thumbsUpSummaryService;
	private final Rq rq;

	private static final String RESPONSE_LIKED = "liked";
	private static final String RESPONSE_UNLIKED = "unliked";

	@PostMapping("/pushLike")
	@ResponseBody
	@Transactional
	public ThumbsUpDTO handleLikeRequest(@RequestParam("postId") Long postId) {
		boolean isLiked = thumbsUpService.isLikedByMember(postId, rq.getMember());
		isLiked = !isLiked;

		int plusNum;
		if (isLiked) {
			plusNum = 1;
			thumbsUpService.likePost(postId, rq.getMember().getId());
		} else {
			plusNum = -1;
			thumbsUpService.unlikePost(postId, rq.getMember().getId());
		}

		// 좋아요 수 카운트
		thumbsUpSummaryService.updateLikeCount(postId, plusNum);

		return thumbsUpSummaryService.getLikeCount(postId, plusNum);
	}

	@GetMapping("/findThumbsUpInfo")
	@ResponseBody
	@Transactional
	public ThumbsUpDTO findLikeRequest(@RequestParam("postId") Long postId) {
		boolean isLiked = thumbsUpService.isLikedByMember(postId, rq.getMember());
		int cntLike = thumbsUpSummaryService.getLikeCount(postId);
		if (cntLike == -1)
			cntLike = 0;

		if (isLiked) {
			return new ThumbsUpDTO(RESPONSE_UNLIKED, cntLike);
		} else {
			return new ThumbsUpDTO(RESPONSE_LIKED, cntLike);
		}
	}
}
