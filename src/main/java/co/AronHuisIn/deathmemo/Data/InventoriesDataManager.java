package co.AronHuisIn.deathmemo.Data;

import co.AronHuisIn.deathmemo.Deathmemo;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.RegistryAccess;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class InventoriesDataManager {
    private static final InventoriesDataManager instance = new InventoriesDataManager();
    public static InventoriesDataManager getInstance() { return instance; }

    private List<InventorySnapshot> snapshots;
    private Path directory = Path.of("deathmemo");

    public List<InventorySnapshot> getSnapshots() { return snapshots; }
    public RegistryAccess registryAccess;

    public void loadSnapshots(String place, RegistryAccess registryAccess)
    {
        this.registryAccess = registryAccess;
        snapshots = new ArrayList<>();
        directory = net.minecraft.client.Minecraft
                .getInstance()
                .gameDirectory
                .toPath()
                .resolve(Deathmemo.MODID).resolve(place);
        if (!Files.isDirectory(directory)) return;
        try (Stream<Path> files = Files.list(directory)) {
            files.filter(path -> path.toString().endsWith(".json")) // только .json
                    .forEach(jsonFile -> {
                        try {
                            String rawJson = Files.readString(jsonFile);
                            JsonObject jsonObject = JsonParser.parseString(rawJson).getAsJsonObject();

                            InventorySnapshot snapshot = new InventorySnapshot(jsonObject, registryAccess);

                            snapshots.add(snapshot);

                        } catch (IOException e) {
                            System.err.println("Ошибка чтения файла: " + jsonFile + " - " + e.getMessage());
                        } catch (IllegalStateException e) {
                            System.err.println("Ошибка парсинга ItemStack из файла: " + jsonFile + " - " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            System.err.println("Ошибка при обходе директории: " + e.getMessage());
        }
    }

    public void addSnapshot(InventorySnapshot snapshot)
    {
        try {
            Files.createDirectories(directory); // ← ключевая строка

            Path filePath = directory.resolve(snapshot.dateTime + ".json");

            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                writer.write(snapshot.Serialize());
            }

            snapshots.add(snapshot);

        } catch (IOException e) {
            Deathmemo.LOGGER.error(e.getMessage());
        }
    }

    public void removeSnapshot(String dateTime)
    {
        try {
            Files.delete(directory.resolve(dateTime + ".json"));
            snapshots.removeIf(s -> s.dateTime.equals(dateTime));
        } catch (IOException e)
        {
            Deathmemo.LOGGER.error(e.getMessage());
        }
    }
}
