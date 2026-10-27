package org.lwjgl.glfw;

/**
 * Constants the prebuilt java-cef native reads out of this class by name.
 *
 * <p>jcef.dll resolves {@code org/lwjgl/glfw/GLFW} on every key and mouse event and reads these static fields
 * (see CefBrowser_N.cpp: {@code ScopedJNIClass cls(env, "org/lwjgl/glfw/GLFW")}). Minecraft shipped LWJGL's GLFW
 * bindings until 26.2; 26.3 moved to SDL and dropped the artifact, so the lookup fails and the native drops every
 * event without a word. The browser then renders but cannot be clicked or typed into.
 *
 * <p>Rebuilding the native is not an option here, so this class stands in for it. It holds nothing but the
 * constants the native reads, with GLFW's values, and has no static initialiser: reading a field never loads a
 * native library. Minecraft input is translated to these values in {@link com.doomscroll.cef.impl.Keys}.
 *
 * <p>If a future Minecraft brings the real bindings back, drop this file: the values are LWJGL's own, so the
 * native behaves identically either way.
 */
public final class GLFW {
	private GLFW() {}

	/** Key and mouse button actions; {@code CefKeyEvent.KEY_PRESS/KEY_RELEASE/KEY_TYPE} use the same numbers. */
	public static final int GLFW_RELEASE = 0;
	public static final int GLFW_PRESS = 1;
	public static final int GLFW_REPEAT = 2;

	/** Modifier bits. The native also reads 0x10/0x20/0x40 from the same field as its mouse button mask. */
	public static final int GLFW_MOD_SHIFT = 0x0001;
	public static final int GLFW_MOD_CONTROL = 0x0002;
	public static final int GLFW_MOD_ALT = 0x0004;
	public static final int GLFW_MOD_SUPER = 0x0008;

	public static final int GLFW_MOUSE_BUTTON_1 = 0;
	public static final int GLFW_MOUSE_BUTTON_2 = 1;
	public static final int GLFW_MOUSE_BUTTON_3 = 2;

	/** Only these keys are read by name; the native maps the rest through the scancode. */
	public static final int GLFW_KEY_ESCAPE = 256;
	public static final int GLFW_KEY_ENTER = 257;
	public static final int GLFW_KEY_TAB = 258;
	public static final int GLFW_KEY_BACKSPACE = 259;
	public static final int GLFW_KEY_DELETE = 261;
	public static final int GLFW_KEY_RIGHT = 262;
	public static final int GLFW_KEY_LEFT = 263;
	public static final int GLFW_KEY_DOWN = 264;
	public static final int GLFW_KEY_UP = 265;
}
