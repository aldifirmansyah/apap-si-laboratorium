package com.apap.TAsilab.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apap.TAsilab.model.*;

public interface PemeriksaanDB extends JpaRepository<PemeriksaanModel, Integer>  {

	PemeriksaanModel findByIdPasien(int id);
	
}
