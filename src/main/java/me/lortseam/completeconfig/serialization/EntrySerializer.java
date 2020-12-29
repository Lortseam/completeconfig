package me.lortseam.completeconfig.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.lortseam.completeconfig.data.Entry;

import java.io.IOException;

public class EntrySerializer extends JsonSerializer<Entry> {

    public static final Class<Entry> TYPE = Entry.class;

    @Override
    public void serialize(Entry entry, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        serializers.defaultSerializeValue(entry.getValue(), generator);
    }

}
