package com.apap.TAsilab.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.apap.TAsilab.model.JadwalJagaModel;
import com.apap.TAsilab.rest.Setting;
import com.apap.TAsilab.rest.StaffDetail;
import com.apap.TAsilab.service.JadwalJagaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class JadwalJagaController {
	

	@Autowired
	private JadwalJagaService jadwalJagaService;
	
	private RestTemplate restTemplate = new RestTemplate();


	private List<StaffDetail> getAllStaff() throws Exception{
		String path = Setting.allStaffUrl;
		List<StaffDetail> listDataStaff = new ArrayList<StaffDetail>();	
		String responsenya = restTemplate.getForEntity(path, String.class).getBody();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(responsenya);
		JsonNode result = node.get("result");
		for(int i=0; i<result.size(); i++) {
			StaffDetail staffs = mapper.treeToValue(result.get(i), StaffDetail.class);
			listDataStaff.add(staffs);
		}		
		return listDataStaff;
		
	}
	
	private Map<Integer, String> getStaff() throws Exception, IOException, Throwable {
//		System.out.println("masuk map");
		List <StaffDetail> staffList  = this.getAllStaff();
		Map<Integer, String> infoStaff = new HashMap<Integer, String>();
		for(StaffDetail staff : staffList) {
			infoStaff.put(staff.getId(), staff.getNama());
		}
//		System.out.println(infoStaff.size());
//		System.out.println(infoStaff.get(633));
		return infoStaff;
	}
	
	public class JadwalJagaListModel{
		private ArrayList<JadwalJagaModel> listJadwalJaga;
		public ArrayList<JadwalJagaModel> getListJadwalJaga(){
			return listJadwalJaga;
		}
		public void setListJadwalJaga(ArrayList<JadwalJagaModel> listJadwalJaga) {
			this.listJadwalJaga = listJadwalJaga;
		}
	}
	
	//belum yg tambah banyak
	@RequestMapping(value = "/lab/jadwal-jaga/tambah", method = RequestMethod.GET)
	public String addJadwalJaga(Model model) throws Exception{
		JadwalJagaListModel listJadwalJaga = new JadwalJagaListModel();		
		ArrayList<JadwalJagaModel> jadwalJaga = new ArrayList<JadwalJagaModel>();
		jadwalJaga.add(new JadwalJagaModel());
		listJadwalJaga.setListJadwalJaga(jadwalJaga);
		
		List<StaffDetail> listStaff = this.getAllStaff();
		model.addAttribute("jadwalJaga", listJadwalJaga);		
		model.addAttribute("listStaff", listStaff);
		return "addJadwalJaga";				
	}
	
	@RequestMapping(value = "/lab/jadwal-jaga/tambah", method = RequestMethod.POST, params= {"add-more"})
	public String addRow(@ModelAttribute JadwalJagaListModel JadwalJaga, Model model) throws Exception{	
		
		if(JadwalJaga.getListJadwalJaga()==null) {
			ArrayList<JadwalJagaModel> jadwalJagaList = new ArrayList<JadwalJagaModel>();
			jadwalJagaList.add(new JadwalJagaModel());
			JadwalJaga.setListJadwalJaga(jadwalJagaList);
		}
		else {
			
			String strWaktuSelesai = JadwalJaga.getListJadwalJaga().get(JadwalJaga.getListJadwalJaga().size()-1).getWaktuSelesai();
			String strWaktuMulai = JadwalJaga.getListJadwalJaga().get(JadwalJaga.getListJadwalJaga().size()-1).getWaktuMulai();
			Date parseWaktuSelesai=new SimpleDateFormat("hh:mm").parse(strWaktuSelesai);
			Date parseWaktuMulai=new SimpleDateFormat("hh:mm").parse(strWaktuMulai);
			
			java.sql.Time inputWaktuSelesai = new java.sql.Time(parseWaktuSelesai.getTime());
			java.sql.Time inputWaktuMulai = new java.sql.Time(parseWaktuMulai.getTime());
			
			if(inputWaktuSelesai.before(inputWaktuMulai)) {
				model.addAttribute("msg", "tolong masukkan jam selesai setelah jam mulai");
				return "failed-date-passed";
			}
			else {
				JadwalJaga.getListJadwalJaga().add(new JadwalJagaModel());
			}			
		}
		
		List<StaffDetail> listStaff = this.getAllStaff();		
		model.addAttribute("jadwalJaga", JadwalJaga);		
		model.addAttribute("listStaff", listStaff);
		return "addJadwalJaga";	
	}
	
	@RequestMapping(value = "/lab/jadwal-jaga/tambah", method = RequestMethod.POST)
	public String addJadwalJagaSubmit(@ModelAttribute JadwalJagaListModel JadwalJaga, Model model) throws ParseException{
		
		String msg = "";
		LocalDate current = LocalDate.now();
		LocalTime currentTime = LocalTime.now();
		
		for(int i=0; i<JadwalJaga.getListJadwalJaga().size(); i++) {
			int idxJadwal = i+1;

			//cek date
			java.sql.Date inputDate = JadwalJaga.getListJadwalJaga().get(i).getTanggal();
			LocalDate inputLocalDate = inputDate.toLocalDate();
			
			boolean isEqual = inputLocalDate.isEqual(current);
			boolean isBefore = inputLocalDate.isBefore(current);
			boolean isAfter = inputLocalDate.isAfter(current);
			
			//cek time
			String strWaktuSelesai = JadwalJaga.getListJadwalJaga().get(i).getWaktuSelesai();
			String strWaktuMulai = JadwalJaga.getListJadwalJaga().get(i).getWaktuMulai();
			Date parseWaktuSelesai=new SimpleDateFormat("hh:mm").parse(strWaktuSelesai);
			Date parseWaktuMulai=new SimpleDateFormat("hh:mm").parse(strWaktuMulai);
			
			java.sql.Time inputWaktuSelesai = new java.sql.Time(parseWaktuSelesai.getTime());
			java.sql.Time inputWaktuMulai = new java.sql.Time(parseWaktuMulai.getTime());
			
			if(isAfter) {
//				System.out.println("jadwal jaga ke " + idxJadwal +  " masuk after");
								
				if(inputWaktuSelesai.before(inputWaktuMulai)) {
					model.addAttribute("msg", "tolong masukkan jam selesai setelah jam mulai");
					return "failed-date-passed";
				}
				else {
					
					try {
						restTemplate.postForObject("https://sigd.herokuapp.com/api/jadwal/tambah/stafLab", JadwalJaga.getListJadwalJaga().get(i), ResponseEntity.class);
						//link diganti sama web service yg dibuat igd : {{link heroku silab : bakal diumumin selanjutnya}}/api/jadwal/tambah/stafLab
					}
					catch(Exception e) {
						
					}
					
					jadwalJagaService.addJadwalJaga(JadwalJaga.getListJadwalJaga().get(i));
					msg += "jadwal jaga ke " + idxJadwal +  " berhasil dimasukkan" + ". ";
				}
			}
			else {	
				if(isEqual) {
//					System.out.println("jadwal jaga ke " + idxJadwal +  " masuk equal");
					
					String strInputTime = JadwalJaga.getListJadwalJaga().get(i).getWaktuMulai();
					Date strParse=new SimpleDateFormat("hh:mm").parse(strInputTime);
					
					java.sql.Time inputTime = new java.sql.Time(strParse.getTime());
					LocalTime inputLocalTime = inputTime.toLocalTime();
					
					boolean timeEqual = inputLocalTime.equals(currentTime);
					boolean timeBefore = inputLocalTime.isBefore(currentTime);
					boolean timeAfter = inputLocalTime.isAfter(currentTime);
										
					 if(!timeBefore) {
						 						
							if(inputWaktuSelesai.before(inputWaktuMulai)) {
								model.addAttribute("msg", "tolong masukkan jam selesai setelah jam mulai");
								return "failed-date-passed";
							}
							else {
								
								try {
									restTemplate.postForObject("https://sigd.herokuapp.com/api/jadwal/tambah/stafLab", JadwalJaga.getListJadwalJaga().get(i), ResponseEntity.class);
									//link diganti sama web service yg dibuat igd : {{link heroku silab : bakal diumumin selanjutnya}}/api/jadwal/tambah/stafLab
								}
								catch(Exception e) {
									
								}
								
								jadwalJagaService.addJadwalJaga(JadwalJaga.getListJadwalJaga().get(i));
								msg += "jadwal jaga ke " + idxJadwal +  " berhasil dimasukkan" + ". ";
							}
							
					 }
					 else {
						 msg += "jadwal jaga ke " + idxJadwal +  " memiliki jam mulai yang dimasukkan sudah berlalu" + ". ";
					 }
					
				}
				else {
//					System.out.println("jadwal jaga ke " + idxJadwal +  " masuk before");
					msg += "jadwal jaga ke " + idxJadwal +  " memiliki date yang dimasukkan sudah berlalu" + ". ";
				}
			}
						
		}		
		model.addAttribute("msg", msg);
		return "success-page";
	}
	
	@RequestMapping(value = "/lab/jadwal-jaga/pilihTanggal", method = RequestMethod.GET)
	public String pilihTanggalJadwalJaga(Model model) throws Throwable {
		return "pilih-tanggal-jadwal-jaga";
	}
	
	
	@RequestMapping(value = "/lab/jadwal-jaga/{tanggal}", method = RequestMethod.GET)
	public String lihatJadwalJaga(@PathVariable(value="tanggal") String tanggal, Model model) throws Throwable{
		String[] splitTgl = tanggal.split("-");
		String gabungTgl = splitTgl[0] + "/" + splitTgl[1] + "/" + splitTgl[2];
		Date tanggalJaga=new SimpleDateFormat("yyyy/MM/dd").parse(gabungTgl);
		List<JadwalJagaModel> listJadwalJaga = jadwalJagaService.getJadwalJagaByTangal(tanggalJaga);
		
		model.addAttribute("listJadwalJaga", listJadwalJaga);
		model.addAttribute("infoStaff", this.getStaff());
		return "lihat-jadwal-jaga";
	}
	
	@RequestMapping(value = "/lab/jadwal-jaga/ubah/{id}", method = RequestMethod.GET)
	public String ubahJadwalJaga(@PathVariable(value="id") int id, Model model) throws Exception, Throwable{
		
		JadwalJagaModel oldJadwalJaga = jadwalJagaService.getJadwalJagaById(id);
		model.addAttribute("oldJadwalJaga", oldJadwalJaga);
		List<StaffDetail> listStaff = this.getAllStaff();
		String staffJaga = "";
		for(int i=0; i<listStaff.size(); i++) {
			if(oldJadwalJaga.getIdStaff() == listStaff.get(i).getId()) {
				staffJaga = listStaff.get(i).getNama(); 
			}
		}
//		System.out.println(staffJaga);
		model.addAttribute("staffJaga", staffJaga);
		model.addAttribute("listStaff", listStaff);
		return "ubah-jadwal-jaga";
		
		
	}
	
	//cek lagi seharusnya outputnya gimana
	@RequestMapping(value = "/lab/jadwal-jaga/ubah/{id}", method = RequestMethod.POST)
	public String ubahJadwalJagaSubmit(@PathVariable(value="id") int id, Model model, @ModelAttribute JadwalJagaModel newJadwalJaga) throws ParseException{
		
		//cek date
		LocalDate current = LocalDate.now();
		LocalTime currentTime = LocalTime.now();

		java.sql.Date inputDate = newJadwalJaga.getTanggal();
		LocalDate inputLocalDate = inputDate.toLocalDate();
		
		boolean isEqual = inputLocalDate.isEqual(current);
		boolean isBefore = inputLocalDate.isBefore(current);
		boolean isAfter = inputLocalDate.isAfter(current);
		
		
		//cek time
		String strWaktuSelesai = newJadwalJaga.getWaktuSelesai();
		String strWaktuMulai = newJadwalJaga.getWaktuMulai();
		Date parseWaktuSelesai=new SimpleDateFormat("hh:mm").parse(strWaktuSelesai);
		Date parseWaktuMulai=new SimpleDateFormat("hh:mm").parse(strWaktuMulai);
		
		java.sql.Time inputWaktuSelesai = new java.sql.Time(parseWaktuSelesai.getTime());
		java.sql.Time inputWaktuMulai = new java.sql.Time(parseWaktuMulai.getTime());
		
		model.addAttribute("newJadwalJaga", newJadwalJaga);
		
		if(isAfter) {
			
			if(inputWaktuSelesai.before(inputWaktuMulai)) {
				model.addAttribute("msg", "tolong masukkan jam selesai setelah jam mulai");
				return "status-ubah";
			}
			else {
				
				try {
					restTemplate.postForObject("https://sigd.herokuapp.com/api/jadwal/tambah/stafLab", newJadwalJaga, ResponseEntity.class);
					//link diganti sama web service yg dibuat igd : https://sigd.herokuapp.com/api/jadwal/tambah/stafLab
					//link test http://localhost:6060/testing/kirim-jadwal
				}
				catch(Exception e) {
					
				}
				
				jadwalJagaService.addJadwalJaga(newJadwalJaga);
				model.addAttribute("msg", "jadwal jaga berhasil di update");
				return "status-ubah";
			}			
			
		}
		else {	
			if(isEqual) {
								
				String strInputTime = newJadwalJaga.getWaktuMulai();
				Date strParse=new SimpleDateFormat("hh:mm").parse(strInputTime);
				
				java.sql.Time inputTime = new java.sql.Time(strParse.getTime());
				LocalTime inputLocalTime = inputTime.toLocalTime();
				
				boolean timeEqual = inputLocalTime.equals(currentTime);
				boolean timeBefore = inputLocalTime.isBefore(currentTime);
				boolean timeAfter = inputLocalTime.isAfter(currentTime);
									
				 if(!timeBefore) {
					 if(inputWaktuSelesai.before(inputWaktuMulai)) {
							model.addAttribute("msg", "tolong masukkan jam selesai setelah jam mulai");
							return "status-ubah";
							
						}
						else {
							
							try {
								restTemplate.postForObject("https://sigd.herokuapp.com/api/jadwal/tambah/stafLab", newJadwalJaga, ResponseEntity.class);
								//link diganti sama web service yg dibuat igd : {{link heroku silab : bakal diumumin selanjutnya}}/api/jadwal/tambah/stafLab
							}
							catch(Exception e) {
								
							}
							
							jadwalJagaService.addJadwalJaga(newJadwalJaga);
							model.addAttribute("msg", "jadwal jaga berhasil di update");
							return "status-ubah";
						}
				 }
				 else {
					 model.addAttribute("msg", "jadwal jaga memiliki jam mulai yang dimasukkan sudah berlalu");
					 return "status-ubah";
				 }
			}
			else {
				model.addAttribute("msg", "jadwal jaga tidak berhasil di update, date sudah berlalu");
				return "status-ubah";
			}
		}
	}	
	
	
	
	
}