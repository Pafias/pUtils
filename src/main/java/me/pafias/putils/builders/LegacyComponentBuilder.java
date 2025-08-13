package me.pafias.putils.builders;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class LegacyComponentBuilder {

    private final String text;
    private String clickCommand;
    private String hoverText;

    public LegacyComponentBuilder(String text) {
        this.text = text;
    }

    public LegacyComponentBuilder clickable(String command) {
        this.clickCommand = command;
        return this;
    }

    public LegacyComponentBuilder hoverable(String text) {
        this.hoverText = text;
        return this;
    }

    public BaseComponent[] build() {
        net.md_5.bungee.api.chat.ComponentBuilder builder = new net.md_5.bungee.api.chat.ComponentBuilder(text);
        if (clickCommand != null) {
            builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickCommand));
        }
        if (hoverText != null) {
            builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(hoverText)}));
        }
        return builder.create();
    }

}
