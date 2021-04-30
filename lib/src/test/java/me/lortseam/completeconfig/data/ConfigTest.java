package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.test.data.containers.EmptyContainer;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class ConfigTest {

    @Test
    public void builder_throwExceptionIfModIDNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> Config.builder(null));
        assertEquals("modID is marked non-null but is null", exception.getMessage());
    }

    @Nested
    public class Builder {

        private static final String MOD_ID = "test";

        private Config.Builder builder;
        private final LogCaptor logCaptor = LogCaptor.forName("CompleteConfig");

        @BeforeEach
        public void beforeEach() {
            builder = Config.builder(MOD_ID);
        }

        @AfterEach
        public void afterEach() {
            logCaptor.clearLogs();
        }

        @Test
        public void setBranch_throwIfBranchNull() {
            NullPointerException exception = assertThrows(NullPointerException.class, () -> builder.setBranch(null));
            assertEquals("branch is marked non-null but is null", exception.getMessage());
        }

        @Test
        public void setBranch_throwIfBranchContainsNullElement() {
            assertThrows(NullPointerException.class, () -> builder.setBranch(new String[]{null}));
        }

        @Test
        public void add_throwIfContainersNull() {
            NullPointerException exception = assertThrows(NullPointerException.class, () -> builder.add((ConfigContainer[]) null));
            assertEquals("containers is marked non-null but is null", exception.getMessage());
        }

        @Test
        public void add_throwIfContainersContainNullElement() {
            assertThrows(NullPointerException.class, () -> builder.add((ConfigContainer) null));
        }

        @Test
        public void build_logWarningAndReturnNullIfChildrenEmpty() {
            assertNull(builder.build());
            assertThat(logCaptor.getWarnLogs()).contains("Mod " + MOD_ID + " tried to create an empty config");
        }

        @Test
        public void build_logWarningAndReturnNullIfEmpty() {
            assertNull(builder.add(new EmptyContainer()).build());
            assertThat(logCaptor.getWarnLogs()).containsExactly("Config of ConfigSource(modID=" + MOD_ID + ", branch=[]) is empty");
        }

    }

}
