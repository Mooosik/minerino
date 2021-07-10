package net.mooosik.minerino.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.mooosik.minerino.config.ModConfig;
import net.mooosik.minerino.twitch.Twitch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.mooosik.minerino.command.CommandInitializer.sendInfoMessage;
import static net.mooosik.minerino.twitch.Twitch.SWITCHMODE;

@Mixin(NewChatGui.class)
public class NewChatGuiMixin {


    /**
     * Injection that modifies the text parameter to perform a notifications check
     * @param text text that gets modified
     * @return
     */
    @ModifyArg(method = "printChatMessage", at = @At(value ="INVOKE",     //It claims this is an error, but it isn't FeelsDankMan
            target = "Lnet/minecraft/client/gui/NewChatGui;printChatMessageWithOptionalDeletion(Lnet/minecraft/util/text/ITextComponent;I)V" ),
            index = 0)
    public ITextComponent modifyText(ITextComponent text) {


        if(!SWITCHMODE) {
            //If its a default minecraft message, add the [Minecraft] prefix
            //This could cause issues if someone is joining the Minecraft twitch channel
            if (text.getString().startsWith("<")) {
                text = new StringTextComponent("")
                        .appendSibling(Twitch.buildLinkedText("Minecraft").mergeStyle(TextFormatting.GREEN))
                        .appendSibling(new StringTextComponent(text.getString()).mergeStyle(TextFormatting.WHITE));
            }

            if (!text.getString().startsWith("[Minerino]") && Twitch.containsNotification(text.getString())) {        //If the message is a notification in this message
                PlayerEntity player = Minecraft.getInstance().player;   //get the player to play a sound at the player's location
                Minecraft.getInstance().world.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.5f, 1f);
                ITextComponent tmp = new StringTextComponent("[Alert]").appendSibling(text).mergeStyle(TextFormatting.RED); //change the color of the message to red so it stands out
                return tmp;
            }

        }
        return text;
    }


    @Inject(at = @At("HEAD"), method = "printChatMessageWithOptionalDeletion", cancellable = true)
    public void addMessage(ITextComponent message, int messageId, CallbackInfo ci) {
        if(ModConfig.getConfig().INFOFLAG) {
            ModConfig.getConfig().INFOFLAG = false;
            sendInfoMessage();
        }
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
