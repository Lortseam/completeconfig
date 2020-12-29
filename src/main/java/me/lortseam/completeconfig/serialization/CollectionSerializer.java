package me.lortseam.completeconfig.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.lortseam.completeconfig.data.Collection;
import me.lortseam.completeconfig.data.Entry;

import java.io.IOException;
import java.util.Map;

public class CollectionSerializer extends JsonSerializer<Collection> {

    public static final Class<Collection> TYPE = Collection.class;

    @Override
    public void serialize(Collection value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeStartObject();
        for (Map.Entry<String, Collection> entry : value.getCollections().entrySet()) {
            generator.writeFieldName(entry.getKey());
            serializers.defaultSerializeValue(entry.getValue(), generator);
        }
        for (Map.Entry<String, Entry> entry : value.getEntries().entrySet()) {
            generator.writeFieldName(entry.getKey());
            serializers.defaultSerializeValue(entry.getValue(), generator);
        }
        generator.writeEndObject();
    }

}
