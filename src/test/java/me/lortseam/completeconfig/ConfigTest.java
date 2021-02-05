package me.lortseam.completeconfig;

import com.google.common.collect.Iterables;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.data.Entry;
import me.lortseam.completeconfig.data.EntryBase;
import me.lortseam.completeconfig.data.containers.*;
import me.lortseam.completeconfig.data.groups.EmptyGroup;
import me.lortseam.completeconfig.data.listeners.*;
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
            NullPointerException exception = assertThrows(NullPointerException.class, () -> builder.add((ConfigContainer[]) null));
            assertEquals("containers is marked non-null but is null", exception.getMessage());
        }

        @Test
        public void add_throwIfContainersContainNullElement() {
            assertThrows(NullPointerException.class, () -> builder.add((ConfigContainer) null));
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
                public void includeFieldInNonPOJOIfAnnotated() {
                    Config config = builder.add(new ContainerWithEntry()).build();
                    assertEquals(1, config.getEntries().size());
                }

                @Test
                public void excludeFieldInNonPOJOIfNotAnnotated() {
                    Config config = builder.add(new ContainerWithField()).build();
                    assertEquals(0, config.getEntries().size());
                }

                @Test
                public void includeFieldInPOJO() {
                    Config config = builder.add(new POJOContainerWithEntry()).build();
                    assertEquals(1, config.getEntries().size());
                }

                @Test
                public void excludeFieldInPOJOIfContainer() {
                    Config config = builder.add(new POJOContainerWithEmptyContainer()).build();
                    assertEquals(0, config.getEntries().size());
                }

                @Test
                public void excludeFieldInPOJOIfIgnoreAnnotated() {
                    Config config = builder.add(new POJOContainerWithIgnoredField()).build();
                    assertEquals(0, config.getEntries().size());
                }

                @Test
                public void includeSuperclassFieldIfNonStatic() {
                    Config config = builder.add(new SubclassOfContainerWithEntry()).build();
                    assertEquals(1, config.getEntries().size());
                }

                @Test
                public void excludeSuperclassFieldIfStatic() {
                    Config config = builder.add(new SubclassOfContainerWithStaticEntry()).build();
                    assertEquals(0, config.getEntries().size());
                }

            }

            @Nested
            public class Container {

                @Test
                public void includeFieldInNonPOJOIfAnnotated() {
                    Config config = builder.add(new ContainerWithContainerWithEntry()).build();
                    assertEquals(1, config.getEntries().size());
                }

                @Test
                public void excludeFieldInNonPOJOIfNotAnnotated() {
                    Config config = builder.add(new ContainerWithNonAnnotatedContainerWithEntry()).build();
                    assertEquals(0, config.getEntries().size());
                }

                @Test
                public void includeFieldInPOJO() {
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
                public void excludeNestedInPOJOIfNonStatic() {
                    Config config = builder.add(new POJOContainerNestingContainerWithEntry()).build();
                    assertEquals(0, config.getEntries().size());
                }

                @Test
                public void includeNestedIfStaticAndPOJO() {
                    Config config = builder.add(new POJOContainerNestingStaticContainerWithEntry()).build();
                    assertEquals(1, config.getEntries().size());
                }

                @Test
                public void excludeStaticNestedIfNonPOJO() {
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
                public void listenCustom() {
                    CustomListener listener = new CustomListener();
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

                @Test
                public void forceUpdate() {
                    ForceUpdateListener listener = new ForceUpdateListener();
                    Config config = builder.add(listener).build();
                    boolean value = !listener.getValue();
                    Iterables.getOnlyElement(config.getEntries()).setValue(value);
                    assertEquals(value, listener.getValue());
                }

                @Test
                public void listenOutside() {
                    OutsideListener listener = new OutsideListener();
                    Config config = builder.add(listener).build();
                    boolean value = !listener.getValue();
                    Iterables.getOnlyElement(config.getEntries()).setValue(value);
                    assertEquals(value, listener.getValue());
                }

            }

        }

    }

}
