package com.kl.grooveo.boundedContext.thumbsUp.entity;

import com.kl.grooveo.boundedContext.library.entity.FileInfo;

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
public class SoundThumbsUp_summary {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "file_info_id")
	FileInfo fileInfo;

	@Column(nullable = false)
	private int likeCount = 0;
}
