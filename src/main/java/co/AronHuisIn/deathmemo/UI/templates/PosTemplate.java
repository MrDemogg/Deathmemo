package co.AronHuisIn.deathmemo.UI.templates;

import co.AronHuisIn.deathmemo.UI.UIKeys;
import co.AronHuisIn.deathmemo.Utils;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.parsing.UIModel;

import java.util.Map;

public final class PosTemplate {
    public static FlowLayout create(UIModel model)
    {
        return model.expandTemplate(
                FlowLayout.class,
                Utils.getOwoExampleName(
                        UIKeys.SnapshotsHistory.SCREEN_ID,
                        UIKeys.SnapshotsHistory.Templates.Pos.TEMPLATE_NAME
                ),
                Map.of()
        );
    }
}
