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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class BaseCollectionTest {

    private BaseCollection baseCollection;
    private final LogCaptor logCaptor = LogCaptor.forName("CompleteConfig");

    @BeforeEach
    public void beforeEach() {
        baseCollection = new BaseCollection(mock(TranslationIdentifier.class)) {};
    }

    @AfterEach
    public void afterEach() {
        logCaptor.clearLogs();
    }

    @Test
    public void resolve_includeFieldIfAnnotated() {
        baseCollection.resolve(new ContainerWithEntry(), new ContainerWithContainerWithEntry(), new ContainerWithGroupWithEntry());
        assertEquals(2, baseCollection.getEntries().size());
        assertEquals(1, baseCollection.getCollections().size());
    }

    @Test
    public void resolve_excludeFieldIfNotAnnotated() {
        baseCollection.resolve(new ContainerWithField());
        assertTrue(baseCollection.isEmpty());
    }

    @Test
    public void resolve_includeFieldInEntries() {
        baseCollection.resolve(new EntriesContainerWithEntry());
        assertEquals(1, baseCollection.getEntries().size());
    }

    @Test
    public void resolve_excludeFieldInEntriesIfContainer() {
        baseCollection.resolve(new EntriesContainerWithEmptyContainer());
        assertTrue(baseCollection.isEmpty());
    }

    @Test
    public void resolve_excludeFieldInEntriesIfIgnoreAnnotated() {
        baseCollection.resolve(new EntriesContainerWithIgnoredField());
        assertTrue(baseCollection.isEmpty());
    }

    @Test
    public void resolve_excludeFieldInEntriesIfTransient() {
        baseCollection.resolve(new EntriesContainerWithTransientField());
        assertTrue(baseCollection.isEmpty());
    }

    @Test
    public void resolve_includeSuperclassFieldIfNonStatic() {
        baseCollection.resolve(new SubclassOfContainerWithEntry(), new SubclassOfContainerWithContainerWithEntry());
        assertEquals(2, baseCollection.getEntries().size());
    }

    @Test
    public void resolve_excludeSuperclassFieldIfStatic() {
        baseCollection.resolve(new SubclassOfContainerWithStaticEntry(), new SubclassOfContainerWithStaticContainerWithEntry());
        assertTrue(baseCollection.isEmpty());
    }

    @Test
    public void resolve_includeFromMethod() {
        baseCollection.resolve(new ContainerIncludingContainerWithEntry(), new ContainerIncludingGroupWithEntry());
        assertEquals(1, baseCollection.getEntries().size());
        assertEquals(1, baseCollection.getCollections().size());
    }

    @Test
    public void resolve_includeNestedIfStatic() {
        baseCollection.resolve(new ContainerNestingStaticContainerWithEntry());
        assertEquals(1, baseCollection.getEntries().size());
    }

    @Test
    public void resolve_throwIfNestedNonContainer() {
        IllegalAnnotationTargetException exception = assertThrows(IllegalAnnotationTargetException.class, () -> baseCollection.resolve(new ContainerNestingStaticClass()));
        assertEquals("Transitive " + ContainerNestingStaticClass.Class.class + " must implement " + ConfigContainer.class.getSimpleName(), exception.getMessage());
    }

    @Test
    public void resolve_throwIfNestedNonStatic() {
        IllegalAnnotationTargetException exception = assertThrows(IllegalAnnotationTargetException.class, () -> baseCollection.resolve(new ContainerNestingContainerWithEntry()));
        assertEquals("Transitive " + ContainerNestingContainerWithEntry.ContainerWithEntry.class + " must be static", exception.getMessage());
    }

    @Test
    public void resolve_logWarningIfEmpty() {
        baseCollection.resolve(new EmptyGroup());
        assertThat(logCaptor.getWarnLogs()).contains("Group emptyGroup is empty");
    }

    @Test
    public void resolve_listenSetter() {
        SetterListener listener = new SetterListener();
        baseCollection.resolve(listener);
        boolean value = !listener.getValue();
        Iterables.getOnlyElement(baseCollection.getEntries()).setValue(value);
        assertEquals(value, listener.getValue());
    }

    @Test
    public void resolve_doNotUpdateListenerField() {
        EmptyListener listener = new EmptyListener();
        baseCollection.resolve(listener);
        boolean oldValue = listener.getValue();
        Iterables.getOnlyElement(baseCollection.getEntries()).setValue(!oldValue);
        assertEquals(oldValue, listener.getValue());
    }

}
