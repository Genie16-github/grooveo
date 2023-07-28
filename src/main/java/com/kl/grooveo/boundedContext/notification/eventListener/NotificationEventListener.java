package com.kl.grooveo.boundedContext.notification.eventListener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kl.grooveo.base.event.EventAfterComment;
import com.kl.grooveo.base.event.EventAfterFollow;
import com.kl.grooveo.base.event.EventAfterPostLike;
import com.kl.grooveo.base.event.EventAfterSoundComment;
import com.kl.grooveo.base.event.EventAfterSoundLike;
import com.kl.grooveo.base.event.EventAfterUnFollow;
import com.kl.grooveo.base.event.EventAfterUpload;
import com.kl.grooveo.boundedContext.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class NotificationEventListener {
	private final NotificationService notificationService;

	@EventListener
	@Transactional
	public void listen(EventAfterPostLike event) {
		notificationService.whenAfterPostLike(event.getThumbsUp());
	}

	@EventListener
	@Transactional
	public void listen(EventAfterSoundComment event) {
		notificationService.whenAfterSoundComment(event.getCommentAuthor(), event.getArtist());
	}

	@EventListener
	@Transactional
	public void listen(EventAfterComment event) {
		notificationService.whenAfterComment(event.getCommentAuthor(), event.getPostAuthor());
	}

	@EventListener
	@Transactional
	public void listen(EventAfterFollow event) {
		notificationService.whenAfterFollow(event.getFollow());
	}

	@EventListener
	@Transactional
	public void listen(EventAfterUnFollow event) {
		notificationService.whenAfterUnFollow(event.getFollow());
	}

	@EventListener
	@Transactional
	public void listen(EventAfterSoundLike event) {
		notificationService.whenAfterSoundLike(event.getSoundThumbsUp());
	}

	@EventListener
	@Transactional
	public void listen(EventAfterUpload event) {
		notificationService.whenAfterUpload(event.getFollow());
	}
}