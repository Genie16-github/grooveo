package com.kl.grooveo.boundedContext.thumbsUp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.thumbsUp.entity.ThumbsUp_summary;

public interface ThumbsUp_summaryRepository extends JpaRepository<ThumbsUp_summary, Long> {
	ThumbsUp_summary findByFreedomPost(FreedomPost freedomPost);

	boolean existsByFreedomPost(FreedomPost freedomPost);
}