package io.fabianbuthere.individualism.event;

import io.fabianbuthere.individualism.Individualism;
import io.fabianbuthere.individualism.client.model.CustomModelLoader;
import io.fabianbuthere.individualism.client.model.ModelMetadataLoader;
import io.fabianbuthere.individualism.client.renderer.PlayerModelRenderer;
import io.fabianbuthere.individualism.item.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Individualism.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientBusEventHandling {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(PlayerModelRenderer::init);
        // TODO: Test and improve this
        event.enqueueWork(() -> ItemProperties.register(
                ModItems.GENERIC_HAT.get(),
                ResourceLocation.fromNamespaceAndPath(Individualism.MOD_ID, "variant"),
                (stack, level, entity, seed) -> stack.getOrCreateTag().getInt("variant")
        ));
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(final RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(CustomModelLoader.getInstance());
        event.registerReloadListener(ModelMetadataLoader.getInstance());
    }
}
