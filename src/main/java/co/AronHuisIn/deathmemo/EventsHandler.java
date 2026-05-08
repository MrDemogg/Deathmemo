package co.AronHuisIn.deathmemo;

import co.AronHuisIn.deathmemo.Data.InventoriesDataManager;
import co.AronHuisIn.deathmemo.Data.InventorySnapshot;
import co.AronHuisIn.deathmemo.UI.SnapshotsHistoryScreen;
import co.AronHuisIn.deathmemo.UI.UITemplates;
import co.AronHuisIn.deathmemo.UI.UIKeys;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.joml.Vector3i;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@EventBusSubscriber(modid = Deathmemo.MODID)
public class EventsHandler {
    private static InventorySnapshot lastInventorySnapshot = null;
    private static final int SNAPSHOT_UPDATE_TICKS_PERIOD = 10;
    private static int ticksSinceLastSnapshotUpdate = SNAPSHOT_UPDATE_TICKS_PERIOD;

    private static String getPlaceName()
    {
        Minecraft mc = Minecraft.getInstance();

        if (mc.getCurrentServer() != null) return mc.getCurrentServer().name + "(" + mc.getCurrentServer().ip + ")";
        else if (mc.getSingleplayerServer() != null) return mc.getSingleplayerServer()
                .getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT)
                .getParent()
                .getFileName()
                .toString();

        return null;
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        Deathmemo.LOGGER.info("HELLO FROM CLIENT SETUP");
        Deathmemo.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

        Layers.add(
                Containers::verticalFlow,
                instance -> {
                    UIModel model = UIModelLoader.get(ResourceLocation.fromNamespaceAndPath(Deathmemo.MODID, UIKeys.SnapshotsHistory.SCREEN_ID));

                    if (model == null)
                    {
                        Deathmemo.LOGGER.error("Model not found");
                        return;
                    }

                    FlowLayout snapshotHistoryBtn = UITemplates.flatButtonTemplate(model, "");

                    Surface btnSurface = snapshotHistoryBtn.surface();
                    Surface btnSurfaceOutline = btnSurface.and(Surface.outline(0xFFFFFFFF));

                    snapshotHistoryBtn.sizing(Sizing.fixed(20), Sizing.fixed(20));

                    TextureComponent texture = Components.texture(
                            ResourceLocation.fromNamespaceAndPath(Deathmemo.MODID, "textures/deathlog.png"),
                            0, -1,
                            20,20,18,18
                    );

                    snapshotHistoryBtn.child(texture);

                    texture.mouseEnter().subscribe(() ->
                            snapshotHistoryBtn.surface(btnSurfaceOutline));
                    texture.mouseLeave().subscribe(() ->
                            snapshotHistoryBtn.surface(btnSurface));
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
    public static void onPlayerTickPost(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.getHealth() <= 0 || !player.level().isClientSide()) return;

        ticksSinceLastSnapshotUpdate++;

        if (ticksSinceLastSnapshotUpdate < SNAPSHOT_UPDATE_TICKS_PERIOD) return;

        ticksSinceLastSnapshotUpdate = 0;

        Inventory inventory = player.getInventory();
        if (inventory.isEmpty()
            || (lastInventorySnapshot != null
            && Utils.calculateInventoryHash(inventory) == Utils.calculateInventoryHash(lastInventorySnapshot.allStacks())))
            return;

        String place = getPlaceName();

        if (place == null) return;

        lastInventorySnapshot = new InventorySnapshot(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")),
                place,
                new Vector3i(player.getBlockX(), player.getBlockY(), player.getBlockZ()),
                inventory,
                InventoriesDataManager.getInstance().registryAccess
        );
    }

    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event)
    {
        if (event.getScreen() instanceof DeathScreen && lastInventorySnapshot != null) {
            InventoriesDataManager.getInstance().addSnapshot(lastInventorySnapshot);
            lastInventorySnapshot = null;
        }
    }
}
