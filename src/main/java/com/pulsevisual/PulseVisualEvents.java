package com.pulsevisual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MainWindow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.Color;

public class PulseVisualEvents {

    private Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.player == null || mc.level == null) return;

        if (PulseVisualMod.fullbrightEnabled) {
            mc.options.gamma = 100.0;
        }

        if (PulseVisualMod.mobGlowEnabled) {
            for (Entity entity : mc.level.entitiesForRendering()) {
                if (entity == mc.player) continue;
                if (entity instanceof PlayerEntity ||
                        entity instanceof MobEntity ||
                        entity instanceof AnimalEntity) {
                    entity.setGlowing(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) return;
        if (mc.player == null) return;

        int y = 5;
        int x = 5;

        mc.font.drawShadow(event.getMatrixStack(),
                "\u00A7d\u00A7lPulse Visual", x, y, 0xAA00FF);
        y += 14;

        float pulse = (float)(Math.sin(System.currentTimeMillis() / 300.0) * 0.3 + 0.7);
        int color = new Color(
                (int)(180 * pulse),
                (int)(50 * pulse),
                (int)(255 * pulse)
        ).getRGB();

        if (PulseVisualMod.espEnabled) {
            mc.font.drawShadow(event.getMatrixStack(), " > Entity ESP", x, y, color);
            y += 11;
        }
        if (PulseVisualMod.tracersEnabled) {
            mc.font.drawShadow(event.getMatrixStack(), " > Tracers", x, y, color);
            y += 11;
        }
        if (PulseVisualMod.fullbrightEnabled) {
            mc.font.drawShadow(event.getMatrixStack(), " > Fullbright", x, y, color);
            y += 11;
        }
        if (PulseVisualMod.chestESPEnabled) {
            mc.font.drawShadow(event.getMatrixStack(), " > Chest ESP", x, y, color);
            y += 11;
        }
        if (PulseVisualMod.xrayEnabled) {
            mc.font.drawShadow(event.getMatrixStack(), " > X-Ray", x, y, color);
            y += 11;
        }
        if (PulseVisualMod.mobGlowEnabled) {
            mc.font.drawShadow(event.getMatrixStack(), " > Mob Glow", x, y, color);
            y += 11;
        }
        if (PulseVisualMod.healthTagsEnabled) {
            mc.font.drawShadow(event.getMatrixStack(), " > Health Tags", x, y, color);
            y += 11;
        }

        MainWindow window = mc.getWindow();
        String wm = "\u00A78Pulse Visual v1.0";
        int ww = mc.font.width(wm);
        mc.font.drawShadow(event.getMatrixStack(), wm,
                window.getGuiScaledWidth() - ww - 5,
                window.getGuiScaledHeight() - 12, 0x555555);
    }
}