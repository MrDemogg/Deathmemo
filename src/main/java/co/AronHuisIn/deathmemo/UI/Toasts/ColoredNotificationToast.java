package co.AronHuisIn.deathmemo.UI.Toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ColoredNotificationToast implements Toast {
    private final Component description;
    private long startTime;
    private final int backgroundColor;
    private final int outlineColor;
    private final int visibleTime;

    // Параметры внешнего вида
    private static final int WIDTH = 160;
    private static final int HEIGHT = 32;

    public ColoredNotificationToast(Component description, int backgroundColor, int outlineColor, int visibleTime) {
        this.description = description;
        this.backgroundColor = backgroundColor;
        this.outlineColor = outlineColor;
        this.visibleTime = visibleTime;
    }

    @Override
    public @NotNull Visibility render(@NotNull GuiGraphics guiGraphics, @NotNull ToastComponent toastManager, long time) {
        if (this.startTime == 0L) {
            this.startTime = time;
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = 0;
        int y = 0;

        guiGraphics.fill(x, y, x + WIDTH, y + HEIGHT, backgroundColor);

        guiGraphics.renderOutline(x, y, WIDTH, HEIGHT, outlineColor);

        guiGraphics.drawString(toastManager.getMinecraft().font, description, x + 8, y + 12, 0xAAAAAA, false);

        long elapsed = time - this.startTime;
        if (elapsed >= visibleTime) {
            return Visibility.HIDE;
        }

        return Visibility.SHOW;
    }

    @Override
    public int width() {
        return WIDTH;
    }

    @Override
    public int height() {
        return HEIGHT;
    }
}