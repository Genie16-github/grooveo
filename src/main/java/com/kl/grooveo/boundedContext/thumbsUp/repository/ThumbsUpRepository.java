package com.kl.grooveo.boundedContext.thumbsUp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.thumbsUp.entity.ThumbsUp;

public interface ThumbsUpRepository extends JpaRepository<ThumbsUp, Long> {
	boolean existsByFreedomPostAndMember(FreedomPost freedomPost, Member member);

	Optional<ThumbsUp> findByFreedomPostAndMember(FreedomPost freedomPost, Member member);
}
