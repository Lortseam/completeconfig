package me.lortseam.completeconfig.exception;

/**
 * Thrown to indicate that a method returns an illegal or inappropriate type.
 */
public class IllegalReturnValueException extends RuntimeException {

    public IllegalReturnValueException(String message) {
        super(message);
    }

}
