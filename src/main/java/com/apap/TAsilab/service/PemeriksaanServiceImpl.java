package com.apap.TAsilab.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
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
import com.apap.TAsilab.repository.JadwalJagaDB;
import com.apap.TAsilab.repository.JenisPemeriksaanDB;
import com.apap.TAsilab.repository.PemeriksaanDB;
import com.apap.TAsilab.rest.KamarDetail;
import com.apap.TAsilab.rest.PasienDetail;


@Service
@Transactional
public class PemeriksaanServiceImpl implements PemeriksaanService{
	
	@Autowired
	private PemeriksaanDB pemeriksaanDb;
	
	private RestTemplate restTemplate = new RestTemplate();
	
	@Autowired
	private JadwalJagaDB jadwalJagaDb;
	
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
	public List<KamarDetail> getAllKamar() throws ParseException {
		
		String response = restTemplate.getForObject("http://siranap.herokuapp.com/api/get-all-kamar", String.class);
		List<KamarDetail> kamar = new ArrayList<KamarDetail>();
		JSONParser parser = new JSONParser();
		
		
		JSONObject json = (JSONObject) parser.parse(response);
		JSONArray jArray = (JSONArray) json.get("result");

        for(int i = 0 ;i<jArray.size();i++) {
        	JSONObject data = (JSONObject) jArray.get(i);
        	KamarDetail kamarDetail = new KamarDetail();
        	int requestPasien = Integer.parseInt(data.get("id").toString());
            int id_pasien = Integer.parseInt(data.get("pasien").toString());
            int assignKamar = Integer.parseInt(data.get("assign").toString());
            kamarDetail.setIdPasien(id_pasien);
            kamarDetail.setRequestPasien(requestPasien);
            kamarDetail.setAssignKamar(assignKamar);
        	kamar.add(kamarDetail);
        }
        	
        return kamar;
	}
	
	@Override
	public void addPemeriksaanDarah() throws ParseException {
		List<PemeriksaanModel> listPemeriksaan = pemeriksaanDb.findAll();
		for(int i = 0;i<this.getAllKamar().size();i++) {
			int sedangPasien = 0;
			
			if(!(pemeriksaanDb.findByIdPasien(this.getAllKamar().get(i).getIdPasien()) != null)) {
				if(listPemeriksaan.size()==0) {
					java.util.Date utilDate = new java.util.Date();
					java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
					PemeriksaanModel pemeriksaanDarah = new PemeriksaanModel();
					JenisPemeriksaanModel jenisPemeriksaanDarah = jenisPemeriksaanDb.findById(12);
					pemeriksaanDarah.setIdPasien(this.getAllKamar().get(i).getIdPasien());
					pemeriksaanDarah.setJenisPemeriksaan(jenisPemeriksaanDarah);
					pemeriksaanDarah.setTanggalPengajuan(sqlDate);
					pemeriksaanDarah.setHasil("Belum ada hasil");
					pemeriksaanDarah.setJadwalJaga(jadwalJagaDb.getOne(1));
					pemeriksaanDarah.setStatus(0);
					pemeriksaanDarah.setTanggalPemeriksaan(sqlDate);
					pemeriksaanDb.save(pemeriksaanDarah);
					sedangPasien = this.getAllKamar().get(i).getIdPasien();
				}
				else {
					for(PemeriksaanModel pemeriksaan : listPemeriksaan) {
						if(!(sedangPasien==this.getAllKamar().get(i).getIdPasien())) {
							if(this.getAllKamar().get(i).getIdPasien()==pemeriksaan.getIdPasien()&&!pemeriksaan.getJenisPemeriksaan().getNama().equals("Darah")) {
								java.util.Date utilDate = new java.util.Date();
								java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
								PemeriksaanModel pemeriksaanDarah = new PemeriksaanModel();
								JenisPemeriksaanModel jenisPemeriksaanDarah = jenisPemeriksaanDb.findById(12);
								pemeriksaanDarah.setIdPasien(this.getAllKamar().get(i).getIdPasien());
								pemeriksaanDarah.setJenisPemeriksaan(jenisPemeriksaanDarah);
								pemeriksaanDarah.setTanggalPengajuan(sqlDate);
								pemeriksaanDarah.setHasil("Belum ada hasil");
								pemeriksaanDarah.setJadwalJaga(jadwalJagaDb.getOne(1));
								pemeriksaanDarah.setStatus(0);
								pemeriksaanDarah.setTanggalPemeriksaan(sqlDate);
								pemeriksaanDb.save(pemeriksaanDarah);
								sedangPasien = this.getAllKamar().get(i).getIdPasien();
							}
							else if(!(this.getAllKamar().get(i).getIdPasien()==pemeriksaan.getIdPasien())&&pemeriksaan.getJenisPemeriksaan().getNama().equals("Darah")) {
								java.util.Date utilDate = new java.util.Date();
								java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
								PemeriksaanModel pemeriksaanDarah = new PemeriksaanModel();
								JenisPemeriksaanModel jenisPemeriksaanDarah = jenisPemeriksaanDb.findById(12);
								pemeriksaanDarah.setIdPasien(this.getAllKamar().get(i).getIdPasien());
								pemeriksaanDarah.setJenisPemeriksaan(jenisPemeriksaanDarah);
								pemeriksaanDarah.setTanggalPengajuan(sqlDate);
								pemeriksaanDarah.setHasil("Belum ada hasil");
								pemeriksaanDarah.setJadwalJaga(jadwalJagaDb.getOne(1));
								pemeriksaanDarah.setStatus(0);
								pemeriksaanDarah.setTanggalPemeriksaan(sqlDate);
								pemeriksaanDb.save(pemeriksaanDarah);
								sedangPasien = this.getAllKamar().get(i).getIdPasien();
							}
							else if(!(this.getAllKamar().get(i).getIdPasien()==pemeriksaan.getIdPasien())&&!pemeriksaan.getJenisPemeriksaan().getNama().equals("Darah")) {
									java.util.Date utilDate = new java.util.Date();
									java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
									PemeriksaanModel pemeriksaanDarah = new PemeriksaanModel();
									JenisPemeriksaanModel jenisPemeriksaanDarah = jenisPemeriksaanDb.findById(12);
									pemeriksaanDarah.setIdPasien(this.getAllKamar().get(i).getIdPasien());
									pemeriksaanDarah.setJenisPemeriksaan(jenisPemeriksaanDarah);
									pemeriksaanDarah.setTanggalPengajuan(sqlDate);
									pemeriksaanDarah.setHasil("Belum ada hasil");
									pemeriksaanDarah.setJadwalJaga(jadwalJagaDb.getOne(1));
									pemeriksaanDarah.setStatus(0);
									pemeriksaanDarah.setTanggalPemeriksaan(sqlDate);
									pemeriksaanDb.save(pemeriksaanDarah);
									sedangPasien = this.getAllKamar().get(i).getIdPasien();
								}
								else if(!(this.getAllKamar().get(i).getIdPasien()==pemeriksaan.getIdPasien())&&!pemeriksaan.getJenisPemeriksaan().getNama().equals("Darah")) {
										java.util.Date utilDate = new java.util.Date();
										java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
										PemeriksaanModel pemeriksaanDarah = new PemeriksaanModel();
										JenisPemeriksaanModel jenisPemeriksaanDarah = jenisPemeriksaanDb.findById(12);
										pemeriksaanDarah.setIdPasien(this.getAllKamar().get(i).getIdPasien());
										pemeriksaanDarah.setJenisPemeriksaan(jenisPemeriksaanDarah);
										pemeriksaanDarah.setTanggalPengajuan(sqlDate);
										pemeriksaanDarah.setHasil("Belum ada hasil");
										pemeriksaanDarah.setJadwalJaga(jadwalJagaDb.getOne(1));
										pemeriksaanDarah.setStatus(0);
										pemeriksaanDarah.setTanggalPemeriksaan(sqlDate);
										pemeriksaanDb.save(pemeriksaanDarah);
										sedangPasien = this.getAllKamar().get(i).getIdPasien();						
								}
							}
					}	
				}
			}
				
			else {
			}
		}
	}

	@Override
	public void updatePemeriksaan(PemeriksaanModel pemeriksaan) throws ParseException {
		if(pemeriksaan.getStatus()!=2) {
			pemeriksaan.setHasil("Belum Ada Hasil");
			JenisPemeriksaanModel jp = jenisPemeriksaanDb.findById(pemeriksaan.getJenisPemeriksaan().getId());
			for (LabSuppliesModel a: jp.getListSupplies()){
				a.setJumlah(a.getJumlah()-1);
			}
		}
		else if(pemeriksaan.getStatus()==2) {
			pemeriksaan.setHasil(pemeriksaan.getHasil().substring(1));
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

	@Override
	public Integer  cekPemeriksaanTerbaru() {
		int jumlah_inbox = 0;
		long millis=System.currentTimeMillis();  
		long diffInMillies =0;
		long diff = 0;
		for(PemeriksaanModel a : pemeriksaanDb.findAll()) {
			diffInMillies = Math.abs(millis - a.getTanggalPemeriksaan().getTime());
			diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
			if(diff==0) {
				jumlah_inbox +=1;
			}
		}
		return jumlah_inbox;
		
	}
}
