package net.mooosik.minerino.command;

import com.mojang.brigadier.CommandDispatcher;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mooosik.minerino.Minerino;
import net.mooosik.minerino.config.ModConfig;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = "minerino", value = Dist.CLIENT)
public class CommandInitializer {

    public static CommandDispatcher<CommandSource> dispatcher;


    @SubscribeEvent
    public static void initialize(final RegisterCommandsEvent event) {
        CommandDispatcher eventDispatcher = event.getDispatcher();

        eventDispatcher.register(literal("minerino").then(MinerinoLogin.build()));
        eventDispatcher.register(literal("minerino").then(MinerinoLogout.build()));
        eventDispatcher.register(literal("minerino").then(MinerinoJoin.build()));
        eventDispatcher.register(literal("minerino").then(MinerinoLeave.build()));
        eventDispatcher.register(literal("minerino").then(MinerinoSwitch.build()));
        eventDispatcher.register(literal("minerino").then(MinerinoAlert.build()));
        eventDispatcher.register(literal("minerino").then(MinerinoIgnore.build()));
        eventDispatcher.register(literal("minerino").then(MinerinoUnignore.build()));
        eventDispatcher.register(literal("minerino").then(MinerinoHelp.build()));
        eventDispatcher.register(literal("minerino").then(MinerinoInfoMessage.build()));
        eventDispatcher.register(literal("minerino").then(MinerinoList.build()));

        eventDispatcher.register(MinerinoList.buildShort());


    }




    /**
     * Setup clientside dispatcher
     * Fixes commands not working on multiplayer servers
     * ISSUE: Doesnt suggest commands
     */
    public static void setup() {
        CommandInitializer.dispatcher = new CommandDispatcher<>();

        dispatcher.register(literal("minerino").then(MinerinoLogin.build()));
        dispatcher.register(literal("minerino").then(MinerinoLogout.build()));
        dispatcher.register(literal("minerino").then(MinerinoJoin.build()));
        dispatcher.register(literal("minerino").then(MinerinoLeave.build()));
        dispatcher.register(literal("minerino").then(MinerinoSwitch.build()));
        dispatcher.register(literal("minerino").then(MinerinoAlert.build()));
        dispatcher.register(literal("minerino").then(MinerinoIgnore.build()));
        dispatcher.register(literal("minerino").then(MinerinoUnignore.build()));
        dispatcher.register(literal("minerino").then(MinerinoHelp.build()));
        dispatcher.register(literal("minerino").then(MinerinoInfoMessage.build()));
        dispatcher.register(literal("minerino").then(MinerinoList.build()));

        dispatcher.register(MinerinoList.buildShort());

    }

    /**
     * Fabric specific info message
     */
    public static void sendInfoMessage() {
        Minecraft.getInstance().ingameGUI.sendChatMessage(ChatType.CHAT, new StringTextComponent("[Minerino] Hi! Minerino on Forge is also working in multiplayer!"), UUID.randomUUID());
        Minecraft.getInstance().ingameGUI.sendChatMessage(ChatType.CHAT, new StringTextComponent("[Minerino] However, for technical (and security) reasons, client-side commands are unkown to the server you're playing on."), UUID.randomUUID());
        Minecraft.getInstance().ingameGUI.sendChatMessage(ChatType.CHAT, new StringTextComponent("[Minerino] Because of this, using /minerino is going to show up as if its an incorrect command."), UUID.randomUUID());
        Minecraft.getInstance().ingameGUI.sendChatMessage(ChatType.CHAT, new StringTextComponent("[Minerino] The commands are still working! Use \"/minerino help\""), UUID.randomUUID());


        Minecraft.getInstance().ingameGUI.sendChatMessage(ChatType.CHAT, new StringTextComponent("[Minerino] Dont show this message again: ").appendSibling(
        new StringTextComponent("[ Click here ] ").mergeStyle(TextFormatting.BLUE)
                .modifyStyle((style) -> style.setClickEvent(
                        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/minerino infoMessage false")))), UUID.randomUUID());

    }

    /**
     * Catch /minerino command to process it on client-side instead of sending it to server
     * @param event
     */
    @SubscribeEvent
    public static void onClientChat(ClientChatEvent event) {

        if(ModConfig.getConfig().INFOFLAG) {
            ModConfig.getConfig().INFOFLAG = false;
            sendInfoMessage();
        }

        if (event.getMessage().startsWith("/minerino ") || event.getMessage().startsWith("/ml")) {
            Minerino.LOGGER.info("ONCLIENTCHAT EVENT");
            final String message = event.getMessage();
            event.setCanceled(true);

            Minecraft.getInstance().enqueue(() -> {
                CommandSource source = Minecraft.getInstance().player.getCommandSource();
                handleCommand(source, message);

                Minecraft.getInstance().ingameGUI.getChatGUI().addToSentMessages(message);
            });
        }
    }

    /**
     * For handling client side commands
     * @author blay09 on github
     * @param source
     * @param command
     * @return
     */
    private static int handleCommand(CommandSource source, String command) {
        StringReader reader = new StringReader(command);
        if (reader.canRead() && reader.peek() == '/') {
            reader.skip();
        }
        Minerino.LOGGER.info(dispatcher != null);
        Minerino.LOGGER.info(reader.getString());

        try {
            return dispatcher.execute(reader, source);
        } catch (CommandException e) {
            source.sendErrorMessage(e.getComponent());
            return 0;
        } catch (CommandSyntaxException e) {
            source.sendErrorMessage(TextComponentUtils.toTextComponent(e.getRawMessage()));
            if (e.getInput() != null && e.getCursor() >= 0) {
                int pos = Math.min(e.getInput().length(), e.getCursor());
                IFormattableTextComponent textComponent = (new StringTextComponent("")).mergeStyle(TextFormatting.GRAY).modifyStyle((style) -> style.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)));
                if (pos > 10) {
                    textComponent.appendString("...");
                }

                textComponent.appendString(e.getInput().substring(Math.max(0, pos - 10), pos));
                if (pos < e.getInput().length()) {
                    ITextComponent errorComponent = (new StringTextComponent(e.getInput().substring(pos))).mergeStyle(TextFormatting.RED, TextFormatting.UNDERLINE);
                    textComponent.appendSibling(errorComponent);
                }

                textComponent.appendSibling((new TranslationTextComponent("command.context.here")).mergeStyle(TextFormatting.RED, TextFormatting.ITALIC));
                source.sendErrorMessage(textComponent);
            }
        } catch (Exception e) {
            IFormattableTextComponent textComponent = new StringTextComponent(e.getMessage() == null ? e.getClass().getName() : e.getMessage());
                System.err.println("Command exception: {}" + command);
                e.printStackTrace();
                StackTraceElement[] stackTrace = e.getStackTrace();
                for (int i = 0; i < Math.min(stackTrace.length, 3); ++i) {
                    textComponent.appendString("\n\n").appendString(stackTrace[i].getMethodName()).appendString("\n ").appendString(stackTrace[i].getFileName()).appendString(":").appendString(String.valueOf(stackTrace[i].getLineNumber()));
                }


            source.sendErrorMessage((new TranslationTextComponent("command.failed")).modifyStyle((style) -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textComponent))));
            if (SharedConstants.developmentMode) {
                source.sendErrorMessage(new StringTextComponent(Util.getMessage(e)));
                System.err.println("'" + command + "' threw an exception");
                e.printStackTrace();
            }

            return 0;
        }

        return 0;
    }



}
