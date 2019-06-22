package davidhickey.bukkit_rtp;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;

public class RTPCommandExecutor implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        sender.sendMessage("Hello friend, keeping well?");
        return true;
    }
}
