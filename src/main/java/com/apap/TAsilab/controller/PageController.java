package com.apap.TAsilab.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.apap.TAsilab.model.PemeriksaanModel;
import com.apap.TAsilab.model.UserRoleModel;
import com.apap.TAsilab.service.PemeriksaanService;
import com.apap.TAsilab.service.UserRoleService;


@Controller
public class PageController {
	
	@Autowired
	private PemeriksaanService pemeriksaanService;
	
	
	@RequestMapping("/")
	public String home(Model model) {
		int jumlah = pemeriksaanService.cekPemeriksaanTerbaru();
		model.addAttribute("jumlah", jumlah);
		return "home";
	}
	
	@RequestMapping("/login")
	public String login() {
		return "login";
	}
}
