package com.apap.TAsilab.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Entity
@Table(name = "pemeriksaan")
public class PemeriksaanModel implements Serializable {
	// abaikan attribute ini
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
	
	@NotNull
    @Column(name = "tanggal_pengajuan", nullable = false)
    private Date tanggalPengajuan;
	
	// id pasien akan digunakan untuk mengambil object pasien dengan API di SI_Appointment
	@NotNull
    @Column(name = "id_pasien", nullable = false)
    private int idPasien;
	
    @Column(name = "tanggal_pemeriksaan", nullable = true)
    private Date tanggalPemeriksaan;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jenis_pemeriksaan", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private JenisPemeriksaanModel jenisPemeriksaan;
    
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jadwal_jaga", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private JadwalJagaModel jadwalJaga;
    
    @NotNull
    @Column(name = "status", nullable = false)
    private int status;
    
    @Size(max = 255)
    @Column(name = "hasil", nullable = true)
    private String hasil;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getTanggalPengajuan() {
		return tanggalPengajuan;
	}

	public void setTanggalPengajuan(Date tanggalPengajuan) {
		this.tanggalPengajuan = tanggalPengajuan;
	}

	public int getIdPasien() {
		return idPasien;
	}

	public void setIdPasien(int idPasien) {
		this.idPasien = idPasien;
	}

	public Date getTanggalPemeriksaan() {
		return tanggalPemeriksaan;
	}

	public void setTanggalPemeriksaan(Date tanggalPemeriksaan) {
		this.tanggalPemeriksaan = tanggalPemeriksaan;
	}

	public JenisPemeriksaanModel getJenisPemeriksaan() {
		return jenisPemeriksaan;
	}

	public void setJenisPemeriksaan(JenisPemeriksaanModel jenisPemeriksaan) {
		this.jenisPemeriksaan = jenisPemeriksaan;
	}

	public JadwalJagaModel getJadwalJaga() {
		return jadwalJaga;
	}

	public void setJadwalJaga(JadwalJagaModel jadwalJaga) {
		this.jadwalJaga = jadwalJaga;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getHasil() {
		return hasil;
	}

	public void setHasil(String hasil) {
		this.hasil = hasil;
	}
}
