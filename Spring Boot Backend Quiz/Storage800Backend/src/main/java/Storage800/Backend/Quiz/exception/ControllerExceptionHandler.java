package Storage800.Backend.Quiz.exception;

import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerExceptionHandler {

	// This will be triggered Globally, when any controller receive an http request

	// This will catch when an input variable should be int and a String is given
	// instead for example.
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	private ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
			HttpServletRequest request) {
		CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
				" '" + ex.getValue() + "' is an invalid input format", request.getRequestURI(), ZonedDateTime.now());
		return exception.toResponseEntity();
	}

	// When you try to access a URL that isn't found at any of the controllers
	// methods.
	@ExceptionHandler(NoHandlerFoundException.class)
	private ResponseEntity<Object> handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest request) {
		CustomException exception = new CustomException(HttpStatus.NOT_FOUND.value(),
				"Please Check The Url you're sending to", "The requested URL was not found", request.getRequestURI(),
				ZonedDateTime.now());
		return exception.toResponseEntity();
	}

	// When you try to send a get request for a post URL for example.
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	private ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpServletRequest request) {
		CustomException exception = new CustomException(HttpStatus.METHOD_NOT_ALLOWED.value(), "METHOD_NOT_ALLOWED",
				"The HTTP method is not allowed for this URL", request.getRequestURI(), ZonedDateTime.now());
		return exception.toResponseEntity();
	}

	// Handle missing request body
	@ExceptionHandler(HttpMessageNotReadableException.class)
	private ResponseEntity<Object> handleRequestMissingBody(HttpMessageNotReadableException ex,
			HttpServletRequest request) {
		CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
				"Please check the request body, as it's missing", request.getRequestURI(), ZonedDateTime.now());
		return exception.toResponseEntity();
	}
	
	// Handle invalid request body
	@ExceptionHandler(HttpMessageConversionException .class)
	private ResponseEntity<Object> handleRequestInvalidBody(HttpMessageConversionException ex,
			HttpServletRequest request) {
		CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
				"Please check the request body, as it's invalid", request.getRequestURI(), ZonedDateTime.now());
		return exception.toResponseEntity();
	}

	// Handle missing path variable
	@ExceptionHandler(MissingPathVariableException.class)
	private ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex,
			HttpServletRequest request) {
		CustomException exception = new CustomException(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST",
				"Missing path variable: " + ex.getVariableName(), request.getRequestURI(), ZonedDateTime.now());
		return exception.toResponseEntity();
	}

}
