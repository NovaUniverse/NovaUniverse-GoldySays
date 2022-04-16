package net.novauniverse.goldysays.game;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GoldyTaskCompletedEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private GoldySaysTask task;

    public GoldyTaskCompletedEvent(GoldySaysTask task) {
        this.task = task;
    }

    public GoldySaysTask getTask() {
        return task;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
