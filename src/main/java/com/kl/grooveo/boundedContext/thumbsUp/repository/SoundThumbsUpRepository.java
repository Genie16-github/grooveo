package com.kl.grooveo.boundedContext.thumbsUp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.thumbsUp.entity.SoundThumbsUp;

public interface SoundThumbsUpRepository extends JpaRepository<SoundThumbsUp, Long> {
	boolean existsByFileInfoAndMember(FileInfo fileInfo, Member member);

	Optional<SoundThumbsUp> findByFileInfoAndMember(FileInfo fileInfo, Member member);

	List<SoundThumbsUp> findAllByMemberIdAndFileInfoIdIn(Long id, long[] ids);
}
