package me.lortseam.completeconfig.gui;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.entry.Entry;

import java.util.function.Predicate;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class Registration<T> implements Predicate<Entry<?>> {

    private final GuiProviderPredicate<T> predicate;
    @Getter(AccessLevel.PACKAGE)
    private final GuiProvider<T> provider;

    @Override
    public boolean test(Entry<?> entry) {
        return predicate.test(entry.getField(), entry.getExtras());
    }

}
