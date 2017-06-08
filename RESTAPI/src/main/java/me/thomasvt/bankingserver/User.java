package me.thomasvt.bankingserver;

public class User {

	private String firstname, lastname, zipcode, housenumber, residence, mobnumber, birthdate;

	public User(String firstname, String lastname, String zipcode, String housenumber, String residence,
			String mobnumber, String birthdate) {
		super();
		this.firstname = firstname;
		this.lastname = lastname;
		this.zipcode = zipcode;
		this.housenumber = housenumber;
		this.residence = residence;
		this.mobnumber = mobnumber;
		this.birthdate = birthdate;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getHousenumber() {
		return housenumber;
	}

	public void setHousenumber(String housenumber) {
		this.housenumber = housenumber;
	}

	public String getResidence() {
		return residence;
	}

	public void setResidence(String residence) {
		this.residence = residence;
	}

	public String getMobnumber() {
		return mobnumber;
	}

	public void setMobnumber(String mobnumber) {
		this.mobnumber = mobnumber;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

}