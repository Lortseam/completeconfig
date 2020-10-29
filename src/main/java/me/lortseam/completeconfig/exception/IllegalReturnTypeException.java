package me.lortseam.completeconfig.exception;

/**
 * Thrown to indicate that a method returns an illegal or inappropriate type.
 */
public class IllegalReturnTypeException extends RuntimeException {

    public IllegalReturnTypeException(String message) {
        super(message);
    }

}
