package davidhickey.bukkit_rtp;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import davidhickey.bukkit_rtp.storage.*;
import davidhickey.bukkit_rtp.command.*;

public class RTPPlugin extends JavaPlugin {

    private RTPDataStore storedData;
    private SuperCommand rtpCommand;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        storedData = new RTPDataStore(getConfig(), getLogger(), this.getServer().getWorlds());

        PluginCommand bukkitRtpCommand = getCommand("rtp");

        rtpCommand = new SuperCommand(this, bukkitRtpCommand);
        rtpCommand.addSubCommand(new RTPTeleportCommand(this));
        rtpCommand.addHelpCommand();

        bukkitRtpCommand.setTabCompleter(new RTPCommandTabCompleter());
        bukkitRtpCommand.setExecutor(new CommandExecutor(){
            public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
                return rtpCommand.executeSuperCommand(sender, command, alias, args);
            }
        });
    }

    public RTPDataStore getDataStorage() {
        return storedData;
    }
}
