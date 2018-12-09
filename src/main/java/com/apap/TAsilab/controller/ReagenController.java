package com.apap.TAsilab.controller;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.apap.TAsilab.model.KebutuhanReagenModel;
import com.apap.TAsilab.model.UserRoleModel;
import com.apap.TAsilab.service.KebutuhanReagenService;
import com.apap.TAsilab.service.LabSuppliesService;
import com.apap.TAsilab.service.UserRoleService;

@Controller
@RequestMapping("/lab/kebutuhan")
public class ReagenController {

	@Autowired
	KebutuhanReagenService kebutuhanReagenService;
	
	@Autowired
	LabSuppliesService labSuppliesService;
	
	@Autowired
	UserRoleService userRoleService;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	private String viewAllKebutuhanReagen(@ModelAttribute KebutuhanReagenModel reagen, Model model) {
		List<KebutuhanReagenModel> listKebutuhanReagen = kebutuhanReagenService.findAll();
		if(listKebutuhanReagen.size()==0) {
			model.addAttribute("header", "Perencanaan Kebutuhan Reagen Belum Diajukan");
		}
		else {
			model.addAttribute("header", "Data Perencanaan Kebutuhan Reagen");
			model.addAttribute("listKebutuhanReagen", listKebutuhanReagen);
		}
		
		// Untuk tambah baru
		model.addAttribute("listReagen", labSuppliesService.getAllReagen());
		model.addAttribute("kebutuhanReagen", new KebutuhanReagenModel());
		
		
		// Role
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserRoleModel user = userRoleService.findUserByUsername(auth.getName());
		model.addAttribute("role", user.getRole());
		
		return "lihat-kebutuhan-reagen";
	}
	
	@RequestMapping(value = "/tambah", method = RequestMethod.POST)
	private String submitPerencanaanReagen(@ModelAttribute KebutuhanReagenModel kebutuhanReagen, Model model) {
		
		long millis=System.currentTimeMillis();
		Date todayDate = new Date(millis);
		kebutuhanReagen.setTanggalUpdate(todayDate);
		
		kebutuhanReagen.setStatus(0);
		
		kebutuhanReagen.setReagen(labSuppliesService.getSuppliesDetailById(kebutuhanReagen.getReagen().getId()));
		
		kebutuhanReagenService.add(kebutuhanReagen);
		
		model.addAttribute("msg", "Perencanaan kebutuhan reagen berhasil ditambah.");
		return "success-page";
	}

	@RequestMapping(value = "/ubah/{id}", method = RequestMethod.GET)
	public String ubahStatus(@PathVariable(value= "id") int idReagen, Model model) {
		KebutuhanReagenModel kebutuhanReagen = kebutuhanReagenService.findReagenById(idReagen).get();
		model.addAttribute("kebutuhanReagen", kebutuhanReagen);
		model.addAttribute("title", "Ubah Status Kebutuhan Reagen");
		return "ubah-kebutuhan-reagen";
	}
	
	@RequestMapping(value = "/ubah", method = RequestMethod.POST)
	public String submitUbahStatus(@ModelAttribute KebutuhanReagenModel kebutuhanReagen, Model model) {
		int statusTemp = kebutuhanReagen.getStatus();
		kebutuhanReagen = kebutuhanReagenService.findReagenById(kebutuhanReagen.getId()).get();
		
		if (statusTemp == kebutuhanReagen.getStatus()) {
			model.addAttribute("msg", "Harap ubah status kebutuhan reagen terlebih dahulu.");
		}
		else {
			kebutuhanReagen.setStatus(statusTemp);
			
			long millis=System.currentTimeMillis();
			Date todayDate = new Date(millis);
			kebutuhanReagen.setTanggalUpdate(todayDate);
			
			kebutuhanReagenService.add(kebutuhanReagen);
			model.addAttribute("msg", "Status perencanaan kebutuhan reagen berhasil diubah.");
		}
		return "success-page";
	}
}
