package com.kl.grooveo.boundedContext.thumbsUp.entity;

import com.kl.grooveo.boundedContext.community.entity.FreedomPost;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ThumbsUp_summary {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "freedom_post_id")
	FreedomPost freedomPost;

	@Column(nullable = false)
	private int likeCount = 0;
}