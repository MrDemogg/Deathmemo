package co.AronHuisIn.deathmemo.events;

import co.AronHuisIn.deathmemo.Deathmemo;
import co.AronHuisIn.deathmemo.packets.CommandRequestPayload;
import co.AronHuisIn.deathmemo.packets.RequestItemPayload;
import co.AronHuisIn.deathmemo.packets.RequestResponsePayload;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Deathmemo.MODID
//? if =1.21.2 {
/*,bus = EventBusSubscriber.Bus.MOD
*///?}
)
public class GeneralEventsHandler {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event)
    {
        PayloadRegistrar registrar = event.registrar("1").optional();

        registrar.playToServer(
                RequestItemPayload.TYPE,
                RequestItemPayload.STREAM_CODEC,
                GeneralEventsHandler::handleRequestItem
        );

        registrar.playToServer(
                CommandRequestPayload.TYPE,
                CommandRequestPayload.STREAM_CODEC,
                GeneralEventsHandler::handleCommandRequest
        );
    }

    public static void handleCommandRequest(final CommandRequestPayload payload, final IPayloadContext context)
    {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            //? if <=1.21.5 {
            ServerLevel level = player.serverLevel();
            //?} else
            //ServerLevel level = player.level();
            MinecraftServer server = level.getServer();
            Commands commands = server.getCommands();
            CommandSourceStack source = player.createCommandSourceStack();

            ParseResults<CommandSourceStack> parseResults = commands.getDispatcher().parse(payload.command(), source);

            try {
                Commands.validateParseResults(parseResults);

                commands.performPrefixedCommand(source, payload.command());

                PacketDistributor.sendToPlayer(player,
                        new RequestResponsePayload(true, Component.translatable("gui.deathmemo.server.command_approve")));

            } catch (CommandSyntaxException e) {
                PacketDistributor.sendToPlayer(player,
                        new RequestResponsePayload(false, Component.translatable("gui.deathmemo.server.no_perm")));
            }
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Ошибка обработки запроса: " + e.getMessage()));
            return null;
        });
    }

    public static void handleRequestItem(final RequestItemPayload payload, final IPayloadContext context)
    {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            if (!player.hasPermissions(2)) return;

            ItemStack stack = payload.item();
            Component stackName = stack.getDisplayName();
            int stackCount = stack.getCount();

            player.getInventory().add(stack);

            PacketDistributor.sendToPlayer(
                    player,
                    new RequestResponsePayload(
                            true,
                            Component.translatable(
                                    "gui.deathmemo.server.give_approve",
                                    stackName,
                                    stackCount
                                )
                    ));
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Ошибка обработки запроса: " + e.getMessage()));
            return null;
        });
    }
}
