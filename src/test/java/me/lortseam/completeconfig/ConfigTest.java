package me.lortseam.completeconfig;

import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.containers.*;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.data.Entry;
import me.lortseam.completeconfig.data.EntryBase;
import me.lortseam.completeconfig.data.EntryMap;
import me.lortseam.completeconfig.io.ConfigSource;
import nl.altindag.log.LogCaptor;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class ConfigTest {

    @Test
    public void builder_throwExceptionIfModIDIsNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> Config.builder(null));
        assertEquals("modID is marked non-null but is null", exception.getMessage());
    }

    @Nested
    public class Builder {

        private static final String MOD_ID = "test";

        private Config.Builder builder;

        @BeforeEach
        public void createBuilder() {
            builder = Config.builder(MOD_ID);
        }

        @AfterEach
        public void cleanUp() throws NoSuchFieldException {
            builder = null;
            ((Set<Config>) ReflectionUtil.getStaticFieldValue(Config.class.getDeclaredField("configs"))).clear();
            ((Set<ConfigSource>) ReflectionUtil.getStaticFieldValue(ConfigSource.class.getDeclaredField("sources"))).clear();
            ((Map<?, EntryBase>) ReflectionUtil.getStaticFieldValue(Entry.class.getDeclaredField("entries"))).clear();
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
            NullPointerException exception = assertThrows(NullPointerException.class, () -> builder.add((ConfigEntryContainer[]) null));
            assertEquals("containers is marked non-null but is null", exception.getMessage());
        }

        @Test
        public void add_throwIfContainersContainNullElement() {
            assertThrows(NullPointerException.class, () -> builder.add((ConfigEntryContainer) null));
        }

        @Test
        public void build_logWarningAndReturnNullIfChildrenEmpty() {
            LogCaptor logCaptor = LogCaptor.forRoot();
            assertNull(builder.build());
            assertThat(logCaptor.getWarnLogs()).containsExactly("[CompleteConfig] Mod " + MOD_ID + " tried to create an empty config!");
        }

        @Nested
        public class Resolving {

            @Test
            public void logWarningIfEmpty() {
                LogCaptor logCaptor = LogCaptor.forRoot();
                builder.add(new EmptyContainer()).build();
                assertThat(logCaptor.getWarnLogs()).containsExactly("[CompleteConfig] Config of ConfigSource(modID=" + MOD_ID + ", branch=[]) is empty!");
            }

            @Nested
            public class Entry {

                @Test
                public void includeAnnotatedFieldOnlyIfNonPOJO() throws NoSuchFieldException {
                    EntryMap entries = builder.add(new ContainerWithSingleEntry()).build().getEntries();
                    assertEquals(1, entries.size());
                    assertEquals(entries.values().iterator().next().getField(), ContainerWithSingleEntry.class.getDeclaredField("entry"));
                }

                @Test
                public void includeFieldIfPOJO() {
                    EntryMap entries = builder.add(new POJOContainerWithSingleEntry()).build().getEntries();
                    assertEquals(1, entries.size());
                }

                @Test
                public void ignoreFieldIfAnnotatedWithIgnore() {
                    EntryMap entries = builder.add(new POJOContainerWithSingleIgnoredField()).build().getEntries();
                    assertEquals(0, entries.size());
                }

                @Test
                public void includeSuperclassField() {
                    EntryMap entries = builder.add(new SubclassOfContainerWithSingleEntry()).build().getEntries();
                    assertEquals(1, entries.size());
                }

                @Test
                public void excludeStaticSuperclassField() {
                    EntryMap entries = builder.add(new SubclassOfContainerWithSingleStaticEntry()).build().getEntries();
                    assertEquals(0, entries.size());
                }

            }

        }

    }

}
