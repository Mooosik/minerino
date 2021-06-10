package net.mooosik.minerino.mixin;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.mooosik.minerino.config.ModConfig;
import net.mooosik.minerino.twitch.Twitch;
import net.mooosik.minerino.util.SizedStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.mooosik.minerino.twitch.Twitch.SWITCHMODE;

@Mixin(ChatHud.class)
public class ChatHudMixin {


    /**
     * Injection that modifies the text parameter to perform a notifications check
     * @param text text that gets modified
     * @return
     */
    @ModifyArg(method = "addMessage", at = @At(value ="INVOKE",     //It claims this is an error, but it isn't FeelsDankMan
            target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;I)V" ),
            index = 0)
    public Text modifyText(Text text) {


        if(!SWITCHMODE) {
            //If its a default minecraft message, add the [Minecraft] prefix
            //This could cause issues if someone is joining the Minecraft twitch channel
            if (text.getString().startsWith("<")) {
                text = Twitch.buildLinkedText("Minecraft").formatted(Formatting.GREEN).append(new LiteralText(text.getString()).formatted(Formatting.WHITE));
            }

            if (!text.getString().startsWith("[Minerino]") && Twitch.containsNotification(text.getString())) {        //If the message is a notification in this message
                PlayerEntity player = MinecraftClient.getInstance().player;   //get the player to play a sound at the player's location
                MinecraftClient.getInstance().world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.5f, 1f);
                Text tmp = new LiteralText("[Alert]").append(text).formatted(Formatting.RED); //change the color of the message to red so it stands out
                return tmp;
            }

        }
        return text;
    }

    @Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;I)V", cancellable = true)
    public void addMessage(Text message, int messageId, CallbackInfo ci) {

        if(!SWITCHMODE) {
            if(!message.getString().startsWith("[Minerino]")) {
                if (message.getString().startsWith("[")) {        //If the message starts with [. This could lead to issues if something else manipulates the chat

                    String startsWith = message.getString().startsWith("[Alert]")?"[Alert][":"[";   //If its an alert, change the startsWith prefix

                        for (String key : Twitch.getChatMessages().keySet()     //Find the correct chat
                        ) {
                            if (message.getString().startsWith(startsWith + key)) {     //If they match, put message on stack
                                Twitch.getChatMessages().get(key).push(message);
                                break;
                            }

                        }
                        //If message is not an alert and is not a message belonging to the current chat, dont show it
                    if (!message.getString().startsWith("[Alert]") && !message.getString().startsWith("[" + ModConfig.getConfig().getActiveChat())) {
                        ci.cancel();
                    }

                }
            }

        }
    }



}
