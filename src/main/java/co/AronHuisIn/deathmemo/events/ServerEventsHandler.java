package co.AronHuisIn.deathmemo.events;

import co.AronHuisIn.deathmemo.Deathmemo;
import co.AronHuisIn.deathmemo.packets.RequestResponsePayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Deathmemo.MODID, value = Dist.DEDICATED_SERVER)
public class ServerEventsHandler {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event)
    {
        PayloadRegistrar registrar = event.registrar("1").optional();

        // Заглушка, просто чтобы сервер знал что такие запросы отправлять можно
        registrar.playToClient(
                RequestResponsePayload.TYPE,
                RequestResponsePayload.STREAM_CODEC,
                (payload, context) -> {}
        );
    }
}
