package me.lortseam.completeconfig;

import com.google.common.collect.Iterables;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.data.containers.*;
import me.lortseam.completeconfig.data.groups.EmptyGroup;
import me.lortseam.completeconfig.data.listeners.EmptyListener;
import me.lortseam.completeconfig.data.listeners.SetterListener;
import me.lortseam.completeconfig.exception.IllegalAnnotationTargetException;
import me.lortseam.completeconfig.io.ConfigSource;
import nl.altindag.log.LogCaptor;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
        private final LogCaptor logCaptor = LogCaptor.forRoot();

        @BeforeEach
        public void createBuilder() {
            builder = Config.builder(MOD_ID);
        }

        @AfterEach
        public void cleanUp() throws NoSuchFieldException {
            builder = null;
            ((Set<ConfigSource>) ReflectionUtil.getStaticFieldValue(ConfigSource.class.getDeclaredField("sources"))).clear();
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
            assertThat(logCaptor.getWarnLogs()).contains("[CompleteConfig] Mod " + MOD_ID + " tried to create an empty config");
        }

        @Nested
        public class Resolution {

            private void assertEmpty(Config config) {
                assertNull(config);
                assertThat(logCaptor.getWarnLogs()).containsExactly("[CompleteConfig] Config of ConfigSource(modID=" + MOD_ID + ", branch=[]) is empty");
            }

            @Test
            public void logWarningIfEmpty() {
                assertEmpty(builder.add(new EmptyContainer()).build());
            }

            @Nested
            public class Entry {

                @Test
                public void includeFieldIfAnnotated() {
                    Config config = builder.add(new ContainerWithEntry()).build();
                    assertEquals(1, config.getEntries().size());
                }

                @Test
                public void excludeFieldIfNotAnnotated() {
                    Config config = builder.add(new ContainerWithField()).build();
                    assertEmpty(config);
                }

                @Test
                public void includeFieldInEntries() {
                    Config config = builder.add(new EntriesContainerWithEntry()).build();
                    assertEquals(1, config.getEntries().size());
                }

                @Test
                public void excludeFieldInEntriesIfContainer() {
                    Config config = builder.add(new EntriesContainerWithEmptyContainer()).build();
                    assertEmpty(config);
                }

                @Test
                public void excludeFieldInEntriesIfIgnoreAnnotated() {
                    Config config = builder.add(new EntriesContainerWithIgnoredField()).build();
                    assertEmpty(config);
                }

                @Test
                public void excludeFieldInEntriesIfTransient() {
                    Config config = builder.add(new EntriesContainerWithTransientField()).build();
                    assertEmpty(config);
                }

                @Test
                public void includeSuperclassFieldIfNonStatic() {
                    Config config = builder.add(new SubclassOfContainerWithEntry()).build();
                    assertEquals(1, config.getEntries().size());
                }

                @Test
                public void excludeSuperclassFieldIfStatic() {
                    Config config = builder.add(new SubclassOfContainerWithStaticEntry()).build();
                    assertEmpty(config);
                }

            }

            @Nested
            public class Container {

                @Test
                public void includeField() {
                    Config config = builder.add(new ContainerWithContainerWithEntry()).build();
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
                public void includeNestedIfStatic() {
                    Config config = builder.add(new ContainerNestingStaticContainerWithEntry()).build();
                    assertEquals(1, config.getEntries().size());
                }

                @Test
                public void throwIfNestedNonContainer() {
                    IllegalAnnotationTargetException exception = assertThrows(IllegalAnnotationTargetException.class, () -> builder.add(new ContainerNestingStaticClass()).build());
                    assertEquals("Transitive " + ContainerNestingStaticClass.Class.class + " must implement " + ConfigContainer.class.getSimpleName(), exception.getMessage());
                }

                @Test
                public void throwIfNestedNonStatic() {
                    IllegalAnnotationTargetException exception = assertThrows(IllegalAnnotationTargetException.class, () -> builder.add(new ContainerNestingContainerWithEntry()).build());
                    assertEquals("Transitive " + ContainerNestingContainerWithEntry.ContainerWithEntry.class + " must be static", exception.getMessage());
                }

            }

            @Nested
            public class Group {

                @Test
                public void logWarningIfEmpty() {
                    builder.add(new EmptyGroup()).build();
                    assertThat(logCaptor.getWarnLogs()).contains("[CompleteConfig] Group emptyGroup is empty");
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

            @Nested
            public class Listener {

                @Test
                public void listenSetter() {
                    SetterListener listener = new SetterListener();
                    Config config = builder.add(listener).build();
                    boolean value = !listener.getValue();
                    Iterables.getOnlyElement(config.getEntries()).setValue(value);
                    assertEquals(value, listener.getValue());
                }

                @Test
                public void doNotUpdateField() {
                    EmptyListener listener = new EmptyListener();
                    Config config = builder.add(listener).build();
                    boolean oldValue = listener.getValue();
                    Iterables.getOnlyElement(config.getEntries()).setValue(!oldValue);
                    assertEquals(oldValue, listener.getValue());
                }

            }

        }

    }

}
