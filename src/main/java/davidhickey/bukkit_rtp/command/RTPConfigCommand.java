package davidhickey.bukkit_rtp.command;

import davidhickey.bukkit_rtp.RTPPlugin;
import davidhickey.bukkit_rtp.storage.*;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.World;
import java.util.UUID;
import davidhickey.davelib.subcommand.*;

public class RTPConfigCommand extends SubCommand {

    public RTPConfigCommand(RTPPlugin plugin) {
        super(
            plugin,
            plugin.getCommand("rtp"),
            RTPConfigCommand.makeExecutor(plugin),
            "config",
            "view the configuration",
            "rtp.config",
            "/<command> config",
            "conf", "configuration"
        );
    }

    private static SubCommandExecutor makeExecutor(final RTPPlugin plugin) {
        return new SubCommandExecutor() {
            public boolean execute(SubCommand command, CommandSender sender, String superAlias, String alias, String[] args) {
                RTPDataStore data = plugin.getDataStorage();

                sender.sendMessage(ChatColor.GRAY + "Configuration for " + ChatColor.GOLD + "RTP" + ChatColor.GRAY + ":");
                sender.sendMessage(ChatColor.GRAY + " - cooldown: " + ChatColor.GOLD + data.getCooldown() + " seconds");
                sender.sendMessage(ChatColor.GRAY + " - max checks: " + ChatColor.GOLD + data.getMaxChecks());
                sender.sendMessage(ChatColor.GRAY + " - enabled worlds:");

                if (data.getEnabledWorldIDs().isEmpty()) {
                    sender.sendMessage(ChatColor.GOLD + "      none!");
                }

                for (UUID worldId : data.getEnabledWorldIDs()) {
                    World world = plugin.getServer().getWorld(worldId);
                    RTPWorldInfo worldInfo = data.getWorldInfo(world);

                    sender.sendMessage(ChatColor.GRAY + "    - " + ChatColor.GOLD + world.getName()
                        + ChatColor.GRAY + " (radius: " + ChatColor.GOLD + worldInfo.getRadius()
                        + ChatColor.GRAY + ", centre: [" + ChatColor.GOLD + worldInfo.getCentre().getBlockX()
                        + ChatColor.GRAY + ", " + ChatColor.GOLD + worldInfo.getCentre().getBlockZ()
                        + ChatColor.GRAY + "])");
                }


                return true;
            }
        };
    }
}
