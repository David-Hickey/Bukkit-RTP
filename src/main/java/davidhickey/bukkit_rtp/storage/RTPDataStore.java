package davidhickey.bukkit_rtp.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.List;

public class RTPDataStore {

    private final Map<UUID, RTPWorldInfo> worldInfo;
    private final Map<UUID, Long> lastTeleported;
    private final int cooldown;

    // maximum number of locations to check before giving up on finding a safe
    // spot to drop the player.
    private final int maxChecks;

    public RTPDataStore(FileConfiguration conf, Logger logger, List<World> worlds) {
        this.worldInfo = new HashMap<>();
        this.lastTeleported = new HashMap<>();

        if (conf.isInt("cooldown-seconds")) {
            this.cooldown = conf.getInt("cooldown-seconds");
        } else {
            logger.warning("Configuration key 'cooldown-seconds' is missing or invalid, defaulting to 60...");
            this.cooldown = 60;
        }

        if (conf.isInt("maximum-checks")) {
            this.maxChecks = conf.getInt("maximum-checks");
        } else {
            logger.warning("Configuration key 'maximum-checks' is missing or invalid, defaulting to 100...");
            this.maxChecks = 100;
        }

        ConfigurationSection enabledWorldsSection = conf.getConfigurationSection("enabled-worlds");

        // Populate the worldInfo map, one world at a time, using the Configuration
        // file given. Any worlds with malformed configuration are skipped with
        // a warning.
        for (String key : enabledWorldsSection.getKeys(false)) {
            World world;
            try {
                world = worldByName(key, worlds);
            } catch (ConfigurationException ex) {
                logger.warning("World '" + key + "' does not exist, ignoring configuration entry...'");
                continue;
            }

            if (!enabledWorldsSection.isInt("radius")) {
                logger.warning("Radius for world '" + key + "' missing or invalid, ignoring configuration entry...'");
                continue;
            }

            if (!enabledWorldsSection.isConfigurationSection(("centre"))) {
                logger.warning("Centre for world '" + key + "' missing or invalid, ignoring configuration entry...'");
                continue;
            }

            int radius = enabledWorldsSection.getInt("radius");
            Location centre;
            try {
                centre = parseConfigurationSectionAsLocation(world, enabledWorldsSection.getConfigurationSection("centre"));
            } catch (ConfigurationException ex) {
                logger.warning("Centre for world '" + key + "' is malformed, ignoring configuration entry...'");
                continue;
            }

            RTPWorldInfo info = new RTPWorldInfo(radius, centre);
            worldInfo.put(world.getUID(), info);
        }
    }

    // throws a ConfigurationException if the world isn't there.
    private World worldByName(String name, List<World> worlds) throws ConfigurationException {
        for (World w : worlds) {
            if (w.getName().equalsIgnoreCase(name)) {
                return w;
            }
        }

        throw new ConfigurationException("World doesn't seem to exist");
    }

    // throws a ConfigurationException if the configuration is malformed.
    private Location parseConfigurationSectionAsLocation(World w, ConfigurationSection s) throws ConfigurationException {
        // y is optional, so if it's not given let's default it to 0.
        int y = s.isInt("y") ? s.getInt("y") : 0;

        int x, z;
        if (s.isInt("x")) {
            x = s.getInt("x");
        } else {
            throw new ConfigurationException("missing an x-coordinate");
        }

        if (s.isInt("z")) {
            z = s.getInt("z");
        } else {
            throw new ConfigurationException("missing a z-coordinate");
        }

        return new Location(w, x, y, z);
    }

    public boolean isEnabledForWorld(World w) {
        return worldInfo.containsKey(w.getUID());
    }

    public boolean canPlayerTeleportNow(Player p, long timeNow) {
        if (lastTeleported.containsKey(p.getUniqueId())) {
            return timeNow - lastTeleported.get(p.getUniqueId()) > 20 * this.cooldown;
        } else {
            return true;
        }
    }

    public void playerTeleported(Player p, long timeNow) {
        lastTeleported.put(p.getUniqueId(), timeNow);
    }

    public RTPWorldInfo getWorldInfo(World w) {
        return this.worldInfo.get(w.getUID());
    }

    public int getMaxChecks() {
        return this.maxChecks;
    }

    public int getCooldown() {
        return this.cooldown;
    }
}
