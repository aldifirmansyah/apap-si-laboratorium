package com.apap.TAsilab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.apap.TAsilab.model.LabSuppliesModel;
import com.apap.TAsilab.model.UserRoleModel;
import com.apap.TAsilab.service.LabSuppliesService;
import com.apap.TAsilab.service.UserRoleService;

@Controller
public class LabSuppliesController {
	@Autowired
	private LabSuppliesService labSupService;
	
	@Autowired
	private UserRoleService userService;

	@RequestMapping(value = "/lab/stok/tambah", method = RequestMethod.GET)
	public String tambahStok(Model model) {
		LabSuppliesModel labSup = new LabSuppliesModel();
		model.addAttribute("labSupplies", labSup);
		return "addPersediaan";
	}
	@RequestMapping(value = "/lab/stok/tambah", method = RequestMethod.POST)
	public String tambahStokSubmit(@ModelAttribute LabSuppliesModel LabSupplies, Model model) {
		labSupService.addSupplies(LabSupplies);
		model.addAttribute("msg", "Data berhasil ditambahkan");
		return "success-page";
	}
	@RequestMapping("/lab/stok")
	public String lihatStok(Model model) {
		UserRoleModel user = userService.findUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());	
		model.addAttribute("role", user.getRole());
		model.addAttribute("allSupplies", labSupService.getListSupplies());
		return "allPersediaan";
	}
	@RequestMapping(value = "/lab/stok/ubah/{id}", method = RequestMethod.GET)
	public String ubahStok(@PathVariable (value="id") int id, Model model) {
		LabSuppliesModel labUbah = labSupService.findById(id);
		model.addAttribute("lab", labUbah);
		return "ubah-lab";
	}
	@RequestMapping(value = "/lab/stok/ubah", method = RequestMethod.POST)
	public String ubahStokSubmit(@ModelAttribute LabSuppliesModel labSupplies, Model model) {
		labSupService.updateLabSupplies(labSupplies);
		model.addAttribute("msg", "Lab Supplies Berhasil Ditambah");
		return "success-page";
	}
}
