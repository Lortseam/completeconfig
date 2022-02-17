package me.lortseam.completeconfig.extensions.clothconfig;

import me.lortseam.completeconfig.data.Entry;
import me.lortseam.completeconfig.data.extension.ClientExtension;
import me.lortseam.completeconfig.gui.GuiProvider;
import me.lortseam.completeconfig.gui.cloth.ClothConfigGuiExtension;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Modifier;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public final class ClothConfigExtension implements ClientExtension, ClothConfigGuiExtension {

    @Override
    public TypeSerializerCollection getTypeSerializers() {
        return TypeSerializerCollection.builder()
                .register(ModifierKeyCode.class, new ModifierKeyCodeSerializer())
                .build();
    }

    @Override
    public List<GuiProvider<FieldBuilder<?, ?>>> getProviders() {
        return List.of(GuiProvider.create((Entry<ModifierKeyCode> entry) -> ConfigEntryBuilder.create()
                        .startModifierKeyCodeField(entry.getText(), entry.getValue())
                        .setDefaultValue(entry.getDefaultValue())
                        .setTooltip(entry.getDescription().map(description -> new Text[]{description}))
                        .setModifierSaveConsumer(entry::setValue),
                ModifierKeyCode.class));
    }

    private static final class ModifierKeyCodeSerializer implements TypeSerializer<ModifierKeyCode> {

        @Override
        public ModifierKeyCode deserialize(Type type, ConfigurationNode node) throws SerializationException {
            if(!node.isMap()) throw new SerializationException("Node must be of type map");
            var map = node.childrenMap();
            String keyCode = map.get("keyCode").getString();
            if(keyCode == null) {
                throw new SerializationException("keyCode entry is missing or has invalid type");
            }
            InputUtil.Key key;
            try {
                key = InputUtil.fromTranslationKey(keyCode);
            } catch(Exception e) {
                throw new SerializationException(e);
            }
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
