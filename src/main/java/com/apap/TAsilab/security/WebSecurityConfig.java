package com.apap.TAsilab.security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.authorizeRequests()
			.antMatchers("/css/**").permitAll()
			.antMatchers("/js/**").permitAll()
			.antMatchers("/user/tambah").permitAll()
			.antMatchers("/lab/kebutuhan/perencanaan").permitAll()
			.antMatchers("/lab/pemeriksaan/permintaan").permitAll()
			.antMatchers("/getAllJenisPemeriksaan").permitAll()
			.antMatchers("/getAllJadwal").permitAll()
			.antMatchers("/lab/kebutuhan/tambah").hasAnyAuthority("STAFF")
			.antMatchers("/lab/kebutuhan").hasAnyAuthority("ADMIN","STAFF")
			.antMatchers("/lab/kebutuhan/ubah/**").hasAnyAuthority("ADMIN")
			.antMatchers("/lab/pemeriksaan/permintaan/**").hasAnyAuthority("ADMIN", "STAFF")
			.antMatchers("/lab/jadwal-jaga/tambah").hasAnyAuthority("ADMIN")
			.antMatchers("/lab/jadwal-jaga/**").hasAnyAuthority("ADMIN","STAFF")
			.antMatchers("/lab/jadwal-jaga/ubah/**").hasAnyAuthority("ADMIN")
			.antMatchers("/lab/stok/tambah").hasAnyAuthority("ADMIN")
			.antMatchers("/lab/stok").hasAnyAuthority("ADMIN","STAFF")
			.antMatchers("/lab/stok/ubah/**").hasAnyAuthority("ADMIN")
			.anyRequest().authenticated()
			.and()
			.formLogin()
			.loginPage("/login")
			.permitAll()
			.and()
			.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login")
			.permitAll();
	}
	
	/*
	@Autowired
	public void configureGlobal (AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
			.passwordEncoder(encoder())
			.withUser("cokicoki").password(encoder().encode("enaksekali"))
			.roles("USER");
	}
	*/
	
	
	@Bean
	public BCryptPasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	public void configAuthetication(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
	}
	
	

}