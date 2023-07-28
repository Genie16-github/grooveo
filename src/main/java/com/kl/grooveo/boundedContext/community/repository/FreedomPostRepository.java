package com.kl.grooveo.boundedContext.community.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kl.grooveo.boundedContext.community.entity.FreedomPost;

public interface FreedomPostRepository extends JpaRepository<FreedomPost, Long> {
	Page<FreedomPost> findAll(Specification<FreedomPost> spec, Pageable pageable);

	Page<FreedomPost> findAllByAuthorId(Long username, Pageable pageable);

	@Modifying
	@Query("update FreedomPost fp set fp.view = fp.view + 1 where fp.id = :id")
	int updateView(@Param("id") Long id);
}
