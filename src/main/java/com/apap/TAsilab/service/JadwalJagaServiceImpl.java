package com.apap.TAsilab.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apap.TAsilab.model.JadwalJagaModel;
import com.apap.TAsilab.repository.JadwalJagaDB;

@Service
@Transactional
public class JadwalJagaServiceImpl implements JadwalJagaService {
	
	@Autowired
	private JadwalJagaDB jadwalJagaDB;
	
	@Override
	public void addJadwalJaga(JadwalJagaModel jadwalJaga) {
		jadwalJagaDB.save(jadwalJaga);
	}
	
	
	@Override
	public List<JadwalJagaModel> getJadwalJagaByTangal(Date tanggal){
		return jadwalJagaDB.findByTanggal(tanggal);
	}
	
	@Override
	public JadwalJagaModel getJadwalJagaById(int id) {
		return jadwalJagaDB.findById(id);
	}
	
	@Override
	public void ubahJadwalJaga(int id, JadwalJagaModel newJadwalJaga) {
		JadwalJagaModel UpdateJadwalJaga = jadwalJagaDB.findById(id);
		UpdateJadwalJaga.setId(newJadwalJaga.getId());
		UpdateJadwalJaga.setTanggal(newJadwalJaga.getTanggal());
		UpdateJadwalJaga.setWaktuMulai(newJadwalJaga.getWaktuMulai());
		UpdateJadwalJaga.setWaktuSelesai(newJadwalJaga.getWaktuSelesai());
		UpdateJadwalJaga.setIdStaff(newJadwalJaga.getIdStaff());
		jadwalJagaDB.save(UpdateJadwalJaga);
	}

	@Override
	public List<JadwalJagaModel> getJadwalAll() {
		// TODO Auto-generated method stub
		return jadwalJagaDB.findAll();
	}

}
