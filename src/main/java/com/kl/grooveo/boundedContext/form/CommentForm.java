package com.kl.grooveo.boundedContext.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {
	@NotEmpty(message = "댓글 등록을 하시려면 내용을 입력해주세요.")
	private String content;
}
