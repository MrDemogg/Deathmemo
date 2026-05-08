package co.AronHuisIn.deathmemo;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(value = Deathmemo.MODID, dist = Dist.CLIENT)
public class Deathmemo {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "deathmemo";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public Deathmemo() {}
}
