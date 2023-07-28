package com.kl.grooveo.boundedContext.thumbsUp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import com.kl.grooveo.boundedContext.thumbsUp.entity.SoundThumbsUp_summary;

public interface SoundThumbsUp_summaryRepository extends JpaRepository<SoundThumbsUp_summary, Long> {
	SoundThumbsUp_summary findByFileInfo(FileInfo fileInfo);

	boolean existsByFileInfo(FileInfo fileInfo);
}
