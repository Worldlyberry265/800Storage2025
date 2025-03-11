package Storage800.Backend.Quiz.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Client {
	
	private int id; // no setter for the id since there is no useCase where we will be required to change it.
	private String firstname;
	private String lastname;
	private String mobile;


//	public Client() {
//    }
	
//	We either use a default empty constructor, or we use the Json Annotations to inform Jackson to use this constructor for deserialization
	@JsonCreator
	public Client(@JsonProperty String firstname, @JsonProperty String lastname, @JsonProperty String mobile) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.mobile = mobile;
	}
	
	public Client(int id, String firstname, String lastname, String mobile) {
		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.mobile = mobile;
	}


	public int getId() {
		return id;
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


	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	
}
