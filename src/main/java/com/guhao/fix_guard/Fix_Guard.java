package com.guhao.fix_guard;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("fix_guard")
public class Fix_Guard {
    public static final Logger LOGGER = LogManager.getLogger(Fix_Guard.class);
    public static final String LEGACY_MODID = "guhao";
    private static final String PROTOCOL_VERSION = "1";
    public static String VERSION = "N/A";
    public Fix_Guard() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    }

}

