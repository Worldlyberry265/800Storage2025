package Storage800.Backend.Quiz.dao.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import Storage800.Backend.Quiz.dao.LoggingDAO;
import Storage800.Backend.Quiz.exception.CustomException;

@Repository
public class LoggingDAOImpl implements LoggingDAO {

	@Autowired
	JdbcTemplate Jtemplate;
	
	@Override
	public ResponseEntity<Object> getSaleTransactionsUpdates() {


		String sql = "Select tl.id, t.sale_id, s.client_id, CONCAT(c.firstname, ' ' , c.lastname) AS client_name,\r\n"
				+ "t.product_id, p.prod_name, tl.old_quantity, tl.new_quantity, tl.old_price, tl.new_price,\r\n"
				+ "tl.creation_date\r\n"
				+ "FROM transaction_logs as tl\r\n"
				+ "INNER JOIN transactions as t ON tl.transaction_id = t.id\r\n"
				+ "INNER JOIN sales as s ON t.sale_id = s.id\r\n"
				+ "INNER JOIN products as p ON t.product_id = p.id\r\n"
				+ "INNER JOIN clients as c ON c.id = s.client_id\r\n"
				+ "ORDER BY t.sale_id";

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		try {

			List<Map<String, Object>> sales = Jtemplate.queryForList(sql);

			// Format the date while keeping other data unchanged
			for (Map<String, Object> sale : sales) {
			    Timestamp timestamp = (Timestamp) sale.get("creation_date");
			        String formattedDate = dateFormat.format(timestamp);
			        sale.remove("creation_date");
			        sale.put("update_date", formattedDate);
			}

			return ResponseEntity.ok(sales);


		} catch (EmptyResultDataAccessException e) {
			CustomException exception = new CustomException(HttpStatus.NOT_FOUND.value(), "NOT_FOUND", "No updates", "/api/logging",
					ZonedDateTime.now());
			return exception.toResponseEntity();
		} catch (DataAccessException e) {
			CustomException exception = new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_SERVER_ERROR",
					"An error occurred while accessing the data", "/api/logging", ZonedDateTime.now());
			return exception.toResponseEntity();
		}

	}
}
