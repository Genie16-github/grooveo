package com.kl.grooveo.boundedContext.notification.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.boundedContext.follow.entity.Follow;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.notification.entity.Notification;
import com.kl.grooveo.boundedContext.notification.repository.NotificationRepository;
import com.kl.grooveo.boundedContext.thumbsUp.entity.SoundThumbsUp;
import com.kl.grooveo.boundedContext.thumbsUp.entity.ThumbsUp;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
	private final NotificationRepository notificationRepository;
	private final Rq rq;

	public List<Notification> findByToMember(Member toMember) {
		return notificationRepository.findByToMemberOrderByCreateDateDesc(toMember);
	}

	@Transactional
	public void whenAfterSoundLike(SoundThumbsUp soundThumbsUp) {
		Notification notification = Notification
			.builder()
			.fromMember(soundThumbsUp.getMember())
			.toMember(soundThumbsUp.getFileInfo().getArtist())
			.typeCode("soundLike")
			.build();

		notificationRepository.save(notification);
	}

	@Transactional
	public void whenAfterPostLike(ThumbsUp thumbsUp) {
		Notification notification = Notification
			.builder()
			.fromMember(thumbsUp.getMember()) // 좋아요 한 멤버
			.toMember(thumbsUp.getFreedomPost().getAuthor()) // 좋아요 받은 멤버
			.typeCode("postLike") // "postLike" -> 커뮤니티 게시글 좋아요 알림
			.build();

		notificationRepository.save(notification);
	}

	@Transactional
	public void whenAfterComment(Member fromMember, Member toMember) {
		Notification notification = Notification
			.builder()
			.fromMember(fromMember) // 댓글 단 멤버
			.toMember(toMember) // 게시글 쓴 멤버
			.typeCode("comment")
			.build();

		notificationRepository.save(notification);
	}

	@Transactional
	public void whenAfterSoundComment(Member fromMember, Member toMember) {
		Notification notification = Notification
			.builder()
			.fromMember(fromMember) // 댓글 단 멤버
			.toMember(toMember)
			.typeCode("soundComment")
			.build();

		notificationRepository.save(notification);
	}

	@Transactional
	public void whenAfterFollow(Follow follow) {
		Notification notification = Notification
			.builder()
			.fromMember(follow.getFollower())
			.toMember(follow.getFollowing())
			.typeCode("follow")
			.build();

		notificationRepository.save(notification);
	}

	@Transactional
	public void whenAfterUnFollow(Follow follow) {

		Notification notification = Notification
			.builder()
			.fromMember(follow.getFollower())
			.toMember(follow.getFollowing())
			.typeCode("unFollow")
			.build();

		notificationRepository.save(notification);
	}

	@Transactional
	public void whenAfterUpload(Follow follow) {

		Notification notification = Notification
			.builder()
			.fromMember(follow.getFollowing())
			.toMember(follow.getFollower())
			.typeCode("upload")
			.build();

		notificationRepository.save(notification);
	}

	@Transactional
	public boolean getAfterReadNotification(Long notificationId) {
		LocalDateTime localDateTime = LocalDateTime.now();
		Optional<Notification> notification = notificationRepository.findById(notificationId);
		if (notification.isEmpty())
			return false;

		if (notification.get().getReadDate() == null) {
			notification.get().setAfterReadNotification(localDateTime);
		}
		return true;
	}

	public boolean countUnreadNotificationsByToMember(Member member) {
		return notificationRepository.countByToMemberAndReadDateIsNull(member) > 0;
	}

	@Transactional
	public boolean deleteNotification(Long notificationId) {
		Optional<Notification> notification = notificationRepository.findById(notificationId);
		if (notification.isEmpty())
			return false;

		notificationRepository.delete(notification.get());
		return true;
	}

	@Transactional
	public boolean allDeleteNotification(Integer deleteType) {
		List<Notification> notificationList = notificationRepository.findByToMember(rq.getMember());
		if (notificationList.size() == 0)
			return false;

		if (deleteType == 1) {    // 읽은 알림 전체 삭제
			notificationList.stream()
				.filter(notification -> notification.getReadDate() != null)
				.forEach(notificationRepository::delete);
		} else if (deleteType == 2) {   // 알림 전체 삭제
			notificationRepository.deleteAll(notificationList);
		}

		return true;
	}

}