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

    @Inject(at = @At("HEAD"), method = "sendMessage(Ljava/lang/String;Z)V", cancellable = true)
    private void sendMessage(String text, boolean showInHistory, CallbackInfo ci) {
        ModConfig config = ModConfig.getConfig();

            if(!config.getActiveChat().equals("Minecraft") && !text.startsWith("/")) {      //If the currently active chat is not minecraft AND its not a command

                Twitch.sendMessage(config.getActiveChat(), text);

                Text mctext = new LiteralText("[" + config.getActiveChat() + "] ").formatted(Formatting.DARK_PURPLE)        //add the text to minecraft (needs to be formatted ofc)
                        .append(new LiteralText(config.getUsername())
                                .formatted(Twitch.getTwitchUserColors().containsKey(config.getUsername())?Twitch.getTwitchUserColors().get(config.getUsername()):Formatting.WHITE)
                                .append(new LiteralText(  ": " + text).formatted(Formatting.WHITE)));

                MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.CHAT, mctext, UUID.randomUUID());
                ci.cancel();
            }
        }
}

