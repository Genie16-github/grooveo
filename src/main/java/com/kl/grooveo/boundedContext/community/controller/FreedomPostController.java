package com.kl.grooveo.boundedContext.community.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.boundedContext.comment.entity.FreedomPostComment;
import com.kl.grooveo.boundedContext.comment.service.FreedomPostCommentService;
import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.community.service.FreedomPostService;
import com.kl.grooveo.boundedContext.form.CommentForm;
import com.kl.grooveo.boundedContext.form.FreedomPostForm;
import com.kl.grooveo.boundedContext.form.ReplyForm;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequestMapping("/community/freedomPost")
@RequiredArgsConstructor
@Controller
public class FreedomPostController {
	private final FreedomPostService freedomPostService;
	private final FreedomPostCommentService freedomPostCommentService;
	private final Rq rq;
	static int boardTypeCode;

	@GetMapping("/{boardType}/list")
	@PreAuthorize("isAuthenticated()")
	public String showList(Model model, @PathVariable("boardType") Integer boardType,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "kw", defaultValue = "") String kw, CategoryForm categoryForm) {
		Page<FreedomPost> paging = this.freedomPostService.getList(boardType, categoryForm.options, kw, page);
		model.addAttribute("boardType", boardType);
		model.addAttribute("paging", paging);
		model.addAttribute("kw", kw);
		boardTypeCode = boardType;

		return "usr/community/freedomPost/list";
	}

	@Setter
	public static class CategoryForm {
		private String options = "";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping(value = "/detail/{id}")
	public String showMoreDetail(Model model, @PathVariable("id") Long id,
		@RequestParam(value = "so", defaultValue = "create") String so,
		@RequestParam(value = "commentPage", defaultValue = "0") int commentPage, CommentForm commentForm,
		ReplyForm replyForm,
		HttpServletRequest request, HttpServletResponse response) {
		FreedomPost freedomPost = this.freedomPostService.getFreedomPost(id);

		// 조회수 관련 로직
		Cookie oldCookie = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {   // 쿠키가 null 인지 검사
			// null 이 아니라면 "postView" 라는 이름의 쿠키가 있는지 검사
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("postView")) {
					oldCookie = cookie;
				}
			}
		}

		if (oldCookie != null) {
			// "postView" 가 존재한다면
			// value 가 현재 접근한 게시글의 id 를 포함하고 있는지 검사
			if (!oldCookie.getValue().contains("[" + id.toString() + "]")) {
				// 포함하고 있지 않으면 조회수 증가
				this.freedomPostService.updateView(id);
				oldCookie.setValue(oldCookie.getValue() + "_[" + id + "]");
				oldCookie.setPath("/");
				oldCookie.setMaxAge(60 * 60 * 24);                            // 쿠키 시간
				response.addCookie(oldCookie);
			}
		} else {
			// "postView" 가 존재하지 않는다면
			// 게시글의 id 를 포함하는 쿠키를 만들고
			// 마찬가지로 조회수 증가
			this.freedomPostService.updateView(id);
			Cookie newCookie = new Cookie("postView", "[" + id + "]");
			newCookie.setPath("/");
			newCookie.setMaxAge(60 * 60 * 24);                                // 쿠키 시간
			response.addCookie(newCookie);
		}

		Page<FreedomPostComment> commentPaging = this.freedomPostCommentService.getList(freedomPost, commentPage, so);

		model.addAttribute("commentPaging", commentPaging);
		model.addAttribute("freedomPost", freedomPost);
		model.addAttribute("so", so);

		return "usr/community/freedomPost/detail";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/create")
	public String freedomPostCreate(Model model, FreedomPostForm freedomPostForm) {
		return "usr/community/freedomPost/form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create")
	public String freedomPostCreate(Model model, @Valid FreedomPostForm freedomPostForm, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "usr/community/freedomPost/form";
		}

		this.freedomPostService.create(boardTypeCode, freedomPostForm.getTitle(), freedomPostForm.getCategory(),
			freedomPostForm.getContent(), rq.getMember());
		return String.format("redirect:/community/freedomPost/%d/list", boardTypeCode);
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{id}")
	public String freedomPostDelete(@PathVariable("id") Long id) {
		FreedomPost freedomPost = this.freedomPostService.getFreedomPost(id);

		if (!freedomPost.getAuthor().getUsername().equals(rq.getMember().getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
		}

		this.freedomPostService.delete(freedomPost);
		return String.format("redirect:/community/freedomPost/%d/list", boardTypeCode);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String freedomPostModify(FreedomPostForm freedomPostForm, @PathVariable("id") Long id) {
		FreedomPost freedomPost = this.freedomPostService.getFreedomPost(id);

		if (!freedomPost.getAuthor().getUsername().equals(rq.getMember().getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}

		freedomPostForm.setTitle(freedomPost.getTitle());
		freedomPostForm.setCategory(freedomPost.getCategory());
		freedomPostForm.setContent(freedomPost.getContent());
		return "usr/community/freedomPost/form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String freedomPostModify(@Valid FreedomPostForm freedomPostForm, BindingResult bindingResult,
		@PathVariable("id") Long id) {
		if (bindingResult.hasErrors()) {
			return "usr/community/freedomPost/form";
		}
		FreedomPost freedomPost = this.freedomPostService.getFreedomPost(id);

		if (!freedomPost.getAuthor().getUsername().equals(rq.getMember().getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}

		this.freedomPostService.modify(freedomPost, freedomPostForm.getTitle(), freedomPostForm.getCategory(),
			freedomPostForm.getContent());
		return String.format("redirect:/community/freedomPost/detail/%s", id);
	}

	@GetMapping("/getView")
	@ResponseBody
	public String getViewCnt(@RequestParam("postId") Long postId) {
		return String.valueOf(freedomPostService.getViewCnt(postId));  // 조회수 String 처리
	}
}
