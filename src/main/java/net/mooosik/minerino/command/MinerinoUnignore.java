package net.mooosik.minerino.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.LiteralText;
import net.mooosik.minerino.config.ModConfig;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MinerinoUnignore {

    /**
     * Remove a user from the ignore list
     * @return
     */
    public static LiteralArgumentBuilder build() {

        return literal("unignore")
                        .then(argument("trigger", StringArgumentType.word()).suggests((context, builder) -> {

                            for (String s : ModConfig.getConfig().getIgnoreList()) {

                                builder.suggest(s);

                            }
                            return builder.buildFuture();

                        })
                                .executes(context -> {
                                    ModConfig config = ModConfig.getConfig();
                                    String trigger = StringArgumentType.getString(context, "trigger");

                                    if(config.getIgnoreList().contains(trigger.toLowerCase())) {
                                        config.getIgnoreList().remove(trigger.toLowerCase());
                                        config.save();
                                        ((FabricClientCommandSource) context.getSource()).sendFeedback(new LiteralText("[Minerino] Successfully removed " + trigger + " from the ignore-list"));
                                        return 1;
                                    }

                                    ((FabricClientCommandSource) context.getSource()).sendError(new LiteralText("[Minerino] " +  trigger + " is not on the ignore-list"));
                                    return -1;
                                }));

    }
}
