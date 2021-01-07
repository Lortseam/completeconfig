package me.lortseam.completeconfig.data;

import java.util.function.Consumer;

interface EntryAccessor<T> {

    Class<T> getType();

    void connect(Consumer<Entry<T>> modifier);

}
