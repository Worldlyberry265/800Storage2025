package Storage800.Backend.Quiz.dao.impl;

import java.util.List;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import Storage800.Backend.Quiz.dao.ClientsDAO;
import Storage800.Backend.Quiz.exception.CustomException;
import Storage800.Backend.Quiz.model.Client;

@Repository
public class ClientsDAOImpl implements ClientsDAO {

	@Autowired
	JdbcTemplate Jtemplate;

	private boolean isNameValid(String input) {
		// Perform input validation to disallow special characters
		String regex = "^[A-Za-z ]+$"; // Regular expression that only allows alphabets and spaces
		return (input.matches(regex) && input.length() >= 3);
	}

	private boolean isMobileValid(String input) {
		// Perform input validation to disallow special characters
		String regex = "^\\+[0-9]{6,}$"; // Regular expression that only allows at least numbers and starts with +
		return input.matches(regex);
	}

	private boolean isMobileUnique(String mobile) { // To search if the mobile is already used
		String sql = "SELECT COUNT(*) FROM clients WHERE mobile = ?";
		int count = Jtemplate.queryForObject(sql, Integer.class, mobile);
		return count == 0; // if not found, it returns 0 => true, else if count = 1 returns false
	}

	private boolean isMobileUnique(String mobile, int id) { // To search if the mobile is already used for not the same
															// client
		String sql = "SELECT COUNT(*) FROM clients WHERE mobile = ? AND id != ?";
		int count = Jtemplate.queryForObject(sql, Integer.class, mobile, id);
		return count == 0; // if not found, it returns 0 => true, else if count = 1 returns false
	}

	private void isClientFound(int id) {
		String sql = "SELECT 1 FROM clients WHERE id = ? LIMIT 1";
		Jtemplate.queryForObject(sql, Integer.class, id);
	}

	@Override
	public ResponseEntity<Object> createClient(Client client) {
		try {
			// Validate input for special characters
			if (!isNameValid(client.getFirstname()) || !isNameValid(client.getLastname())) {
				CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
						"Invalid Name, Only Letters and spaces are allowed, and should be of minimum 3 length",
						"/api/clients", ZonedDateTime.now());
				return exception.toResponseEntity();
			}
			if (!isMobileValid(client.getMobile())) {
				CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
						"Invalid mobile, It should start with a plus sign (+) and continue with numbers",
						"/api/clients", ZonedDateTime.now());
				return exception.toResponseEntity();
			}

			if (!isMobileUnique(client.getMobile())) {
				CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
						"Mobile not unique, please enter a different one.", "/api/clients", ZonedDateTime.now());
				return exception.toResponseEntity();
			}

			int result = Jtemplate.update("INSERT INTO clients (firstname, lastname, mobile) VALUES (?, ?, ?)",
					client.getFirstname(), client.getLastname(), client.getMobile());

			if (result > 0) {
				return ResponseEntity.ok("Client Created");
			}
		} catch (DataAccessException e) {
			CustomException exception = new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"INTERNAL_SERVER_ERROR", "An error occurred while accessing the data", "/api/clients",
					ZonedDateTime.now());
			return exception.toResponseEntity();

		}
		CustomException exception = new CustomException(HttpStatus.SERVICE_UNAVAILABLE.value(), "SERVICE_UNAVAILABLE",
				"An error occurred while accessing the data", "/api/clients", ZonedDateTime.now());
		return exception.toResponseEntity();

	}

	@Override
	public ResponseEntity<Object> updateClient(int id, Client client) {

		try {

			isClientFound(id);

			// Validate input for special characters
			if (!isNameValid(client.getFirstname()) || !isNameValid(client.getLastname())) {
				CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
						"Invalid Name, Only Letters and spaces are allowed, and should be of minimum 3 length",
						"/api/clients/" + id, ZonedDateTime.now());
				return exception.toResponseEntity();
			}
			if (!isMobileValid(client.getMobile())) {
				CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
						"Invalid mobile, It should start with a plus sign (+) and continue with at least 6 numbers",
						"/api/clients/" + id, ZonedDateTime.now());
				return exception.toResponseEntity();
			}

			if (!isMobileUnique(client.getMobile(), id)) {
				CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
						"This mobile already exists, You should use a different mobile.", "/api/clients/" + id,
						ZonedDateTime.now());
				return exception.toResponseEntity();
			}

			String updateSql = "UPDATE clients SET firstname = ?, lastname = ?, mobile = ? WHERE id = ?";

			int result = Jtemplate.update(updateSql, client.getFirstname(), client.getLastname(), client.getMobile(),
					id);
			if (result > 0) {
				return ResponseEntity.ok("Client Updated");
			}
		} catch (EmptyResultDataAccessException e) {
			CustomException exception = new CustomException(HttpStatus.NOT_FOUND.value(), "Client Not found",
					"Invalid Id", "/api/clients/" + id, ZonedDateTime.now());
			return exception.toResponseEntity();
		} catch (DataAccessException e) {
			CustomException exception = new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"INTERNAL_SERVER_ERROR", "An error occurred while accessing the data", "/api/clients/" + id,
					ZonedDateTime.now());
			return exception.toResponseEntity();

		}
		CustomException exception = new CustomException(HttpStatus.SERVICE_UNAVAILABLE.value(), "SERVICE_UNAVAILABLE",
				"An error occurred while accessing the data", "/api/clients/" + id, ZonedDateTime.now());
		return exception.toResponseEntity();
	}

	@Override
	public ResponseEntity<Object> getAllClients() {

		CustomException exception = new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Internal Server Error", "Server Out Of Service", "/api/clients", ZonedDateTime.now());

		String sql = "SELECT * from Clients";

		try {

			List<Client> clients = Jtemplate.query(sql, (resultSet, rowNum) -> {

				Client client = new Client(resultSet.getInt("id"), resultSet.getString("firstname"),
						resultSet.getString("lastname"), resultSet.getString("mobile"));
				return client;
			});
			return ResponseEntity.ok(clients);

		} catch (EmptyResultDataAccessException e) {
			exception = new CustomException(HttpStatus.NOT_FOUND.value(), "NOT_FOUND", "Empty Database", "/api/clients",
					ZonedDateTime.now());
			return exception.toResponseEntity();
		} catch (DataAccessException e) {
			exception = new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_SERVER_ERROR",
					"An error occurred while accessing the data", "/api/clients", ZonedDateTime.now());
			return exception.toResponseEntity();
		}

	}

}
