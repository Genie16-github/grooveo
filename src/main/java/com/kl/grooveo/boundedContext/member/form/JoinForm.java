package com.kl.grooveo.boundedContext.member.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JoinForm {

	@NotBlank
	@Size(min = 3, max = 30)
	private String username;

	@NotBlank
	@Size(min = 3, max = 30)
	private String password;

	@NotBlank
	@Size(min = 3, max = 30)
	private String confirmPassword;

	@NotBlank
	@Size(min = 2, max = 10)
	private String name;

	@NotBlank
	@Size(min = 2, max = 10)
	private String nickName;

	@NotBlank
	@Email
	@Size(min = 3, max = 30)
	private String email;

}
