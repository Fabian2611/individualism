package io.fabianbuthere.individualism.item.custom;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

/**
 * An item which can be worn in a specific armor slot.
 * Does not provide any armor properties or rendering behavior.
 */
// TODO: Make this have no rendering behavior
public class WearableItem extends Item implements Equipable {
    private final ArmorItem.Type type;
    private final EquipmentSlot equipmentSlot;

    public WearableItem(Properties properties, ArmorItem.Type type) {
        super(properties);
        this.type = type;
        this.equipmentSlot = switch(type) {
            case HELMET -> EquipmentSlot.HEAD;
            case CHESTPLATE -> EquipmentSlot.CHEST;
            case LEGGINGS -> EquipmentSlot.LEGS;
            case BOOTS -> EquipmentSlot.FEET;
        };
    }

    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return equipmentSlot;
    }
}
