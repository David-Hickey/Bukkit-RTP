package davidhickey.bukkit_rtp.command;

import org.bukkit.command.CommandSender;

public interface SubCommandExecutor {

    public boolean execute(SubCommand command, CommandSender sender, String alias, String[] args);

}
