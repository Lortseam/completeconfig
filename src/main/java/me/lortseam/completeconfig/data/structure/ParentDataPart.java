package me.lortseam.completeconfig.data.structure;

import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ParentDataPart<C, CV extends DataPart> extends DataPart {

    Iterable<C> getChildren();

    CommentedConfigurationNode navigateToChild(CommentedConfigurationNode node, C c);

    CV retrieveChildValue(C c);

    @Override
    default void apply(CommentedConfigurationNode node) {
        propagateToChildren(childNode -> !childNode.virtual(), child -> child::apply, node);
    }

    @Override
    default void fetch(CommentedConfigurationNode node) {
        propagateToChildren(child -> child::fetch, node);
    }

    default void propagateToChildren(Predicate<CommentedConfigurationNode> childNodeCondition, Function<CV, Consumer<CommentedConfigurationNode>> functionSupplier, CommentedConfigurationNode node) {
        for (C child : getChildren()) {
            CommentedConfigurationNode childNode = navigateToChild(node, child);
            if (!childNodeCondition.test(childNode)) {
                continue;
            }
            functionSupplier.apply(retrieveChildValue(child)).accept(childNode);
        }
    }

    default void propagateToChildren(Function<CV, Consumer<CommentedConfigurationNode>> functionSupplier, CommentedConfigurationNode node) {
        propagateToChildren((childNode) -> true, functionSupplier, node);
    }

}
