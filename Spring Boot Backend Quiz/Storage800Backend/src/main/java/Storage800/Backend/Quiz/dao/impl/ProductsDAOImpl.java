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

import Storage800.Backend.Quiz.dao.ProductsDAO;
import Storage800.Backend.Quiz.exception.CustomException;
import Storage800.Backend.Quiz.model.Product;
import Storage800.Backend.Quiz.record.ProductDTO;

@Repository
public class ProductsDAOImpl implements ProductsDAO {

	@Autowired
	JdbcTemplate Jtemplate;

//	// All the regexes below reduce significantly the percentage of successful sql
//	// injections by not allowing semicolons and quotes

	private boolean isprod_nameValid(String input) {
		// Perform input validation to disallow special characters
		String regex = "^[A-Za-z ]+$"; // Regular expression that only allows alphabets and spaces
		return (input.matches(regex) && input.length() >= 3);
	}

	private boolean isDescValid(String input) {
		// Perform input validation to disallow special characters
		String regex = "^[A-Za-z ,]+$"; // Regular expression that only allows alphabets, commas, and spaces
		return input.matches(regex);
	}

	private void isProductIdFound(int id) {
		String sql = "SELECT 1 FROM Products WHERE id = ? LIMIT 1";
		Jtemplate.queryForObject(sql, Integer.class, id);
	}

	@Override
	public ResponseEntity<Object> createProduct(ProductDTO product) {
		try {
			// Validate input for special characters
			if (!isprod_nameValid(product.prod_name())) {
				CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
						"Invalid prod_name, Only Letters and spaces are allowed.", "/api/products",
						ZonedDateTime.now());
				return exception.toResponseEntity();
			}
			if (!isprod_nameValid(product.category())) {
				CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
						"Invalid Category, Only Letters and spaces are allowed.", "/api/products", ZonedDateTime.now());
				return exception.toResponseEntity();
			}
			if (!isDescValid(product.description()) && !product.description().isEmpty()) {
				CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
						"Invalid Description, Only Letters , commas, and spaces are allowed.", "/api/products",
						ZonedDateTime.now());
				return exception.toResponseEntity();
			}

			int result = Jtemplate.update(
					"INSERT INTO products (prod_name, descrip, category, creation_date) VALUES (?, ?, ?, CURRENT_TIMESTAMP)",
					product.prod_name(), product.description(), product.category());

			if (result > 0) {
				return ResponseEntity.ok("Product Created");
			}
		} catch (DataAccessException e) {
			CustomException exception = new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"INTERNAL_SERVER_ERROR", "An error occurred while accessing the data", "/api/products",
					ZonedDateTime.now());
			return exception.toResponseEntity();

		}
		CustomException exception = new CustomException(HttpStatus.SERVICE_UNAVAILABLE.value(), "SERVICE_UNAVAILABLE",
				"An error occurred while accessing the data", "/api/products", ZonedDateTime.now());
		return exception.toResponseEntity();

	}

	@Override
	public ResponseEntity<Object> updateProduct(int id, ProductDTO product) {
		try {
			// Validate input for special characters
			if (!isprod_nameValid(product.prod_name())) {
				CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
						"Invalid prod_name, Only Letters and spaces are allowed.", "/api/products/" + id,
						ZonedDateTime.now());
				return exception.toResponseEntity();
			}
			if (!isprod_nameValid(product.category())) {
				CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
						"Invalid Category, Only Letters and spaces are allowed.", "/api/products/" + id,
						ZonedDateTime.now());
				return exception.toResponseEntity();
			}
			if (!isDescValid(product.description()) && !product.description().isEmpty()) {
				CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
						"Invalid Description, Only Letters , commas, and spaces are allowed.", "/api/products/" + id,
						ZonedDateTime.now());
				return exception.toResponseEntity();
			}

			isProductIdFound(id);

			String updateSql = "UPDATE products SET prod_name = ?, descrip = ?, category = ? WHERE id = ?";

			int result = Jtemplate.update(updateSql, product.prod_name(), product.description(),
					product.category(), id);

			if (result > 0) {
				return ResponseEntity.ok("Product Updated");
			}
		} catch (EmptyResultDataAccessException e) {
			CustomException exception = new CustomException(HttpStatus.NOT_FOUND.value(), "Product Not found",
					"Invalid Id", "/api/products/" + id, ZonedDateTime.now());
			return exception.toResponseEntity();
		} catch (DataAccessException e) {
			CustomException exception = new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"INTERNAL_SERVER_ERROR", "An error occurred while accessing the data", "/api/products/" + id,
					ZonedDateTime.now());
			return exception.toResponseEntity();

		}
		CustomException exception = new CustomException(HttpStatus.SERVICE_UNAVAILABLE.value(), "SERVICE_UNAVAILABLE",
				"An error occurred while accessing the data", "/api/products/" + id, ZonedDateTime.now());
		return exception.toResponseEntity();
	}

	@Override
	public ResponseEntity<Object> getAllProducts() {

		CustomException exception = new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Internal Server Error", "Server Out Of Service", "/api/products", ZonedDateTime.now());

		String sql = "SELECT * from products";

		try {

			List<Product> products = Jtemplate.query(sql, (resultSet, rowNum) -> {
				Product product = new Product(resultSet.getInt("id"), resultSet.getString("prod_name"),
						resultSet.getString("descrip"), resultSet.getString("category"),
						resultSet.getTimestamp("creation_date"));
				return product;
			});
			return ResponseEntity.ok(products);

		} catch (EmptyResultDataAccessException e) {
			exception = new CustomException(HttpStatus.NOT_FOUND.value(), "NOT_FOUND", "Empty Database",
					"/api/products", ZonedDateTime.now());
			return exception.toResponseEntity();
		} catch (DataAccessException e) {
			exception = new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_SERVER_ERROR",
					"An error occurred while accessing the data", "/api/products", ZonedDateTime.now());
			return exception.toResponseEntity();
		}

	}

}
