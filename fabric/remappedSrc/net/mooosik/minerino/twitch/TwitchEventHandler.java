package net.mooosik.minerino.twitch;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.mooosik.minerino.config.ModConfig;

import java.util.UUID;

public class TwitchEventHandler {

    @EventSubscriber
    public void printChannelMessage(ChannelMessageEvent event) {

        if(ModConfig.getConfig().getIgnoreList().contains(event.getUser().getName().toLowerCase())) {
            return;
        }

        MutableText message = new LiteralText("").append(Twitch.buildLinkedCommandText(event.getChannel().getName(),"/minerino switch ").formatted(Formatting.DARK_PURPLE));
        Formatting userNameFormatting;
        if(Twitch.getTwitchUserColors().containsKey(event.getUser().getName())) {
            userNameFormatting = Twitch.getTwitchUserColors().get(event.getUser().getName());
        } else {
            userNameFormatting = Twitch.calculateMinecraftColor(event.getMessageEvent().getTagValue("color").get());
            Twitch.getTwitchUserColors().put(event.getUser().getName(), userNameFormatting);
        }

        message.append(new LiteralText(event.getUser().getName()).formatted(userNameFormatting)).append(new LiteralText(": " +event.getMessage()).formatted(Formatting.WHITE));

        MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.CHAT, message, UUID.randomUUID());



    }


}
