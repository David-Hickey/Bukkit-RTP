package davidhickey.bukkit_rtp.command;

import davidhickey.bukkit_rtp.RTPPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class RTPReloadCommand extends SubCommand {

    public RTPReloadCommand(RTPPlugin plugin) {
        super(
            plugin,
            plugin.getCommand("rtp"),
            RTPReloadCommand.makeExecutor(plugin),
            "reload",
            "reload the configuration",
            "rtp.reload",
            "/<command> reload",
            "rl"
        );
    }

    private static SubCommandExecutor makeExecutor(final RTPPlugin plugin) {
        return new SubCommandExecutor() {
            public boolean execute(SubCommand command, CommandSender sender, String superAlias, String alias, String[] args) {
                long start = System.currentTimeMillis();
                sender.sendMessage(ChatColor.GRAY + "Reloading RTP config...");
                plugin.reloadDataStorage();
                sender.sendMessage(ChatColor.GRAY + "Reloading RTP config finished in "
                    + ChatColor.GOLD + (System.currentTimeMillis() - start) + " ms"
                    + ChatColor.GRAY + ".");

                return true;
            }
        };
    }
}
