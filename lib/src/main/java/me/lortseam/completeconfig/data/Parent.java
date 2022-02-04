package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigGroup;
import me.lortseam.completeconfig.data.structure.Identifiable;
import me.lortseam.completeconfig.data.structure.StructurePart;
import me.lortseam.completeconfig.data.structure.client.Translatable;
import me.lortseam.completeconfig.util.ReflectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

abstract class Parent implements StructurePart, Translatable {

    private static <C extends StructurePart & Identifiable> void propagateToChildren(Collection<C> children, CommentedConfigurationNode node, Predicate<CommentedConfigurationNode> childNodeCondition, BiConsumer<C, CommentedConfigurationNode> function) {
        for (C child : children) {
            CommentedConfigurationNode childNode = node.node(child.getId());
            if (!childNodeCondition.test(childNode)) {
                continue;
            }
            function.accept(child, childNode);
        }
    }

    private static <C extends StructurePart & Identifiable> void propagateToChildren(Collection<C> children, CommentedConfigurationNode node, BiConsumer<C, CommentedConfigurationNode> function) {
        propagateToChildren(children, node, childNode -> true, function);
    }

    private final EntrySet entries = new EntrySet(this);
    private final ClusterSet clusters = new ClusterSet(this);

    public final Collection<Entry> getEntries() {
        return Collections.unmodifiableCollection(entries);
    }

    public final Collection<Cluster> getClusters() {
        return Collections.unmodifiableCollection(clusters);
    }

    final void resolveContainer(ConfigContainer container) {
        entries.resolve(container);
        for (Class<? extends ConfigContainer> clazz : container.getConfigClasses()) {
            resolve(Arrays.stream(clazz.getDeclaredFields()).filter(field -> {
                if (field.isAnnotationPresent(ConfigContainer.Transitive.class)) {
                    if (!ConfigContainer.class.isAssignableFrom(field.getType())) {
                        throw new AssertionError("Transitive field " + field + " must implement " + ConfigContainer.class.getSimpleName());
                    }
                    return !Modifier.isStatic(field.getModifiers()) || clazz == container.getClass();
                }
                return false;
            }).map(field -> {
                if (!field.canAccess(container)) {
                    field.setAccessible(true);
                }
                try {
                    return (ConfigContainer) field.get(container);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }).toArray(ConfigContainer[]::new));
            Class<?>[] nestedClasses = clazz.getDeclaredClasses();
            ArrayUtils.reverse(nestedClasses);
            resolve(Arrays.stream(nestedClasses).filter(nestedClass -> {
                if (nestedClass.isAnnotationPresent(ConfigContainer.Transitive.class)) {
                    if (!ConfigContainer.class.isAssignableFrom(nestedClass)) {
                        throw new AssertionError("Transitive " + nestedClass + " must implement " + ConfigContainer.class.getSimpleName());
                    }
                    if (!Modifier.isStatic(nestedClass.getModifiers())) {
                        throw new AssertionError("Transitive " + nestedClass + " must be static");
                    }
                    return true;
                }
                return false;
            }).map(nestedClass -> {
                try {
                    return (ConfigContainer) ReflectionUtils.instantiateClass(nestedClass);
                } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    throw new RuntimeException("Failed to instantiate nested " + nestedClass, e);
                }
            }).toArray(ConfigContainer[]::new));
        }
        resolve(container.getTransitives());
    }

    final void resolve(ConfigContainer... containers) {
        for (ConfigContainer container : containers) {
            if (container instanceof ConfigGroup) {
                clusters.resolve((ConfigGroup) container);
            } else {
                resolveContainer(container);
            }
        }
    }

    @Override
    public final void apply(CommentedConfigurationNode node) {
        propagateToChildren(entries, node, childNode -> !childNode.isNull(), StructurePart::apply);
        propagateToChildren(clusters, node, childNode -> !childNode.isNull(), StructurePart::apply);
    }

    @Override
    public void fetch(CommentedConfigurationNode node) {
        propagateToChildren(entries, node, StructurePart::fetch);
        propagateToChildren(clusters, node, StructurePart::fetch);
    }

    final boolean isEmpty() {
        return entries.isEmpty() && clusters.isEmpty();
    }

}
