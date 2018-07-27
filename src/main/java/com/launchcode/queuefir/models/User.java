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

	@Column(name = "CONTACT_INFO")
	private String contactInfo;

	@Column(name = "PARTNER_ID")
	private Long partnerId;

	@Autowired @Transient @Getter @Setter
	private boolean loggedIn;

	@Autowired @Transient
	private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

	public User(String username, String password, String fullName, int zipCode, boolean seekingKefir, String contactInfo) {
		this.username = username;
		// The password should be salted eventually.
		this.password = this.bCryptPasswordEncoder.encode(password);
		this.fullName = fullName;
		this.zipCode = zipCode;
		this.seekingKefir = seekingKefir;
		this.loggedIn = false;
		this.contactInfo = contactInfo;
		this.partnerId = 0L;
	}

	@Override
	public String toString() {
		return "User{" +
				"username='" + username + '\'' +
				", fullName='" + fullName + '\'' +
				", zipCode=" + zipCode +
				", seekingKefir=" + seekingKefir +
				", contactInfo='" + contactInfo + '\'' +
				'}';
	}

	public void setPassword(String password) {
		this.password = bCryptPasswordEncoder.encode(password);
	}

	public boolean authenticate(String password) {
		return bCryptPasswordEncoder.matches(password, this.password);
	}

}
