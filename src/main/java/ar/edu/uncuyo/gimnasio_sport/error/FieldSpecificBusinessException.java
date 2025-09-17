package ar.edu.uncuyo.gimnasio_sport.error;

import lombok.Getter;

@Getter
public class FieldSpecificBusinessException extends BusinessException {
    private final String field;

    public FieldSpecificBusinessException(String field, String messageKey) {
        super(messageKey);
        this.field = field;
    }
}
