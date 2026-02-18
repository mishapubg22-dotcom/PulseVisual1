package com.pulsevisual;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class PulseVisualGUI extends Screen {

    private int guiX, guiY;
    private int guiWidth = 260;
    private int guiHeight = 320;
    private List<ModButton> modButtons = new ArrayList<>();

    private static class ModButton {
        int x, y, w, h, id;
        String name, icon;
        boolean enabled;
        Color accent;
        float hover = 0f;

        ModButton(int id, int x, int y, int w, int h,
                  String name, boolean enabled, String icon, Color accent) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.name = name;
            this.enabled = enabled;
            this.icon = icon;
            this.accent = accent;
        }

        boolean isHovered(double mx, double my) {
            return mx >= x && mx <= x + w && my >= y && my <= y + h;
        }
    }

    public PulseVisualGUI() {
        super(new StringTextComponent("Pulse Visual"));
    }

    @Override
    protected void init() {
        super.init();
        guiX = (width - guiWidth) / 2;
        guiY = (height - guiHeight) / 2;

        modButtons.clear();
        int bw = 220, bh = 26;
        int sx = guiX + (guiWidth - bw) / 2;
        int sy = guiY + 55;
        int gap = 34;

        modButtons.add(new ModButton(0, sx, sy, bw, bh,
                "Entity ESP", PulseVisualMod.espEnabled, "*", new Color(0, 200, 255)));
        modButtons.add(new ModButton(1, sx, sy + gap, bw, bh,
                "Tracers", PulseVisualMod.tracersEnabled, ">", new Color(255, 100, 255)));
        modButtons.add(new ModButton(2, sx, sy + gap * 2, bw, bh,
                "Fullbright", PulseVisualMod.fullbrightEnabled, "O", new Color(255, 220, 50)));
        modButtons.add(new ModButton(3, sx, sy + gap * 3, bw, bh,
                "Chest ESP", PulseVisualMod.chestESPEnabled, "#", new Color(255, 180, 0)));
        modButtons.add(new ModButton(4, sx, sy + gap * 4, bw, bh,
                "X-Ray", PulseVisualMod.xrayEnabled, "X", new Color(255, 50, 50)));
        modButtons.add(new ModButton(5, sx, sy + gap * 5, bw, bh,
                "Mob Glow", PulseVisualMod.mobGlowEnabled, "+", new Color(180, 50, 255)));
        modButtons.add(new ModButton(6, sx, sy + gap * 6 + 10, bw, bh,
                "Health Tags", PulseVisualMod.healthTagsEnabled, "H", new Color(255, 80, 80)));
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float pt) {
        fill(ms, 0, 0, width, height, new Color(0, 0, 0, 120).getRGB());

        fillGradient(ms, guiX, guiY, guiX + guiWidth, guiY + guiHeight,
                new Color(15, 5, 30, 240).getRGB(),
                new Color(25, 8, 50, 245).getRGB());

        float pulse = (float)(Math.sin(System.currentTimeMillis() / 600.0) * 0.4 + 0.6);
        int ba = (int)(200 * pulse);
        int bc = new Color(160, 0, 255, ba).getRGB();

        fill(ms, guiX, guiY, guiX + guiWidth, guiY + 2, bc);
        fill(ms, guiX, guiY + guiHeight - 2, guiX + guiWidth, guiY + guiHeight, bc);
        fill(ms, guiX, guiY, guiX + 2, guiY + guiHeight, bc);
        fill(ms, guiX + guiWidth - 2, guiY, guiX + guiWidth, guiY + guiHeight, bc);

        fillGradient(ms, guiX + 2, guiY + 2, guiX + guiWidth - 2, guiY + 40,
                new Color(140, 0, 255, 30).getRGB(),
                new Color(140, 0, 255, 0).getRGB());

        String title = "\u00A7d\u00A7lPULSE \u00A7f\u00A7lVISUAL";
        int tw = font.width(title);
        font.drawShadow(ms, title, guiX + (guiWidth - tw) / 2f, guiY + 14, 0xFFFFFF);

        String sub = "\u00A78Visual Enhancement Mod";
        int sw = font.width(sub);
        font.drawShadow(ms, sub, guiX + (guiWidth - sw) / 2f, guiY + 28, 0x888888);

        int lineY = guiY + 45;
        for (int i = guiX + 15; i < guiX + guiWidth - 15; i++) {
            float prog = (float)(i - guiX - 15) / (guiWidth - 30);
            float wave = (float)(Math.sin(prog * Math.PI + System.currentTimeMillis() / 500.0) * 0.5 + 0.5);
            fill(ms, i, lineY, i + 1, lineY + 1,
                    new Color(160, 0, 255, (int)(100 * wave)).getRGB());
        }

        for (ModButton btn : modButtons) {
            drawButton(ms, btn, mouseX, mouseY);
        }

        int fy = guiY + guiHeight - 22;
        font.drawShadow(ms, "\u00A78v1.0.0", guiX + 10, fy, 0x555555);

        long active = modButtons.stream().filter(b -> b.enabled).count();
        String ac = "\u00A7d" + active + "\u00A77/" + modButtons.size() + " active";
        int acw = font.width(ac);
        font.drawShadow(ms, ac, guiX + (guiWidth - acw) / 2f, fy, 0xAAAAAA);

        String hint = "\u00A77RShift to close";
        int hw = font.width(hint);
        font.drawShadow(ms, hint, guiX + guiWidth - hw - 10, fy, 0x777777);

        super.render(ms, mouseX, mouseY, pt);
    }

    private void drawButton(MatrixStack ms, ModButton btn, int mx, int my) {
        boolean hovered = btn.isHovered(mx, my);
        btn.hover = hovered ? Math.min(1f, btn.hover + 0.1f) : Math.max(0f, btn.hover - 0.08f);

        int bgA = (int)(40 + 30 * btn.hover);
        Color bg;
        if (btn.enabled) {
            bg = new Color(btn.accent.getRed() / 4, btn.accent.getGreen() / 4,
                    btn.accent.getBlue() / 4, bgA + 20);
        } else {
            bg = new Color(30, 30, 40, bgA);
        }

        fill(ms, btn.x + 2, btn.y + 2, btn.x + btn.w + 2, btn.y + btn.h + 2,
                new Color(0, 0, 0, 30).getRGB());
        fill(ms, btn.x, btn.y, btn.x + btn.w, btn.y + btn.h, bg.getRGB());

        Color frame;
        if (btn.enabled) {
            float glow = (float)(Math.sin(System.currentTimeMillis() / 800.0 + btn.id) * 0.3 + 0.7);
            frame = new Color(
                    Math.min(255, (int)(btn.accent.getRed() * glow)),
                    Math.min(255, (int)(btn.accent.getGreen() * glow)),
                    Math.min(255, (int)(btn.accent.getBlue() * glow)),
                    Math.min(255, (int)(150 + 105 * glow)));
        } else {
            frame = new Color(80, 80, 100, (int)(60 + 40 * btn.hover));
        }

        fill(ms, btn.x, btn.y, btn.x + btn.w, btn.y + 1, frame.getRGB());
        fill(ms, btn.x, btn.y + btn.h - 1, btn.x + btn.w, btn.y + btn.h, frame.getRGB());

        if (btn.enabled) {
            fill(ms, btn.x, btn.y, btn.x + 3, btn.y + btn.h, btn.accent.getRGB());
        } else {
            fill(ms, btn.x, btn.y, btn.x + 3, btn.y + btn.h, new Color(60, 60, 70).getRGB());
        }

        if (btn.hover > 0) {
            fillGradient(ms, btn.x + 3, btn.y + 1,
                    btn.x + (int)(btn.w * 0.4f), btn.y + btn.h - 1,
                    new Color(255, 255, 255, (int)(15 * btn.hover)).getRGB(),
                    new Color(255, 255, 255, 0).getRGB());
        }

        int textY = btn.y + (btn.h - 8) / 2;
        font.drawShadow(ms, btn.icon, btn.x + 12, textY,
                btn.enabled ? btn.accent.getRGB() : 0x666666);
        font.drawShadow(ms, btn.name, btn.x + 28, textY,
                btn.enabled ? 0xFFFFFF : 0x888888);

        String status = btn.enabled ? "ON" : "OFF";
        Color sc = btn.enabled ? new Color(0, 255, 100) : new Color(255, 60, 60);
        int statusW = font.width(status) + 8;
        int statusX = btn.x + btn.w - statusW - 8;
        int statusY = btn.y + (btn.h - 12) / 2;

        fill(ms, statusX, statusY, statusX + statusW, statusY + 12,
                new Color(sc.getRed(), sc.getGreen(), sc.getBlue(), 30).getRGB());
        fill(ms, statusX, statusY, statusX + statusW, statusY + 1, sc.getRGB());
        fill(ms, statusX, statusY + 11, statusX + statusW, statusY + 12, sc.getRGB());
        font.drawShadow(ms, status, statusX + 4, statusY + 2, sc.getRGB());

        if (btn.enabled) {
            float dp = (float)(Math.sin(System.currentTimeMillis() / 400.0 + btn.id * 0.5) * 0.5 + 0.5);
            fill(ms, btn.x + btn.w - 8, btn.y + btn.h / 2 - 1,
                    btn.x + btn.w - 5, btn.y + btn.h / 2 + 2,
                    new Color(btn.accent.getRed(), btn.accent.getGreen(),
                            btn.accent.getBlue(), (int)(100 + 155 * dp)).getRGB());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        for (ModButton btn : modButtons) {
            if (btn.isHovered(mouseX, mouseY)) {
                minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.5f, 1f);

                switch (btn.id) {
                    case 0:
                        PulseVisualMod.espEnabled = !PulseVisualMod.espEnabled;
                        btn.enabled = PulseVisualMod.espEnabled;
                        break;
                    case 1:
                        PulseVisualMod.tracersEnabled = !PulseVisualMod.tracersEnabled;
                        btn.enabled = PulseVisualMod.tracersEnabled;
                        break;
                    case 2:
                        PulseVisualMod.fullbrightEnabled = !PulseVisualMod.fullbrightEnabled;
                        btn.enabled = PulseVisualMod.fullbrightEnabled;
                        if (btn.enabled) minecraft.options.gamma = 100.0;
                        else minecraft.options.gamma = 1.0;
                        break;
                    case 3:
                        PulseVisualMod.chestESPEnabled = !PulseVisualMod.chestESPEnabled;
                        btn.enabled = PulseVisualMod.chestESPEnabled;
                        break;
                    case 4:
                        PulseVisualMod.xrayEnabled = !PulseVisualMod.xrayEnabled;
                        btn.enabled = PulseVisualMod.xrayEnabled;
                        minecraft.levelRenderer.allChanged();
                        break;
                    case 5:
                        PulseVisualMod.mobGlowEnabled = !PulseVisualMod.mobGlowEnabled;
                        btn.enabled = PulseVisualMod.mobGlowEnabled;
                        break;
                    case 6:
                        PulseVisualMod.healthTagsEnabled = !PulseVisualMod.healthTagsEnabled;
                        btn.enabled = PulseVisualMod.healthTagsEnabled;
                        break;
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            minecraft.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}