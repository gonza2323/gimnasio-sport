package ar.edu.uncuyo.gimnasio_sport.error;

import lombok.Getter;
import lombok.Setter;

@Getter
public class BusinessException extends RuntimeException {
    private final String messageKey;

    @Setter
    private String viewName; // mutable

    public BusinessException(String messageKey) {
        this.messageKey = messageKey;
    }
}
