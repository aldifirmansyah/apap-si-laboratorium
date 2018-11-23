package com.apap.TAsilab.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.apap.TAsilab.model.PemeriksaanModel;
import com.apap.TAsilab.service.PemeriksaanService;


@Controller
public class PageController {
	
	@Autowired
	private PemeriksaanService pemeriksaanService;
	
	@RequestMapping("/")
	public String home() {
		return "home";
	}
	
	@RequestMapping("/login")
	public String login() {
		return "login";
	}

	@RequestMapping(value = "/pemeriksaan/view/{id}", method=RequestMethod.GET)
	private String viewPemeriksaan(@PathVariable(value="id") Long id, Model model) {
		PemeriksaanModel pemeriksaan = pemeriksaanService.findPemeriksaanById(id);
		model.addAttribute("id", pemeriksaan.getId());
		model.addAttribute("idPasien", pemeriksaan.getIdPasien());
		model.addAttribute("tglPeriksa", pemeriksaan.getTanggalPemeriksaan());
		model.addAttribute("tglPengajuan", pemeriksaan.getTanggalPengajuan());
		model.addAttribute("status", pemeriksaan.getStatus());
		model.addAttribute("jenisPemeriksaan", pemeriksaan.getJenisPemeriksaan().getNama());
		model.addAttribute("idStaff", pemeriksaan.getJadwalJaga().getIdStaff());
		model.addAttribute("tglJaga", pemeriksaan.getJadwalJaga().getTanggal());
		model.addAttribute("waktuMulai", pemeriksaan.getJadwalJaga().getWaktuMulai());
		model.addAttribute("waktuSelesai", pemeriksaan.getJadwalJaga().getWaktuSelesai());
		model.addAttribute("hasil", pemeriksaan.getHasil());
		return "view-pemeriksaan";

	}
}
