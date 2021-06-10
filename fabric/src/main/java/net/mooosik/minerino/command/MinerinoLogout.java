package net.mooosik.minerino.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.LiteralText;
import net.mooosik.minerino.config.ModConfig;
import net.mooosik.minerino.twitch.Twitch;

import static net.minecraft.server.command.CommandManager.literal;

public class MinerinoLogout {

    /**
     * Build the "login" command
     * @return
     */
    public static LiteralArgumentBuilder build() {
        return literal("logout").executes(context -> {       //if only login
            ModConfig config = ModConfig.getConfig();

            if(Twitch.getClient() == null) {
                ((FabricClientCommandSource) context.getSource()).sendFeedback(new LiteralText("[Minerino] You are not logged in!"));
                return -1;
            }

            Twitch.close();


            Twitch.switchChat("Minecraft");
            ((FabricClientCommandSource) context.getSource()).sendFeedback(new LiteralText("[Minerino] Closed connection to Twitch"));
            return 1;

        });


    }
}
