package com.kl.grooveo.boundedContext.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplyForm {
	@NotEmpty(message = "답글을 등록하시려면 내용을 입력해주세요.")
	private String content;
}
