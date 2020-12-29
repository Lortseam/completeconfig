package me.lortseam.completeconfig.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.lortseam.completeconfig.data.Collection;
import me.lortseam.completeconfig.data.CollectionMap;
import me.lortseam.completeconfig.data.EntryMap;

import java.io.IOException;
import java.util.Iterator;

public class CollectionDeserializer extends JsonDeserializer<Collection> {

    public static final Class<Collection> TYPE = Collection.class;

    @Override
    public Collection deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectNode collections = new ObjectNode(JsonNodeFactory.instance);
        ObjectNode entries = new ObjectNode(JsonNodeFactory.instance);
        ObjectNode collection = parser.readValueAsTree();
        Iterator<String> iterator = collection.fieldNames();
        while (iterator.hasNext()) {
            String key = iterator.next();
            JsonNode value = collection.findValue(key);
            if (value.isObject()) {
                collections.set(key, value);
            } else {
                entries.set(key, value);
            }
        }
        context.readValue(collections.traverse(), CollectionMap.class);
        context.readValue(entries.traverse(), EntryMap.class);
        return null;
    }

}
