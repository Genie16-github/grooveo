package com.kl.grooveo.boundedContext.reply.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.kl.grooveo.base.exception.DataNotFoundException;
import com.kl.grooveo.boundedContext.comment.entity.SoundPostComment;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.reply.entity.SoundPostReply;
import com.kl.grooveo.boundedContext.reply.repository.SoundPostReplyRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SoundPostReplyService {

	private final SoundPostReplyRepository soundPostReplyRepository;

	public SoundPostReply create(SoundPostComment soundPostComment, String content, Member author) {
		SoundPostReply soundPostReply = new SoundPostReply();
		soundPostReply.setContent(content);
		soundPostReply.setAuthor(author);
		soundPostReply.setSoundPostComment(soundPostComment);
		soundPostReply.setCreateDate(LocalDateTime.now());
		this.soundPostReplyRepository.save(soundPostReply);

		return soundPostReply;
	}

	public SoundPostReply getReply(Long id) {
		return this.soundPostReplyRepository.findById(id).orElseThrow(
			() -> new DataNotFoundException("해당 답글이 없습니다.")
		);
	}

	public void delete(SoundPostReply soundPostReply) {
		this.soundPostReplyRepository.delete(soundPostReply);
	}
}
