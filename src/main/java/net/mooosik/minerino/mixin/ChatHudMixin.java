package net.mooosik.minerino.mixin;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.mooosik.minerino.twitch.Twitch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {


    /**
     * Injection that modifies the text parameter to perform a notifications check
     * @param text text that gets modified
     * @return
     */
    @ModifyArg(method = "addMessage", at = @At(value ="INVOKE",
            target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;I)V" ),
            index = 0)
    public Text modifyText(Text text) {

        if(text.getString().startsWith("<")) {
            // System.out.println(message.getString());

             text = new LiteralText("[Minecraft] ").formatted(Formatting.GREEN).append(text).formatted(Formatting.WHITE);

        }

        if(Twitch.containsNotification(text.getString())) {        //If there is a notification in this message
            PlayerEntity player = MinecraftClient.getInstance().player;   //get the player to play a sound at the player's location
            MinecraftClient.getInstance().world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 1f, 0.5f);
            Text tmp = new LiteralText(text.getString()).formatted(Formatting.RED); //change the color of the message to red so it stands out
            return tmp;
        }


        return text;
    }

    @Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;)V", cancellable = true)
    public void addMessage(Text message, CallbackInfo ci) {


        if(message.getString().startsWith("<")) {
           // System.out.println(message.getString());
           // message = new LiteralText("[Minecraft] ").append(message);
        //ci.cancel();
        }



    }



}
