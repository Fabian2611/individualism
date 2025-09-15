package io.fabianbuthere.individualism.item;

import io.fabianbuthere.individualism.Individualism;
import io.fabianbuthere.individualism.item.custom.WearableItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Individualism.MOD_ID);

    public static final RegistryObject<Item> GENERIC_HAT = ITEMS.register("generic_hat",
            () -> new WearableItem(new Item.Properties().stacksTo(1), ArmorItem.Type.HELMET));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
