package net.mooosik.minerino.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.mooosik.minerino.config.ModConfig;
import net.mooosik.minerino.twitch.Twitch;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class MinerinoLogout {

    /**
     * Build the "logout" command
     * @return
     */
    public static LiteralArgumentBuilder build() {
        return literal("logout").executes(context -> {       //if only login
            ModConfig config = ModConfig.getConfig();

            if(Twitch.getClient() == null) {
                ((CommandSource) context.getSource()).sendFeedback(new StringTextComponent("[Minerino] You are not logged in!"), false);
                return -1;
            }

            Twitch.close();


            Twitch.switchChat("Minecraft");
            ((CommandSource) context.getSource()).sendFeedback(new StringTextComponent("[Minerino] Closed connection to Twitch"), false);
            return 1;

        });


    }
}
