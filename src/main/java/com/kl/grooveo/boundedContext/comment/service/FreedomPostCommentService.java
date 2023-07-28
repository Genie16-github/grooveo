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

import com.kl.grooveo.base.event.EventAfterComment;
import com.kl.grooveo.base.exception.DataNotFoundException;
import com.kl.grooveo.boundedContext.comment.entity.FreedomPostComment;
import com.kl.grooveo.boundedContext.comment.repository.FreedomPostCommentRepository;
import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.member.entity.Member;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FreedomPostCommentService {
	private final FreedomPostCommentRepository freedomPostCommentRepository;
	private final ApplicationEventPublisher publisher;

	public FreedomPostComment create(FreedomPost freedomPost, String content, Member author) {
		FreedomPostComment freedomPostComment = new FreedomPostComment();
		freedomPostComment.setContent(content);
		freedomPostComment.setCreateDate(LocalDateTime.now());
		freedomPostComment.setFreedomPost(freedomPost);
		freedomPostComment.setAuthor(author);
		this.freedomPostCommentRepository.save(freedomPostComment);

		publisher.publishEvent(new EventAfterComment(this, author, freedomPost.getAuthor()));

		return freedomPostComment;
	}

	public FreedomPostComment getComment(Long id) {
		return this.freedomPostCommentRepository.findById(id).orElseThrow(
			() -> new DataNotFoundException("해당 댓글을 찾을 수 없습니다.")
		);
	}

	public void delete(FreedomPostComment freedomPostComment) {
		this.freedomPostCommentRepository.delete(freedomPostComment);
	}

	public Page<FreedomPostComment> getList(FreedomPost freedomPost, int commentPage, String so) {
		List<Sort.Order> sorts = new ArrayList<>();
		if (so.equals("create")) {
			sorts.add(Sort.Order.asc("createDate"));
		} else {
			sorts.add(Sort.Order.desc("createDate"));
		}

		Pageable pageable = PageRequest.of(commentPage, 5, Sort.by(sorts));
		return this.freedomPostCommentRepository.findAllByFreedomPost(freedomPost, pageable);
	}

	public Page<FreedomPostComment> getCommentList(Long userId, int page) {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		return freedomPostCommentRepository.findAllByAuthorId(userId, pageable);
	}
}
