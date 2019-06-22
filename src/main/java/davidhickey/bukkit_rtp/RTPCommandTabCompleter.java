package davidhickey.bukkit_rtp;

import org.bukkit.command.TabCompleter;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import java.util.Arrays;

public class RTPCommandTabCompleter implements TabCompleter {

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> outputList = new ArrayList<>(Arrays.asList("help"));

        for (String commandName : Arrays.asList("tp", "reload", "config")) {
            if (sender.hasPermission("rtp." + commandName)) {
                outputList.add(commandName);
            }
        }

        return outputList;
    }

}
