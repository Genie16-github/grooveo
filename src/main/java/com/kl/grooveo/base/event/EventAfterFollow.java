package com.kl.grooveo.base.event;

import org.springframework.context.ApplicationEvent;

import com.kl.grooveo.boundedContext.follow.entity.Follow;

import lombok.Getter;

@Getter
public class EventAfterFollow extends ApplicationEvent {

	private final Follow follow;

	public EventAfterFollow(Object source, Follow follow) {
		super(source);
		this.follow = follow;
	}
}