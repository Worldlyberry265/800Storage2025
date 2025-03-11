package Storage800.Backend.Quiz.dao.impl;

import java.sql.Statement;
import java.sql.PreparedStatement;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import Storage800.Backend.Quiz.dao.SalesDAO;
import Storage800.Backend.Quiz.exception.CustomException;
import Storage800.Backend.Quiz.model.Sale;
import Storage800.Backend.Quiz.model.Transaction;
import Storage800.Backend.Quiz.record.SaleDTO;
import Storage800.Backend.Quiz.record.SalesProductDTO;

@Repository
public class SalesDAOImpl implements SalesDAO {

	@Autowired
	JdbcTemplate Jtemplate;

	public int calcTotalPrice(SalesProductDTO products[]) {
		int total = 0;
		for (int i = 0; i < products.length; i++) {
			total += (products[i].price() * products[i].quantity());
		}
		return total;
	}

	@Override
	@Transactional
	public ResponseEntity<Object> createSale(SaleDTO sale) {
		// I'm not checking here if the product or the client are found, since typically
		// they should be found as the
		// client is buying the product.
		try {

			String sql = "INSERT INTO sales (client_id, seller_id, total_price, creation_date, version) "
					+ "VALUES (?, ?, ?, CURRENT_TIMESTAMP, 1)";

			KeyHolder keyHolder = new GeneratedKeyHolder();
			int sales_result = Jtemplate.update(connection -> {
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, sale.client_id());
				ps.setInt(2, sale.seller_id());
				ps.setDouble(3, calcTotalPrice(sale.products()));
				return ps;
			}, keyHolder);

			int generatedSaleId = keyHolder.getKey().intValue();

			for (int i = 0; i < sale.products().length; i++) {
				int transactions_result = Jtemplate.update(
						"INSERT INTO transactions (sale_id, product_id, quantity, price) " + "VALUES (?, ?, ?, ?)",
						generatedSaleId, sale.products()[i].product_id(), sale.products()[i].quantity(),
						sale.products()[i].price());

				if (transactions_result == 0) {
					CustomException exception = new CustomException(HttpStatus.SERVICE_UNAVAILABLE.value(),
							"SERVICE_UNAVAILABLE", "An error occurred while accessing the data", "/api/sales",
							ZonedDateTime.now());
					return exception.toResponseEntity();
				}
			}

			if (sales_result > 0) {
				return ResponseEntity.ok("Sale Created");
			}
		} catch (DataAccessException e) {
			// Exception occurred while accessing the data
			CustomException exception = new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"INTERNAL_SERVER_ERROR", "If you're inputting the same product more than once, it can cause this",
					"/api/sales", ZonedDateTime.now());
			return exception.toResponseEntity();

		}
		CustomException exception = new CustomException(HttpStatus.SERVICE_UNAVAILABLE.value(), "SERVICE_UNAVAILABLE",
				"An error occurred while accessing the data", "/api/sales", ZonedDateTime.now());
		return exception.toResponseEntity();

	}

	@Transactional
	@Override
	public ResponseEntity<Object> updateSale(SalesProductDTO saleProducts[], int sale_id) {
		// I'm not checking here if the product or the client are found, since typically
		// they should be found as the
		// client is updating its purchase for example.
		
//		!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//		NOTE!
//		I didn't take care of deletion, aka returning all products in transactions with 0 quantity, because that would be deletion
//		and deletion of a sale wasn't required in the assessment.
//		!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

		try {

			// We fetch the sale version to increment it
			String versionSql = "Select version from Sales where id = ? LIMIT 1 ";

			int version = Jtemplate.queryForObject(versionSql, Integer.class, sale_id);

			String sales_sql = "UPDATE Sales SET total_price = ?, version = ? WHERE id = ?";

			int sales_result = Jtemplate.update(sales_sql, calcTotalPrice(saleProducts), version + 1, sale_id);

			if (sales_result == 0) {
				CustomException exception = new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"INTERNAL_SERVER_ERROR", "An error occurred while updating the Sale", "/api/sales/" + sale_id,
						ZonedDateTime.now());
				return exception.toResponseEntity();
			}

//			We update the sale transactions 
			String transactions_sql = "UPDATE TRANSACTIONS SET quantity = ?, price = ? WHERE sale_id = ? AND product_id = ?";

			String transaction_info_sql = "select id, quantity, price from TRANSACTIONS WHERE sale_id = ? AND product_id = ? LIMIT 1";

//			Then we insert these updates in the logs
			String transaction_log_sql = "INSERT INTO transaction_logs (transaction_id, old_quantity, new_quantity, old_price, new_price, creation_date)"
					+ " VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

//			Then we get the ids of the transactions that were updated(since not all transactions will be updated, rather will be deleted => will have their quantity 0)
			StringBuilder updatedProducts = new StringBuilder();

			for (int i = 0; i < saleProducts.length; i++) {

				updatedProducts.append(saleProducts[i].product_id());

				if (i != saleProducts.length - 1) {
					updatedProducts.append(",");
				}

//				We get the info of the transactions before updating them, to insert their current/old values in the logs
				Transaction transactionInfo = Jtemplate.queryForObject(transaction_info_sql, (rs, rowNum) -> {
					Transaction transaction = new Transaction(rs.getInt("id"), rs.getInt("quantity"),
							rs.getFloat("price"));
					return transaction;
				}, sale_id, saleProducts[i].product_id());

//				We then do the actual transactions update
				int transactions_result = Jtemplate.update(transactions_sql, saleProducts[i].quantity(),
						saleProducts[i].price(), sale_id, saleProducts[i].product_id());

				// We then do the actual transactions_logs insertions
				int transaction_log_result = Jtemplate.update(transaction_log_sql, transactionInfo.getId(),
						transactionInfo.getQuantity(), saleProducts[i].quantity(), transactionInfo.getPrice(),
						saleProducts[i].price());

//				If any of the updates/insertions didnt work, we return an error, stop the execution, and roll by by the @Transaction
				if (transactions_result == 0 || transaction_log_result == 0) {
					CustomException exception = new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
							"INTERNAL_SERVER_ERROR",
							"An error occurred while updating the transaction or inserting the transaction log",
							"/api/sales/" + sale_id, ZonedDateTime.now());
					return exception.toResponseEntity();
				}
			}
//			We get the ids of the transactions that weren't send with the SaleDTO, meaning the client isn't desiring anymore to buy the product,
//			so instead of deleting the transaction and losing track of it, we update its quantity to be 0
			String deletedProducts_sql = "SELECT product_id from transactions Where product_id NOT IN ("
					+ updatedProducts.toString() + ")" + " AND sale_id = ?";

			List<Integer> deletedProductIds = Jtemplate.queryForList(deletedProducts_sql, Integer.class, sale_id);

			if (deletedProductIds != null && !deletedProductIds.isEmpty()) {
				String deletedTransactions_sql = "UPDATE TRANSACTIONS SET quantity = ? WHERE sale_id = ? AND product_id = ?";
				for (int i = 0; i < deletedProductIds.size(); i++) {

					Transaction transactionInfo = Jtemplate.queryForObject(transaction_info_sql, (rs, rowNum) -> {
						Transaction transaction = new Transaction(rs.getInt("id"), rs.getInt("quantity"),
								rs.getFloat("price"));
						return transaction;
					}, sale_id, saleProducts[i].product_id());

//					We update the transactions to have product quantity = 0 
					int transactions_result = Jtemplate.update(deletedTransactions_sql, 0, sale_id,
							deletedProductIds.get(i));

					// We insert the update into transactions_logs
					int transaction_log_result = Jtemplate.update(transaction_log_sql, transactionInfo.getId(),
							transactionInfo.getQuantity(), 0, transactionInfo.getPrice(), transactionInfo.getPrice());

					if (transactions_result == 0 || transaction_log_result == 0) {
						CustomException exception = new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
								"INTERNAL_SERVER_ERROR",
								"An error occurred while updating the transaction or inserting the transaction log",
								"/api/sales/" + sale_id, ZonedDateTime.now());
						return exception.toResponseEntity();
					}
				}
			}
			return ResponseEntity.ok("Sale Updated");

		} catch (EmptyResultDataAccessException e) {
			CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(),
					"You should at least have 1 product", "You can't take out all products", "/api/sales/" + sale_id, ZonedDateTime.now());
			return exception.toResponseEntity();

		} catch (DataAccessException e) {
			CustomException exception = new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"INTERNAL_SERVER_ERROR", "An error occurred while accessing the data", "/api/sales/" + sale_id,
					ZonedDateTime.now());
			return exception.toResponseEntity();
		}
	}

	@Override
	public ResponseEntity<Object> getAllSales() {

		CustomException exception = new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Internal Server Error", "Server Out Of Service", "/api/sales", ZonedDateTime.now());

		String sql = "select s.id, s.client_id, CONCAT(c.firstname, ' ' , c.lastname) AS client_name, s.seller_id, s.total_price, s.creation_date\r\n"
				+ "		from sales as s , clients as c\r\n" + "		WHERE c.id = s.client_id";

		try {
			List<Sale> sales = Jtemplate.query(sql, (resultSet, rowNum) -> {
				Sale sale = new Sale(resultSet.getInt("id"), resultSet.getTimestamp("creation_date"),
						resultSet.getInt("client_id"), resultSet.getString("client_name"),
						resultSet.getInt("seller_id"), resultSet.getFloat("total_price"));
				return sale;
			});

			return ResponseEntity.ok(sales);

		} catch (EmptyResultDataAccessException e) {
			exception = new CustomException(HttpStatus.NOT_FOUND.value(), "NOT_FOUND", "Empty Database", "/api/sales",
					ZonedDateTime.now());
			return exception.toResponseEntity();
		} catch (DataAccessException e) {
			exception = new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_SERVER_ERROR",
					"An error occurred while accessing the data", "/api/sales", ZonedDateTime.now());
			return exception.toResponseEntity();
		}

	}

}
