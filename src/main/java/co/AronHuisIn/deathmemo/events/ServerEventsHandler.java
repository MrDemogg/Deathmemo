package co.AronHuisIn.deathmemo.events;

import co.AronHuisIn.deathmemo.Deathmemo;
import co.AronHuisIn.deathmemo.packets.RequestItemPayload;
import co.AronHuisIn.deathmemo.packets.RequestItemResponsePayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Deathmemo.MODID)
public class ServerEventsHandler {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event)
    {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                RequestItemPayload.TYPE,
                RequestItemPayload.STREAM_CODEC,
                ServerEventsHandler::handleRequestItem
        );

        registrar.playToClient(
                RequestItemResponsePayload.TYPE,
                RequestItemResponsePayload.STREAM_CODEC,
                ClientEventsHandler::handleRequestItemResponse
        );
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
                    new RequestItemResponsePayload(
                            true,
                            Component.translatable(
                                    "gui.deathmemo.server.give_approve",
                                    stackName,
                                    stackCount
                                ).getString()
                    ));
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Ошибка обработки запроса: " + e.getMessage()));
            return null;
        });
    }
}
