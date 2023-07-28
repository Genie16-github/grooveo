package com.kl.grooveo.boundedContext.member.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModifyPasswordForm {

	@NotBlank
	@Size(min = 3, max = 30)
	private String previousPassword;

	@NotBlank
	@Size(min = 3, max = 30)
	private String newPassword;

	@NotBlank
	@Size(min = 3, max = 30)
	private String confirmNewPassword;
}