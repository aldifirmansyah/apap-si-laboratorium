package com.apap.TAsilab.service;


import java.util.List;
import java.util.Map;

import org.json.simple.parser.ParseException;
import com.apap.TAsilab.model.PemeriksaanModel;
import com.apap.TAsilab.rest.KamarDetail;
import com.apap.TAsilab.rest.PasienDetail;


public interface PemeriksaanService {
	PemeriksaanModel findPemeriksaanById(int id);
	List<PemeriksaanModel> findAll();
	void updatePemeriksaan(PemeriksaanModel pemeriksaan) throws ParseException;
	PasienDetail getPasien(int idPasien) throws ParseException;
	Map<Integer, PasienDetail> getPatient() throws ParseException;
	void delete(PemeriksaanModel pemeriksaan);
	void addPemeriksaanDarah() throws ParseException;
	List<KamarDetail> getAllKamar() throws ParseException;
//	Map<Integer, Pemeriksaan> cekPemeriksaan();
	
	
	
}
