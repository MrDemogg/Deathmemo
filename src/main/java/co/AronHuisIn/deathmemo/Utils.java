package co.AronHuisIn.deathmemo;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
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

    public static String getOwoExampleName(String xmlId, String exampleId)
    {
        return exampleId + "@" + Deathmemo.MODID + ":" + xmlId;
    }
}
