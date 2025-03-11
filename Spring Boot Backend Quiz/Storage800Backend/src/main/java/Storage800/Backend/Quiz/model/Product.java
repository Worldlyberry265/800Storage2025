package Storage800.Backend.Quiz.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Product {
	
	private int id; // no setter for the id since there is no useCase where we will be required to change it.
	private String prod_name;
	private String description;
	private String category;
	private String creation_date;// no setter for the creation_date since there is no useCase where we will be required to change it.
		
	public Product(int Id, String prod_name, String description, String category, Timestamp creation_date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String formattedDate = dateFormat.format(creation_date);
		this.id = Id;
		this.prod_name = prod_name;
		this.description = description;
		this.category = category;
		this.creation_date = formattedDate;
	}
	
	
	public int getId() {
		return id;
	}

	public String getprod_name() {
		return prod_name;
	}

	public void setprod_name(String prod_name) {
		this.prod_name = prod_name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCreation_Date() {
		return creation_date;
	}
	
	
}
