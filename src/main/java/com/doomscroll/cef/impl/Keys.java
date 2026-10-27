package com.doomscroll.cef.impl;

import com.mojang.blaze3d.platform.InputConstants;

/**
 * Translates Minecraft 26.3 input into what the prebuilt java-cef native expects.
 *
 * <p>Minecraft moved from GLFW to SDL in 26.3: key codes are SDL scancodes (A is 4, not 65), mouse buttons start
 * at 1 instead of 0, and modifiers are SDL key-mod bits (shift is 0x0003, not 0x0001). The native was built
 * against GLFW and, on Windows, derives the virtual key from the scancode with MapVirtualKey, so it needs a
 * Windows scan code in GLFW's spelling: the Set 1 code, plus 0x100 on keys that arrive with an E0 prefix.
 *
 * <p>Everything below is a static table lookup, so the conversion costs nothing per event. Keys missing from the
 * tables (media keys, international extras) come through with a scan code of 0; Chromium then sees the character
 * events, which is what typing relies on.
 */
public final class Keys {
	private Keys() {}

	/** SDL scancode -> GLFW key code, for the keys the native looks up by name and for Ctrl shortcuts. */
	private static final int[] GLFW_KEY = new int[256];
	/** SDL scancode -> Windows Set 1 scan code, 0x100 marking the E0-prefixed (extended) keys, as GLFW reports it. */
	private static final int[] WIN_SCANCODE = new int[256];

	private static void map(int sdlScancode, int glfwKey, int windowsScancode) {
		GLFW_KEY[sdlScancode] = glfwKey;
		WIN_SCANCODE[sdlScancode] = windowsScancode;
	}

	static {
		// Letters: SDL 4..29 = A..Z, GLFW 65..90 = 'A'..'Z'
		int[] letterScan = {0x1E, 0x30, 0x2E, 0x20, 0x12, 0x21, 0x22, 0x23, 0x17, 0x24, 0x25, 0x26, 0x32,
				0x31, 0x18, 0x19, 0x10, 0x13, 0x1F, 0x14, 0x16, 0x2F, 0x11, 0x2D, 0x15, 0x2C};
		for (int i = 0; i < 26; i++) {
			map(4 + i, 'A' + i, letterScan[i]);
		}
		// Digit row: SDL 30..38 = 1..9, SDL 39 = 0; GLFW 48..57 = '0'..'9'
		for (int i = 0; i < 9; i++) {
			map(30 + i, '1' + i, 0x02 + i);
		}
		map(39, '0', 0x0B);

		map(40, 257, 0x1C);   // Return
		map(41, 256, 0x01);   // Escape
		map(42, 259, 0x0E);   // Backspace
		map(43, 258, 0x0F);   // Tab
		map(44, 32, 0x39);    // Space
		map(45, 45, 0x0C);    // Minus
		map(46, 61, 0x0D);    // Equals
		map(47, 91, 0x1A);    // Left bracket
		map(48, 93, 0x1B);    // Right bracket
		map(49, 92, 0x2B);    // Backslash
		map(50, 92, 0x2B);    // Non-US hash, same place as backslash
		map(51, 59, 0x27);    // Semicolon
		map(52, 39, 0x28);    // Apostrophe
		map(53, 96, 0x29);    // Grave
		map(54, 44, 0x33);    // Comma
		map(55, 46, 0x34);    // Period
		map(56, 47, 0x35);    // Slash
		map(57, 280, 0x3A);   // Caps lock

		for (int i = 0; i < 10; i++) {
			map(58 + i, 290 + i, 0x3B + i);  // F1..F10
		}
		map(68, 300, 0x57);   // F11
		map(69, 301, 0x58);   // F12

		map(70, 283, 0x137);  // Print screen (E0 37)
		map(71, 281, 0x46);   // Scroll lock
		map(72, 284, 0x45);   // Pause
		map(73, 260, 0x152);  // Insert (E0 52)
		map(74, 268, 0x147);  // Home (E0 47)
		map(75, 266, 0x149);  // Page up (E0 49)
		map(76, 261, 0x153);  // Delete (E0 53)
		map(77, 269, 0x14F);  // End (E0 4F)
		map(78, 267, 0x151);  // Page down (E0 51)
		map(79, 262, 0x14D);  // Right (E0 4D)
		map(80, 263, 0x14B);  // Left (E0 4B)
		map(81, 264, 0x150);  // Down (E0 50)
		map(82, 265, 0x148);  // Up (E0 48)

		map(83, 282, 0x145);  // Num lock
		map(84, 331, 0x135);  // Keypad divide (E0 35)
		map(85, 332, 0x37);   // Keypad multiply
		map(86, 333, 0x4A);   // Keypad minus
		map(87, 334, 0x4E);   // Keypad plus
		map(88, 335, 0x11C);  // Keypad enter (E0 1C)
		int[] keypadDigitScan = {0x4F, 0x50, 0x51, 0x4B, 0x4C, 0x4D, 0x47, 0x48, 0x49};
		for (int i = 0; i < 9; i++) {
			map(89 + i, 321 + i, keypadDigitScan[i]);  // Keypad 1..9
		}
		map(98, 320, 0x52);   // Keypad 0
		map(99, 330, 0x53);   // Keypad period
		map(100, 161, 0x56);  // Non-US backslash
		map(101, 348, 0x15D); // Application / menu (E0 5D)
		map(103, 336, 0x59);  // Keypad equals

		map(224, 341, 0x1D);  // Left control
		map(225, 340, 0x2A);  // Left shift
		map(226, 342, 0x38);  // Left alt
		map(227, 343, 0x15B); // Left super (E0 5B)
		map(228, 345, 0x11D); // Right control (E0 1D)
		map(229, 344, 0x36);  // Right shift
		map(230, 346, 0x138); // Right alt (E0 38)
		map(231, 347, 0x15C); // Right super (E0 5C)
	}

	/** GLFW key code for a Minecraft key event, or 0 when the key is not in the table. */
	public static int glfwKey(int sdlScancode) {
		return sdlScancode >= 0 && sdlScancode < GLFW_KEY.length ? GLFW_KEY[sdlScancode] : 0;
	}

	/** Windows scan code in GLFW's spelling; the native turns it into the virtual key. 0 when unknown. */
	public static int windowsScancode(int sdlScancode) {
		return sdlScancode >= 0 && sdlScancode < WIN_SCANCODE.length ? WIN_SCANCODE[sdlScancode] : 0;
	}

	/** Enter, either one, in Minecraft's numbering: Chromium submits a form on the \r character event. */
	public static boolean isEnter(int sdlScancode) {
		return sdlScancode == InputConstants.KEY_RETURN || sdlScancode == InputConstants.KEY_NUMPADENTER;
	}

	/**
	 * SDL modifier bits -> GLFW modifier bits. Caps Lock and Num Lock are dropped: the native reads 0x10/0x20/0x40
	 * of the same field as its mouse button mask, so passing them through would look like a held mouse button.
	 */
	public static int glfwMods(int sdlModifiers) {
		int out = 0;
		if ((sdlModifiers & InputConstants.MOD_SHIFT) != 0) {
			out |= GLFW_MOD_SHIFT;
		}
		if ((sdlModifiers & InputConstants.MOD_CONTROL) != 0) {
			out |= GLFW_MOD_CONTROL;
		}
		if ((sdlModifiers & InputConstants.MOD_ALT) != 0) {
			out |= GLFW_MOD_ALT;
		}
		if ((sdlModifiers & InputConstants.MOD_SUPER) != 0) {
			out |= GLFW_MOD_SUPER;
		}
		return out;
	}

	private static final int GLFW_MOD_SHIFT = 0x0001;
	private static final int GLFW_MOD_CONTROL = 0x0002;
	private static final int GLFW_MOD_ALT = 0x0004;
	private static final int GLFW_MOD_SUPER = 0x0008;

	/** Minecraft mouse button -> CEF button (0 left, 1 middle, 2 right). */
	public static int cefButton(int minecraftButton) {
		return switch (minecraftButton) {
			case InputConstants.MOUSE_BUTTON_MIDDLE -> 1;
			case InputConstants.MOUSE_BUTTON_RIGHT -> 2;
			default -> 0;
		};
	}
}
