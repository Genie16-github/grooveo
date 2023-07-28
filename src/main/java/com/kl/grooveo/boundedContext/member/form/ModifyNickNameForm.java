package com.kl.grooveo.boundedContext.member.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModifyNickNameForm {

	@NotBlank
	@Size(min = 2, max = 10)
	private String nickName;
}