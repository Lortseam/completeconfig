package me.lortseam.completeconfig.extension.clothconfig;

import me.lortseam.completeconfig.extension.ClientExtension;
import me.shedaniel.clothconfig2.api.Modifier;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.lang.reflect.Type;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public final class ClothConfigClientExtension implements ClientExtension {

    @Override
    public TypeSerializerCollection getTypeSerializers() {
        return TypeSerializerCollection.builder()
                .register(ModifierKeyCode.class, new ModifierKeyCodeSerializer())
                .build();
    }

    private static final class ModifierKeyCodeSerializer implements TypeSerializer<ModifierKeyCode> {

        @Override
        public ModifierKeyCode deserialize(Type type, ConfigurationNode node) throws SerializationException {
            if(!node.isMap()) throw new SerializationException(node, type, "Node must be of type map");
            var map = node.childrenMap();
            var key = InputUtil.fromTranslationKey(requireNonNull(map.get("keyCode"), "keyCode").getString());
            if (key == InputUtil.UNKNOWN_KEY) {
                return ModifierKeyCode.unknown();
            }
            var modifier = Modifier.none();
            if (map.containsKey("modifier")) {
                modifier = Modifier.of((short) map.get("modifier").getInt());
            }
            return ModifierKeyCode.of(key, modifier);
        }

        @Override
        public void serialize(Type type, ModifierKeyCode obj, ConfigurationNode node) throws SerializationException {
            node.set(Map.of(
                    "keyCode", obj.getKeyCode().getTranslationKey(),
                    "modifier", obj.getModifier().getValue()
            ));
        }

    }

}
