package co.AronHuisIn.deathmemo.UI.templates;

import co.AronHuisIn.deathmemo.UI.UIKeys;
import co.AronHuisIn.deathmemo.Utils;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.parsing.UIModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class FlatButtonTemplate {

    public static FlowLayout create(UIModel model, String btnText)
    {
        return create(model, btnText, UIKeys.SnapshotsHistory.Templates.FlatButton.TEMPLATE_NAME, null, null);
    }

    public static FlowLayout create(UIModel model, String btnText, Surface mouseLeaveSurface, Surface mouseEnterSurface)
    {
        return create(model, btnText, UIKeys.SnapshotsHistory.Templates.FlatButton.TEMPLATE_NAME, mouseLeaveSurface, mouseEnterSurface);
    }

    public static FlowLayout create(UIModel model, String btnText, Surface hoverCombineSurface)
    {
        FlowLayout flatButton = create(model, btnText, null, null);
        Surface baseSurface = flatButton.surface();
        Surface hoverSurface = baseSurface.and(hoverCombineSurface);
        flatButton.mouseEnter().subscribe(() -> flatButton.surface(hoverSurface));
        flatButton.mouseLeave().subscribe(() -> flatButton.surface(baseSurface));
        return flatButton;
    }

    public static FlowLayout create(UIModel model, String btnText, String buttonId, @Nullable Surface mouseLeaveSurface, @Nullable Surface mouseEnterSurface)
    {
        FlowLayout flatButton = model.expandTemplate(
                FlowLayout.class,
                Utils.getOwoExampleName(
                        UIKeys.SnapshotsHistory.SCREEN_ID,
                        UIKeys.SnapshotsHistory.Templates.FlatButton.TEMPLATE_NAME
                ),
                Map.of(
                        UIKeys.SnapshotsHistory.Templates.FlatButton.BUTTON_TEXT_ATTR, btnText,
                        UIKeys.SnapshotsHistory.Templates.FlatButton.BUTTON_ID_ATTR, buttonId
                )
        );

        if (mouseLeaveSurface != null) flatButton.surface(mouseLeaveSurface);
        if (mouseEnterSurface != null) {
            Surface baseSurface = flatButton.surface();
            flatButton.mouseEnter().subscribe(() -> flatButton.surface(mouseEnterSurface));
            flatButton.mouseLeave().subscribe(() -> flatButton.surface(baseSurface));
        }

        //? if <1.21.9 {
        flatButton.mouseDown().subscribe((x, y, button) -> {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f));
            return true;
        });
        //?} else {
        /*flatButton.mouseDown().subscribe((buttonEvent, doubled) -> {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f));
           return true;
        });
        *///?}

        return flatButton;
    }
}
