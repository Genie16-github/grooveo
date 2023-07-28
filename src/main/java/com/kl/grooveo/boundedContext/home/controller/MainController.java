package com.kl.grooveo.boundedContext.home.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.kl.grooveo.boundedContext.library.entity.FileInfo;
import com.kl.grooveo.boundedContext.library.service.FileInfoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {

	private final FileInfoService fileInfoService;

	@GetMapping("/")
	public String main(Model model) {
		List<FileInfo> latestSongs = fileInfoService.getLatestSongs();
		List<FileInfo> popularSongs = fileInfoService.getPopularSongs();

		model.addAttribute("latestSongs", latestSongs);
		model.addAttribute("popularSongs", popularSongs);

		return "usr/home/main";
	}
}