package co.AronHuisIn.deathmemo;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static JsonElement serializeItemStack(ItemStack stack, RegistryAccess registryAccess) {
        var ops = RegistryOps.create(JsonOps.INSTANCE, registryAccess);
        var result = ItemStack.CODEC.encodeStart(ops, stack);
        return result.result().orElseThrow(() ->
                new IllegalStateException("Failed to serialize ItemStack: " + result.error())
        );
    }

    public static ItemStack deserializeItemStack(JsonElement json, RegistryAccess registryAccess) {
        var ops = RegistryOps.create(JsonOps.INSTANCE, registryAccess);
        var result = ItemStack.CODEC.parse(ops, json);
        return result.result().orElseThrow(() ->
                new IllegalStateException("Failed to deserialize ItemStack: " + result.error())
        );
    }

    public static List<ItemStack> copyItemStacks(List<ItemStack> originalStacks)
    {
        List<ItemStack> copyStacks = new ArrayList<>();
        for (ItemStack itemStack : originalStacks)
        {
            copyStacks.add(itemStack.copy());
        }

        return copyStacks;
    }

    public static long calculateInventoryHash(Inventory inventory) {
        List<ItemStack> inventoryItems = new ArrayList<>();
        inventoryItems.addAll(inventory.items);
        inventoryItems.addAll(inventory.offhand);
        inventoryItems.addAll(inventory.armor);
        return calculateInventoryHash(inventoryItems);
    }

    public static long calculateInventoryHash(List<ItemStack> inventoryItems) {
        long hash = 1L;

        for (int slot = 0; slot < inventoryItems.size(); slot++) {
            ItemStack stack = inventoryItems.get(slot);

            hash = 31 * hash + slot;

            if (stack.isEmpty()) {
                hash = 31 * hash;
                continue;
            }

            hash = 31 * hash + net.minecraft.core.registries.BuiltInRegistries.ITEM.getId(stack.getItem());
            hash = 31 * hash + stack.getCount();
            hash = 31 * hash + stack.getDamageValue();
        }

        return hash;
    }

    public static String getOwoExampleName(String xmlId, String exampleId)
    {
        return exampleId + "@" + Deathmemo.MODID + ":" + xmlId;
    }
}
