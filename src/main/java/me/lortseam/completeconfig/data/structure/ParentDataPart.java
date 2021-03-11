package me.lortseam.completeconfig.data.structure;

import com.google.common.base.Predicates;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public interface ParentDataPart<C extends DataPart> extends DataPart {

    Iterable<C> getChildren();

    @Override
    default void apply(CommentedConfigurationNode node) {
        propagateToChildren(childNode -> !childNode.virtual(), DataPart::apply, node);
    }

    @Override
    default void fetch(CommentedConfigurationNode node) {
        propagateToChildren(Predicates.alwaysTrue(), DataPart::fetch, node);
    }

    default void propagateToChildren(Predicate<CommentedConfigurationNode> childNodeCondition, BiConsumer<C, CommentedConfigurationNode> function, CommentedConfigurationNode node) {
        for (C child : getChildren()) {
            CommentedConfigurationNode childNode = node.node(child.getID());
            if (!childNodeCondition.test(childNode)) {
                continue;
            }
            function.accept(child, childNode);
        }
    }

}
