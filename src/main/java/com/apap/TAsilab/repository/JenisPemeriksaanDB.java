package com.apap.TAsilab.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.apap.TAsilab.model.JenisPemeriksaanModel;

public interface JenisPemeriksaanDB extends JpaRepository<JenisPemeriksaanModel, Long> {
	JenisPemeriksaanModel findById(long id);
}
