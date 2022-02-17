package me.lortseam.completeconfig.gui.coat;

import de.siphalor.coat.handler.ConfigEntryHandler;
import de.siphalor.coat.handler.Message;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.data.BoundedEntry;
import me.lortseam.completeconfig.data.Entry;
import me.lortseam.completeconfig.util.NumberUtils;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public final class GenericEntryHandler<T> implements ConfigEntryHandler<T> {

    private final Entry<T> entry;

    @Override
    public T getDefault() {
        return entry.getDefaultValue();
    }

    @Override
    public @NotNull Collection<Message> getMessages(T value) {
        List<Message> messages = new ArrayList<>();
        if (entry instanceof BoundedEntry entry) {
            if (NumberUtils.compare((Number) value, entry.getMin()) < 0) {
                messages.add(new Message(Message.Level.ERROR, new TranslatableText("completeconfig.gui.coat.message.tooSmall", entry.getMin())));
            } else if (NumberUtils.compare((Number) value, entry.getMax()) > 0) {
                messages.add(new Message(Message.Level.ERROR, new TranslatableText("completeconfig.gui.coat.message.tooLarge", entry.getMax())));
            }
        }
        if (entry.requiresRestart() && !value.equals(entry.getValue())) {
            messages.add(new Message(Message.Level.INFO, new TranslatableText("completeconfig.gui.coat.message.requiresRestart")));
        }
        return messages;
    }

    @Override
    public void save(T value) {
        entry.setValue(value);
    }

    @Override
    public Text asText(T value) {
        return entry.getValueTextSupplier().apply(value);
    }

}
