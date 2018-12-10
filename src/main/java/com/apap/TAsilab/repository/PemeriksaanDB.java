package com.apap.TAsilab.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apap.TAsilab.model.*;

public interface PemeriksaanDB extends JpaRepository<PemeriksaanModel, Integer>  {

	List<PemeriksaanModel> findByIdPasien(int id);
	
}
