package co.AronHuisIn.deathmemo.UI.templates;

import co.AronHuisIn.deathmemo.UI.UIKeys;
import co.AronHuisIn.deathmemo.Utils;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.parsing.UIModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

import java.util.Map;

public final class FlatButtonTemplate {
    public static FlowLayout create(UIModel model, String btnText)
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
