package com.kl.grooveo.base.event;

import org.springframework.context.ApplicationEvent;

import com.kl.grooveo.boundedContext.thumbsUp.entity.SoundThumbsUp;

import lombok.Getter;

@Getter
public class EventAfterSoundLike extends ApplicationEvent {
	private final SoundThumbsUp soundThumbsUp;

	public EventAfterSoundLike(Object source, SoundThumbsUp soundThumbsUp) {
		super(source);
		this.soundThumbsUp = soundThumbsUp;
	}
}
