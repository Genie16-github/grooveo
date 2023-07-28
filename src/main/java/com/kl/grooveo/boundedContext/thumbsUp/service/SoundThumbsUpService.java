package com.kl.grooveo.boundedContext.thumbsUp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.kl.grooveo.base.event.EventAfterSoundLike;
import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import com.kl.grooveo.boundedContext.library.service.SoundTrackService;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.service.MemberService;
import com.kl.grooveo.boundedContext.thumbsUp.entity.SoundThumbsUp;
import com.kl.grooveo.boundedContext.thumbsUp.entity.SoundThumbsUp_summary;
import com.kl.grooveo.boundedContext.thumbsUp.repository.SoundThumbsUpRepository;
import com.kl.grooveo.boundedContext.thumbsUp.repository.SoundThumbsUp_summaryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SoundThumbsUpService {
	private final SoundThumbsUp_summaryRepository soundThumbsUpSummaryRepository;
	private final SoundThumbsUpRepository soundThumbsUpRepository;
	private final SoundTrackService soundTrackService;
	private final MemberService memberService;
	private final ApplicationEventPublisher publisher;

	public void likePost(Long postId, Long memberId) {
		FileInfo fileInfo = soundTrackService.getSoundTrack(postId);
		Optional<Member> member = memberService.findById(memberId);

		if (member.isEmpty() || fileInfo == null)
			return;

		SoundThumbsUp soundThumbsUp = new SoundThumbsUp();
		soundThumbsUp.setFileInfo(fileInfo);
		soundThumbsUp.setMember(member.get());
		soundThumbsUpRepository.save(soundThumbsUp);

		publisher.publishEvent(new EventAfterSoundLike(this, soundThumbsUp));

		if (!soundThumbsUpSummaryRepository.existsByFileInfo(fileInfo)) {
			SoundThumbsUp_summary soundThumbsUpSummary = new SoundThumbsUp_summary();
			soundThumbsUpSummary.setFileInfo(fileInfo);
			soundThumbsUpSummary.setLikeCount(0);
			soundThumbsUpSummaryRepository.save(soundThumbsUpSummary);
		}
	}

	public void unlikePost(Long postId, Long memberId) {
		FileInfo fileInfo = soundTrackService.getSoundTrack(postId);
		Optional<Member> member = memberService.findById(memberId);

		if (member.isEmpty() || fileInfo == null)
			return;

		Optional<SoundThumbsUp> soundThumbsUpOptional = soundThumbsUpRepository.findByFileInfoAndMember(fileInfo,
			member.get());
		soundThumbsUpOptional.ifPresent(soundThumbsUpRepository::delete);

		if (!soundThumbsUpSummaryRepository.existsByFileInfo(fileInfo)) {
			SoundThumbsUp_summary soundThumbsUpSummary = new SoundThumbsUp_summary();
			soundThumbsUpSummary.setFileInfo(fileInfo);
			soundThumbsUpSummary.setLikeCount(0);
			soundThumbsUpSummaryRepository.save(soundThumbsUpSummary);
		}
	}

	public boolean isLikedByMember(Long postId, Member member) {
		FileInfo fileInfo = soundTrackService.getSoundTrack(postId);
		return soundThumbsUpRepository.existsByFileInfoAndMember(fileInfo, member);
	}

	public List<SoundThumbsUp> findAllByMemberIdAndFileInfoIdIn(Long id, long[] ids) {
		return soundThumbsUpRepository.findAllByMemberIdAndFileInfoIdIn(id, ids);
	}
}