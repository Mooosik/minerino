package net.mooosik.minerino.command;

import com.mojang.brigadier.CommandDispatcher;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class CommandInitializer {

    @SubscribeEvent
    public static void initialize(final RegisterCommandsEvent event) {

        CommandDispatcher dispatcher = event.getDispatcher();

        dispatcher.register(literal("minerino").then(MinerinoLogin.build()));
        dispatcher.register(literal("minerino").then(MinerinoLogout.build()));
        dispatcher.register(literal("minerino").then(MinerinoJoin.build()));
        dispatcher.register(literal("minerino").then(MinerinoLeave.build()));
        dispatcher.register(literal("minerino").then(MinerinoSwitch.build()));
        dispatcher.register(literal("minerino").then(MinerinoAlert.build()));
        dispatcher.register(literal("minerino").then(MinerinoIgnore.build()));
        dispatcher.register(literal("minerino").then(MinerinoUnignore.build()));

    }
}
