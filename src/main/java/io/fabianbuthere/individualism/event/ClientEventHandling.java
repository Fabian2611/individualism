package io.fabianbuthere.individualism.event;

import io.fabianbuthere.individualism.Individualism;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Individualism.MOD_ID, value = Dist.CLIENT)
public class ClientEventHandling {
    // Use for client side runtime events
}
