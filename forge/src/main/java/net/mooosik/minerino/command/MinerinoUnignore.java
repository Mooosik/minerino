package net.mooosik.minerino.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.mooosik.minerino.config.ModConfig;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

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
                                ((CommandSource) context.getSource()).sendFeedback(new StringTextComponent("[Minerino] Successfully removed " + trigger + " from the ignore-list"), false);
                                return 1;
                            }

                            ((CommandSource) context.getSource()).sendErrorMessage(new StringTextComponent("[Minerino] " +  trigger + " is not on the ignore-list"));
                            return -1;
                        }));

    }
}
