package ar.edu.uncuyo.gimnasio_sport.error;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
