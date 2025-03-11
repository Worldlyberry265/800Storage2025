package Storage800.Backend.Quiz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import Storage800.Backend.Quiz.dao.LoggingDAO;

@RestController
public class LoggingController {
	
	@Autowired
	private LoggingDAO logDAO;
	
	@GetMapping("/api/logging")
	public ResponseEntity<Object> GetSaleTransactionsUpdates() {
		ResponseEntity<Object> result = logDAO.getSaleTransactionsUpdates();
		return result;
	}

}
