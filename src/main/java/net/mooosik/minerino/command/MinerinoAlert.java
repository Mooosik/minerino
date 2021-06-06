package net.mooosik.minerino.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.LiteralText;
import net.mooosik.minerino.config.ModConfig;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MinerinoAlert {

    public static LiteralArgumentBuilder build() {

        return literal("alert")
                .then(literal("add")
                .then(argument("trigger",StringArgumentType.word())
                .executes(context -> {       //if only login
            ModConfig config = ModConfig.getConfig();
            String trigger = StringArgumentType.getString(context, "trigger");
            if(config.getNotificationList().contains(trigger.toLowerCase())) {
                ((FabricClientCommandSource) context.getSource()).sendError(new LiteralText("[Minerino] " +  trigger + " is already a notification"));
                return -1;
            }

            config.getNotificationList().add(trigger.toLowerCase());
            ((FabricClientCommandSource) context.getSource()).sendFeedback(new LiteralText("[Minerino] Successfully added " + trigger + " as notification"));
            return 1;

            })))
                .then(literal("remove")
                .then(argument("trigger", StringArgumentType.word())
                .executes(context -> {
            ModConfig config = ModConfig.getConfig();
            String trigger = StringArgumentType.getString(context, "trigger");

            if(config.getNotificationList().contains(trigger.toLowerCase())) {
                config.getNotificationList().remove(trigger.toLowerCase());
                ((FabricClientCommandSource) context.getSource()).sendFeedback(new LiteralText("[Minerino] Successfully removed " + trigger + " from notifications"));
                return 1;
            }

            ((FabricClientCommandSource) context.getSource()).sendError(new LiteralText("[Minerino] " +  trigger + " is not a notification"));
           return -1;
           })));

    }}
