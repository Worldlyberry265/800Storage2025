package Storage800.Backend.Quiz.dao;

import org.springframework.http.ResponseEntity;

import Storage800.Backend.Quiz.record.ProductDTO;

public interface ProductsDAO { 

	ResponseEntity<Object> createProduct(ProductDTO product);

	ResponseEntity<Object> updateProduct(int id, ProductDTO product);

	ResponseEntity<Object> getAllProducts();

}