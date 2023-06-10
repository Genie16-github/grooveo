package com.kl.grooveo.boundedContext.member.controller;

import com.kl.grooveo.base.email.service.EmailService;
import com.kl.grooveo.base.rq.Rq;
import com.kl.grooveo.base.rsData.RsData;
import com.kl.grooveo.boundedContext.comment.entity.FreedomPostComment;
import com.kl.grooveo.boundedContext.comment.service.FreedomPostCommentService;
import com.kl.grooveo.boundedContext.community.entity.FreedomPost;
import com.kl.grooveo.boundedContext.community.service.FreedomPostService;
import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.form.*;
import com.kl.grooveo.boundedContext.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/member")
public class MemberController {

    private final MemberService memberService;
    private final FreedomPostService freedomPostService;
    private final FreedomPostCommentService freedomPostCommentService;
    private final EmailService emailService;
    private final Rq rq;

    @GetMapping("/join")
    public String showJoin() {
        return "usr/member/join";
    }

    @PostMapping("/join")
    public String join(@Valid JoinForm joinForm, HttpServletRequest request) {
        RsData emailVerified = memberService.isEmailVerified(request);

        if (emailVerified.isFail()) {
            return rq.historyBack(emailVerified);
        }

        RsData<Member> joinRs = memberService.join(joinForm.getUsername(), joinForm.getPassword(),
                joinForm.getName(), joinForm.getNickName(), joinForm.getEmail());

        if (joinRs.isFail()) {
            return rq.historyBack(joinRs);
        }

        return rq.redirectWithMsg("/usr/member/login", joinRs);
    }

    @GetMapping("/login")
    public String showLogin(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "exception", required = false) String exception,
                            Model model) {
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);
        return "usr/member/login";
    }

    @GetMapping("/findUsername")
    public String showFindId() {
        return "usr/member/findUsername";
    }

    @PostMapping("/findUsername")
    public String findId(@Valid FindUsernameForm findUsernameForm) {
        RsData findIdRs = memberService.findUsername(findUsernameForm.getEmail());

        if (findIdRs.isFail()) {
            return rq.historyBack(findIdRs);
        }

        return rq.redirectWithMsg("/usr/member/login", findIdRs);
    }

    @GetMapping("/findPassword")
    public String showFindPassword() {
        return "usr/member/findPassword";
    }

    @PostMapping("/findPassword")
    public String findPassword(@Valid FindPasswordForm findPasswordForm) {
        RsData findPasswordRs = memberService.findUserPassword(findPasswordForm.getUsername(), findPasswordForm.getEmail());

        if (findPasswordRs.isFail()) {
            return rq.historyBack(findPasswordRs);
        }

        return rq.redirectWithMsg("/usr/member/login", findPasswordRs);
    }

    @PostMapping("/sendCode")
    @ResponseBody
    public String sendVerificationCode(HttpServletRequest request, String userEmail) {
        HttpSession session = request.getSession();

        emailService.sendVerificationCode(session, userEmail);

        return "";
    }

    @PostMapping("/certification")
    @ResponseBody
    public boolean checkVerificationCode(HttpServletRequest request, String userEmail, String inputCode) {
        HttpSession session = request.getSession();

        return emailService.emailCertification(session, userEmail, inputCode);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPage")
    public String showMyPage() {
        return "usr/member/myPage";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPage/post")
    public String showMyPost(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        Member member = rq.getMember();
        List<FreedomPost> freedomPosts = member.getFreedomPosts();
        Page<FreedomPost> paging = freedomPostService.getList(member.getId(), page);
        model.addAttribute("paging", paging);
        model.addAttribute("freedomPosts", freedomPosts);
        return "usr/member/myPage/post";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPage/comment")
    public String showMyComment(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        Member member = rq.getMember();
        List<FreedomPostComment> freedomPostComments = member.getFreedomPostComments();
        Page<FreedomPostComment> paging = freedomPostCommentService.getCommentList(member.getId(), page);
        model.addAttribute("paging", paging);
        model.addAttribute("freedomPostComments", freedomPostComments);
        return "usr/member/myPage/comment";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPage/library")
    public String showMyLibrary() {
        return "usr/member/myPage/library";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPage/modifyPassword")
    public String showModifyPassword() {
        return "usr/member/myPage/modifyPassword";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/myPage/modifyPassword")
    public String modifyPassword(@Valid ModifyPasswordForm modifyPasswordForm, HttpSession session) {
        RsData<Member> member = memberService.modifyPassword(rq.getMember(), modifyPasswordForm.getPreviousPassword(),
                modifyPasswordForm.getNewPassword(), modifyPasswordForm.getConfirmNewPassword());

        if (member.isFail()) {
            return rq.historyBack(member);
        }

        session.invalidate();

        return rq.redirectWithMsg("/usr/member/login", member);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPage/modifyNickName")
    public String showModifyNickName() {
        return "usr/member/myPage/modifyNickName";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/myPage/modifyNickName")
    public String modifyInfo(@Valid ModifyNickNameForm modifyInfoForm) {
        RsData<Member> member = memberService.modifyNickName(rq.getMember(), modifyInfoForm.getNickName());

        if (member.isFail()) {
            return rq.historyBack(member);
        }

        return rq.redirectWithMsg("/usr/member/myPage/me", member);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPage/modifyEmail")
    public String showModifyEmail() {
        return "usr/member/myPage/modifyEmail";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/myPage/modifyEmail")
    public String modifyEmail(@Valid ModifyEmailForm modifyEmailForm) {
        RsData<Member> member = memberService.modifyEmail(rq.getMember(), modifyEmailForm.getEmail());

        if (member.isFail()) {
            return rq.historyBack(member);
        }

        return rq.redirectWithMsg("/usr/member/myPage/me", member);
    }
}