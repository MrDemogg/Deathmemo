package co.AronHuisIn.deathmemo.Data;

import co.AronHuisIn.deathmemo.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

public class InventorySnapshot {
    private final RegistryAccess registryAccess;

    public String dateTime;
    public String place;
    public Vector3i pos;
    public String dimension;
    public int xp;

    public List<ItemStack> items;
    public List<ItemStack> offhand;
    public List<ItemStack> armor;
    public List<ItemStack> allStacks()
    {
        List<ItemStack> all = new ArrayList<>();
        all.addAll(items);
        all.addAll(offhand);
        all.addAll(armor);
        return all;
    }

    public InventorySnapshot(String dateTime, String placeName, Vector3i pos, String dimension, int xp, Inventory inventory, RegistryAccess registryAccess) {
        this.dateTime = dateTime;
        this.place = placeName;
        this.pos = pos;
        this.registryAccess = registryAccess;
        this.dimension = dimension;
        this.xp = xp;

        items = Utils.copyItemStacks(inventory.items);
        offhand = Utils.copyItemStacks(inventory.offhand);
        armor = Utils.copyItemStacks(inventory.armor);
    }

    public InventorySnapshot(JsonObject snapshotJson, RegistryAccess registryAccess)
    {
        this.registryAccess = registryAccess;
        items = loadStacksArray(snapshotJson.get(JsonDataKeys.ITEMS));
        offhand = loadStacksArray(snapshotJson.get(JsonDataKeys.OFFHAND));
        armor = loadStacksArray(snapshotJson.get(JsonDataKeys.ARMOR));

        dateTime = snapshotJson.get(JsonDataKeys.DATE_TIME).getAsString();
        place = snapshotJson.get(JsonDataKeys.PLACE).getAsString();
        pos = new Vector3i(snapshotJson.get(JsonDataKeys.POS).getAsJsonArray().asList().stream().mapToInt(JsonElement::getAsInt).toArray());
        xp = snapshotJson.get(JsonDataKeys.XP).getAsInt();
        dimension = snapshotJson.get(JsonDataKeys.DIMENSION).getAsString();
    }

    public String Serialize()
    {
        JsonObject jsonData = new JsonObject();
        jsonData.add(JsonDataKeys.ITEMS, saveStacksArray(items));
        jsonData.add(JsonDataKeys.ARMOR, saveStacksArray(armor));
        jsonData.add(JsonDataKeys.OFFHAND, saveStacksArray(offhand));
        jsonData.addProperty(JsonDataKeys.DATE_TIME, dateTime);
        jsonData.addProperty(JsonDataKeys.PLACE, place);
        jsonData.addProperty(JsonDataKeys.XP, xp);
        jsonData.addProperty(JsonDataKeys.DIMENSION, dimension);

        JsonArray posJson = new JsonArray(3);
        posJson.add(pos.x);
        posJson.add(pos.y);
        posJson.add(pos.z);

        jsonData.add(JsonDataKeys.POS, posJson);

        return jsonData.toString();
    }

    private List<ItemStack> loadStacksArray(JsonElement elem) {
        List<ItemStack> list = new ArrayList<>();
        if (elem == null || !elem.isJsonArray()) return list;
        for (JsonElement e : elem.getAsJsonArray()) {
            if (e.isJsonNull()) {
                list.add(ItemStack.EMPTY);
            } else {
                list.add(Utils.deserializeItemStack(e, registryAccess));
            }
        }
        return list;
    }

    private JsonArray saveStacksArray(List<ItemStack> list) {
        JsonArray arr = new JsonArray();
        for (ItemStack stack : list) {
            if (stack.isEmpty()) {
                arr.add(JsonNull.INSTANCE);
            } else {
                arr.add(Utils.serializeItemStack(stack, registryAccess));
            }
        }
        return arr;
    }
}
