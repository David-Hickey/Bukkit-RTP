package davidhickey.bukkit_rtp;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import davidhickey.bukkit_rtp.storage.RTPDataStore;

public class RTPPlugin extends JavaPlugin {

    private RTPDataStore storedData;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        storedData = new RTPDataStore(getConfig(), getLogger(), this.getServer().getWorlds());

        PluginCommand rtpCommand = getCommand("rtp");
        rtpCommand.setTabCompleter(new RTPCommandTabCompleter());
        rtpCommand.setExecutor(new RTPCommandExecutor());
    }
}
