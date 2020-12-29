package me.lortseam.completeconfig.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import me.lortseam.completeconfig.data.Collection;
import me.lortseam.completeconfig.data.CollectionMap;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class CollectionMapDeserializer extends JsonDeserializer<CollectionMap> {

    private static final YAMLMapper MAPPER = new YAMLMapper();
    public static final Class<CollectionMap> TYPE = CollectionMap.class;

    private final Map<String, Collection> configMap;
    private final String collectionID;

    public CollectionMapDeserializer(Map<String, Collection> configMap, String collectionID) {
        this.configMap = configMap;
        this.collectionID = collectionID;
    }

    private CollectionMapDeserializer(Map<String, Collection> configMap) {
        this(configMap, null);
    }

    @Override
    public CollectionMap deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        LinkedHashMap<String, JsonNode> map = MAPPER.readValue(parser, TypeFactory.defaultInstance().constructMapType(LinkedHashMap.class, String.class, JsonNode.class));
        if (collectionID == null) {
            for (Map.Entry<String, JsonNode> entry : map.entrySet()) {
                deserializeCollection(entry.getKey(), entry.getValue());
            }
        } else {
            deserializeCollection(collectionID, map.get(collectionID));
        }
        return null;
    }

    private void deserializeCollection(String collectionID, JsonNode node) throws IOException {
        Collection collection = configMap.get(collectionID);
        if (collection == null) {
            return;
        }
        new YAMLMapper().registerModule(new SimpleModule()
                .addDeserializer(CollectionDeserializer.TYPE, new CollectionDeserializer())
                .addDeserializer(CollectionMapDeserializer.TYPE, new CollectionMapDeserializer(collection.getCollections()))
                .addDeserializer(EntryMapDeserializer.TYPE, new EntryMapDeserializer(collection.getEntries()))
        ).readValue(node.traverse(), Collection.class);
    }

}
