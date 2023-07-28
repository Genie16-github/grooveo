package com.kl.grooveo.boundedContext.member.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FindUsernameForm {

	@NotBlank
	@Size(min = 4, max = 30)
	private final String email;
}