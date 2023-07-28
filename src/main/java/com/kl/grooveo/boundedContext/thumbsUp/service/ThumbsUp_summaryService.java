package com.kl.grooveo.boundedContext.thumbsUp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.community.service.FreedomPostService;
import com.kl.grooveo.boundedContext.response.ThumbsUpDTO;
import com.kl.grooveo.boundedContext.thumbsUp.entity.ThumbsUp_summary;
import com.kl.grooveo.boundedContext.thumbsUp.repository.ThumbsUp_summaryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ThumbsUp_summaryService {
	private final ThumbsUp_summaryRepository thumbsUpSummaryRepository;
	private final FreedomPostService freedomPostService;

	@Transactional
	public void updateLikeCount(Long postId, int num) {
		FreedomPost freedomPost = freedomPostService.getFreedomPost(postId);
		if (freedomPost == null)
			return;
		ThumbsUp_summary thumbsUpSummary = thumbsUpSummaryRepository.findByFreedomPost(freedomPost);
		if (thumbsUpSummary != null) {
			thumbsUpSummary.setLikeCount(thumbsUpSummary.getLikeCount() + num);
			thumbsUpSummaryRepository.save(thumbsUpSummary);
		}
	}

	@Transactional
	public ThumbsUpDTO getLikeCount(Long postId, int plusNum) {
		FreedomPost freedomPost = freedomPostService.getFreedomPost(postId);
		ThumbsUp_summary thumbsUpSummary = thumbsUpSummaryRepository.findByFreedomPost(freedomPost);
		if (thumbsUpSummary == null) {
			return new ThumbsUpDTO("error", -1);
		}

		String result = (plusNum == 1) ? "unliked" : "liked";
		return new ThumbsUpDTO(result, thumbsUpSummary.getLikeCount());
	}

	public int getLikeCount(Long postId) {
		FreedomPost freedomPost = freedomPostService.getFreedomPost(postId);
		ThumbsUp_summary thumbsUpSummary = thumbsUpSummaryRepository.findByFreedomPost(freedomPost);
		if (thumbsUpSummary == null) {
			return -1;
		}

		return thumbsUpSummary.getLikeCount();
	}
}
