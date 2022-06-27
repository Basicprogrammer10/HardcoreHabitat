package com.connorcode.hardcorehabitat;

import com.mojang.logging.LogUtils;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HardcoreStateLoader {
    public static boolean load() throws IOException {
        File hardcoreFile = getFile();

        // Check if file exists, if not make it and fill it with 0
        if (!hardcoreFile.exists()) {
            assert hardcoreFile.createNewFile();
            Files.write(hardcoreFile.toPath(), new byte[]{1});
            return true;
        }

        // Read file
        byte[] bytes = Files.readAllBytes(hardcoreFile.toPath());
        if (bytes.length != 1) {
            invalidFile();
            return true;
        }

        // Parse file data
        if (bytes[0] != 0b0 && bytes[0] != 0b1) {
            invalidFile();
            return true;
        }

        return bytes[0] == 0b1;
    }

    public static void save(boolean state) throws IOException {
        File hardcoreFile = getFile();
        Files.write(hardcoreFile.toPath(), new byte[]{(byte) (state ? 1 : 0)});
    }

    private static File getFile() {
        // Get path to /world/hardcore
        Path worldFolder = FabricLoader.getInstance().getGameDir().resolve(HardcoreHabitat.properties.levelName);
        return new File(worldFolder + File.separator + "hardcore");
    }

    private static void invalidFile() {
        LogUtils.getLogger().error("Hardcore file is invalid, assuming season is running");
    }
}
