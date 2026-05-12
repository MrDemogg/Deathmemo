package co.AronHuisIn.deathmemo.UI.templates;

import co.AronHuisIn.deathmemo.UI.UIKeys;
import co.AronHuisIn.deathmemo.Utils;
import co.AronHuisIn.deathmemo.packets.RequestItemPayload;
import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.parsing.UIModel;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
//? if <1.21.7 {
import net.neoforged.neoforge.network.PacketDistributor;
//?} else
//import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.Map;

public final class SlotTemplate {
    public static FlowLayout create(UIModel model) {
        FlowLayout slot =  model.expandTemplate(
                FlowLayout.class,
                Utils.getOwoExampleName(
                        UIKeys.SnapshotsHistory.SCREEN_ID,
                        UIKeys.SnapshotsHistory.Templates.ItemSlot.TEMPLATE_NAME
                ),
                Map.of()
        );

        ItemComponent item = slot.childById(ItemComponent.class, UIKeys.SnapshotsHistory.Templates.ItemSlot.ITEM);

        item.mouseEnter().subscribe(() -> item.id(UIKeys.SnapshotsHistory.Templates.ItemSlot.HOVERED_ITEM));
        item.mouseLeave().subscribe(() -> item.id(UIKeys.SnapshotsHistory.Templates.ItemSlot.ITEM));
        //? if <1.21.9 {
        slot.mouseDown().subscribe((x,y,mouse) -> {
            handleSlotClick(mouse, item.stack());
            return true;
        });
        //?} else {
        /*slot.mouseDown().subscribe((buttonEvent, doubled) -> {
            handleSlotClick(buttonEvent.button(), item.stack());
            return true;
        });
        *///?}

        return slot;
    }

    private static void handleSlotClick(double mouse, ItemStack stack)
    {
        if (Minecraft.getInstance().getConnection() == null) return;

        if (mouse == 0 && !stack.equals(ItemStack.EMPTY))
        {
            try {
                //? if <1.21.7 {
                PacketDistributor.sendToServer(new RequestItemPayload(stack));
                //?} else
                 //ClientPacketDistributor.sendToServer(new RequestItemPayload(stack));
            } catch (UnsupportedOperationException e) {
                System.err.println("Payload не зарегистрирован: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Сетевая ошибка: " + e.getMessage());
            }
        }
    }
}
