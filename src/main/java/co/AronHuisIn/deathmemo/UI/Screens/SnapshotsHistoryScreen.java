package co.AronHuisIn.deathmemo.UI.Screens;

import co.AronHuisIn.deathmemo.Data.InventoriesDataManager;
import co.AronHuisIn.deathmemo.Data.InventorySnapshot;
import co.AronHuisIn.deathmemo.Deathmemo;
import co.AronHuisIn.deathmemo.UI.UIKeys;
import co.AronHuisIn.deathmemo.UI.templates.FlatButtonTemplate;
import co.AronHuisIn.deathmemo.UI.templates.PosTemplate;
import co.AronHuisIn.deathmemo.UI.templates.SlotTemplate;
import co.AronHuisIn.deathmemo.UI.templates.SnapshotContainerTemplate;
import co.AronHuisIn.deathmemo.packets.CommandRequestPayload;
import co.AronHuisIn.deathmemo.packets.RequestItemPayload;
import co.AronHuisIn.deathmemo.packets.RequestResponsePayload;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SnapshotsHistoryScreen extends BaseUIModelScreen<FlowLayout> {
    private final List<ItemComponent> armorSlots = new ArrayList<>();
    private final List<ItemComponent> itemSlots = new ArrayList<>();
    private ItemComponent offhandSlot;

    private final List<ItemComponent> allSlots = new ArrayList<>();
    private InventorySnapshot currentSnapshot;

    private FlowLayout openedDate;
    private FlowLayout infoContainer;
    private FlowLayout snapshotContainer;
    private FlowLayout snapshotWindow;
    private FlowLayout posBtn;
    private FlowLayout xpBtn;
    private LabelComponent posXLabel;
    private LabelComponent posYLabel;
    private LabelComponent posZLabel;
    private LabelComponent scrollEmptyLabel;
    private LabelComponent dimensionNamespaceLabel;
    private LabelComponent dimensionNameLabel;
    private LabelComponent xpLabel;

    private FlowLayout rootLayout;

    public SnapshotsHistoryScreen() {
        super(FlowLayout.class, ResourceLocation.fromNamespaceAndPath(Deathmemo.MODID, UIKeys.SnapshotsHistory.SCREEN_ID));
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        for (ItemComponent item : allSlots)
        {
            if (Objects.equals(item.id(), UIKeys.SnapshotsHistory.Templates.ItemSlot.HOVERED_ITEM))
            {
                ItemStack stack = item.stack();
                if (!stack.isEmpty()) context.renderTooltip(this.font, stack, mouseX, mouseY);
                break;
            }
        }
    }

    private void setupExit()
    {
        FlowLayout closeBtn = FlatButtonTemplate.create(
                this.model,
                Component.translatable("gui.deathmemo.exit").toString(),
                Surface.BLANK,
                Surface.outline(0xFFFFFFFF)
        );
        closeBtn.surface(Surface.BLANK);
        closeBtn.sizing(Sizing.fixed(60), Sizing.fixed(20));
        closeBtn.childById(LabelComponent.class, UIKeys.SnapshotsHistory.Templates.FlatButton.BUTTON_TEXT).text(Component.translatable("gui.deathmemo.exit"));
        closeBtn.mouseDown().subscribe((x,y,button) -> {
            Minecraft.getInstance().setScreen(new PauseScreen(true));
            return true;
        });

        rootLayout.childById(FlowLayout.class, UIKeys.SnapshotsHistory.CLOSE_BTN)
                .child(closeBtn);
    }

    private void setupSnapshotWindow()
    {
        snapshotWindow = rootLayout.childById(FlowLayout.class, UIKeys.SnapshotsHistory.SNAPSHOT_WINDOW);
        snapshotContainer = SnapshotContainerTemplate.create(this.model);
    }

    private void setupSnapshotsScroll()
    {
        scrollEmptyLabel = rootLayout.childById(LabelComponent.class, UIKeys.SnapshotsHistory.SCROLL_EMPTY);

        List<InventorySnapshot> snapshots = InventoriesDataManager.getInstance().getSnapshots();

        FlowLayout dateTimesScroll = rootLayout.childById(FlowLayout.class, UIKeys.SnapshotsHistory.DATE_TIMES);

        for (String dateTime : snapshots.stream().map(snapshot -> snapshot.dateTime).toList().reversed())
        {
            String date = dateTime.split("_")[0];
            String time = dateTime.split("_")[1];
            FlowLayout flatButton = FlatButtonTemplate.create(this.model, "");

            FlowLayout horizontalLayout = Containers.horizontalFlow(Sizing.fill(), Sizing.fill());
            horizontalLayout.positioning(Positioning.absolute(0,0));
            flatButton.child(horizontalLayout);

            FlowLayout verticalLayout = Containers.verticalFlow(Sizing.expand(100), Sizing.fill());
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

            FlowLayout deleteButton = FlatButtonTemplate.create(this.model, "", Surface.outline(0xFFfc7e7e));
            deleteButton.sizing(Sizing.fixed(30), Sizing.fixed(30));
            deleteButton.alignment(HorizontalAlignment.RIGHT, deleteButton.verticalAlignment());

            TextureComponent deleteTexture = Components.texture(
                    ResourceLocation.fromNamespaceAndPath(Deathmemo.MODID, "textures/close.png"),
                    0, 0,
                    30, 30, 30, 30
            );

            deleteButton.child(deleteTexture);
            deleteButton.mouseDown().subscribe((x,y,button) -> {
                flatButton.remove();
                InventoriesDataManager.getInstance().removeSnapshot(dateTime);
                updateSlots(null);
                return true;
            });

            horizontalLayout.child(deleteButton);

            dateTimesScroll.child(flatButton);
        }
    }

    private void setupInfo()
    {
        FlowLayout posContainer = PosTemplate.create(this.model);
        posXLabel = posContainer.childById(LabelComponent.class, UIKeys.SnapshotsHistory.Templates.Pos.POS_X);
        posYLabel = posContainer.childById(LabelComponent.class, UIKeys.SnapshotsHistory.Templates.Pos.POS_Y);
        posZLabel = posContainer.childById(LabelComponent.class, UIKeys.SnapshotsHistory.Templates.Pos.POS_Z);

        dimensionNamespaceLabel = snapshotContainer.childById(LabelComponent.class, UIKeys.SnapshotsHistory.DIMENSION_NAMESPACE);
        dimensionNameLabel = snapshotContainer.childById(LabelComponent.class, UIKeys.SnapshotsHistory.DIMENSION_NAME);
        xpLabel = Components.label(Component.empty());

        if (posBtn != null) posBtn.child(posContainer);
        else {
            posContainer.margins(Insets.top(10));
            infoContainer.child(posContainer);
        }
        if (xpBtn != null) xpBtn.child(xpLabel);
        else {
            xpLabel.margins(Insets.top(10));
            infoContainer.child(xpLabel);
        }
    }

    private void setupOperatorTools()
    {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (
                connection == null
                        || !connection.hasChannel(CommandRequestPayload.TYPE)
                        || !connection.hasChannel(RequestItemPayload.TYPE)
                        || !connection.hasChannel(RequestResponsePayload.TYPE)) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && !player.hasPermissions(2)) return;

        FlowLayout recoveryBtn = FlatButtonTemplate.create(
                this.model,
                Component.translatable("gui.deathmemo.recovery").getString(),
                Surface.outline(0xFFFFFFFF)
        );
        recoveryBtn.sizing(Sizing.fixed(160), Sizing.fixed(10));
        recoveryBtn.verticalAlignment(VerticalAlignment.BOTTOM);
        recoveryBtn.horizontalAlignment(HorizontalAlignment.CENTER);
        recoveryBtn.mouseDown().subscribe((x,y,mouse) -> {
            handleRecovery();
            return true;
        });
        snapshotContainer.childById(FlowLayout.class, UIKeys.SnapshotsHistory.ITEMS_CONTAINER).child(recoveryBtn);

        posBtn = FlatButtonTemplate.create(this.model, "", UIKeys.SnapshotsHistory.POS_BTN, Surface.BLANK, Surface.outline(0xFFFFFFFF));
        posBtn.sizing(Sizing.content(1));
        posBtn.mouseDown().subscribe((x,y,mouse) -> {
            handleTP();
            return true;
        });

        xpBtn = FlatButtonTemplate.create(this.model, "", UIKeys.SnapshotsHistory.XP_BTN, Surface.BLANK, Surface.outline(0xFFFFFFFF));
        xpBtn.margins(Insets.top(10));
        xpBtn.sizing(Sizing.content(1));
        xpBtn.mouseDown().subscribe((x,y,mouse) -> {
            handleXP();
            return true;
        });

        infoContainer.child(posBtn);
        infoContainer.child(xpBtn);
    }

    private void handleTP()
    {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || currentSnapshot == null) return;
        String playerName = player.getGameProfile().getName();
        PacketDistributor.sendToServer(
                new CommandRequestPayload("execute in " + currentSnapshot.dimension + " run tp " + playerName
                        + " " + currentSnapshot.pos.x
                        + " " + currentSnapshot.pos.y
                        + " " + currentSnapshot.pos.z
                )
        );
    }

    private void handleXP()
    {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || currentSnapshot == null) return;
        String playerName = player.getGameProfile().getName();
        PacketDistributor.sendToServer(
                new CommandRequestPayload("xp add " + playerName + " " + currentSnapshot.xp + " points")
        );
    }

    private void handleRecovery()
    {
        if (currentSnapshot == null) return;
        for (ItemStack stack : currentSnapshot.allStacks())
            if (!stack.isEmpty()) PacketDistributor.sendToServer(
                    new RequestItemPayload(stack)
            );
    }

    private void setupSlots()
    {
        GridLayout armorGrid = snapshotContainer.childById(GridLayout.class, UIKeys.SnapshotsHistory.ARMOR);
        GridLayout itemsGrid = snapshotContainer.childById(GridLayout.class, UIKeys.SnapshotsHistory.ITEMS);

        FlowLayout offhand = SlotTemplate.create(this.model);
        snapshotContainer
                .childById(FlowLayout.class, UIKeys.SnapshotsHistory.OFFHAND)
                .child(offhand);

        offhandSlot = offhand.childById(ItemComponent.class, UIKeys.SnapshotsHistory.Templates.ItemSlot.ITEM);

        for (int c = 0; c < 4; c++) {
            FlowLayout slotLayout = SlotTemplate.create(this.model);
            armorSlots.add(slotLayout.childById(ItemComponent.class, UIKeys.SnapshotsHistory.Templates.ItemSlot.ITEM));
            armorGrid.child(slotLayout, 0, c);
        }

        for (int row = 0; row < 4; row++) {
            for (int col = 8; col >= 0; col--) {
                FlowLayout slotLayout = SlotTemplate.create(this.model);
                ItemComponent itemComponent = slotLayout.childById(ItemComponent.class, UIKeys.SnapshotsHistory.Templates.ItemSlot.ITEM);
                itemSlots.add(itemComponent);
                itemsGrid.child(slotLayout, row, col);
            }
        }

        allSlots.addAll(armorSlots);
        allSlots.addAll(itemSlots);
        allSlots.add(offhandSlot);
    }

    @Override
    protected void build(FlowLayout rootLayout) {
        this.rootLayout = rootLayout;

        setupExit();
        setupSnapshotWindow();
        setupSnapshotsScroll();
        infoContainer = snapshotContainer.childById(FlowLayout.class, UIKeys.SnapshotsHistory.INFO_CONTAINER);
        setupOperatorTools();
        setupInfo();
        setupSlots();

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

    private void setPosLabels(@Nullable Vector3i pos)
    {
        String x = pos != null ? String.valueOf(pos.x) : "-";
        String y = pos != null ? String.valueOf(pos.y) : "-";
        String z = pos != null ? String.valueOf(pos.z) : "-";

        posXLabel.text(Component.literal("X: ").append(Component.literal(x).withStyle(style -> style.withColor(0xFF8080))));
        posYLabel.text(Component.literal("Y: ").append(Component.literal(y).withStyle(style -> style.withColor(0x80FF80))));
        posZLabel.text(Component.literal("Z: ").append(Component.literal(z).withStyle(style -> style.withColor(0x8080FF))));
    }

    private void setInfo(@Nullable InventorySnapshot snapshot)
    {
        Vector3i pos;
        String[] dimension;
        String xp = "-";

        if (snapshot != null)
        {
            pos = snapshot.pos;
            dimension = snapshot.dimension.split(":", 2);
            dimension[0] += ":";
            if (snapshot.xp > 0) xp = String.valueOf(snapshot.xp);

            if (!snapshotContainer.hasParent()) snapshotWindow.child(snapshotContainer);
        }
        else
        {
            snapshotContainer.remove();
            return;
        }

        setPosLabels(pos);
        dimensionNamespaceLabel.text(Component.literal(dimension[0]));
        dimensionNameLabel.text(Component.literal(dimension[1]));
        xpLabel.text(Component.literal("XP: ").append(Component.literal(xp).withStyle(style -> style.withColor(0xFFE3F2FD))));
    }

    private void updateSlots(@Nullable InventorySnapshot snapshot)
    {
        currentSnapshot = snapshot;
        if (snapshot == null) {
            handleEmptyState();
            return;
        }

        handleSnapshotState(snapshot);
    }

    private void handleSnapshotState(InventorySnapshot snapshot) {
        scrollEmptyLabel.text(Component.empty());

        setInfo(snapshot);
        updateOffhand(snapshot);
        updateArmor(snapshot);
        updateItems(snapshot);
    }

    private void updateOffhand(InventorySnapshot snapshot) {
        offhandSlot.stack(
                snapshot.offhand.isEmpty()
                        ? ItemStack.EMPTY
                        : snapshot.offhand.getFirst()
        );
    }

    private void updateArmor(InventorySnapshot snapshot) {
        for (int i = 0; i < armorSlots.size() && i < snapshot.armor.size(); i++) {
            armorSlots.get(i).stack(snapshot.armor.get(i));
        }
    }

    private void updateItems(InventorySnapshot snapshot) {
        for (int i = 0; i < itemSlots.size() && i < snapshot.items.size(); i++) {
            ItemStack stack = snapshot.items.get(snapshot.items.size() - i - 1);
            itemSlots.get(i).stack(stack);
        }
    }

    private void handleEmptyState() {
        if (InventoriesDataManager.getInstance().getSnapshots().isEmpty())
            scrollEmptyLabel.text(Component.translatable("gui.deathmemo.empty"));

        offhandSlot.stack(ItemStack.EMPTY);

        clearSlots(armorSlots);
        clearSlots(itemSlots);

        setInfo(null);
    }

    private void clearSlots(List<ItemComponent> slots) {
        for (ItemComponent slot : slots) {
            slot.stack(ItemStack.EMPTY);
        }
    }
}
