package me.lortseam.completeconfig.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfigTest {

    private static final String MOD_ID = "test";

    @Test
    public void _throwExceptionIfArgNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> new Config((ConfigOptions.Builder) null));
        assertEquals("optionsBuilder is marked non-null but is null", exception.getMessage());
    }

}
