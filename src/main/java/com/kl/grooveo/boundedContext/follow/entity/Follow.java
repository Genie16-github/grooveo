package com.kl.grooveo.boundedContext.follow.entity;

import com.kl.grooveo.base.baseEntity.BaseEntity;
import com.kl.grooveo.boundedContext.member.entity.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
public class Follow extends BaseEntity {

	@ManyToOne
	private Member following; // 내가 좋아하는 사람

	@ManyToOne
	private Member follower; // 나를 좋아하는 사람
}