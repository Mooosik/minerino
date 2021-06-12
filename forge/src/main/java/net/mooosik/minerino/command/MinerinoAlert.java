package net.mooosik.minerino.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.mooosik.minerino.config.ModConfig;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class MinerinoAlert {
    /**
     * Command for adding / removing alerts
     * @return
     */
    public static LiteralArgumentBuilder build() {

        return literal("alert")
                .then(literal("add")
                        .then(argument("trigger", StringArgumentType.word())
                                .executes(context -> {       //if only login
                                    ModConfig config = ModConfig.getConfig();
                                    String trigger = StringArgumentType.getString(context, "trigger");
                                    if(config.getNotificationList().contains(trigger.toLowerCase())) {
                                        ((CommandSource) context.getSource()).sendErrorMessage(new StringTextComponent("[Minerino] " +  trigger + " is already a notification"));
                                        return -1;
                                    }

                                    config.getNotificationList().add(trigger.toLowerCase());
                                    config.save();
                                    ((CommandSource) context.getSource()).sendFeedback(new StringTextComponent("[Minerino] Successfully added " + trigger + " as notification"), false);
                                    return 1;

                                })))
                .then(literal("remove")
                        .then(argument("trigger", StringArgumentType.word()).suggests((context, builder) -> {

                            for (String s : ModConfig.getConfig().getNotificationList()) {

                                builder.suggest(s);

                            }
                            return builder.buildFuture();

                        })
                                .executes(context -> {
                                    ModConfig config = ModConfig.getConfig();
                                    String trigger = StringArgumentType.getString(context, "trigger");

                                    if(config.getNotificationList().contains(trigger.toLowerCase())) {
                                        config.getNotificationList().remove(trigger.toLowerCase());
                                        config.save();
                                        ((CommandSource) context.getSource()).sendFeedback(new StringTextComponent("[Minerino] Successfully removed " + trigger + " from notifications"), false);
                                        return 1;
                                    }

                                    ((CommandSource) context.getSource()).sendErrorMessage(new StringTextComponent("[Minerino] " +  trigger + " is not a notification"));
                                    return -1;
                                })));

    }

}
