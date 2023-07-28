package com.kl.grooveo.boundedContext.response;

import lombok.Data;

@Data
public class ThumbsUpDTO {
	private String result;
	private int likeCnt;

	public ThumbsUpDTO(String result, int likeCnt) {
		this.result = result;
		this.likeCnt = likeCnt;
	}
}
