package com.tcoded.componentsplitter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ComponentSplitter {

    public static List<Component> restructureMultiLine(List<Component> originalLines) {
        Objects.requireNonNull(originalLines, "Component list cannot be null");
        List<Component> lines = new ArrayList<>();

        for (Component originalLine : originalLines) {
            // Has new line?
            if (hasNewLine(originalLine)) {
                Ref<MutableComponent> workingStack = new Ref<>(null);
                MutableComponent lastPart = appendRecursive(lines, workingStack, originalLine);
                lines.add(lastPart.root().build());
            } else {
                lines.add(originalLine);
            }
        }

        return lines;
    }

    private static boolean hasNewLine(Component line) {
        if (line instanceof TextComponent text) {
            if (text.content().contains("\n")) return true;
        }

        for (Component child : line.children()) {
            if (hasNewLine(child)) return true;
        }

        return false;
    }

    public static MutableComponent appendRecursive(List<Component> lines, Ref<MutableComponent> workingStack, Component original) {
        Objects.requireNonNull(original, "Component cannot be null");

        // Push this component onto the working stack
        // cwc = Current working component
        MutableComponent cwc = workingStack.get();
        MutableComponent parent = cwc;

        Component originalNoChildren = original.children(List.of());
        cwc = new MutableComponent(cwc, originalNoChildren);

        if (parent != null) parent.appendChild(cwc);
        parent = null; // Delete to avoid bookkeeping the parent reference

        workingStack.set(cwc);

        // Build the components
        if (original instanceof TextComponent text) {
            String content = text.content();
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < content.length(); i++) {
                char c = content.charAt(i);

                if (c == '\n') {
                    String line = sb.toString();
                    sb = new StringBuilder(); // Reset the StringBuilder for the next section

                    Component textPart = applyFormatting(original, line);
                    cwc.setBase(textPart);

                    lines.add(cwc.root().build());

                    // Rebuild a new cwc for the next line
                    cwc = cwc.copyStylesOnly();
                    workingStack.set(cwc);

                    continue;
                }

                sb.append(c);

                // Apply any remaining text
                if (i >= content.length() - 1) {
                    String line = sb.toString();

                    Component textPart = applyFormatting(original, line);
                    cwc.setBase(textPart);
                }

            }
        }

        // Append children recursively
        for (Component child : original.children()) {
            appendRecursive(lines, workingStack, child);
        }

        // Pop the current working component
        cwc = workingStack.get();
        workingStack.set(cwc.getParent());
        return cwc;
    }

    private static Component applyFormatting(Component template, String line) {
        // Merge selectively to avoid inheriting unwanted decorations like strikethrough
        var templateStyle = template.style();
        var style = net.kyori.adventure.text.format.Style.empty();
        if (templateStyle.color() != null) style = style.color(templateStyle.color());
        if (templateStyle.font() != null) style = style.font(templateStyle.font());
        // Preserve allowed decorations
        if (templateStyle.decoration(net.kyori.adventure.text.format.TextDecoration.BOLD) == net.kyori.adventure.text.format.TextDecoration.State.TRUE) {
            style = style.decoration(net.kyori.adventure.text.format.TextDecoration.BOLD, net.kyori.adventure.text.format.TextDecoration.State.TRUE);
        }
        if (templateStyle.decoration(net.kyori.adventure.text.format.TextDecoration.UNDERLINED) == net.kyori.adventure.text.format.TextDecoration.State.TRUE) {
            style = style.decoration(net.kyori.adventure.text.format.TextDecoration.UNDERLINED, net.kyori.adventure.text.format.TextDecoration.State.TRUE);
        }
        // Explicitly disable strikethrough and obfuscated
        style = style.decoration(net.kyori.adventure.text.format.TextDecoration.STRIKETHROUGH, net.kyori.adventure.text.format.TextDecoration.State.FALSE)
                     .decoration(net.kyori.adventure.text.format.TextDecoration.OBFUSCATED, net.kyori.adventure.text.format.TextDecoration.State.FALSE);
        // Preserve click/hover events if present
        return Component.text(line)
                .style(style)
                .clickEvent(template.clickEvent())
                .hoverEvent(template.hoverEvent());
    }

}
