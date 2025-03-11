package Storage800.Backend.Quiz.model;

import java.sql.Timestamp;

public class TransactionLog {

	private int id;
	private int transaction_id;
	private int old_quantity;
	private int new_quantity;
	private int old_price;
	private int new_price;
	private Timestamp creation_date;
	
	public TransactionLog(int id, int transaction_id, int old_quantity, int new_quantity, int old_price, int new_price,
			Timestamp creation_date) {
		this.id = id;
		this.transaction_id = transaction_id;
		this.old_quantity = old_quantity;
		this.new_quantity = new_quantity;
		this.old_price = old_price;
		this.new_price = new_price;
		this.creation_date = creation_date;
	}

	public int getId() {
		return id;
	}

	public int getTransaction_id() {
		return transaction_id;
	}

	public int getOld_quantity() {
		return old_quantity;
	}

	public void setOld_quantity(int old_quantity) {
		this.old_quantity = old_quantity;
	}

	public int getNew_quantity() {
		return new_quantity;
	}

	public void setNew_quantity(int new_quantity) {
		this.new_quantity = new_quantity;
	}

	public int getOld_price() {
		return old_price;
	}

	public void setOld_price(int old_price) {
		this.old_price = old_price;
	}

	public int getNew_price() {
		return new_price;
	}

	public void setNew_price(int new_price) {
		this.new_price = new_price;
	}

	public Timestamp getCreation_date() {
		return creation_date;
	}

	
}
