package co.AronHuisIn.deathmemo.UI;

import co.AronHuisIn.deathmemo.Data.InventoriesDataManager;
import co.AronHuisIn.deathmemo.Data.InventorySnapshot;
import co.AronHuisIn.deathmemo.Deathmemo;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

public class SnapshotsHistoryScreen extends BaseUIModelScreen<FlowLayout> {
    private final List<ItemComponent> armorSlots = new ArrayList<>();
    private final List<ItemComponent> itemSlots = new ArrayList<>();
    private ItemComponent offhandSlot;

    private FlowLayout openedDate;
    private LabelComponent posXLabel;
    private LabelComponent posYLabel;
    private LabelComponent posZLabel;
    private LabelComponent scrollEmptyLabel;

    public SnapshotsHistoryScreen() {
        super(FlowLayout.class, ResourceLocation.fromNamespaceAndPath(Deathmemo.MODID, UIKeys.SnapshotsHistory.SCREEN_ID));
    }

    @Override
    protected void build(FlowLayout rootLayout) {
        scrollEmptyLabel = rootLayout.childById(LabelComponent.class, UIKeys.SnapshotsHistory.SCROLL_EMPTY);

        FlowLayout closeBtn = UITemplates.flatButtonTemplate(this.model, Component.translatable("gui.deathmemo.exit").toString());
        closeBtn.surface(Surface.BLANK);
        closeBtn.sizing(Sizing.fixed(60), Sizing.fixed(20));
        closeBtn.childById(LabelComponent.class, UIKeys.SnapshotsHistory.Examples.FlatButton.BUTTON_TEXT).text(Component.translatable("gui.deathmemo.exit"));
        closeBtn.mouseEnter().subscribe(() -> closeBtn.surface(Surface.outline(0xFFFFFFFF)));
        closeBtn.mouseLeave().subscribe(() -> closeBtn.surface(Surface.BLANK));
        closeBtn.mouseDown().subscribe((x,y,button) -> {
            Minecraft.getInstance().setScreen(new PauseScreen(true));
            return true;
        });

        rootLayout.childById(FlowLayout.class, UIKeys.SnapshotsHistory.CLOSE_BTN)
                .child(closeBtn);

        List<InventorySnapshot> snapshots = InventoriesDataManager.getInstance().getSnapshots();

        FlowLayout dateTimesScroll = rootLayout.childById(FlowLayout.class, UIKeys.SnapshotsHistory.DATE_TIMES);

        for (String dateTime : snapshots.stream().map(snapshot -> snapshot.dateTime).toList())
        {
            String date = dateTime.split("_")[0];
            String time = dateTime.split("_")[1];
            FlowLayout flatButton = UITemplates.flatButtonTemplate(this.model, "");

            FlowLayout horizontalLayout = Containers.horizontalFlow(Sizing.fill(), Sizing.fill());
            horizontalLayout.positioning(Positioning.absolute(0,0));
            flatButton.child(horizontalLayout);

            FlowLayout verticalLayout = Containers.verticalFlow(Sizing.fill(80), Sizing.fill());
            verticalLayout.padding(Insets.of(5));
            horizontalLayout.child(verticalLayout);
            verticalLayout.child(Components.label(Component.literal(date)));
            verticalLayout.child(Components.label(Component.literal(time)));

            flatButton.sizing(Sizing.fill(100), Sizing.fixed(30))
                    .margins(Insets.top(10))
                    .mouseDown().subscribe((x, y, button) -> {
                        selectDate(flatButton, dateTime);
                        return true;
                    });

            FlowLayout deleteButton = UITemplates.flatButtonTemplate(this.model, "");
            deleteButton.sizing(Sizing.fixed(30), Sizing.fixed(30));
            deleteButton.alignment(HorizontalAlignment.RIGHT, deleteButton.verticalAlignment());

            Surface deleteBtnSurface = deleteButton.surface();
            Surface deleteBtnSurfaceOutline = deleteBtnSurface.and(Surface.outline(0xFFfc7e7e));

            TextureComponent deleteTexture = Components.texture(
                    ResourceLocation.fromNamespaceAndPath(Deathmemo.MODID, "textures/close.png"),
                    0, 0,
                    30, 30, 30, 30
            );

            deleteButton.child(deleteTexture);
            deleteTexture.mouseEnter().subscribe(() -> deleteButton.surface(deleteBtnSurfaceOutline));
            deleteTexture.mouseLeave().subscribe(() -> deleteButton.surface(deleteBtnSurface));
            deleteButton.mouseDown().subscribe((x,y,button) -> {
                flatButton.remove();
                InventoriesDataManager.getInstance().removeSnapshot(dateTime);
                updateSlots(null);
                return true;
            });

            horizontalLayout.child(deleteButton);

            dateTimesScroll.child(flatButton);
        }

        posXLabel = rootLayout.childById(LabelComponent.class, UIKeys.SnapshotsHistory.POS_X);
        posYLabel = rootLayout.childById(LabelComponent.class, UIKeys.SnapshotsHistory.POS_Y);
        posZLabel = rootLayout.childById(LabelComponent.class, UIKeys.SnapshotsHistory.POS_Z);

        GridLayout armorGrid = rootLayout.childById(GridLayout.class, UIKeys.SnapshotsHistory.ARMOR);
        GridLayout itemsGrid = rootLayout.childById(GridLayout.class, UIKeys.SnapshotsHistory.ITEMS);
        offhandSlot = rootLayout
                .childById(FlowLayout.class, UIKeys.SnapshotsHistory.OFFHAND)
                .childById(ItemComponent.class, UIKeys.SnapshotsHistory.Examples.ItemSlot.ITEM);

        for (int c = 0; c < 4; c++) {
            FlowLayout slotLayout = UITemplates.slotTemplate(this.model);
            armorSlots.add(slotLayout.childById(ItemComponent.class, UIKeys.SnapshotsHistory.Examples.ItemSlot.ITEM));
            armorGrid.child(slotLayout, 0, c);
        }

        for (int row = 0; row < 4; row++) {
            for (int col = 8; col >= 0; col--) {
                FlowLayout slotLayout = UITemplates.slotTemplate(this.model);
                itemSlots.add(slotLayout.childById(ItemComponent.class, UIKeys.SnapshotsHistory.Examples.ItemSlot.ITEM));
                itemsGrid.child(slotLayout, row, col);
            }
        }

        updateSlots(null);
    }

    private void selectDate(FlowLayout dateBtn, String dateTime)
    {
        if (openedDate != null) {
            if (openedDate.equals(dateBtn)) return;
            openedDate.surface(Surface.flat(0xFF2A2A2A));
        }
        openedDate = dateBtn;
        openedDate.surface(Surface.flat(0xFF2A2A2A).and(Surface.outline(0xFFFFFFFF)));

        InventorySnapshot snapshot = InventoriesDataManager.getInstance().getSnapshots()
                .stream()
                .filter(s -> s.dateTime.equals(dateTime))
                .findFirst().orElse(null);

        updateSlots(snapshot);
    }

    private void setPosLabels(Vector3i pos)
    {
        String x = pos != null ? String.valueOf(pos.x) : "-";
        String y = pos != null ? String.valueOf(pos.y) : "-";
        String z = pos != null ? String.valueOf(pos.z) : "-";

        posXLabel.text(Component.literal("X: ").append(Component.literal(x).withStyle(style -> style.withColor(0xFF8080))));
        posYLabel.text(Component.literal("Y: ").append(Component.literal(y).withStyle(style -> style.withColor(0x80FF80))));
        posZLabel.text(Component.literal("Z: ").append(Component.literal(z).withStyle(style -> style.withColor(0x8080FF))));
    }

    private void updateSlots(InventorySnapshot snapshot)
    {
        if (snapshot == null)
        {
            if (InventoriesDataManager.getInstance().getSnapshots().isEmpty())
                scrollEmptyLabel.text(Component.translatable("gui.deathmemo.empty"));

            setPosLabels(null);
            offhandSlot.stack(ItemStack.EMPTY);
            for (ItemComponent slot : armorSlots) slot.stack(ItemStack.EMPTY);
            for (ItemComponent slot : itemSlots) slot.stack(ItemStack.EMPTY);
            return;
        }

        scrollEmptyLabel.text(Component.empty());

        setPosLabels(snapshot.pos);

        offhandSlot.stack(snapshot.offhand.isEmpty() ? ItemStack.EMPTY : snapshot.offhand.getFirst());

        for (int i = 0; i < armorSlots.size() && i < snapshot.armor.size(); i++) {
            armorSlots.get(i).stack(snapshot.armor.get(i));
        }

        for (int i = 0; i < itemSlots.size() && i < snapshot.items.size(); i++) {
            itemSlots.get(i).stack(snapshot.items.get(snapshot.items.size() - i - 1));
        }
    }
}
