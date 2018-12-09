package com.apap.TAsilab.controller;



import java.util.List;
import java.util.Map;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.apap.TAsilab.model.JadwalJagaModel;
import com.apap.TAsilab.model.JenisPemeriksaanModel;
import com.apap.TAsilab.model.LabSuppliesModel;
import com.apap.TAsilab.model.PemeriksaanModel;
import com.apap.TAsilab.rest.HasilLab;

import com.apap.TAsilab.rest.KamarDetail;
import com.apap.TAsilab.rest.PasienDetail;
import com.apap.TAsilab.service.JadwalJagaService;
import com.apap.TAsilab.service.JenisPemeriksaanService;
import com.apap.TAsilab.service.LabSuppliesService;
import com.apap.TAsilab.service.PemeriksaanService;


@Controller
public class PemeriksaanController {

	@Autowired
	PemeriksaanService pemeriksaanService;
	
	@Autowired
	JenisPemeriksaanService jenisPemeriksaanService;
	
	@Autowired
	LabSuppliesService labService;
	
	@Autowired
	JadwalJagaService jadwalService;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Bean
	public RestTemplate rest() {
		return new RestTemplate();
	}
	
	@PostMapping(value="/kirim/hasil-lab")
	public String addLabResult(@RequestParam (value="id") int id) {
		PemeriksaanModel pemeriksaan = pemeriksaanService.findPemeriksaanById(id);
		HasilLab hasil = new HasilLab();
		PasienDetail pasien = new PasienDetail();
		pasien.setId(pemeriksaan.getIdPasien());
		hasil.setJenis(pemeriksaan.getJenisPemeriksaan().getNama());
		hasil.setHasil(pemeriksaan.getHasil());
		hasil.setTanggalPengajuan(pemeriksaan.getTanggalPengajuan());
		hasil.setPasien(pasien);
		try {
			restTemplate.postForObject("http://si-appointment.herokuapp.com/api/03/addLabResult", hasil, ResponseEntity.class);
			return "lihat-daftar-pemeriksaan";
		}
		catch(Exception e) {
			pemeriksaanService.delete(pemeriksaan);
			return "lihat-daftar-pemeriksaan";
		}
	}
	
	@RequestMapping(value = "/lab/pemeriksaan/permintaan", method = RequestMethod.GET)
	public String viewAllPemeriksaan(Model model) throws ParseException {
		List<PemeriksaanModel> listPemeriksaan = pemeriksaanService.findAll();
		Map<Integer, PasienDetail> mapPasien = pemeriksaanService.getPatient();
//		Map<Integer, KamarDetail> mapKamar = pemeriksaanService.getRoom();
		
		if(listPemeriksaan.size()==0) {
			model.addAttribute("header", "Tidak ada permintaan pemeriksaan");
		}
		else {
			model.addAttribute("listPemeriksaan", listPemeriksaan);
			model.addAttribute("mapPasien", mapPasien);
			model.addAttribute("header", "Daftar Permintaan Pemeriksaan");
			
		}
		model.addAttribute("title", "Daftar Pemeriksaan");
		return "lihat-daftar-pemeriksaan";
	}
	

	@RequestMapping(value = "/lab/pemeriksaan/{id}", method = RequestMethod.GET)
	public String ubahStatus(@PathVariable(value= "id") int idPemeriksaan, Model model) throws ParseException {
		PemeriksaanModel pemeriksaan = pemeriksaanService.findPemeriksaanById(idPemeriksaan);
		Map<Integer, PasienDetail> mapPasien = pemeriksaanService.getPatient();
		List<JadwalJagaModel> jadwal = jadwalService.getJadwalAll();
		// kondisi perubahan status dari proses menjadi selesai
		if(pemeriksaan.getStatus()==1) {
			model.addAttribute("old", pemeriksaan);
			model.addAttribute("mapPasien", mapPasien);
			model.addAttribute("tanggal", jadwal);
		}
		else {
			JenisPemeriksaanModel jenisPemeriksaan = jenisPemeriksaanService.findById(pemeriksaan.getJenisPemeriksaan().getId());
			List<LabSuppliesModel> lab = jenisPemeriksaan.getListSupplies();
			boolean stokAda = labService.cekLabSupplies(lab);
			if(stokAda==false) {
				model.addAttribute("msg", "Stok lab habis, Status tidak dapat dirubah");
				return "success-page";
			}
		}
		model.addAttribute("mapPasien", mapPasien);
		model.addAttribute("old", pemeriksaan);
		model.addAttribute("tanggal", jadwal);
		return "ubah-status";
	}

	
	@RequestMapping(value = "/lab/pemeriksaan", method = RequestMethod.POST)
	public String ubahStatusSubmit(@ModelAttribute PemeriksaanModel pemeriksaan, Model model) throws ParseException {

		pemeriksaanService.updatePemeriksaan(pemeriksaan);
		model.addAttribute("msg", "Status Pemeriksaan berhasil diubah");
		return "success-page";
	}
}
