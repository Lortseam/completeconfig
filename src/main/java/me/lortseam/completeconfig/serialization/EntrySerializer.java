package me.lortseam.completeconfig.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.lortseam.completeconfig.entry.Entry;

import java.lang.reflect.Type;

@NoArgsConstructor
public class EntrySerializer implements JsonSerializer<Entry> {

    public static final Type TYPE = Entry.class;

    @Override
    public JsonElement serialize(Entry entry, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(entry.getValue());
    }

}
