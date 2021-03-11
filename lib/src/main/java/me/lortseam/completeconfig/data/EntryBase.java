package me.lortseam.completeconfig.data;

import lombok.Getter;
import me.lortseam.completeconfig.util.TypeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.function.Consumer;

public abstract class EntryBase<T> {

    @Getter
    protected final Field field;
    @Getter
    protected final Type type;
    @Getter
    protected final Class<T> typeClass;

    protected EntryBase(Field field) {
        this.field = field;
        type = TypeUtils.getFieldType(field);
        typeClass = (Class<T>) TypeUtils.getTypeClass(type);
    }

    abstract void interact(Consumer<Entry<T>> interaction);

}
