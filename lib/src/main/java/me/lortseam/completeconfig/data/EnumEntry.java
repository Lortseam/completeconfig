package me.lortseam.completeconfig.data;

import com.google.common.base.CaseFormat;
import me.lortseam.completeconfig.data.entry.EntryOrigin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.util.function.Function;

public class EnumEntry<T extends Enum<?>> extends Entry<T> {

    public EnumEntry(EntryOrigin origin) {
        super(origin);
    }

    @Environment(EnvType.CLIENT)
    public final Function<Enum, Text> getValueTextSupplier() {
        return enumValue -> translation.append(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, enumValue.name())).toText();
    }

}
