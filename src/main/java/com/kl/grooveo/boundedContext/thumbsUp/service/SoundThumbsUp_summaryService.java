package com.kl.grooveo.boundedContext.thumbsUp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import com.kl.grooveo.boundedContext.library.service.SoundTrackService;
import com.kl.grooveo.boundedContext.response.ThumbsUpDTO;
import com.kl.grooveo.boundedContext.thumbsUp.entity.SoundThumbsUp_summary;
import com.kl.grooveo.boundedContext.thumbsUp.repository.SoundThumbsUp_summaryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SoundThumbsUp_summaryService {
	private final SoundThumbsUp_summaryRepository soundThumbsUpSummaryRepository;
	private final SoundTrackService soundTrackService;

	@Transactional
	public void updateLikeCount(Long postId, int num) {
		FileInfo fileInfo = soundTrackService.getSoundTrack(postId);
		if (fileInfo == null)
			return;
		SoundThumbsUp_summary soundThumbsUpSummary = soundThumbsUpSummaryRepository.findByFileInfo(fileInfo);
		if (soundThumbsUpSummary != null) {
			soundThumbsUpSummary.setLikeCount(soundThumbsUpSummary.getLikeCount() + num);
			soundThumbsUpSummaryRepository.save(soundThumbsUpSummary);
		}
	}

	@Transactional
	public ThumbsUpDTO getLikeCount(Long postId, int plusNum) {
		FileInfo fileInfo = soundTrackService.getSoundTrack(postId);
		SoundThumbsUp_summary soundThumbsUpSummary = soundThumbsUpSummaryRepository.findByFileInfo(fileInfo);
		if (soundThumbsUpSummary == null) {
			return new ThumbsUpDTO("error", -1);
		}

		String result = (plusNum == 1) ? "unliked" : "liked";
		return new ThumbsUpDTO(result, soundThumbsUpSummary.getLikeCount());
	}

	public int getLikeCount(Long postId) {
		FileInfo fileInfo = soundTrackService.getSoundTrack(postId);
		SoundThumbsUp_summary soundThumbsUpSummary = soundThumbsUpSummaryRepository.findByFileInfo(fileInfo);
		if (soundThumbsUpSummary == null) {
			return -1;
		}

		return soundThumbsUpSummary.getLikeCount();
	}
}
