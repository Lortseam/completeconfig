package me.lortseam.completeconfig;

import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.containers.*;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.data.Entry;
import me.lortseam.completeconfig.data.EntryBase;
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
    public void builder_throwExceptionIfModIDNull() {
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
            assertThat(logCaptor.getWarnLogs()).contains("[CompleteConfig] Mod " + MOD_ID + " tried to create an empty config!");
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
                public void includeAnnotatedFieldIfNonPOJO() {
                    Config config = builder.add(new ContainerWithEntry()).build();
                    assertEquals(1, config.getEntries().size());
                }

                @Test
                public void excludeNonAnnotatedFieldIfNonPOJO() {
                    Config config = builder.add(new ContainerWithField()).build();
                    assertEquals(0, config.getEntries().size());
                }

                @Test
                public void includeFieldIfPOJO() {
                    Config config = builder.add(new POJOContainerWithEntry()).build();
                    assertEquals(1, config.getEntries().size());
                }

                @Test
                public void excludeContainerFieldIfPOJO() {
                    Config config = builder.add(new POJOContainerWithEmptyContainer()).build();
                    assertEquals(0, config.getEntries().size());
                }

                @Test
                public void ignoreFieldIfAnnotatedWithIgnoreAndPOJO() {
                    Config config = builder.add(new POJOContainerWithIgnoredField()).build();
                    assertEquals(0, config.getEntries().size());
                }

                @Test
                public void includeSuperclassField() {
                    Config config = builder.add(new SubclassOfContainerWithEntry()).build();
                    assertEquals(1, config.getEntries().size());
                }

                @Test
                public void excludeStaticSuperclassField() {
                    Config config = builder.add(new SubclassOfContainerWithStaticEntry()).build();
                    assertEquals(0, config.getEntries().size());
                }

            }

            @Nested
            public class Container {

                @Test
                public void includeAnnotatedFieldIfNonPOJO() {
                    Config config = builder.add(new ContainerWithContainerWithEntry()).build();
                    assertEquals(1, config.getEntries().size());
                }

                @Test
                public void excludeNonAnnotatedFieldIfNonPOJO() {
                    Config config = builder.add(new ContainerWithNonAnnotatedContainerWithEntry()).build();
                    assertEquals(0, config.getEntries().size());
                }

                @Test
                public void includeFieldIfPOJO() {
                    Config config = builder.add(new POJOContainerWithContainerWithEntry()).build();
                    assertEquals(1, config.getEntries().size());
                }

                @Test
                public void includeSuperclassField() {
                    Config config = builder.add(new SubclassOfContainerWithContainerWithEntry()).build();
                    assertEquals(1, config.getEntries().size());
                }

                @Test
                public void includeOfMethod() {
                    Config config = builder.add(new ContainerIncludingContainerWithEntry()).build();
                    assertEquals(1, config.getEntries().size());
                }

                @Test
                public void excludeNestedIfPOJO() {
                    Config config = builder.add(new POJOContainerNestingContainerWithEntry()).build();
                    assertEquals(0, config.getEntries().size());
                }

                @Test
                public void includeNestedStaticIfPOJO() {
                    Config config = builder.add(new POJOContainerNestingStaticContainerWithEntry()).build();
                    assertEquals(1, config.getEntries().size());
                }

                @Test
                public void excludeNestedStaticIfNonPOJO() {
                    Config config = builder.add(new ContainerNestingStaticContainerWithEntry()).build();
                    assertEquals(0, config.getEntries().size());
                }

            }

            @Nested
            public class Group {

                @Test
                public void logWarningIfEmpty() {
                    LogCaptor logCaptor = LogCaptor.forRoot();
                    builder.add(new EmptyGroup()).build();
                    assertThat(logCaptor.getWarnLogs()).contains("[CompleteConfig] Group emptyGroup is empty!");
                }

                @Test
                public void includeField() {
                    Config config = builder.add(new ContainerWithGroupWithEntry()).build();
                    assertEquals(1, config.getCollections().size());
                }

                @Test
                public void includeOfMethod() {
                    Config config = builder.add(new ContainerIncludingGroupWithEntry()).build();
                    assertEquals(1, config.getCollections().size());
                }

            }

        }

    }

}
