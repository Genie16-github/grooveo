package com.kl.grooveo.boundedContext.form;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SoundTrackForm {
	@NotEmpty(message = "제목은 필수항목입니다.")
	@Size(max = 200)
	private String title;

	@NotEmpty(message = "설명은 필수항목입니다.")
	private String description;

	private MultipartFile soundFile;

	private MultipartFile albumCover;

	public SoundTrackForm(String title, String description, MultipartFile soundFile, MultipartFile albumCover) {
		this.title = title;
		this.description = description;
		this.soundFile = soundFile;
		this.albumCover = albumCover;
	}
}
