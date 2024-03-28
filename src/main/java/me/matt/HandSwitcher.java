package me.matt;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class HandSwitcher implements ModInitializer {
	private static KeyBinding swapHandKeyBinding;
	private static KeyBinding swapEveryHit;
	private static long lastPressTime = 0;
	private static final long COOLDOWN_MILLIS = 100;
	private static boolean swapEveryHitEnabled = false;
	private static boolean handSwappedThisSwing = false;

	@Override
	public void onInitialize() {
		swapHandKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"SwapHand",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_P,
				"HandSwitcher"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> onTick());
		swapEveryHit = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"SwapEveryHit",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_K,
				"HandSwitcher"
		));
	}

	public void onTick() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (swapHandKeyBinding.isPressed() && System.currentTimeMillis() - lastPressTime > COOLDOWN_MILLIS) {
			swapHand();
			lastPressTime = System.currentTimeMillis();
		}

		if (swapEveryHit.isPressed() && System.currentTimeMillis() - lastPressTime > COOLDOWN_MILLIS) {
			swapEveryHitEnabled = !swapEveryHitEnabled;
			if (swapEveryHitEnabled) {
				client.player.sendMessage(Text.of("§fSwapEveryHit §aenabled"));
			} else {
				client.player.sendMessage(Text.of("§fSwapEveryHit §cdisabled"));
			}
			lastPressTime = System.currentTimeMillis();
		}

		if (swapEveryHitEnabled) {
			if (client.player != null && client.player.handSwingProgress > 0 && !handSwappedThisSwing) {
				swapHand();
				handSwappedThisSwing = true;
			} else if (client.player != null && client.player.handSwingProgress == 0) {
				handSwappedThisSwing = false;
			}
		}
	}

	private void swapHand() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player != null) {
			client.player.setMainArm(client.player.getMainArm() == net.minecraft.util.Arm.RIGHT ? net.minecraft.util.Arm.LEFT : net.minecraft.util.Arm.RIGHT);
		}
	}
}