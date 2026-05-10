package co.AronHuisIn.deathmemo.events;

import co.AronHuisIn.deathmemo.Data.InventoriesDataManager;
import co.AronHuisIn.deathmemo.Data.InventorySnapshot;
import co.AronHuisIn.deathmemo.Deathmemo;
import co.AronHuisIn.deathmemo.UI.Screens.SnapshotsHistoryScreen;
import co.AronHuisIn.deathmemo.UI.Toasts.ColoredNotificationToast;
import co.AronHuisIn.deathmemo.UI.UIKeys;
import co.AronHuisIn.deathmemo.UI.templates.FlatButtonTemplate;
import co.AronHuisIn.deathmemo.packets.RequestResponsePayload;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.layers.Layer;
import io.wispforest.owo.ui.layers.Layers;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIModelLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.joml.Vector3i;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@EventBusSubscriber(modid = Deathmemo.MODID, value= Dist.CLIENT)
public class ClientEventsHandler {

    private static String getPlaceName()
    {
        Minecraft mc = Minecraft.getInstance();

        if (mc.getCurrentServer() != null) return "server_" + mc.getCurrentServer().ip.replace(":", "_").replace(".", "_");
        else if (mc.getSingleplayerServer() != null) return mc.getSingleplayerServer()
                .getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT)
                .getParent()
                .getFileName()
                .toString();

        return null;
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event)
    {
        PayloadRegistrar registrar = event.registrar("1").optional();

        registrar.playToClient(
                RequestResponsePayload.TYPE,
                RequestResponsePayload.STREAM_CODEC,
                ClientEventsHandler::handleRequestItemResponse
        );
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        Layers.add(
                Containers::verticalFlow,
                instance -> {
                    UIModel model = UIModelLoader.get(ResourceLocation.fromNamespaceAndPath(Deathmemo.MODID, UIKeys.SnapshotsHistory.SCREEN_ID));

                    if (model == null)
                    {
                        Deathmemo.LOGGER.error("Model not found");
                        return;
                    }

                    FlowLayout snapshotHistoryBtn = FlatButtonTemplate.create(model, "", Surface.outline(0xFFFFFFFF));

                    snapshotHistoryBtn.sizing(Sizing.fixed(20), Sizing.fixed(20));

                    TextureComponent texture = Components.texture(
                            ResourceLocation.fromNamespaceAndPath(Deathmemo.MODID, "textures/deathlog.png"),
                            0, -1,
                            20,20,18,18
                    );

                    snapshotHistoryBtn.child(texture);
                    snapshotHistoryBtn.mouseDown().subscribe((x, y, mouse) ->
                    {
                        Minecraft.getInstance().setScreen(new SnapshotsHistoryScreen());
                        return true;
                    });

                    instance.adapter.rootComponent.child(snapshotHistoryBtn);

                    instance.alignComponentToWidget(
                            widget -> widget instanceof Button button
                                    && button.getMessage().getString().equals(Component.translatable("gui.stats").getString()),
                            Layer.Instance.AnchorSide.RIGHT,
                            0.5f,
                            snapshotHistoryBtn
                    );
                },
                PauseScreen.class);
    }

    @SubscribeEvent
    public static void onClientLoggingIn(ClientPlayerNetworkEvent.LoggingIn event)
    {
        InventoriesDataManager.getInstance().loadSnapshots(getPlaceName(), event.getPlayer().registryAccess());
    }

    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event)
    {
        if (event.getScreen() instanceof DeathScreen) {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;

            if (player == null || (player.getInventory().isEmpty() && player.totalExperience == 0)) return;

            InventoriesDataManager.getInstance().addSnapshot(
                    new InventorySnapshot(
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")),
                            getPlaceName(),
                            new Vector3i(player.getBlockX(), player.getBlockY(), player.getBlockZ()),
                            player.level().dimension().location().toString(),
                            player.totalExperience,
                            player.getInventory(),
                            InventoriesDataManager.getInstance().registryAccess
                    )
            );
        }
    }

    public static void handleRequestItemResponse(final RequestResponsePayload payload, final IPayloadContext context)
    {
        context.enqueueWork(() -> Minecraft.getInstance().getToasts().addToast(
                new ColoredNotificationToast(
                        payload.message(),
                        0xFF1D1D1D,
                        payload.approved() ? 0xFF80FF80 : 0xFFFF8080,
                        2000
                )
        ));
    }
}
