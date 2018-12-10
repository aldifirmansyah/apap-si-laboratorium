package com.apap.TAsilab.repository;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apap.TAsilab.model.JadwalJagaModel;

@Repository
public interface JadwalJagaDB extends JpaRepository<JadwalJagaModel, Integer>{


	List<JadwalJagaModel> findByTanggal(Date tanggal);
	JadwalJagaModel findById(int id);
}
