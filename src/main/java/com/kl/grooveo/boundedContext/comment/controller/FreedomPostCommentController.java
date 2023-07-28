package com.kl.grooveo.boundedContext.comment.controller;

import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.boundedContext.comment.entity.FreedomPostComment;
import com.kl.grooveo.boundedContext.comment.service.FreedomPostCommentService;
import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.community.service.FreedomPostService;
import com.kl.grooveo.boundedContext.form.CommentForm;
import com.kl.grooveo.boundedContext.form.ReplyForm;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/comment")
@RequiredArgsConstructor
@Controller
public class FreedomPostCommentController {
	private final FreedomPostService freedomPostService;
	private final FreedomPostCommentService freedomPostCommentService;
	private final MemberService memberService;
	private final Rq rq;

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/{id}")
	public String create(Model model, @PathVariable("id") Long id,
		@RequestParam(value = "commentPage", defaultValue = "0") int commentPage,
		@RequestParam(value = "so", defaultValue = "create") String so,
		@Valid CommentForm commentForm, BindingResult bindingResult, ReplyForm replyForm) {

		FreedomPost freedomPost = freedomPostService.getFreedomPost(id);
		Member member = memberService.findByUsername(rq.getMember().getUsername()).orElseThrow();
		Page<FreedomPostComment> commentPaging = getCommentPaging(freedomPost, commentPage, "create");

		if (bindingResult.hasErrors()) {
			model.addAttribute("freedomPost", freedomPost);
			model.addAttribute("commentPaging", commentPaging);
			return "usr/community/freedomPost/detail";
		}

		FreedomPostComment freedomPostComment = createFreedomPostComment(freedomPost, commentForm.getContent(), member);
		commentPaging = getCommentPaging(freedomPost, commentPage, so);

		if (so.equals("recent")) {
			return createRedirectUrl(freedomPostComment.getFreedomPost().getId(), 0, so, freedomPostComment.getId());
		}

		return createRedirectUrl(freedomPostComment.getFreedomPost().getId(), commentPaging.getTotalPages() - 1, so,
			freedomPostComment.getId());
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{id}")
	public String delete(@PathVariable("id") Long id) {
		FreedomPostComment freedomPostComment = freedomPostCommentService.getComment(id);

		if (!freedomPostComment.getAuthor().getUsername().equals(rq.getMember().getUsername())) {
			throw new AccessDeniedException("삭제권한이 없습니다.");
		}

		freedomPostCommentService.delete(freedomPostComment);
		return createDetailRedirectUrl(freedomPostComment.getFreedomPost().getId());
	}

	private Page<FreedomPostComment> getCommentPaging(FreedomPost freedomPost, int commentPage, String so) {
		return freedomPostCommentService.getList(freedomPost, commentPage, so);
	}

	private FreedomPostComment createFreedomPostComment(FreedomPost freedomPost, String content, Member member) {
		return freedomPostCommentService.create(freedomPost, content, member);
	}

	private String createRedirectUrl(Long postId, int commentPage, String so, Long commentId) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/community/freedomPost/detail/{postId}")
			.queryParam("commentPage", commentPage)
			.queryParam("so", so)
			.fragment("comment-" + commentId);

		return "redirect:" + builder.buildAndExpand(postId).toUriString();
	}

	private String createDetailRedirectUrl(Long postId) {
		return "redirect:/community/freedomPost/detail/" + postId;
	}
}
