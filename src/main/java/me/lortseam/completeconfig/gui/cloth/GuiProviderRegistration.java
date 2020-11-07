package me.lortseam.completeconfig.gui.cloth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.entry.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
//TODO: Functional interfaces should not get extended
class GuiProviderRegistration<T> implements Predicate<Entry<?>> {

    private final GuiProviderPredicate<T> predicate;
    @Getter(AccessLevel.PACKAGE)
    private final GuiProvider<T> provider;

    @Override
    public boolean test(Entry<?> entry) {
        return predicate.test(entry.getField(), entry.getExtras());
    }

}
