package com.pulsevisual;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.EnderChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class PulseVisualRenderer {

    private Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (mc.player == null || mc.level == null) return;

        float pt = event.getPartialTicks();
        Vector3d cam = mc.gameRenderer.getMainCamera().getPosition();

        if (PulseVisualMod.espEnabled) {
            renderEntityESP(cam, pt);
        }
        if (PulseVisualMod.tracersEnabled) {
            renderTracers(cam, pt);
        }
        if (PulseVisualMod.chestESPEnabled) {
            renderChestESP(cam);
        }
    }

    private void renderEntityESP(Vector3d cam, float pt) {
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity == mc.player) continue;
            if (!(entity instanceof PlayerEntity) &&
                    !(entity instanceof MobEntity) &&
                    !(entity instanceof AnimalEntity)) continue;

            double x = lerp(entity.xOld, entity.getX(), pt) - cam.x;
            double y = lerp(entity.yOld, entity.getY(), pt) - cam.y;
            double z = lerp(entity.zOld, entity.getZ(), pt) - cam.z;

            float hw = entity.getBbWidth() / 2.0f;
            AxisAlignedBB box = new AxisAlignedBB(
                    x - hw, y, z - hw,
                    x + hw, y + entity.getBbHeight(), z + hw
            );

            Color color;
            if (entity instanceof PlayerEntity) {
                color = new Color(0, 200, 255, 180);
            } else if (entity instanceof MobEntity) {
                color = new Color(255, 50, 50, 180);
            } else {
                color = new Color(50, 255, 50, 180);
            }

            drawBox(box, color);
        }
    }

    private void renderTracers(Vector3d cam, float pt) {
        setupGL();
        GL11.glLineWidth(1.5f);

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity == mc.player) continue;
            if (!(entity instanceof PlayerEntity) && !(entity instanceof MobEntity)) continue;

            double x = lerp(entity.xOld, entity.getX(), pt) - cam.x;
            double y = lerp(entity.yOld, entity.getY(), pt) - cam.y;
            double z = lerp(entity.zOld, entity.getZ(), pt) - cam.z;

            if (entity instanceof PlayerEntity) {
                GL11.glColor4f(0.0f, 0.8f, 1.0f, 0.8f);
            } else {
                GL11.glColor4f(1.0f, 0.2f, 0.2f, 0.8f);
            }

            GL11.glBegin(GL11.GL_LINES);
            GL11.glVertex3d(0, mc.player.getEyeHeight(), 0);
            GL11.glVertex3d(x, y + entity.getBbHeight() / 2, z);
            GL11.glEnd();
        }

        endGL();
    }

    private void renderChestESP(Vector3d cam) {
        for (TileEntity te : mc.level.blockEntityList) {
            if (!(te instanceof ChestTileEntity) && !(te instanceof EnderChestTileEntity)) continue;

            BlockPos pos = te.getBlockPos();
            double x = pos.getX() - cam.x;
            double y = pos.getY() - cam.y;
            double z = pos.getZ() - cam.z;

            AxisAlignedBB box = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);

            Color color;
            if (te instanceof EnderChestTileEntity) {
                color = new Color(200, 0, 255, 150);
            } else {
                color = new Color(255, 200, 0, 150);
            }

            drawBox(box, color);
        }
    }

    private void drawBox(AxisAlignedBB b, Color c) {
        setupGL();
        GL11.glLineWidth(2.0f);

        float r = c.getRed() / 255f;
        float g = c.getGreen() / 255f;
        float bl = c.getBlue() / 255f;
        float a = c.getAlpha() / 255f;

        GL11.glColor4f(r, g, bl, a * 0.25f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(b.minX, b.minY, b.minZ);
        GL11.glVertex3d(b.maxX, b.minY, b.minZ);
        GL11.glVertex3d(b.maxX, b.minY, b.maxZ);
        GL11.glVertex3d(b.minX, b.minY, b.maxZ);
        GL11.glVertex3d(b.minX, b.maxY, b.minZ);
        GL11.glVertex3d(b.maxX, b.maxY, b.minZ);
        GL11.glVertex3d(b.maxX, b.maxY, b.maxZ);
        GL11.glVertex3d(b.minX, b.maxY, b.maxZ);
        GL11.glEnd();

        GL11.glColor4f(r, g, bl, a);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(b.minX, b.minY, b.minZ);
        GL11.glVertex3d(b.maxX, b.minY, b.minZ);
        GL11.glVertex3d(b.maxX, b.minY, b.maxZ);
        GL11.glVertex3d(b.minX, b.minY, b.maxZ);
        GL11.glVertex3d(b.minX, b.minY, b.minZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(b.minX, b.maxY, b.minZ);
        GL11.glVertex3d(b.maxX, b.maxY, b.minZ);
        GL11.glVertex3d(b.maxX, b.maxY, b.maxZ);
        GL11.glVertex3d(b.minX, b.maxY, b.maxZ);
        GL11.glVertex3d(b.minX, b.maxY, b.minZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(b.minX, b.minY, b.minZ);
        GL11.glVertex3d(b.minX, b.maxY, b.minZ);
        GL11.glVertex3d(b.maxX, b.minY, b.minZ);
        GL11.glVertex3d(b.maxX, b.maxY, b.minZ);
        GL11.glVertex3d(b.maxX, b.minY, b.maxZ);
        GL11.glVertex3d(b.maxX, b.maxY, b.maxZ);
        GL11.glVertex3d(b.minX, b.minY, b.maxZ);
        GL11.glVertex3d(b.minX, b.maxY, b.maxZ);
        GL11.glEnd();

        endGL();
    }

    private void setupGL() {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
    }

    private void endGL() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private double lerp(double a, double b, float t) {
        return a + (b - a) * t;
    }
}