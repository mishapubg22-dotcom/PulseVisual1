package com.pulsevisual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class PulseVisualKeyHandler {

    private static KeyBinding keyESP;
    private static KeyBinding keyTracers;
    private static KeyBinding keyFullbright;
    private static KeyBinding keyChestESP;
    private static KeyBinding keyXray;
    private static KeyBinding keyMenu;
    private static KeyBinding keyHealthTags;

    public static void register() {
        keyESP = new KeyBinding("ESP", GLFW.GLFW_KEY_J, "Pulse Visual");
        keyTracers = new KeyBinding("Tracers", GLFW.GLFW_KEY_K, "Pulse Visual");
        keyFullbright = new KeyBinding("Fullbright", GLFW.GLFW_KEY_L, "Pulse Visual");
        keyChestESP = new KeyBinding("Chest ESP", GLFW.GLFW_KEY_O, "Pulse Visual");
        keyXray = new KeyBinding("X-Ray", GLFW.GLFW_KEY_X, "Pulse Visual");
        keyMenu = new KeyBinding("Menu", GLFW.GLFW_KEY_RIGHT_SHIFT, "Pulse Visual");
        keyHealthTags = new KeyBinding("Health Tags", GLFW.GLFW_KEY_H, "Pulse Visual");

        ClientRegistry.registerKeyBinding(keyESP);
        ClientRegistry.registerKeyBinding(keyTracers);
        ClientRegistry.registerKeyBinding(keyFullbright);
        ClientRegistry.registerKeyBinding(keyChestESP);
        ClientRegistry.registerKeyBinding(keyXray);
        ClientRegistry.registerKeyBinding(keyMenu);
        ClientRegistry.registerKeyBinding(keyHealthTags);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (keyESP.consumeClick()) {
            PulseVisualMod.espEnabled = !PulseVisualMod.espEnabled;
            sendMsg(mc, "Entity ESP", PulseVisualMod.espEnabled);
        }
        if (keyTracers.consumeClick()) {
            PulseVisualMod.tracersEnabled = !PulseVisualMod.tracersEnabled;
            sendMsg(mc, "Tracers", PulseVisualMod.tracersEnabled);
        }
        if (keyFullbright.consumeClick()) {
            PulseVisualMod.fullbrightEnabled = !PulseVisualMod.fullbrightEnabled;
            if (PulseVisualMod.fullbrightEnabled) {
                mc.options.gamma = 100.0;
            } else {
                mc.options.gamma = 1.0;
            }
            sendMsg(mc, "Fullbright", PulseVisualMod.fullbrightEnabled);
        }
        if (keyChestESP.consumeClick()) {
            PulseVisualMod.chestESPEnabled = !PulseVisualMod.chestESPEnabled;
            sendMsg(mc, "Chest ESP", PulseVisualMod.chestESPEnabled);
        }
        if (keyXray.consumeClick()) {
            PulseVisualMod.xrayEnabled = !PulseVisualMod.xrayEnabled;
            mc.levelRenderer.allChanged();
            sendMsg(mc, "X-Ray", PulseVisualMod.xrayEnabled);
        }
        if (keyHealthTags.consumeClick()) {
            PulseVisualMod.healthTagsEnabled = !PulseVisualMod.healthTagsEnabled;
            sendMsg(mc, "Health Tags", PulseVisualMod.healthTagsEnabled);
        }
        if (keyMenu.consumeClick()) {
            mc.setScreen(new PulseVisualGUI());
        }
    }

    private void sendMsg(Minecraft mc, String name, boolean on) {
        String status = on ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF";
        mc.player.sendMessage(
                new StringTextComponent(
                        TextFormatting.DARK_PURPLE + "[" +
                                TextFormatting.LIGHT_PURPLE + "Pulse Visual" +
                                TextFormatting.DARK_PURPLE + "] " +
                                TextFormatting.WHITE + name + ": " + status
                ), mc.player.getUUID());
    }
}