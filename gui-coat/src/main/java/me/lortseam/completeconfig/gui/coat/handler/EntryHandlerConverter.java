package me.lortseam.completeconfig.gui.coat.handler;

import de.siphalor.coat.handler.ConfigEntryHandler;
import de.siphalor.coat.handler.Message;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.data.Entry;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class EntryHandlerConverter<T, C> implements ConfigEntryHandler<C> {

    public static <T extends Number> EntryHandlerConverter<T, String> numberToString(BasicEntryHandler<T, ? extends Entry<T>> handler, Function<String, T> parser) {
        return new EntryHandlerConverter<>(handler, parser, Object::toString, () -> new Message(Message.Level.ERROR, new TranslatableText("completeconfig.gui.coat.message.invalidNumber")));
    }

    public static <T extends Number> EntryHandlerConverter<T, String> numberToString(Entry<T> entry, Function<String, T> parser) {
        return numberToString(new BasicEntryHandler<>(entry), parser);
    }

    private final BasicEntryHandler<T, ? extends Entry<T>> handler;
    private final Function<C, T> converterFrom;
    private final Function<T, C> converterTo;
    private final Supplier<Message> errorMessageSupplier;

    private Optional<T> tryConvertFrom(C convertedValue) {
        try {
            return Optional.of(converterFrom.apply(convertedValue));
        } catch (Exception ignore) {
            return Optional.empty();
        }
    }

    @Override
    public final C getDefault() {
        return converterTo.apply(handler.getDefault());
    }

    @Override
    public final @NotNull Collection<Message> getMessages(C convertedValue) {
        return tryConvertFrom(convertedValue).map(handler::getMessages).orElseGet(() -> {
            return Collections.singleton(errorMessageSupplier.get());
        });
    }

    @Override
    public final void save(C convertedValue) {
        tryConvertFrom(convertedValue).ifPresent(handler::save);
    }

    @Override
    public final Text asText(C convertedValue) {
        return handler.asText(converterFrom.apply(convertedValue));
    }

}
