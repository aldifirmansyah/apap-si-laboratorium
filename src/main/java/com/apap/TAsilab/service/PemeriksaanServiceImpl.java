package com.apap.TAsilab.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.apap.TAsilab.model.JenisPemeriksaanModel;
import com.apap.TAsilab.model.LabSuppliesModel;
import com.apap.TAsilab.model.PemeriksaanModel;
import com.apap.TAsilab.repository.JenisPemeriksaanDB;
import com.apap.TAsilab.repository.PemeriksaanDB;
import com.apap.TAsilab.rest.KamarDetail;
import com.apap.TAsilab.rest.PasienDetail;


@Service
@Transactional
public class PemeriksaanServiceImpl implements PemeriksaanService{
	
	@Autowired
	private PemeriksaanDB pemeriksaanDb;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private JenisPemeriksaanDB jenisPemeriksaanDb;
	
	@Override
	public PasienDetail getPasien(int idPasien) throws ParseException {
		PasienDetail pasien = new PasienDetail();
		JSONParser parser = new JSONParser();
		String response = restTemplate.getForObject("http://si-appointment.herokuapp.com/api/getPasien/"+idPasien, String.class);
        JSONObject json = (JSONObject) parser.parse(response);
        JSONObject result = (JSONObject) json.get("result");
        String nama = (String) result.get("nama");
        int id_pasien = Integer.parseInt(result.get("id").toString());
        pasien.setId(id_pasien);
        pasien.setNama(nama);
        return pasien;
	}
	
	@Override
	public Map<Integer, PasienDetail> getPatient() throws ParseException {
		Map<Integer, PasienDetail> mapPasien = new HashMap<Integer, PasienDetail>();
		List<PemeriksaanModel> listPemeriksaan = pemeriksaanDb.findAll();
		for (PemeriksaanModel pemeriksaan : listPemeriksaan){
			PasienDetail pasien = this.getPasien((int)pemeriksaan.getIdPasien());
			mapPasien.put(pemeriksaan.getId(), pasien);
		}
		return mapPasien;
	}
	
//	@Override
//	public Map<Integer, KamarDetail> getRoom() throws ParseException {
//		Map<Integer, KamarDetail> mapKamar = new HashMap<Integer, KamarDetail>();
//		List<PemeriksaanModel> listPemeriksaan = pemeriksaanDb.findAll();
//		for (PemeriksaanModel pemeriksaan : listPemeriksaan){
//			KamarDetail kamar = this.getKamar((int)pemeriksaan.getIdPasien());
//			mapKamar.put(pemeriksaan.getId(), kamar);
//		}
//		
//		return mapKamar;
//	}
	
//	@Override
//	public KamarDetail getKamar(int idPasien) throws ParseException {
//		KamarDetail kamar = new KamarDetail();
//		JSONParser parser = new JSONParser();
//		String response = restTemplate.getForObject("http://si-rawatInap.herokuapp.com/api/get-all-kamar", String.class);
//        JSONObject json = (JSONObject) parser.parse(response);
//        JSONObject result = (JSONObject) json.get("result");
//        int requestPasien = (int) result.get("id");
//        int id_pasien = Integer.parseInt(result.get("id_pasien").toString());
//        int assignKamar = (int) result.get("assign");
//        kamar.setIdPasien(id_pasien);
//        kamar.setRequestPasien(requestPasien);
//        kamar.setAssignKamar(assignKamar);
//        return kamar;
//	}
	
	@Override
	public PemeriksaanModel findPemeriksaanById(int id) {
		// TODO Auto-generated method stub
		return pemeriksaanDb.findById(id).get();
	}

	@Override
	public List<PemeriksaanModel> findAll() {
		// TODO Auto-generated method stub
		return pemeriksaanDb.findAll();
	}

	@Override
	public void updatePemeriksaan(PemeriksaanModel pemeriksaan) throws ParseException {
		if(pemeriksaan.getStatus()==1) {
			pemeriksaan.setHasil("Belum Ada Hasil");
		}
		else if(pemeriksaan.getStatus()==2) {
			pemeriksaan.setHasil(pemeriksaan.getHasil());
		}
		JenisPemeriksaanModel jp = jenisPemeriksaanDb.findById(pemeriksaan.getJenisPemeriksaan().getId());
		for (LabSuppliesModel a: jp.getListSupplies()){
			a.setJumlah(a.getJumlah()-1);
		}
		
		pemeriksaan.setTanggalPemeriksaan(pemeriksaan.getTanggalPemeriksaan());
		pemeriksaan.setStatus(pemeriksaan.getStatus());
		pemeriksaanDb.save(pemeriksaan);
	}

	@Override
	public void delete(PemeriksaanModel pemeriksaan) {
		// TODO Auto-generated method stub
		pemeriksaanDb.delete(pemeriksaan);
		
	}
}
