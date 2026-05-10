package co.AronHuisIn.deathmemo.UI.components;

import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIParsing;
import org.w3c.dom.Element;

public class HoverAwareFlowLayout extends FlowLayout {

    protected HoverAwareFlowLayout(Sizing horizontalSizing, Sizing verticalSizing, Algorithm algorithm) {
        super(horizontalSizing, verticalSizing, algorithm);
    }

    @Override
    protected void updateHoveredState(int mouseX, int mouseY, boolean nowHovered) {
        this.hovered = nowHovered;

        if (nowHovered) {
            this.mouseEnterEvents.sink().onMouseEnter();
        } else {
            this.mouseLeaveEvents.sink().onMouseLeave();
        }
    }

    public static HoverAwareFlowLayout parse(Element element) {
        UIParsing.expectAttributes(element, "direction");
        return
                element.getAttribute("direction").equals("horizontal")
                        ? new HoverAwareFlowLayout(Sizing.content(), Sizing.content(), Algorithm.HORIZONTAL)
                        : new HoverAwareFlowLayout(Sizing.content(), Sizing.content(), Algorithm.VERTICAL);
    }
}