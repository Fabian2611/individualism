package io.fabianbuthere.individualism;

import io.fabianbuthere.individualism.data.ClothingLoader;
import io.fabianbuthere.individualism.data.ClothingRegistry;
import io.fabianbuthere.individualism.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Individualism.MOD_ID)
public class Individualism {
    public static final String MOD_ID = "individualism";
    private static final Logger LOGGER = LogManager.getLogger();

    private static ClothingRegistry clothingRegistry;
    private static ClothingLoader clothingLoader;

    public static final CreativeModeTab CLOTHING_TAB = CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.individualism.clothing"))
            .icon(() -> new ItemStack(ModItems.CLOTHING_ICON.get()))
            .build();

    @SuppressWarnings("removal")
    public Individualism() {
        this(FMLJavaModLoadingContext.get());
    }

    public Individualism(FMLJavaModLoadingContext context) {
        LOGGER.info("Initializing Individualism mod");

        IEventBus modEventBus = context.getModEventBus();

        ModItems.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        ArmorMaterial clothingMaterial = createClothingMaterial();
        clothingRegistry = new ClothingRegistry(clothingMaterial);
        clothingRegistry.register(modEventBus);
        clothingLoader = new ClothingLoader(clothingRegistry);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Individualism common setup");
    }

    private ArmorMaterial createClothingMaterial() {
        return new ArmorMaterial() {
            @Override
            public int getDurabilityForType(ArmorItem.Type type) {
                return 0;
            }

            @Override
            public int getDefenseForType(ArmorItem.Type type) {
                return 0;
            }

            @Override
            public int getEnchantmentValue() {
                return 15;
            }

            @Override
            public SoundEvent getEquipSound() {
                return SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("minecraft", "item.armor.equip_leather"));
            }

            @Override
            public Ingredient getRepairIngredient() {
                return Ingredient.EMPTY;
            }

            @Override
            public String getName() {
                return MOD_ID + ":clothing";
            }

            @Override
            public float getToughness() {
                return 0.0F;
            }

            @Override
            public float getKnockbackResistance() {
                return 0.0F;
            }
        };
    }

    @SubscribeEvent
    public void onAddReloadListeners(AddReloadListenerEvent event) {
        LOGGER.info("Registering clothing data reload listener");
        event.addListener(clothingLoader);
    }

    public static ClothingRegistry getClothingRegistry() {
        return clothingRegistry;
    }

    public static ClothingLoader getClothingLoader() {
        return clothingLoader;
    }
}
