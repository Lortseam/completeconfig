package me.lortseam.completeconfig.data;

import com.google.common.base.CaseFormat;
import net.minecraft.text.Text;

import java.util.function.Function;

public class EnumEntry<T extends Enum<?>> extends Entry<T> {

    public EnumEntry(EntryOrigin origin) {
        super(origin);
    }

    @Override
    public Function<T, Text> getValueTextSupplier() {
        return enumValue -> getTranslation().append(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, enumValue.name())).toText();
    }

}
