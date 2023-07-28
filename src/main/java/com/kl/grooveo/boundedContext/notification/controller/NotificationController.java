package com.kl.grooveo.boundedContext.notification.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.boundedContext.notification.entity.Notification;
import com.kl.grooveo.boundedContext.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/usr/notification")
@RequiredArgsConstructor
public class NotificationController {
	private final Rq rq;
	private final NotificationService notificationService;

	@GetMapping("/list")
	@PreAuthorize("isAuthenticated()")
	public String showList(Model model) {

		List<Notification> notifications = notificationService.findByToMember(rq.getMember());

        /*
            본인에 의한 알림 제외
            알림 자체가 생성되지 않도록 추후 수정 필요
         */
		List<Notification> filteredNotifications = notifications.stream()
			.filter(notification -> !notification.getFromMember().getUsername().equals(rq.getMember().getUsername()))
			.toList();

		model.addAttribute("notifications", filteredNotifications);

		return "usr/notification/list";
	}

	@PostMapping("/readNotification")
	@ResponseBody
	@PreAuthorize("isAuthenticated()")
	public String afterReadNotification(@RequestParam Long notificationId) {
		if (notificationService.getAfterReadNotification(notificationId)) {
			return "success";
		}
		return "fail";
	}

	@PostMapping("/deleteNotification")
	@ResponseBody
	@PreAuthorize("isAuthenticated()")
	public String deleteNotification(@RequestParam Long notificationId) {
		if (notificationService.deleteNotification(notificationId)) {
			return "success";
		}
		return "fail";
	}

	@PostMapping("/allDelete")
	@ResponseBody
	@PreAuthorize("isAuthenticated()")
	public String allDeleteNotification(@RequestParam Integer deleteType) {
		if (notificationService.allDeleteNotification(deleteType)) {
			return "success";
		}
		return "fail";
	}
}
