package net.mooosik.minerino.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

// getString(ctx, "string")
// word()
// literal("foo")
import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.DISPATCHER;
import static net.minecraft.server.command.CommandManager.literal;
// argument("bar", word())
import static net.minecraft.server.command.CommandManager.argument;
// Import everything
import static net.minecraft.server.command.CommandManager.*;




public class CommandInitializer implements ModInitializer {

    public static String PREFIX = "minerino";

    @Override
    public void onInitialize() {
        CommandDispatcher<FabricClientCommandSource> dispatcher = DISPATCHER;


       dispatcher.register(literal("minerino")
               .then(MinerinoLogin.build()));

        dispatcher.register(literal("minerino")
                .then(MinerinoLogout.build()));

        dispatcher.register(literal("minerino")
                .then(MinerinoJoin.build()));

        dispatcher.register(literal("minerino")
                .then(MinerinoLeave.build()));

        dispatcher.register(literal("minerino")
                .then(MinerinoChat.build()));

        dispatcher.register(literal("minerino")
                .then(MinerinoAlert.build()));

        dispatcher.register(literal("minerino")
                .then(MinerinoIgnore.build()));

        dispatcher.register(literal("minerino")
                .then(MinerinoUnignore.build()));
    }
}
