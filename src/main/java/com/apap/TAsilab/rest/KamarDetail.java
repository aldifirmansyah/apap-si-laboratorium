package com.apap.TAsilab.rest;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KamarDetail {
	private int idPasien;
	private int requestPasien;
	private int assignKamar;
	private List<KamarDetail> result;
	
	
	public int getIdPasien() {
		return idPasien;
	}
	public void setIdPasien(int idPasien) {
		this.idPasien = idPasien;
	}
	public int getRequestPasien() {
		return requestPasien;
	}
	public void setRequestPasien(int requestPasien) {
		this.requestPasien = requestPasien;
	}
	public int getAssignKamar() {
		return assignKamar;
	}
	public void setAssignKamar(int assignKamar) {
		this.assignKamar = assignKamar;
	}
	public List<KamarDetail> getResult() {
		return result;
	}
	public void setResult(List<KamarDetail> result) {
		this.result = result;
	}
	
	
	
	
}
