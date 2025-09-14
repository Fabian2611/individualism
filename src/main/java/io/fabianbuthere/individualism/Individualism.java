package io.fabianbuthere.individualism;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Individualism.MOD_ID)
public class Individualism
{
    public static final String MOD_ID = "individualism";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Individualism(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();


        modEventBus.addListener(this::commonSetup);


        MinecraftForge.EVENT_BUS.register(this);
    }

    @SuppressWarnings("removal")
    public Individualism() {
        this(FMLJavaModLoadingContext.get());
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }
}
