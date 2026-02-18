package com.pulsevisual;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("pulsevisual")
public class PulseVisualMod {

    public static final Logger LOGGER = LogManager.getLogger();

    public static boolean espEnabled = true;
    public static boolean fullbrightEnabled = true;
    public static boolean tracersEnabled = false;
    public static boolean chestESPEnabled = true;
    public static boolean mobGlowEnabled = true;
    public static boolean xrayEnabled = false;
    public static boolean healthTagsEnabled = true;

    public PulseVisualMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new PulseVisualRenderer());
        MinecraftForge.EVENT_BUS.register(new PulseVisualEvents());
        MinecraftForge.EVENT_BUS.register(new PulseVisualKeyHandler());
        MinecraftForge.EVENT_BUS.register(new PulseVisualNameTags());
        PulseVisualKeyHandler.register();
        LOGGER.info("[Pulse Visual] Loaded!");
    }
}