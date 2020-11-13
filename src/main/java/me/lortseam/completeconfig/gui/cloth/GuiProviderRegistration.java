package me.lortseam.completeconfig.gui.cloth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.entry.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class GuiProviderRegistration<T> {

    private final GuiProviderPredicate<T> predicate;
    @Getter(AccessLevel.PACKAGE)
    private final GuiProvider<T> provider;

    public boolean test(Entry<?> entry) {
        return predicate.test(entry.getField(), entry.getExtras());
    }

}
