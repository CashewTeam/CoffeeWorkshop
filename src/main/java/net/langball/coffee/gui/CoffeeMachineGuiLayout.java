package net.langball.coffee.gui;

/**
 * Shared layout constants for the Coffee Machine GUI.
 *
 * <p>Used by both {@link ContainerCoffeeMachine} (slot positions) and
 * {@link GuiCoffeeMachine} (background + progress indicator positions) to
 * keep Java coordinates and PNG backgrounds in sync.
 */
public final class CoffeeMachineGuiLayout {

    /** Background PNG dimensions (standard inventory GUIs are 176×166). */
    public static final int IMAGE_WIDTH = 176;
    public static final int IMAGE_HEIGHT = 166;

    // ── Machine slot positions (inventory X/Y) ────────────────────────────

    /** Slot 0 — Base (coffee powder, tea leaf, etc.) */
    public static final int BASE_X = 26;
    public static final int BASE_Y = 20;

    /** Slot 2 — Additive (cocoa powder, syrup, ice slag) */
    public static final int ADDITIVE_X = 50;
    public static final int ADDITIVE_Y = 20;

    /** Slot 1 — Modifier (water bucket, milk bucket) */
    public static final int MODIFIER_X = 26;
    public static final int MODIFIER_Y = 44;

    /** Slot 3 — Container (cup, cup_glass) */
    public static final int CONTAINER_X = 50;
    public static final int CONTAINER_Y = 44;

    /** Slot 4 — Output (drink result) */
    public static final int OUTPUT_X = 134;
    public static final int OUTPUT_Y = 32;

    // ── Progress / decoration positions (in-gui X/Y) ──────────────────────

    /** Progress arrow X on the background image. */
    public static final int PROGRESS_X = 86;
    /** Progress arrow Y on the background image. */
    public static final int PROGRESS_Y = 32;

    /** Flame / running indicator X on the background image. */
    public static final int FLAME_X = 72;
    /** Flame / running indicator Y on the background image. */
    public static final int FLAME_Y = 35;

    /** Height of the flame indicator in pixels. */
    public static final int FLAME_HEIGHT = 14;

    /** Width of the progress arrow in pixels. */
    public static final int PROGRESS_WIDTH = 24;
    /** Height of the progress arrow in pixels. */
    public static final int PROGRESS_HEIGHT = 16;

    private CoffeeMachineGuiLayout() {
    }
}
