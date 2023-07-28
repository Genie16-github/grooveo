package com.kl.grooveo.boundedContext.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findByToMemberOrderByCreateDateDesc(Member toMember);

	int countByToMemberAndReadDateIsNull(Member member);

	List<Notification> findByToMember(Member toMember);
}
