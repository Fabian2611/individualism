package io.fabianbuthere.individualism.item;

import io.fabianbuthere.individualism.Individualism;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Individualism.MOD_ID);

    public static final RegistryObject<CreativeModeTab> INDIVIDUALISM_TAB = CREATIVE_MODE_TABS.register("individualism_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.individualism"))
                    .icon(() -> ModItems.GENERIC_HAT.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.GENERIC_HAT.get());
                        // TODO: Populate at resource reload
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
