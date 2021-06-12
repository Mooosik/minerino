package net.mooosik.minerino.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.LiteralText;
import net.mooosik.minerino.config.ModConfig;
import net.mooosik.minerino.twitch.Twitch;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MinerinoIgnore {

    /**
     * add a user to the ignore list
     * @return
     */
    public static LiteralArgumentBuilder build() {

        return literal("ignore")
                        .then(argument("username",StringArgumentType.word())
                                .executes(context -> {       //if only login
                                    ModConfig config = ModConfig.getConfig();
                                    String trigger = StringArgumentType.getString(context, "username");
                                    if(config.getIgnoreList().contains(trigger.toLowerCase())) {
                                        ((FabricClientCommandSource) context.getSource()).sendError(new LiteralText("[Minerino] " +  trigger + " is already on the ignore-list"));
                                        return -1;
                                    }

                                    config.getIgnoreList().add(trigger.toLowerCase());
                                    config.save();
                                    ((FabricClientCommandSource) context.getSource()).sendFeedback(new LiteralText("[Minerino] Successfully added " + trigger + " to the ignore-list"));
                                    return 1;

                                }));
    }}
