package io.fabianbuthere.individualism.event;

import io.fabianbuthere.individualism.Individualism;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Individualism.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerBusEventHandling {
    // Use for server side lifecycle events
    // Make sure to manually ensure server side only handling
}
