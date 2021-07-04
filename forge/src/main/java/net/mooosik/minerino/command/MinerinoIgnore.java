package net.mooosik.minerino.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.mooosik.minerino.config.ModConfig;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class MinerinoIgnore {
    /**
     * Add a user to the ignore list
     * @return
     */
    public static LiteralArgumentBuilder build() {

        return literal("ignore")
                .then(argument("username", StringArgumentType.word())
                        .executes(context -> {       //if only login
                            ModConfig config = ModConfig.getConfig();
                            String trigger = StringArgumentType.getString(context, "username");
                            if(config.getIgnoreList().contains(trigger.toLowerCase())) {
                                ((CommandSource) context.getSource()).sendErrorMessage(new StringTextComponent("[Minerino] " +  trigger + " is already on the ignore-list"));
                                return -1;
                            }

                            config.getIgnoreList().add(trigger.toLowerCase());
                            config.save();
                            ((CommandSource) context.getSource()).sendFeedback(new StringTextComponent("[Minerino] Successfully added " + trigger + " to the ignore-list"), false);
                            return 1;

                        }));
    }
}
