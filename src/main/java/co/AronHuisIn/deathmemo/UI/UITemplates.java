package co.AronHuisIn.deathmemo.UI;

import co.AronHuisIn.deathmemo.Utils;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.parsing.UIModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

import java.util.Map;

public final class UITemplates {
    public static FlowLayout slotTemplate(UIModel model)
    {
        return model.expandTemplate(
                FlowLayout.class,
                Utils.getOwoExampleName(
                        UIKeys.SnapshotsHistory.SCREEN_ID,
                        UIKeys.SnapshotsHistory.Examples.ItemSlot.EXAMPLE_NAME
                ),
                Map.of()
        );
    }

    public static FlowLayout flatButtonTemplate(UIModel model, String btnText)
    {
        FlowLayout flatButton = model.expandTemplate(
                FlowLayout.class,
                Utils.getOwoExampleName(
                        UIKeys.SnapshotsHistory.SCREEN_ID,
                        UIKeys.SnapshotsHistory.Examples.FlatButton.EXAMPLE_NAME
                ),
                Map.of(
                        UIKeys.SnapshotsHistory.Examples.FlatButton.BUTTON_TEXT_ATTR, btnText
                )
        );

        flatButton.mouseDown().subscribe((x, y, button) ->{
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f));
            return true;
        });

        return flatButton;
    }
}
