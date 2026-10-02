package com.soldierskull.eclipse.stats;

/**
 * Every distributable attribute a player can spend points on.
 *
 * HOW TO ADD A NEW ATTRIBUTE (example: a "Sorte" / Luck attribute):
 *   1. Add one line below, e.g.:
 *          SORTE("Sorte"),
 *   PlayerStats stores/reads/writes it automatically. The only code YOU
 *   still have to write is what the attribute actually DOES to gameplay
 *   (StatAttributeHandler) and a button on the Status Screen.
 */
public enum AttributeType {

    STRENGTH("Strength"),
    DEFENSE("Defense"),
    SPEED("Speed"),
    CONSTITUTION("Constitution"),
    ENERGY("Energy");

    private final String displayName;

    AttributeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}