package com.launchcode.queuefir.models;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter
@Data
@Entity @Table(name = "USERS")
public class User {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "USERNAME", unique = true)
	private String username;

	@Setter(AccessLevel.NONE)
	@Column(name = "PASSWORD")
	private String password;

	@Column(name = "FULL_NAME")
	private String fullName;

	@Column(name = "ZIP_CODE")
	private int zipCode;

	@Column(name = "SEEKING_KEFIR")
	private boolean seekingKefir;

	@Autowired @Transient
	private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

	public User(String username, String password, String fullName, int zipCode, boolean seekingKefir) {
		this.username = username;
		this.password = this.bCryptPasswordEncoder.encode(password);
		this.fullName = fullName;
		this.zipCode = zipCode;
		this.seekingKefir = seekingKefir;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", fullName='" + fullName + '\'' +
				", zipCode=" + zipCode +
				", seekingKefir=" + seekingKefir +
				'}';
	}

	public void setPassword(String password) {
		this.password = bCryptPasswordEncoder.encode(password);
	}

	public boolean authenticate(String password) {
		return bCryptPasswordEncoder.matches(password, this.password);
	}

}
