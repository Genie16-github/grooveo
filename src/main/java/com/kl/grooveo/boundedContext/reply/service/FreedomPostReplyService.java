package com.kl.grooveo.boundedContext.reply.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.kl.grooveo.base.exception.DataNotFoundException;
import com.kl.grooveo.boundedContext.comment.entity.FreedomPostComment;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.reply.entity.FreedomPostReply;
import com.kl.grooveo.boundedContext.reply.repository.FreedomPostReplyRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FreedomPostReplyService {

	private final FreedomPostReplyRepository freedomPostReplyRepository;

	public FreedomPostReply create(FreedomPostComment freedomPostComment, String content, Member author) {
		FreedomPostReply freedomPostReply = new FreedomPostReply();
		freedomPostReply.setContent(content);
		freedomPostReply.setAuthor(author);
		freedomPostReply.setFreedomPostComment(freedomPostComment);
		freedomPostReply.setCreateDate(LocalDateTime.now());
		this.freedomPostReplyRepository.save(freedomPostReply);

		return freedomPostReply;
	}

	public FreedomPostReply getReply(Long id) {
		return this.freedomPostReplyRepository.findById(id).orElseThrow(
			() -> new DataNotFoundException("해당 답글이 없습니다.")
		);
	}

	public void delete(FreedomPostReply freedomPostReply) {
		this.freedomPostReplyRepository.delete(freedomPostReply);
	}
}
