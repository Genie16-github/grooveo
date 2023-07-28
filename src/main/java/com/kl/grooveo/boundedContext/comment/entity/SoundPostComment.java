package com.kl.grooveo.boundedContext.comment.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.reply.entity.SoundPostReply;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SoundPostComment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private FileInfo fileInfo;

	@ManyToOne
	private Member author;

	@Column(columnDefinition = "TEXT")
	private String content;

	@OneToMany(mappedBy = "soundPostComment", cascade = CascadeType.REMOVE)
	private List<SoundPostReply> replyList;

	private LocalDateTime createDate;
	private LocalDateTime deleteDate;
}