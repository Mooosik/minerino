package net.mooosik.minerino.mixin;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.mooosik.minerino.config.ModConfig;
import net.mooosik.minerino.twitch.Twitch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.mooosik.minerino.twitch.Twitch.SWITCHMODE;

/**
 * TODO: further simplify this class as the way it is structured is unnecessarily complicated at the moment
 */
@Mixin(ChatHud.class)
public class ChatHudMixin {

    @Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;I)V", cancellable = true)
    public void addMessage(Text message, int messageId, CallbackInfo ci) {

        if(!SWITCHMODE) {
            //message = modifyText(message);

            if (message.getString().startsWith("<")) {
                message = Twitch.buildLinkedCommandText("MC", "/minerino switch ").formatted(Formatting.GREEN).append(new LiteralText(message.getString()).formatted(Formatting.WHITE));
            }


            if(message.getString().startsWith("[Server]")) {        //if its a server message
                message = new LiteralText("").append(message).formatted(Formatting.LIGHT_PURPLE).formatted(Formatting.ITALIC);
                Twitch.getChatMessages().get("MC").push(message);        //add it to Minecraft logs

            } else if(!message.getString().startsWith("[Minerino]")) {

                if(Twitch.containsNotification(message.getString())) {        //If the message is a notification in this message
                    PlayerEntity player = MinecraftClient.getInstance().player;   //get the player to play a sound at the player's location
                    MinecraftClient.getInstance().world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.5f, 1f);

                    message = new LiteralText("[Alert]").append(message).formatted(Formatting.RED); //change the color of the message to red so it stands out
                }


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
