package me.lortseam.completeconfig.data.structure;

import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public interface ParentStructurePart<C extends StructurePart & Identifiable> extends StructurePart {

    Iterable<C> getChildren();

    @Override
    default void apply(CommentedConfigurationNode node) {
        propagateToChildren(childNode -> !childNode.isNull(), StructurePart::apply, node);
    }

    @Override
    default void fetch(CommentedConfigurationNode node) {
        propagateToChildren(childNode -> true, StructurePart::fetch, node);
    }

    default void propagateToChildren(Predicate<CommentedConfigurationNode> childNodeCondition, BiConsumer<C, CommentedConfigurationNode> function, CommentedConfigurationNode node) {
        for (C child : getChildren()) {
            CommentedConfigurationNode childNode = node.node(child.getId());
            if (!childNodeCondition.test(childNode)) {
                continue;
            }
            function.accept(child, childNode);
        }
    }

}
