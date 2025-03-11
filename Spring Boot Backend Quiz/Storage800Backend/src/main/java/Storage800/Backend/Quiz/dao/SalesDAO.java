package Storage800.Backend.Quiz.dao;

import org.springframework.http.ResponseEntity;

import Storage800.Backend.Quiz.record.SaleDTO;
import Storage800.Backend.Quiz.record.SalesProductDTO;

public interface SalesDAO {

	ResponseEntity<Object> createSale(SaleDTO sale);

	ResponseEntity<Object> updateSale(SalesProductDTO saleProducts[], int sale_id);

	ResponseEntity<Object> getAllSales();
	

}
