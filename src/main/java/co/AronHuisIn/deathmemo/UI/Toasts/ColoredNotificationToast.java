package co.AronHuisIn.deathmemo.UI.Toasts;

//import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
//? if =1.21.1 {
import net.minecraft.client.gui.components.toasts.ToastComponent;
import org.jetbrains.annotations.NotNull;
//?} elif >=1.21.2 {
/*import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.MethodsReturnNonnullByDefault;
import javax.annotation.ParametersAreNonnullByDefault;
*///?}
import net.minecraft.network.chat.Component;

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

    //? if >=1.21.2
    //private static long lastUpdateTime;

    public ColoredNotificationToast(Component description, int backgroundColor, int outlineColor, int visibleTime) {
        this.description = description;
        this.backgroundColor = backgroundColor;
        this.outlineColor = outlineColor;
        this.visibleTime = visibleTime;

        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(description);
        this.computedWidth = Math.min(MAX_WIDTH, textWidth + PADDING * 2);
    }

    //? if =1.21.1 {
    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    public @NotNull Visibility render(@NotNull GuiGraphics guiGraphics, @NotNull ToastComponent toastManager, long time) {
        if (this.startTime == 0L) {
            this.startTime = time;
        }
        //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Font font = toastManager.getMinecraft().font;

        guiGraphics.fill(0, 0, computedWidth, HEIGHT, backgroundColor);
        guiGraphics.renderOutline(0, 0, computedWidth, HEIGHT, outlineColor);

        int textX = PADDING;
        int textY = (HEIGHT - font.lineHeight) / 2;
        guiGraphics.drawString(font, description, textX, textY, 0xAAAAAA, false);

        long elapsed = time - this.startTime;
        return (elapsed >= visibleTime) ? Visibility.HIDE : Visibility.SHOW;
    }
    //?} elif >=1.21.2 {

    /*@Override
    public @MethodsReturnNonnullByDefault Visibility getWantedVisibility() {
        if (this.startTime == 0L) {
            return Visibility.SHOW;
        }
        long elapsed = lastUpdateTime - this.startTime;
        return (elapsed >= visibleTime) ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public void update(@ParametersAreNonnullByDefault ToastManager toastManager, long time) {
        if (this.startTime == 0L) {
            this.startTime = time;
        }
        lastUpdateTime = time;
    }

    @Override
    public void render(GuiGraphics guiGraphics, Font font, long time) {
        //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        guiGraphics.fill(0, 0, computedWidth, HEIGHT, backgroundColor);
        //? if <1.21.9 {
        guiGraphics.renderOutline(0, 0, computedWidth, HEIGHT, outlineColor);
        //?} else {
        /^int left = 0;
        int top = 0;
        int right = computedWidth;
        int bottom = HEIGHT;
        int thickness = 1;

        guiGraphics.fill(left, top, right, top + thickness, outlineColor);
        guiGraphics.fill(left, bottom - thickness, right, bottom, outlineColor);
        guiGraphics.fill(left, top + thickness, left + thickness, bottom - thickness, outlineColor);
        guiGraphics.fill(right - thickness, top + thickness, right, bottom - thickness, outlineColor);
        ^///?}

        int textY = (HEIGHT - font.lineHeight) / 2;
        guiGraphics.drawString(font, description, PADDING, textY, 0xAAAAAAAA, false);
    }
    *///?}
    @Override
    public int width() { return computedWidth; }

    @Override
    public int height() { return HEIGHT; }
}