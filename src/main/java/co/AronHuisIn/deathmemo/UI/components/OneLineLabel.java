package co.AronHuisIn.deathmemo.UI.components;

import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.w3c.dom.Element;

import java.util.Map;

public class OneLineLabel extends LabelComponent {

    private Component originalText;

    public OneLineLabel(Component text) {
        super(text);
        this.originalText = text;
    }

    @Override
    public LabelComponent text(Component text) {
        this.originalText = text;
        return super.text(text);
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        var font = Minecraft.getInstance().font;

        int availableWidth = this.width();

        if (availableWidth <= 0) {
            super.draw(context, mouseX, mouseY, partialTicks, delta);
            return;
        }

        String fullText = originalText.getString();
        String displayText = fullText;

        if (font.width(fullText) > availableWidth) {
            String ellipsis = "...";
            int ellipsisWidth = font.width(ellipsis);
            int targetWidth = availableWidth - ellipsisWidth;

            if (targetWidth <= 0) {
                displayText = ellipsis;
            } else {
                int end = fullText.length();

                while (end > 0 &&
                        font.width(fullText.substring(0, end)) > targetWidth) {
                    end--;
                }

                displayText = fullText.substring(0, end) + ellipsis;
            }
        }

        super.text(Component.literal(displayText));
        super.draw(context, mouseX, mouseY, partialTicks, delta);

        super.text(originalText);
    }

    public static OneLineLabel parse(Element element)
    {
        Map<String, Element> children = UIParsing.childElements(element);
        UIParsing.expectChildren(element, children, "text");
        return new OneLineLabel(UIParsing.parseText(children.get("text")));
    }
}
