package net.mooosik.minerino.mixin;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.mooosik.minerino.config.ModConfig;
import net.mooosik.minerino.twitch.Twitch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import java.util.Date;
import java.util.UUID;

@Mixin(Screen.class)
public class ScreenMixin {

    /**
     * This mixin intercepts a message written in minecraft chat and sends it to twitch instead of sending it to the server
     * @param text
     * @param showInHistory
     * @param ci
     */
    @Inject(at = @At("HEAD"), method = "sendMessage(Ljava/lang/String;Z)V", cancellable = true)
    private void sendMessage(String text, boolean showInHistory, CallbackInfo ci) {
        ModConfig config = ModConfig.getConfig();

            if(!config.getActiveChat().equals("MC") && !text.startsWith("/")) {      //If the currently active chat is not minecraft AND its not a command

                Twitch.sendMessage(config.getActiveChat(), text);   //send the message via the twitch client

                //Now that the message is sent, it needs to be added to minecraft (but only locally)
                Text mctext = new LiteralText("[" + config.getActiveChat() + "] ").formatted(Formatting.DARK_PURPLE)        //Format the message
                        .append(new LiteralText(config.getUsername())
                                .formatted(Twitch.getTwitchUserColors().containsKey(config.getUsername())?Twitch.getTwitchUserColors().get(config.getUsername()):Formatting.WHITE)
                                .append(new LiteralText(  ": " + text).formatted(Formatting.WHITE)));
                //Add the message
                MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.CHAT, mctext, UUID.randomUUID());
                ci.cancel();    //Cancel the method (this is the part that actually stops the message from being sent to minecraft)
            }
        }
}

