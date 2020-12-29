package me.lortseam.completeconfig.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.data.Entry;
import me.lortseam.completeconfig.data.EntryMap;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class EntryMapDeserializer extends JsonDeserializer<EntryMap> {

    private static final YAMLMapper MAPPER = new YAMLMapper();
    public static final Class<EntryMap> TYPE = EntryMap.class;

    private final EntryMap configMap;

    @Override
    public EntryMap deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        LinkedHashMap<String, JsonNode> map = MAPPER.readValue(parser, TypeFactory.defaultInstance().constructMapType(LinkedHashMap.class, String.class, JsonNode.class));
        for (Map.Entry<String, JsonNode> mapEntry : map.entrySet()) {
            Entry<?> entry = configMap.get(mapEntry.getKey());
            if (entry == null) {
                break;
            }
            new YAMLMapper().registerModule(new SimpleModule()
                    .addDeserializer(EntryDeserializer.TYPE, new EntryDeserializer<>(entry))
            ).readValue(mapEntry.getValue().traverse(), Entry.class);
        }
        return null;
    }

}