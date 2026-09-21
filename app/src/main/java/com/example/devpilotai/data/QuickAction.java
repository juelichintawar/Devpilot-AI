package com.example.devpilotai.data;

import java.util.Objects;

/**
 * Model class for a Quick Action item on the dashboard.
 */
public class QuickAction {
    private final String title;
    private final int iconRes;
    private final int backgroundColor;

    public QuickAction(String title, int iconRes, int backgroundColor) {
        this.title = title;
        this.iconRes = iconRes;
        this.backgroundColor = backgroundColor;
    }

    public String getTitle() { return title; }
    public int getIconRes() { return iconRes; }
    public int getBackgroundColor() { return backgroundColor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuickAction that = (QuickAction) o;
        return iconRes == that.iconRes &&
                backgroundColor == that.backgroundColor &&
                Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, iconRes, backgroundColor);
    }
}
