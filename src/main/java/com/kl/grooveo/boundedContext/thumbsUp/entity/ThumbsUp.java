package com.kl.grooveo.boundedContext.thumbsUp.entity;

import java.time.LocalDateTime;

import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.member.entity.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ThumbsUp {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private FreedomPost freedomPost;

	@ManyToOne
	private Member member;

	private LocalDateTime createDate;
	private LocalDateTime deleteDate;
}
