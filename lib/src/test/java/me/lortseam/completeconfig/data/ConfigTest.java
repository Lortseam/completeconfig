package me.lortseam.completeconfig.data;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfigTest {

    private static final String MOD_ID = "test";

    private final LogCaptor logCaptor = LogCaptor.forName("CompleteConfig");

    @AfterEach
    public void afterEach() {
        logCaptor.clearLogs();
    }

    @Test
    public void _throwExceptionIfArgNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> new Config(null));
        assertEquals("modId is marked non-null but is null", exception.getMessage());
        exception = assertThrows(NullPointerException.class, () -> new Config(MOD_ID, (String[]) null));
        assertEquals("branch is marked non-null but is null", exception.getMessage());
        assertThrows(NullPointerException.class, () -> new Config(MOD_ID, new String[]{null}));
    }

    @Test
    public void load_logWarningIfEmpty() {
        new Config(MOD_ID).load();
        assertThat(logCaptor.getWarnLogs()).contains("Config of ConfigSource(modId=" + MOD_ID + ", branch=[]) is empty");
    }

}
