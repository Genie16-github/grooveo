package com.kl.grooveo.boundedContext.follow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kl.grooveo.boundedContext.follow.entity.Follow;
import com.kl.grooveo.boundedContext.member.entity.Member;

public interface FollowRepository extends JpaRepository<Follow, Long> {

	Optional<Follow> findByFollower_usernameAndFollowing_username(String followerUsername, String followingUsername);

	Optional<Follow> findByFollowerAndFollowing(Member follower, Member following);

	boolean existsByFollowerAndFollowing(Member follower, Member following);
}