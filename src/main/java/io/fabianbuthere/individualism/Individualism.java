package io.fabianbuthere.individualism;

import io.fabianbuthere.individualism.block.ModBlocks;
import io.fabianbuthere.individualism.item.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Individualism.MOD_ID)
public class Individualism {
    public static final String MOD_ID = "individualism";
    private static final Logger LOGGER = LogManager.getLogger();

    @SuppressWarnings("removal")
    public Individualism() {
        this(FMLJavaModLoadingContext.get());
    }

    public Individualism(FMLJavaModLoadingContext context) {
        LOGGER.info("Initializing Individualism mod");

        IEventBus modEventBus = context.getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Individualism common setup");
    }
}
