package com.kl.grooveo.base.event;

import org.springframework.context.ApplicationEvent;

import com.kl.grooveo.boundedContext.member.entity.Member;

import lombok.Getter;

@Getter
public class EventAfterSoundComment extends ApplicationEvent {
	private final Member artist;
	private final Member commentAuthor;

	public EventAfterSoundComment(Object source, Member commentAuthor, Member artist) {
		super(source);
		this.commentAuthor = commentAuthor;
		this.artist = artist;
	}
}
