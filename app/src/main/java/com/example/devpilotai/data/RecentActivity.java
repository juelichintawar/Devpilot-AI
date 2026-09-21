package com.example.devpilotai.data;

import java.util.Objects;

/**
 * Model class representing a recent activity item in the dashboard.
 */
public class RecentActivity {
    private final String title;
    private final String subtitle;
    private final String time;
    private final int iconRes;
    private final int iconColor;
    private final int iconBackground;

    public RecentActivity(String title, String subtitle, String time, int iconRes, int iconColor, int iconBackground) {
        this.title = title;
        this.subtitle = subtitle;
        this.time = time;
        this.iconRes = iconRes;
        this.iconColor = iconColor;
        this.iconBackground = iconBackground;
    }

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getTime() { return time; }
    public int getIconRes() { return iconRes; }
    public int getIconColor() { return iconColor; }
    public int getIconBackground() { return iconBackground; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecentActivity that = (RecentActivity) o;
        return iconRes == that.iconRes &&
                iconColor == that.iconColor &&
                iconBackground == that.iconBackground &&
                Objects.equals(title, that.title) &&
                Objects.equals(subtitle, that.subtitle) &&
                Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, subtitle, time, iconRes, iconColor, iconBackground);
    }
}
