package me.lortseam.completeconfig.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfigTest {

    private static final String MOD_ID = "test";

    @Test
    public void _throwExceptionIfArgNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> new Config(null));
        assertEquals("modId is marked non-null but is null", exception.getMessage());
        exception = assertThrows(NullPointerException.class, () -> new Config(MOD_ID, (String[]) null));
        assertEquals("branch is marked non-null but is null", exception.getMessage());
        assertThrows(NullPointerException.class, () -> new Config(MOD_ID, new String[]{null}));
    }

}
