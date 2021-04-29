package me.lortseam.completeconfig.data;

import com.google.common.collect.Iterables;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import me.lortseam.completeconfig.exception.IllegalAnnotationTargetException;
import me.lortseam.completeconfig.test.data.containers.*;
import me.lortseam.completeconfig.test.data.groups.EmptyGroup;
import me.lortseam.completeconfig.test.data.listeners.EmptyListener;
import me.lortseam.completeconfig.test.data.listeners.SetterListener;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class BaseCollectionTest {

    private BaseCollection baseCollection;
    private final LogCaptor logCaptor = LogCaptor.forRoot();

    @BeforeEach
    public void beforeEach() {
        baseCollection = new BaseCollection(mock(TranslationIdentifier.class)) {};
    }

    @AfterEach
    public void afterEach() {
        logCaptor.clearLogs();
    }

    private void resolve(ConfigContainer... containers) {
        baseCollection.resolve(Arrays.asList(containers));
    }

    @Test
    public void resolve_includeFieldIfAnnotated() {
        resolve(new ContainerWithEntry(), new ContainerWithContainerWithEntry(), new ContainerWithGroupWithEntry());
        assertEquals(2, baseCollection.getEntries().size());
        assertEquals(1, baseCollection.getCollections().size());
    }

    @Test
    public void resolve_excludeFieldIfNotAnnotated() {
        resolve(new ContainerWithField());
        assertTrue(baseCollection.isEmpty());
    }

    @Test
    public void resolve_includeFieldInEntries() {
        resolve(new EntriesContainerWithEntry());
        assertEquals(1, baseCollection.getEntries().size());
    }

    @Test
    public void resolve_excludeFieldInEntriesIfContainer() {
        resolve(new EntriesContainerWithEmptyContainer());
        assertTrue(baseCollection.isEmpty());
    }

    @Test
    public void resolve_excludeFieldInEntriesIfIgnoreAnnotated() {
        resolve(new EntriesContainerWithIgnoredField());
        assertTrue(baseCollection.isEmpty());
    }

    @Test
    public void resolve_excludeFieldInEntriesIfTransient() {
        resolve(new EntriesContainerWithTransientField());
        assertTrue(baseCollection.isEmpty());
    }

    @Test
    public void resolve_includeSuperclassFieldIfNonStatic() {
        resolve(new SubclassOfContainerWithEntry(), new SubclassOfContainerWithContainerWithEntry());
        assertEquals(2, baseCollection.getEntries().size());
    }

    @Test
    public void resolve_excludeSuperclassFieldIfStatic() {
        resolve(new SubclassOfContainerWithStaticEntry());
        // TODO: Add container test
        assertTrue(baseCollection.isEmpty());
    }

    @Test
    public void resolve_includeFromMethod() {
        resolve(new ContainerIncludingContainerWithEntry(), new ContainerIncludingGroupWithEntry());
        assertEquals(1, baseCollection.getEntries().size());
        assertEquals(1, baseCollection.getCollections().size());
    }

    @Test
    public void resolve_includeNestedIfStatic() {
        resolve(new ContainerNestingStaticContainerWithEntry());
        assertEquals(1, baseCollection.getEntries().size());
    }

    @Test
    public void resolve_throwIfNestedNonContainer() {
        IllegalAnnotationTargetException exception = assertThrows(IllegalAnnotationTargetException.class, () -> resolve(new ContainerNestingStaticClass()));
        assertEquals("Transitive " + ContainerNestingStaticClass.Class.class + " must implement " + ConfigContainer.class.getSimpleName(), exception.getMessage());
    }

    @Test
    public void resolve_throwIfNestedNonStatic() {
        IllegalAnnotationTargetException exception = assertThrows(IllegalAnnotationTargetException.class, () -> resolve(new ContainerNestingContainerWithEntry()));
        assertEquals("Transitive " + ContainerNestingContainerWithEntry.ContainerWithEntry.class + " must be static", exception.getMessage());
    }

    @Test
    public void resolve_logWarningIfEmpty() {
        resolve(new EmptyGroup());
        assertThat(logCaptor.getWarnLogs()).contains("[CompleteConfig] Group emptyGroup is empty");
    }

    @Test
    public void resolve_listenSetter() {
        SetterListener listener = new SetterListener();
        resolve(listener);
        boolean value = !listener.getValue();
        Iterables.getOnlyElement(baseCollection.getEntries()).setValue(value);
        assertEquals(value, listener.getValue());
    }

    @Test
    public void resolve_doNotUpdateListenerField() {
        EmptyListener listener = new EmptyListener();
        resolve(listener);
        boolean oldValue = listener.getValue();
        Iterables.getOnlyElement(baseCollection.getEntries()).setValue(!oldValue);
        assertEquals(oldValue, listener.getValue());
    }

}
