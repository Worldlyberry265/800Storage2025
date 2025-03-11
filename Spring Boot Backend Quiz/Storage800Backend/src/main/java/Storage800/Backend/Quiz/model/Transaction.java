package Storage800.Backend.Quiz.model;

public class Transaction {
	
	private int id; // no setter for the id since there is no useCase where we will be required to change it.
	private int sale_id; // no setter for the id since there is no useCase where we will be required to change it.
	private int product_id; // no setter for the id since there is no useCase where we will be required to change it.
	private int quantity;
	private float price;
	
	public Transaction(int id, int quantity, float price) {
		this.id = id;
		this.quantity = quantity;
		this.price = price;
	}
	
	public Transaction(int id, int sale_id, int product_id, int quantity, float price) {
		this.id = id;
		this.sale_id = sale_id;
		this.product_id = product_id;
		this.quantity = quantity;
		this.price = price;
	}

	public int getId() {
		return id;
	}

	public int getSale_id() {
		return sale_id;
	}

	public int getProduct_id() {
		return product_id;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}
	
	

}
