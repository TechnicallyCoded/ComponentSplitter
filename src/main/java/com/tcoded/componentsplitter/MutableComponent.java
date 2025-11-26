package com.tcoded.componentsplitter;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;

import java.util.LinkedList;
import java.util.List;

public class MutableComponent {

    private MutableComponent parent;
    private Component base;
    private List<MutableComponent> children;

    public MutableComponent(MutableComponent parent, Component base) {
        Preconditions.checkArgument(base.children().isEmpty(), "Base component must not have children");
        this.parent = parent;
        this.base = base;
        this.children = new LinkedList<>();
    }

    public MutableComponent getParent() {
        return parent;
    }

    public void setParent(MutableComponent parent) {
        this.parent = parent;
    }

    public Component getBase() {
        return base;
    }

    public void setBase(Component base) {
        this.base = base;
    }

    public List<MutableComponent> getChildren() {
        return children;
    }

    public void setChildren(List<MutableComponent> children) {
        this.children = children;
    }

    public void appendChild(MutableComponent child) {
        this.children.add(child);
    }

    public void removeChild(MutableComponent child) {
        this.children.remove(child);
    }

    public Component build() {
        Component result = this.base;
        for (MutableComponent child : this.children) {
            result = result.append(child.build());
        }
        return result;
    }

    public MutableComponent copyStylesOnly() {
        MutableComponent parentCopy = this.parent != null ?
                this.parent.copyStylesOnly() :
                null;
        Component baseCopy = Component.empty().style(this.base.style());
        MutableComponent selfCopy = new MutableComponent(parentCopy, baseCopy);

        if (parentCopy != null) {
            parentCopy.appendChild(selfCopy);
        }

        return selfCopy;
    }

    public MutableComponent root() {
        MutableComponent current = this;
        while (current.parent != null) {
            current = current.parent;
        }
        return current;
    }

    public int depth() {
        int depth = 0;
        MutableComponent current = this;
        while (current.parent != null) {
            depth++;
            current = current.parent;
        }
        return depth;
    }

}
