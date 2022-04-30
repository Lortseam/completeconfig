package me.lortseam.completeconfig.gui.coat.handler;

import de.siphalor.coat.handler.ConfigEntryHandler;
import de.siphalor.coat.handler.Message;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.data.Entry;
import net.minecraft.text.Text;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class BasicEntryHandler<T, E extends Entry<T>> implements ConfigEntryHandler<T> {

    protected final E entry;
    private final List<Message> messages = new ArrayList<>();

    @Override
    public final T getDefault() {
        return entry.getDefaultValue();
    }

    @Override
    public final @NotNull Collection<Message> getMessages(T value) {
        messages.clear();
        onUpdate(value);
        return messages;
    }

    @MustBeInvokedByOverriders
    public void onUpdate(T value) {
        if (entry.requiresRestart() && !value.equals(entry.getValue())) {
            addMessage(new Message(Message.Level.INFO, Text.translatable("completeconfig.gui.coat.message.requiresRestart")));
        }
    }

    protected final void addMessage(Message message) {
        messages.add(message);
    }

    @Override
    public final void save(T value) {
        entry.setValue(value);
    }

    @Override
    public final Text asText(T value) {
        return entry.getValueTextSupplier().apply(value);
    }

}
