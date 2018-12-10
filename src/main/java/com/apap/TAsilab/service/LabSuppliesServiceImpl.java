package com.apap.TAsilab.service;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apap.TAsilab.model.LabSuppliesModel;
import com.apap.TAsilab.repository.LabSuppliesDB;

@Service
@Transactional
public class LabSuppliesServiceImpl implements LabSuppliesService {
	@Autowired
	private LabSuppliesDB labSuppliesDb;

	@Override
	public List<LabSuppliesModel> getAllReagen() {		
		return labSuppliesDb.findByJenis("reagen");
	}

	@Override
	public List<LabSuppliesModel> getAllSupplies() {
		return labSuppliesDb.findAll();
	}

	@Override
	public LabSuppliesModel getSuppliesDetailById(int id) {
		return labSuppliesDb.findById(id).get();
	}
	
	@Override
	public List<LabSuppliesModel> findLabSuppliesByJenis(String jenis) {
		// TODO Auto-generated method stub
		return labSuppliesDb.findByJenis(jenis);
	}

	@Override
	public List<LabSuppliesModel> getListSupplies() {
		// TODO Auto-generated method stub
		return labSuppliesDb.findAll();
	}

	@Override
	public void addSupplies(LabSuppliesModel labSup) {
		// TODO Auto-generated method stub
		labSuppliesDb.save(labSup);
	}

	@Override
	public LabSuppliesModel findById(int id) {
		// TODO Auto-generated method stub
		return labSuppliesDb.getOne(id);
	}

	@Override
	public void updateLabSupplies(LabSuppliesModel labSupplies) {
		// TODO Auto-generated method stub
		labSuppliesDb.save(labSupplies);
	}

	@Override
	public boolean cekLabSupplies(List<LabSuppliesModel> list) {
		for(LabSuppliesModel a: list) {
			// Kondisi jika lab supplies
			//untuk jenis pemeriksaan tertentu sudah mendekati habis == 1
			// munculkan message error pada template lihat-daftar-pemeriksaan
			if(a.getJumlah()==0) {
				return false;
			}
		}
		return true;
	}
}