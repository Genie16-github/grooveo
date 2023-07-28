package com.kl.grooveo.boundedContext.member.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FindPasswordForm {

	@NotBlank
	@Size(min = 4, max = 30)
	private final String username;

	@NotBlank
	@Size(min = 4, max = 30)
	private final String email;
}