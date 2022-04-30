package me.lortseam.completeconfig.gui.coat.handler;

import de.siphalor.coat.handler.Message;
import me.lortseam.completeconfig.data.BoundedEntry;
import me.lortseam.completeconfig.util.NumberUtils;
import net.minecraft.text.Text;

public class BoundedEntryHandler<T extends Number> extends BasicEntryHandler<T, BoundedEntry<T>> {

    public BoundedEntryHandler(BoundedEntry<T> entry) {
        super(entry);
    }

    @Override
    public void onUpdate(T value) {
        if (NumberUtils.compare(value, entry.getMin()) < 0) {
            addMessage(new Message(Message.Level.ERROR, Text.translatable("completeconfig.gui.coat.message.tooSmall", entry.getMin())));
        } else if (NumberUtils.compare(value, entry.getMax()) > 0) {
            addMessage(new Message(Message.Level.ERROR, Text.translatable("completeconfig.gui.coat.message.tooLarge", entry.getMax())));
        }
        super.onUpdate(value);
    }

}
