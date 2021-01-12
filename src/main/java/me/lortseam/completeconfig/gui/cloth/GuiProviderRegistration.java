package me.lortseam.completeconfig.gui.cloth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.data.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class GuiProviderRegistration {

    private final Predicate<Entry<?>> predicate;
    @Getter(AccessLevel.PACKAGE)
    private final GuiProvider<?> provider;

    boolean test(Entry<?> entry) {
        return predicate.test(entry);
    }

}
