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

    /**
     * Incoming message
     * @param event
     */
    @EventSubscriber
    public void printChannelMessage(ChannelMessageEvent event) {

        //Ignore if is on ignore list
        if(ModConfig.getConfig().getIgnoreList().contains(event.getUser().getName().toLowerCase())) {
            return;
        }

        //Append channel to the message and make it "clickable"
        MutableText message = new LiteralText("")
                .append(Twitch.buildLinkedCommandText(event.getChannel().getName(),"/minerino switch ").formatted(Formatting.DARK_PURPLE));

        Formatting userNameFormatting;

        //If username already has a color (due to it being assigned previously), get that color
        if(Twitch.getTwitchUserColors().containsKey(event.getUser().getName())) {
            userNameFormatting = Twitch.getTwitchUserColors().get(event.getUser().getName());
        } else {    //Else generate a color
            try {
                userNameFormatting = Twitch.calculateMinecraftColor(event.getMessageEvent().getTagValue("color").get());
            } catch (Exception e) {
                userNameFormatting = Formatting.GRAY;
            }

            Twitch.getTwitchUserColors().put(event.getUser().getName(), userNameFormatting);
        }

        //Append username to message
        message.append(new LiteralText(event.getUser().getName()).formatted(userNameFormatting)).append(new LiteralText(": " +event.getMessage()).formatted(Formatting.WHITE));

        //Send the message to the minecraft client
        MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.CHAT, message, UUID.randomUUID());
    }


}
