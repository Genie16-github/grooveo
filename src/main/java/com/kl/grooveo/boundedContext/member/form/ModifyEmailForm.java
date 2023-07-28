package com.kl.grooveo.boundedContext.member.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModifyEmailForm {

	@NotBlank
	@Email
	@Size(min = 3, max = 30)
	private String email;
}

