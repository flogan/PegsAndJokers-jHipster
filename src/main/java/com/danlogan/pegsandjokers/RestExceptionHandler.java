package com.danlogan.pegsandjokers;

//import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
//import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.danlogan.pegsandjokers.domain.CannotMoveToAPositionYouOccupyException;
import com.danlogan.pegsandjokers.domain.CannotStartGameWithoutPlayersException;
import com.danlogan.pegsandjokers.domain.InvalidGameStateException;
import com.danlogan.pegsandjokers.infrastructure.GameNotFoundException;
import com.danlogan.pegsandjokers.domain.PlayerNotFoundException;
import com.danlogan.pegsandjokers.domain.PlayerPositionNotFoundException;
import com.danlogan.pegsandjokers.domain.InvalidMoveException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

   @Override
   protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
       String error = "Malformed JSON request";
       return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
   }

   private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
       return new ResponseEntity<>(apiError, apiError.getStatus());
   }

   //other exception handlers below
   
   @ExceptionHandler(CannotStartGameWithoutPlayersException.class)
   protected ResponseEntity<Object> handleCannotStartGameWithoutPlayersException(CannotStartGameWithoutPlayersException ex) {
	   
	   ApiError apiError = new ApiError(BAD_REQUEST);
       apiError.setMessage(ex.getMessage());
       return buildResponseEntity(apiError);
   }

   @ExceptionHandler(GameNotFoundException.class)
   protected ResponseEntity<Object> handleGameNotFoundException(GameNotFoundException ex){
	   
	   ApiError apiError = new ApiError(NOT_FOUND);
	   apiError.setMessage(ex.getMessage());
	   return buildResponseEntity(apiError);
   }
   
   @ExceptionHandler(PlayerNotFoundException.class)
   protected ResponseEntity<Object> handlePlayerNotFoundException(PlayerNotFoundException ex){

	   ApiError apiError = new ApiError(NOT_FOUND);
	   apiError.setMessage(ex.getMessage());
	   return buildResponseEntity(apiError);
   }
   
   @ExceptionHandler(InvalidGameStateException.class)
   protected ResponseEntity<Object> handleInvalidGameStateException(InvalidGameStateException ex){
	   ApiError apiError = new ApiError(BAD_REQUEST);
	   apiError.setMessage(ex.getMessage());
	   return buildResponseEntity(apiError);
	   
   }
   
   @ExceptionHandler(CannotMoveToAPositionYouOccupyException.class)
   protected ResponseEntity<Object> handleCannotMoveToAPositionYouOccupyException(CannotMoveToAPositionYouOccupyException ex){
	   ApiError apiError = new ApiError(BAD_REQUEST);
	   apiError.setMessage(ex.getMessage());
	   return buildResponseEntity(apiError);
	   
   }

   @ExceptionHandler(InvalidMoveException.class)
   protected ResponseEntity<Object> handleInvalidMoveException(InvalidMoveException ex){
	   ApiError apiError = new ApiError(BAD_REQUEST);
	   apiError.setMessage(ex.getMessage());
	   return buildResponseEntity(apiError);
	   
   }
   @ExceptionHandler(PlayerPositionNotFoundException.class)
   protected ResponseEntity<Object> handlePlayerPositionNotFoundException(PlayerNotFoundException ex){

	   ApiError apiError = new ApiError(NOT_FOUND);
	   apiError.setMessage(ex.getMessage());
	   return buildResponseEntity(apiError);
   }

}