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
import com.kl.grooveo.boundedContext.comment.entity.FreedomPostComment;
import com.kl.grooveo.boundedContext.comment.service.FreedomPostCommentService;
import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.form.CommentForm;
import com.kl.grooveo.boundedContext.form.ReplyForm;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.service.MemberService;
import com.kl.grooveo.boundedContext.reply.entity.FreedomPostReply;
import com.kl.grooveo.boundedContext.reply.service.FreedomPostReplyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/reply")
@RequiredArgsConstructor
@Controller
public class FreedomPostReplyController {

	private final FreedomPostCommentService freedomPostCommentService;
	private final FreedomPostReplyService freedomPostReplyService;
	private final MemberService memberService;
	private final Rq rq;

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/{id}")
	public String create(Model model, @PathVariable("id") Long id,
		@Valid ReplyForm replyForm, @RequestParam(value = "commentPage", defaultValue = "0") int commentPage,
		@RequestParam(value = "so", defaultValue = "create") String so,
		BindingResult bindingResult, CommentForm commentForm) {

		FreedomPostComment freedomPostComment = this.freedomPostCommentService.getComment(id);
		Member member = this.memberService.findByUsername(rq.getMember().getUsername()).orElseThrow(
			() -> new DataNotFoundException("해당 유저를 찾을 수 없습니다.")
		);

		FreedomPost freedomPost = freedomPostComment.getFreedomPost();
		Page<FreedomPostComment> commentPaging = this.freedomPostCommentService.getList(freedomPost, commentPage,
			"create");

		if (bindingResult.hasErrors()) {
			model.addAttribute("freedomPost", freedomPost);
			model.addAttribute("commentPaging", commentPaging);
			return "usr/community/freedomPost/detail";
		}

		FreedomPostReply freedomPostReply = this.freedomPostReplyService.create(freedomPostComment,
			replyForm.getContent(), member);
		model.addAttribute("freedomPost", freedomPost);
		model.addAttribute("commentPaging", commentPaging);
		return String.format("redirect:/community/freedomPost/detail/%s?commentPage=%s&so=%s#reply-%s",
			freedomPostReply.getFreedomPostComment().getFreedomPost().getId(), commentPage, so,
			freedomPostReply.getFreedomPostComment().getId());
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{id}")
	public String delete(@PathVariable("id") Long id) {
		FreedomPostReply freedomPostReply = this.freedomPostReplyService.getReply(id);
		FreedomPostComment freedomPostComment = this.freedomPostCommentService.getComment(
			freedomPostReply.getFreedomPostComment().getId());

		if (!freedomPostReply.getAuthor().getUsername().equals(rq.getMember().getUsername())
			&& !freedomPostComment.getAuthor().getUsername().equals(rq.getMember().getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
		}

		this.freedomPostReplyService.delete(freedomPostReply);
		return String.format("redirect:/community/freedomPost/detail/%s", freedomPostComment.getFreedomPost().getId());
	}
}
