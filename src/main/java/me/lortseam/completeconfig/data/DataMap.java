package me.lortseam.completeconfig.data;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;
import me.lortseam.completeconfig.data.structure.DataPart;
import me.lortseam.completeconfig.data.structure.ParentDataPart;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class DataMap<T extends DataPart> extends LinkedHashMap<String, T> implements ParentDataPart<Map.Entry<String, T>, T> {

    protected final TranslationIdentifier translation;

    @Override
    public T put(String id, T value) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("ID must not be null or blank");
        }
        if (containsKey(id)) {
            throw new IllegalArgumentException("Duplicate ID: " + id);
        }
        return super.put(id, value);
    }

    @Override
    public Iterable<Map.Entry<String, T>> getChildren() {
        return entrySet();
    }

    @Override
    public CommentedConfigurationNode navigateToChild(CommentedConfigurationNode node, Map.Entry<String, T> stringTEntry) {
        return node.node(stringTEntry.getKey());
    }

    @Override
    public T retrieveChildValue(Map.Entry<String, T> stringTEntry) {
        return stringTEntry.getValue();
    }

}
