package com.kl.grooveo.boundedContext.reply.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.kl.grooveo.base.exception.DataNotFoundException;
import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.boundedContext.comment.entity.SoundPostComment;
import com.kl.grooveo.boundedContext.comment.service.SoundPostCommentService;
import com.kl.grooveo.boundedContext.form.CommentForm;
import com.kl.grooveo.boundedContext.form.ReplyForm;
import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.service.MemberService;
import com.kl.grooveo.boundedContext.reply.entity.SoundPostReply;
import com.kl.grooveo.boundedContext.reply.service.SoundPostReplyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/library/reply")
@RequiredArgsConstructor
@Controller
public class SoundPostReplyController {

	private final SoundPostCommentService soundPostCommentService;
	private final SoundPostReplyService soundPostReplyService;
	private final MemberService memberService;
	private final Rq rq;

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/{id}")
	public String create(Model model, @PathVariable("id") Long id,
		@Valid ReplyForm replyForm, @RequestParam(value = "commentPage", defaultValue = "0") int commentPage,
		@RequestParam(value = "so", defaultValue = "create") String so,
		BindingResult bindingResult, CommentForm commentForm) {

		SoundPostComment soundPostComment = this.soundPostCommentService.getComment(id);
		Member member = this.memberService.findByUsername(rq.getMember().getUsername()).orElseThrow(
			() -> new DataNotFoundException("해당 유저를 찾을 수 없습니다.")
		);

		FileInfo fileInfo = soundPostComment.getFileInfo();
		Page<SoundPostComment> commentPaging = this.soundPostCommentService.getList(fileInfo, commentPage, "create");

		if (bindingResult.hasErrors()) {
			model.addAttribute("fileInfo", fileInfo);
			model.addAttribute("commentPaging", commentPaging);
			return "usr/library/soundDetail";
		}

		SoundPostReply soundPostReply = this.soundPostReplyService.create(soundPostComment, replyForm.getContent(),
			member);
		model.addAttribute("fileInfo", fileInfo);
		model.addAttribute("commentPaging", commentPaging);
		return String.format("redirect:/library/soundDetail/%s?commentPage=%s&so=%s#reply-%s",
			soundPostReply.getSoundPostComment().getFileInfo().getId(), commentPage, so,
			soundPostReply.getSoundPostComment().getId());
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{id}")
	public String delete(@PathVariable("id") Long id) {
		SoundPostReply soundPostReply = this.soundPostReplyService.getReply(id);
		SoundPostComment soundPostComment = this.soundPostCommentService.getComment(
			soundPostReply.getSoundPostComment().getId());

		if (!soundPostReply.getAuthor().getUsername().equals(rq.getMember().getUsername())
			&& !soundPostComment.getAuthor().getUsername().equals(rq.getMember().getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
		}

		this.soundPostReplyService.delete(soundPostReply);
		return String.format("redirect:/library/soundDetail/%s", soundPostComment.getFileInfo().getId());
	}
}
