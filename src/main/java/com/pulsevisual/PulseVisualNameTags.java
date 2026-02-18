package com.pulsevisual;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PulseVisualNameTags {

    private Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Post<?, ?> event) {
        if (!PulseVisualMod.healthTagsEnabled) return;

        LivingEntity entity = event.getEntity();
        if (!(entity instanceof PlayerEntity)) return;
        if (entity == mc.player) return;

        float hp = entity.getHealth();
        float maxHp = entity.getMaxHealth();
        float pct = hp / maxHp;

        String color;
        if (pct > 0.6f) color = "\u00A7a";
        else if (pct > 0.3f) color = "\u00A7e";
        else color = "\u00A7c";

        String text = color + String.format("%.1f HP", hp);

        MatrixStack ms = event.getMatrixStack();
        ms.pushPose();
        ms.translate(0, entity.getBbHeight() + 0.5, 0);
        ms.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
        ms.scale(-0.025f, -0.025f, 0.025f);

        int tw = mc.font.width(text);
        mc.font.drawInBatch(text, -tw / 2f, 0, 0xFFFFFF,
                true, ms.last().pose(), event.getBuffers(),
                true, 0, 15728880);

        ms.popPose();
    }
}