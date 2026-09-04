package com.tcoded.componentsplitter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComponentSplitterTest {

    @Test
    void preservesStrikethroughOnMultilineChildComponents() {
        Component component = Component.text("first\n")
                .decoration(TextDecoration.STRIKETHROUGH, false)
                .append(Component.text(" ", Style.style(TextDecoration.STRIKETHROUGH)));

        List<Component> lines = ComponentSplitter.restructureMultiLine(List.of(component));

        assertEquals(2, lines.size());
        assertEquals(
                TextDecoration.State.TRUE,
                decorationForText(lines.get(1), " ", Style.empty(), TextDecoration.STRIKETHROUGH)
        );
    }

    @Test
    void childDecorationTakesPrecedenceOverParentDecoration() {
        Component component = Component.text("first\n")
                .decoration(TextDecoration.STRIKETHROUGH, true)
                .append(Component.text("child").decoration(TextDecoration.STRIKETHROUGH, false));

        List<Component> lines = ComponentSplitter.restructureMultiLine(List.of(component));

        assertEquals(
                TextDecoration.State.FALSE,
                decorationForText(lines.get(1), "child", Style.empty(), TextDecoration.STRIKETHROUGH)
        );
    }

    private static TextDecoration.State decorationForText(
            Component component,
            String expectedText,
            Style inheritedStyle,
            TextDecoration decoration
    ) {
        Style effectiveStyle = component.style().merge(inheritedStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
        if (component instanceof TextComponent textComponent && textComponent.content().equals(expectedText)) {
            return effectiveStyle.decoration(decoration);
        }

        for (Component child : component.children()) {
            TextDecoration.State state = decorationForText(child, expectedText, effectiveStyle, decoration);
            if (state != TextDecoration.State.NOT_SET) {
                return state;
            }
        }
        return TextDecoration.State.NOT_SET;
    }
}
