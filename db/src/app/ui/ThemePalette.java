package app.ui;

import app.config.ThemeMode;
import java.awt.Color;

public final class ThemePalette {
    private final Color background;
    private final Color board;
    private final Color gridLine;
    private final Color cellDefault;
    private final Color cellHover;
    private final Color xColor;
    private final Color oColor;
    private final Color text;
    private final Color accent;
    private final Color winHighlight;
    private final Color winLineColor;
    private final Color buttonHover;
    private final Color buttonPress;
    private final Color shadow;

    public ThemePalette(
        Color background,
        Color board,
        Color gridLine,
        Color cellDefault,
        Color cellHover,
        Color xColor,
        Color oColor,
        Color text,
        Color accent,
        Color winHighlight,
        Color winLineColor,
        Color buttonHover,
        Color buttonPress,
        Color shadow
    ) {
        this.background = background;
        this.board = board;
        this.gridLine = gridLine;
        this.cellDefault = cellDefault;
        this.cellHover = cellHover;
        this.xColor = xColor;
        this.oColor = oColor;
        this.text = text;
        this.accent = accent;
        this.winHighlight = winHighlight;
        this.winLineColor = winLineColor;
        this.buttonHover = buttonHover;
        this.buttonPress = buttonPress;
        this.shadow = shadow;
    }

    public static ThemePalette forMode(ThemeMode mode) {
        if (mode == ThemeMode.DARK) {
            return new ThemePalette(
                new Color(0x1C1C1E),
                new Color(0x2C2C2E),
                new Color(0x3A3A3C),
                new Color(0x1C1C1E),
                new Color(0x3A3A3C),
                new Color(0x60A5FA),
                new Color(0xF87171),
                new Color(0xFFFFFF),
                new Color(0x0A84FF),
                new Color(0x34C759),
                new Color(0x34C759),
                new Color(0x3A3A3C),
                new Color(0x48484A),
                new Color(0, 0, 0, 120)
            );
        }
        if (mode == ThemeMode.OCEAN) {
            return new ThemePalette(
                new Color(8, 44, 77),
                new Color(14, 72, 118),
                new Color(0x38BDF8),
                new Color(14, 72, 118),
                new Color(22, 88, 138),
                new Color(105, 200, 255),
                new Color(255, 147, 149),
                new Color(230, 245, 255),
                new Color(86, 207, 225),
                new Color(131, 237, 173),
                new Color(131, 237, 173),
                new Color(104, 182, 194),
                new Color(75, 140, 156),
                new Color(3, 25, 47, 120)
            );
        }
        return new ThemePalette(
            new Color(0xF5F7FA),
            new Color(0xFFFFFF),
            new Color(0xD1D5DB),
            new Color(0xFFFFFF),
            new Color(0xE5E7EB),
            new Color(0x2563EB),
            new Color(0xDC2626),
            new Color(0x111827),
            new Color(0x10B981),
            new Color(0x22C55E),
            new Color(0x10B981),
            new Color(0xF3F4F6),
            new Color(0xE5E7EB),
            new Color(30, 41, 59, 70)
        );
    }

    public Color getBackground() {
        return background;
    }

    public Color getBoard() {
        return board;
    }

    public Color getGridLine() {
        return gridLine;
    }

    public Color getCellDefault() {
        return cellDefault;
    }

    public Color getCellHover() {
        return cellHover;
    }

    public Color getXColor() {
        return xColor;
    }

    public Color getOColor() {
        return oColor;
    }

    public Color getText() {
        return text;
    }

    public Color getAccent() {
        return accent;
    }

    public Color getWinHighlight() {
        return winHighlight;
    }

    public Color getWinLineColor() {
        return winLineColor;
    }

    public Color getButtonHover() {
        return buttonHover;
    }

    public Color getButtonPress() {
        return buttonPress;
    }

    public Color getShadow() {
        return shadow;
    }
}
