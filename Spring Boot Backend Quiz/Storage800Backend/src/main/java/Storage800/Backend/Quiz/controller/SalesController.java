package Storage800.Backend.Quiz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import Storage800.Backend.Quiz.dao.SalesDAO;
import Storage800.Backend.Quiz.record.SaleDTO;
import Storage800.Backend.Quiz.record.SalesProductDTO;

@RestController
public class SalesController {

	@Autowired
	private SalesDAO sDAO;
	
	@PostMapping("/api/sales")
	public ResponseEntity<Object> CreateSale(@RequestBody SaleDTO sale) {
		ResponseEntity<Object> result = sDAO.createSale(sale);
		return result;
	}

	@PutMapping("/api/sales/{sale_id}")
	public ResponseEntity<Object> UpdateSale(@PathVariable int sale_id, @RequestBody  SalesProductDTO saleProducts[]) {
		ResponseEntity<Object> result = sDAO.updateSale(saleProducts, sale_id);
		return result;
	}

	@GetMapping("/api/sales")
	public ResponseEntity<Object> GetAllSales() {
		ResponseEntity<Object> result = sDAO.getAllSales();
		return result;
	}
	

}
