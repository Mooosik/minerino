package net.mooosik.minerino.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

// getString(ctx, "string")
// word()
// literal("foo")
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.DISPATCHER;
import static net.minecraft.server.command.CommandManager.literal;
// argument("bar", word())
// Import everything


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
                .then(MinerinoSwitch.build()));

        dispatcher.register(literal("minerino")
                .then(MinerinoAlert.build()));

        dispatcher.register(literal("minerino")
                .then(MinerinoIgnore.build()));

        dispatcher.register(literal("minerino")
                .then(MinerinoUnignore.build()));
    }
}
