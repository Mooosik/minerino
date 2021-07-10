package net.mooosik.minerino.twitch;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.mooosik.minerino.config.ModConfig;

import java.util.UUID;

public class TwitchEventHandler {

    @EventSubscriber
    public void printChannelMessage(ChannelMessageEvent event) {

        if(ModConfig.getConfig().getIgnoreList().contains(event.getUser().getName().toLowerCase())) {
            return;
        }
        IFormattableTextComponent message = new StringTextComponent("")
                .appendSibling(Twitch.buildLinkedCommandText(event.getChannel().getName(),"/minerino switch ").mergeStyle(TextFormatting.DARK_PURPLE));

        TextFormatting userNameFormatting;
        if(Twitch.getTwitchUserColors().containsKey(event.getUser().getName())) {
            userNameFormatting = Twitch.getTwitchUserColors().get(event.getUser().getName());
        } else {
            userNameFormatting = Twitch.calculateMinecraftColor(event.getMessageEvent().getTagValue("color").get());
            Twitch.getTwitchUserColors().put(event.getUser().getName(), userNameFormatting);
        }
        message.appendSibling(new StringTextComponent(event.getUser().getName()).mergeStyle(userNameFormatting))
                .appendSibling(new StringTextComponent(": " +event.getMessage()).mergeStyle(TextFormatting.WHITE));

        Minecraft.getInstance().ingameGUI.sendChatMessage(ChatType.CHAT, message, UUID.randomUUID());


    }


}

