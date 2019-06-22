package davidhickey.bukkit_rtp;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.World;
import java.util.HashMap;

public class RTPPlugin extends JavaPlugin {

    private HashMap<Player, Long> lastUsed;
    private HashMap<World, WorldInfo> enabledWorldInfo;

    private boolean enabledForWorld(World w) {
        return enabledWorldInfo.hasKey(w);
    }

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        lastUsed = new HashMap<>();
        enabledWorldInfo = new HashMap<>();
    }
}
