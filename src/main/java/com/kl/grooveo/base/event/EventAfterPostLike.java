package com.kl.grooveo.base.event;

import org.springframework.context.ApplicationEvent;

import com.kl.grooveo.boundedContext.thumbsUp.entity.ThumbsUp;

import lombok.Getter;

@Getter
public class EventAfterPostLike extends ApplicationEvent {
	private final ThumbsUp thumbsUp;

	public EventAfterPostLike(Object source, ThumbsUp thumbsUp) {
		super(source);
		this.thumbsUp = thumbsUp;
	}
}
