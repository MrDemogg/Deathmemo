//? if =1.21.2 {
/*package co.AronHuisIn.deathmemo.events;

import co.AronHuisIn.deathmemo.Data.InventoriesDataManager;
import co.AronHuisIn.deathmemo.Data.InventorySnapshot;
import co.AronHuisIn.deathmemo.Deathmemo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.joml.Vector3i;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@EventBusSubscriber(modid = Deathmemo.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class ClientGameEventsHandler {
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
}

*///?}