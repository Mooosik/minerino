package net.mooosik.minerino.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.mooosik.minerino.config.ModConfig;
import net.mooosik.minerino.twitch.Twitch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Screen.class)
public class ScreenMixin {

    @Inject(at = @At("HEAD"), method = "sendMessage(Ljava/lang/String;Z)V", cancellable = true)
    private void sendMessage(String text, boolean showInHistory, CallbackInfo ci) {
        ModConfig config = ModConfig.getConfig();

        if(!config.getActiveChat().equals("Minecraft") && !text.startsWith("/")) {      //If the currently active chat is not minecraft AND its not a command

            Twitch.getClient().getChat().sendMessage(config.getActiveChat(), text);     //Send the message via TwitchClient

            IFormattableTextComponent mctext = new StringTextComponent("[" + config.getActiveChat() + "] ").mergeStyle(TextFormatting.DARK_PURPLE)        //add the text to minecraft (needs to be formatted ofc)
                    .appendSibling(new StringTextComponent(config.getUsername())
                            .mergeStyle(Twitch.getTwitchUserColors().containsKey(config.getUsername())?Twitch.getTwitchUserColors().get(config.getUsername()):TextFormatting.WHITE)
                            .appendSibling(new StringTextComponent(  ": " + text).mergeStyle(TextFormatting.WHITE)));

            Minecraft.getInstance().ingameGUI.sendChatMessage(ChatType.CHAT, mctext, UUID.randomUUID());
            ci.cancel();
        }
    }
}
