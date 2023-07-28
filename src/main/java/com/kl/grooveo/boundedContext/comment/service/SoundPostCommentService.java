package com.kl.grooveo.boundedContext.comment.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kl.grooveo.base.event.EventAfterSoundComment;
import com.kl.grooveo.base.exception.DataNotFoundException;
import com.kl.grooveo.boundedContext.comment.entity.SoundPostComment;
import com.kl.grooveo.boundedContext.comment.repository.SoundPostCommentRepository;
import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import com.kl.grooveo.boundedContext.member.entity.Member;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SoundPostCommentService {
	private final SoundPostCommentRepository soundPostCommentRepository;
	private final ApplicationEventPublisher publisher;

	public SoundPostComment create(FileInfo fileInfo, String content, Member author) {
		SoundPostComment soundPostComment = new SoundPostComment();
		soundPostComment.setContent(content);
		soundPostComment.setCreateDate(LocalDateTime.now());
		soundPostComment.setFileInfo(fileInfo);
		soundPostComment.setAuthor(author);
		this.soundPostCommentRepository.save(soundPostComment);

		// 음원 게시글에 댓글 작성 이벤트 발행
		publisher.publishEvent(new EventAfterSoundComment(this, author, fileInfo.getArtist()));

		return soundPostComment;
	}

	public SoundPostComment getComment(Long id) {
		return this.soundPostCommentRepository.findById(id).orElseThrow(
			() -> new DataNotFoundException("해당 댓글을 찾을 수 없습니다.")
		);
	}

	public void delete(SoundPostComment soundPostComment) {
		this.soundPostCommentRepository.delete(soundPostComment);
	}

	public Page<SoundPostComment> getList(FileInfo fileInfo, int commentPage, String so) {
		List<Sort.Order> sorts = new ArrayList<>();
		if (so.equals("create")) {
			sorts.add(Sort.Order.asc("createDate"));
		} else {
			sorts.add(Sort.Order.desc("createDate"));
		}

		Pageable pageable = PageRequest.of(commentPage, 5, Sort.by(sorts));
		return this.soundPostCommentRepository.findAllByFileInfo(fileInfo, pageable);
	}

	public Page<SoundPostComment> getCommentList(Long userId, int page) {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		return soundPostCommentRepository.findAllByAuthorId(userId, pageable);
	}

}
