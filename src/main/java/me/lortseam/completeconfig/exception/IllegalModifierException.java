package me.lortseam.completeconfig.exception;

/**
 * Thrown to indicate that an illegal or inappropriate modifier was applied to a member.
 */
public class IllegalModifierException extends RuntimeException {

    public IllegalModifierException(String message) {
        super(message);
    }

}
