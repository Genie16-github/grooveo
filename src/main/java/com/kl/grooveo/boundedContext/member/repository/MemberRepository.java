package com.kl.grooveo.boundedContext.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kl.grooveo.boundedContext.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByUsername(String username);

	Optional<Member> findByEmail(String email);

	Optional<Member> findByUsernameAndEmail(String username, String email);

	Optional<Member> findByNickName(String nickName);
}