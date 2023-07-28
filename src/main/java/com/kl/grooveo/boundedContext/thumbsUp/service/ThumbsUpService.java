package com.kl.grooveo.boundedContext.thumbsUp.service;

import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.kl.grooveo.base.event.EventAfterPostLike;
import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.community.service.FreedomPostService;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.service.MemberService;
import com.kl.grooveo.boundedContext.thumbsUp.entity.ThumbsUp;
import com.kl.grooveo.boundedContext.thumbsUp.entity.ThumbsUp_summary;
import com.kl.grooveo.boundedContext.thumbsUp.repository.ThumbsUpRepository;
import com.kl.grooveo.boundedContext.thumbsUp.repository.ThumbsUp_summaryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ThumbsUpService {
	private final ThumbsUp_summaryRepository thumbsUpSummaryRepository;
	private final ThumbsUpRepository thumbsUpRepository;
	private final FreedomPostService freedomPostService;
	private final MemberService memberService;
	private final ApplicationEventPublisher publisher;

	public void likePost(Long postId, Long memberId) {
		FreedomPost freedomPost = freedomPostService.getFreedomPost(postId);
		Optional<Member> member = memberService.findById(memberId);

		if (member.isEmpty() || freedomPost == null)
			return;

		ThumbsUp thumbsUp = new ThumbsUp();
		thumbsUp.setFreedomPost(freedomPost);
		thumbsUp.setMember(member.get());
		thumbsUpRepository.save(thumbsUp);

		publisher.publishEvent(new EventAfterPostLike(this, thumbsUp));

		if (!thumbsUpSummaryRepository.existsByFreedomPost(freedomPost)) {
			ThumbsUp_summary thumbsUpSummary = new ThumbsUp_summary();
			thumbsUpSummary.setFreedomPost(freedomPost);
			thumbsUpSummary.setLikeCount(0);
			thumbsUpSummaryRepository.save(thumbsUpSummary);
		}
	}

	public void unlikePost(Long postId, Long memberId) {
		FreedomPost freedomPost = freedomPostService.getFreedomPost(postId);
		Optional<Member> member = memberService.findById(memberId);

		if (member.isEmpty() || freedomPost == null)
			return;

		Optional<ThumbsUp> thumbsUpOptional = thumbsUpRepository.findByFreedomPostAndMember(freedomPost, member.get());
		thumbsUpOptional.ifPresent(thumbsUpRepository::delete);

		if (!thumbsUpSummaryRepository.existsByFreedomPost(freedomPost)) {
			ThumbsUp_summary thumbsUpSummary = new ThumbsUp_summary();
			thumbsUpSummary.setFreedomPost(freedomPost);
			thumbsUpSummary.setLikeCount(0);
			thumbsUpSummaryRepository.save(thumbsUpSummary);
		}
	}

	public boolean isLikedByMember(Long postId, Member member) {
		FreedomPost freedomPost = freedomPostService.getFreedomPost(postId);
		return thumbsUpRepository.existsByFreedomPostAndMember(freedomPost, member);
	}
}