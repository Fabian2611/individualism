package io.fabianbuthere.individualism.event;

import io.fabianbuthere.individualism.Individualism;
import io.fabianbuthere.individualism.render.ClothingWearableRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Individualism.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandling {

    @SubscribeEvent
    public static void onResourceReload(RegisterClientReloadListenersEvent event) {
        ClothingWearableRenderer.clearCache();
    }

    @SubscribeEvent
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        Minecraft.getInstance().execute(ClothingWearableRenderer::clearCache);
    }
}
