package com.apap.TAsilab.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.apap.TAsilab.model.LabSuppliesModel;

public interface LabSuppliesDB extends JpaRepository<LabSuppliesModel, Long>{
	List<LabSuppliesModel> findByJenis(String jenis);

}
