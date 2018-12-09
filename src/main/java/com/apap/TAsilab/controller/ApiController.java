package com.apap.TAsilab.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.apap.TAsilab.model.JenisPemeriksaanModel;
import com.apap.TAsilab.model.KebutuhanReagenModel;
import com.apap.TAsilab.model.PemeriksaanModel;
import com.apap.TAsilab.repository.JadwalJagaDB;
import com.apap.TAsilab.repository.JenisPemeriksaanDB;
import com.apap.TAsilab.repository.KebutuhanReagenDB;
import com.apap.TAsilab.repository.PemeriksaanDB;
import com.apap.TAsilab.rest.BaseResponse;

/**
 * 
 * Khusus untuk api fitur 8 dan
 * Implementasi api controller
 * Base url: 
 */
@RestController
public class ApiController {
	@Autowired
	PemeriksaanDB pemeriksaanDb;
	
	@Autowired
	KebutuhanReagenDB kebutuhanReagenDb;
	
	@Autowired
	JenisPemeriksaanDB jenisPeriksaDb;
	
	@Autowired
	JadwalJagaDB jadwalDb;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	/*
	 * API Untuk
	 * Menerima request pemeriksaan
	 */
	@PostMapping(value="/lab/pemeriksaan/permintaan")
	public BaseResponse<PemeriksaanModel> addPermintaanPemeriksaan(@RequestBody PemeriksaanModel pemeriksaanLab, BindingResult bindingResult){
		BaseResponse<PemeriksaanModel> response = new BaseResponse<PemeriksaanModel>();
		java.util.Date uDate = new java.util.Date();
		java.sql.Date sDate = new java.sql.Date(uDate.getTime());
		if (bindingResult.hasErrors()) {
			response.setStatus(500);
			response.setMessage("Error Data");
			response.setResult(null);
		}
		else if(pemeriksaanLab.getIdPasien()<=0) {
			response.setStatus(500);
			response.setMessage("ID Pasien tidak boleh minus");
			response.setResult(null);
		}
		else if(pemeriksaanLab.getTanggalPengajuan().before(sDate)) {
			response.setStatus(500);
			response.setMessage("Tangggal Tidak boleh masa lalu");
			response.setResult(null);
		}
		else {
			// Set tanggal pengajuan berdasarkan submit
			pemeriksaanLab.setStatus(0);
			//cek tanggal pemeriksaan dengan jadwal jaga
			//jika tidak ada jadwal jaga pada tanggal tersebut,
			pemeriksaanLab.setJadwalJaga(jadwalDb.findAll().get(0));
			pemeriksaanLab.setTanggalPemeriksaan(jadwalDb.findAll().get(0).getTanggal());
			pemeriksaanLab.setHasil("Belum Ada Hasil");
			pemeriksaanDb.save(pemeriksaanLab);
			response.setStatus(200);
			response.setMessage("Success");
			response.setResult(pemeriksaanLab);
		}
		return response;
	}
	

	@GetMapping(value = "/lab/kebutuhan/perencanaan")
    public BaseResponse<List<KebutuhanReagenModel>> getAllKebutuhanReagen() {
        BaseResponse<List<KebutuhanReagenModel>> response = new BaseResponse<List<KebutuhanReagenModel>>();
        response.setStatus(200);
        response.setMessage("success");
        List<KebutuhanReagenModel> listKebutuhanReagen = kebutuhanReagenDb.findAll();
        
        response.setResult(listKebutuhanReagen);
        return response;
    }
	
	/*
	 * API untuk
	 * Memberikan daftar jenis pemeriksaan yang disediakan laboratorium
	 */
	@GetMapping(value="/getAllJenisPemeriksaan")
	public BaseResponse<List<JenisPemeriksaanModel>> viewJenisPemeriksaan(){
		BaseResponse<List<JenisPemeriksaanModel>> response = new BaseResponse<List<JenisPemeriksaanModel>>();
		response.setStatus(200);
		response.setMessage("Success");
		response.setResult(jenisPeriksaDb.findAll());
		return response;
	}
}
