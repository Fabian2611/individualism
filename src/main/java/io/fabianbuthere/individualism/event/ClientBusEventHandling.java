package io.fabianbuthere.individualism.event;

import io.fabianbuthere.individualism.Individualism;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Individualism.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientBusEventHandling {
    // Use for client side mod loading events
}
