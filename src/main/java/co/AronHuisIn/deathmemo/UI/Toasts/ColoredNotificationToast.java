package co.AronHuisIn.deathmemo.UI.Toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
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
    private final int computedWidth;

    private static final int HEIGHT = 32;
    private static final int MAX_WIDTH = 320;
    private static final int PADDING = 8;

    public ColoredNotificationToast(Component description, int backgroundColor, int outlineColor, int visibleTime) {
        this.description = description;
        this.backgroundColor = backgroundColor;
        this.outlineColor = outlineColor;
        this.visibleTime = visibleTime;

        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(description);
        this.computedWidth = Math.min(MAX_WIDTH, textWidth + PADDING * 2);
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    public @NotNull Visibility render(@NotNull GuiGraphics guiGraphics, @NotNull ToastComponent toastManager, long time) {
        if (this.startTime == 0L) {
            this.startTime = time;
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Font font = toastManager.getMinecraft().font;

        guiGraphics.fill(0, 0, computedWidth, HEIGHT, backgroundColor);
        guiGraphics.renderOutline(0, 0, computedWidth, HEIGHT, outlineColor);

        int textX = PADDING;
        int textY = (HEIGHT - font.lineHeight) / 2;
        guiGraphics.drawString(font, description, textX, textY, 0xAAAAAA, false);

        long elapsed = time - this.startTime;
        return (elapsed >= visibleTime) ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public int width() {
        return computedWidth; // теперь возвращаем реальную ширину
    }

    @Override
    public int height() {
        return HEIGHT;
    }
}