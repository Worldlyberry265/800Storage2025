package Storage800.Backend.Quiz.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Sale {
	
	private int id; // no setter for the id since there is no useCase where we will be required to change it.
	private String creation_date;
	private int client_id; // no setter for the id since there is no useCase where we will be required to change it.
	private String client_name;
	private int seller_id; // no setter for the id since there is no useCase where we will be required to change it.
	private float total_price;
	private int version;
	
	public Sale(int id, Timestamp creation_date, int client_id, String client_name,int seller_id, float total_price) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String formattedDate = dateFormat.format(creation_date);
		this.id = id;
		this.creation_date = formattedDate;
		this.client_id = client_id; 
		this.client_name = client_name;
		this.seller_id = seller_id;
		this.total_price = total_price;
	}
	

	public int getId() {
		return id;
	}


	public String getCreation_date() {
		return creation_date;
	}

	public int getClient_id() {
		return client_id;
	}
	
	public String getClient_name() {
		return client_name;
	}


	public int getSeller_id() {
		return seller_id;
	}


	public float getTotal_price() {
		return total_price;
	}

	public void setTotal_price(float total_price) {
		this.total_price = total_price;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	public int getVersion() {
		return this.version;
	}
}
