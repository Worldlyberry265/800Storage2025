package Storage800.Backend.Quiz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import Storage800.Backend.Quiz.dao.ClientsDAO;
import Storage800.Backend.Quiz.model.Client;

@RestController
public class ClientsController {

	@Autowired
	private ClientsDAO cDAO;
	
	@PostMapping("/api/clients")
	public ResponseEntity<Object> CreateClient(@RequestBody Client client) {
		ResponseEntity<Object> result = cDAO.createClient(client);
		return result;
	}

	@PutMapping("/api/clients/{id}")
	public ResponseEntity<Object> UpdateClient(@PathVariable int id,@RequestBody Client client) {
		ResponseEntity<Object> result = cDAO.updateClient(id, client);
		return result;
	}

	@GetMapping("/api/clients")
	public ResponseEntity<Object> GetAllClients() {
		ResponseEntity<Object> result = cDAO.getAllClients();
		return result;
	}
}
