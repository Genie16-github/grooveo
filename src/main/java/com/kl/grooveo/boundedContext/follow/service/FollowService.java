package com.kl.grooveo.boundedContext.follow.service;

import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kl.grooveo.base.event.EventAfterFollow;
import com.kl.grooveo.base.event.EventAfterUnFollow;
import com.kl.grooveo.base.rsData.RsData;
import com.kl.grooveo.boundedContext.follow.entity.Follow;
import com.kl.grooveo.boundedContext.follow.repository.FollowRepository;
import com.kl.grooveo.boundedContext.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

	private final FollowRepository followRepository;
	private final ApplicationEventPublisher publisher;

	@Transactional
	public RsData following(Member follower, Member following) {
		Optional<Follow> opFollow = followRepository.findByFollower_usernameAndFollowing_username(
			follower.getUsername(), following.getUsername());

		if (opFollow.isPresent()) {
			return RsData.of("F-1", "이미 팔로우 팔로우 하셨습니다.");
		}

		if (follower.getUsername().equals(following.getUsername())) {
			return RsData.of("F-2", "자기 자신을 팔로우 할 수 없습니다.");
		}

		Follow follow = Follow
			.builder()
			.follower(follower)
			.following(following)
			.build();

		follower.getFollowingPeople().add(follow);
		following.getFollowingPeople().add(follow);

		followRepository.save(follow);

		publisher.publishEvent(new EventAfterFollow(this, follow));

		return RsData.of("S-1", "팔로우가 가능합니다.");
	}

	@Transactional
	public RsData unFollowing(Member follower, Member following) {
		Optional<Follow> follow = followRepository.findByFollower_usernameAndFollowing_username(follower.getUsername(),
			following.getUsername());

		if (follow.isEmpty()) {
			return RsData.of("F-1", "상대방을 팔로우를 하지 않아서 언팔로우를 할 수 없습니다.");
		}

		if (follower.getUsername().equals(following.getUsername())) {
			return RsData.of("F-2", "자기 자신을 언팔로우 할 수 없습니다.");
		}

		follower.getFollowingPeople().remove(follow.get());
		following.getFollowerPeople().remove(follow.get());

		followRepository.delete(follow.get());

		publisher.publishEvent(new EventAfterUnFollow(this, follow.get()));

		return RsData.of("S-1", "언팔로우가 가능합니다.");
	}

	public Optional<Follow> findByFollowerAndFollowing(Member follower, Member following) {
		return followRepository.findByFollowerAndFollowing(follower, following);
	}

	public boolean isFollowing(Member followUser, Member followingUser) {
		return followRepository.existsByFollowerAndFollowing(followUser, followingUser);
	}
}