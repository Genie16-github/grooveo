package com.kl.grooveo.boundedContext.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileInfoForm {
	@NotEmpty(message = "The description is required.")
	private String description;
}
