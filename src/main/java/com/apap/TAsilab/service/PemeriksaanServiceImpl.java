package com.apap.TAsilab.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
	
//	@Override
//	public Map<Integer,Pemeriksaan> cekPemeriksaan() {
//		Map<Integer, Pemeriksaan> mapPemeriksaan = new HashMap<Integer, Pemeriksaan>();
//		List<PemeriksaanModel> listPemeriksaan = pemeriksaanDb.findAll();
//		for (PemeriksaanModel pemeriksaan : listPemeriksaan){
//			System.out.println(listPemeriksaan.size());
//			if(mapPemeriksaan.isEmpty()) {
//				Pemeriksaan pasien = new Pemeriksaan();
//				pasien.setId_pasien(pemeriksaan.getIdPasien());
//				pasien.setJenisPemeriksaan(pemeriksaan.getJenisPemeriksaan().getNama());
//				System.out.println(pemeriksaan.getIdPasien());
//				System.out.println(pasien.getId_pasien());
//				mapPemeriksaan.put(pemeriksaan.getIdPasien(),pasien);
//				System.out.println("masuk sini loh");
//			}
//			else {
//				System.out.println(pemeriksaan.getIdPasien());
//				System.out.println(mapPemeriksaan.keySet().size());
//				for(Integer keyPasien : mapPemeriksaan.keySet()) {
//					if(pemeriksaan.getIdPasien()==keyPasien&&!pemeriksaan.getJenisPemeriksaan().getNama().equals(mapPemeriksaan.get(keyPasien))) {
//						Pemeriksaan pasien = new Pemeriksaan();
//						pasien.setId_pasien(pemeriksaan.getIdPasien());
//						pasien.setJenisPemeriksaan(pemeriksaan.getJenisPemeriksaan().getNama());
//						mapPemeriksaan.put(pemeriksaan.getIdPasien(),pasien);
//						System.out.println("cek1");
//						break;
//					}
//					else if(!(pemeriksaan.getIdPasien()==keyPasien)&&pemeriksaan.getJenisPemeriksaan().getNama().equals(mapPemeriksaan.get(keyPasien))) {
//						Pemeriksaan pasien = new Pemeriksaan();
//						pasien.setId_pasien(pemeriksaan.getIdPasien());
//						pasien.setJenisPemeriksaan(pemeriksaan.getJenisPemeriksaan().getNama());
//						mapPemeriksaan.put(pemeriksaan.getIdPasien(),pasien);
//						System.out.println("cek2");
//						break;
//					}
//					else if(!(pemeriksaan.getIdPasien()==keyPasien)&&!pemeriksaan.getJenisPemeriksaan().getNama().equals(mapPemeriksaan.get(keyPasien))) {
//						Pemeriksaan pasien = new Pemeriksaan();
//						pasien.setId_pasien(pemeriksaan.getIdPasien());
//						pasien.setJenisPemeriksaan(pemeriksaan.getJenisPemeriksaan().getNama());
//						mapPemeriksaan.put(pemeriksaan.getIdPasien(),pasien);
//						System.out.println("cek3");
//						break;
//					}
//					
//				}
//			}
//			
//		}
//		System.out.println("sudah disini");
//		pemeriksaanPasien = mapPemeriksaan;
//		return mapPemeriksaan;
//	}
	

	@Override
	public void addPemeriksaanDarah() throws ParseException {
		System.out.println("masuk add");
		List<PemeriksaanModel> listPemeriksaan = pemeriksaanDb.findAll();
		System.out.println(listPemeriksaan.size());
		for(int i = 0;i<this.getAllKamar().size();i++) {
			int sedangPasien = 0;
			for(PemeriksaanModel pemeriksaan : listPemeriksaan) {
				if(!(sedangPasien==this.getAllKamar().get(i).getIdPasien())) {
					if(pemeriksaanDb.findById(this.getAllKamar().get(i).getIdPasien())!=null) {
						
					}
					if(this.getAllKamar().get(i).getIdPasien()==pemeriksaan.getIdPasien()&&!pemeriksaan.getJenisPemeriksaan().getNama().equals("Darah")) {
						System.out.println("masuk id sama dan bukan darah");
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
						pemeriksaanDarah.setTanggalPemeriksaan(null);
						pemeriksaanDb.save(pemeriksaanDarah);
						sedangPasien = this.getAllKamar().get(i).getIdPasien();
					}
					else if(!(this.getAllKamar().get(i).getIdPasien()==pemeriksaan.getIdPasien())&&pemeriksaan.getJenisPemeriksaan().getNama().equals("Darah")) {
						System.out.println("masuk id beda dan darah");
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
						pemeriksaanDarah.setTanggalPemeriksaan(null);
						pemeriksaanDb.save(pemeriksaanDarah);
						sedangPasien = this.getAllKamar().get(i).getIdPasien();
					}
					else if(!(this.getAllKamar().get(i).getIdPasien()==pemeriksaan.getIdPasien())&&!pemeriksaan.getJenisPemeriksaan().getNama().equals("Darah")) {
						Optional<PemeriksaanModel> pemeriksaan2 = pemeriksaanDb.findById(this.getAllKamar().get(i).getIdPasien());
						if(!listPemeriksaan.contains(pemeriksaan2)) {
							System.out.println("masuk id beda dan bukan darah");
							System.out.println(this.getAllKamar().get(i));
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
							pemeriksaanDarah.setTanggalPemeriksaan(null);
							pemeriksaanDb.save(pemeriksaanDarah);
							sedangPasien = this.getAllKamar().get(i).getIdPasien();
						}
					}
				}
				
			}
			
//			for(Integer keyPasien : pemeriksaanPasien.keySet()) {
//				System.out.println(keyPasien);
//				System.out.println("masuk sana");
//				System.out.println(this.getAllKamar().get(i).getIdPasien());
//				if(!(sedangPasien==this.getAllKamar().get(i).getIdPasien())) {
//					if(this.getAllKamar().get(i).getIdPasien()==keyPasien&&!pemeriksaanPasien.get(keyPasien).getJenisPemeriksaan().equals("Darah")) {
//						System.out.println("masuk id sama dan bukan darah");
//						java.util.Date utilDate = new java.util.Date();
//						java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
//						PemeriksaanModel pemeriksaanDarah = new PemeriksaanModel();
//						JenisPemeriksaanModel jenisPemeriksaanDarah = jenisPemeriksaanDb.findById(12);
//						pemeriksaanDarah.setIdPasien(this.getAllKamar().get(i).getIdPasien());
//						pemeriksaanDarah.setJenisPemeriksaan(jenisPemeriksaanDarah);
//						pemeriksaanDarah.setTanggalPengajuan(sqlDate);
//						pemeriksaanDarah.setHasil("Belum ada hasil");
//						pemeriksaanDarah.setJadwalJaga(jadwalJagaDb.getOne(1));
//						pemeriksaanDarah.setStatus(0);
//						pemeriksaanDarah.setTanggalPemeriksaan(null);
//						pemeriksaanDb.save(pemeriksaanDarah);
//					}
//						
//					else if(!(this.getAllKamar().get(i).getIdPasien()==keyPasien)&&pemeriksaanPasien.get(keyPasien).getJenisPemeriksaan().equals("Darah")) {
//						System.out.println("masuk id beda dan darah");
//						java.util.Date utilDate = new java.util.Date();
//						java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
//						PemeriksaanModel pemeriksaanDarah = new PemeriksaanModel();
//						JenisPemeriksaanModel jenisPemeriksaanDarah = jenisPemeriksaanDb.findById(12);
//						pemeriksaanDarah.setIdPasien(this.getAllKamar().get(i).getIdPasien());
//						pemeriksaanDarah.setJenisPemeriksaan(jenisPemeriksaanDarah);
//						pemeriksaanDarah.setTanggalPengajuan(sqlDate);
//						pemeriksaanDarah.setHasil("Belum ada hasil");
//						pemeriksaanDarah.setJadwalJaga(jadwalJagaDb.getOne(1));
//						pemeriksaanDarah.setStatus(0);
//						pemeriksaanDarah.setTanggalPemeriksaan(null);
//						pemeriksaanDb.save(pemeriksaanDarah);
//					}
//					else if(!(this.getAllKamar().get(i).getIdPasien()==keyPasien)&&!pemeriksaanPasien.get(keyPasien).getJenisPemeriksaan().equals("Darah")) {
//						System.out.println("masuk id beda dan bukan darah");
//						java.util.Date utilDate = new java.util.Date();
//						java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
//						PemeriksaanModel pemeriksaanDarah = new PemeriksaanModel();
//						JenisPemeriksaanModel jenisPemeriksaanDarah = jenisPemeriksaanDb.findById(12);
//						pemeriksaanDarah.setIdPasien(this.getAllKamar().get(i).getIdPasien());
//						pemeriksaanDarah.setJenisPemeriksaan(jenisPemeriksaanDarah);
//						pemeriksaanDarah.setTanggalPengajuan(sqlDate);
//						pemeriksaanDarah.setHasil("Belum ada hasil");
//						pemeriksaanDarah.setJadwalJaga(jadwalJagaDb.getOne(1));
//						pemeriksaanDarah.setStatus(0);
//						pemeriksaanDarah.setTanggalPemeriksaan(null);
//						pemeriksaanDb.save(pemeriksaanDarah);
//						sedangPasien = this.getAllKamar().get(i).getIdPasien();
//					}
//				}			
//			}
		}
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
