package davidhickey.bukkit_rtp.command;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.PluginCommand;
import org.bukkit.ChatColor;

public class SuperCommand {

    protected final JavaPlugin plugin;
    protected final PluginCommand pluginCommand;
    protected final List<SubCommand> commands;
    protected SubCommand defaultSubCommand;

    public SuperCommand(JavaPlugin plugin, PluginCommand command) {
        this.plugin = plugin;
        this.pluginCommand = command;
        this.commands = new ArrayList<>();
        this.defaultSubCommand = null;
    }

    public void addSubCommand(SubCommand subCommand) {
        for (SubCommand preExistingCommand : commands) {
            if (preExistingCommand.hasCommonNamesOrAliases(subCommand)) {
                throw new IllegalArgumentException("Added command shares alias with previous command");
            }
        }

        commands.add(subCommand);
    }

    public SubCommand getByNameOrAlias(String nameOrAlias) {
        for (SubCommand command : this.commands) {
            if (command.isNameOrAlias(nameOrAlias)) {
                return command;
            }
        }

        return null;
    }

    public void addHelpCommand() {
        addSubCommand(new SubCommand(plugin, this.pluginCommand, new SubCommandExecutor(){
            public boolean execute(SubCommand command, CommandSender sender, String superAlias, String alias, String[] args) {
                printHelp(sender, superAlias);
                return true;
            }
        }, "help", "display help", null, "/<command> help", "h"));
    }

    protected void printHelp(CommandSender sender, String superAlias) {
        sender.sendMessage(ChatColor.GRAY + "Help for command " + ChatColor.GOLD + "/" + superAlias + ChatColor.GRAY + ":");
        for (SubCommand command : commands) {
            sender.sendMessage(" " + ChatColor.GOLD + "/" + superAlias + " " + command.getName() + ChatColor.GRAY + ":");
            sender.sendMessage("  " + ChatColor.GRAY + "Description: " + ChatColor.GOLD + command.getDescription());
            sender.sendMessage("  " + ChatColor.GRAY + "Usage: " + ChatColor.GOLD + command.getUsage(superAlias));
            if (command.getAliases().length > 0) {
                sender.sendMessage("  " + ChatColor.GRAY + "Aliases: " + ChatColor.GOLD
                    + String.join(ChatColor.GRAY + ", " + ChatColor.GOLD, command.getAliases()));
            }
            sender.sendMessage("");
        }
    }

    public void setDefaultSubcommand(SubCommand newDefault) {
        this.defaultSubCommand = newDefault;
    }

    public boolean executeSuperCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            if (this.defaultSubCommand != null) {
                this.defaultSubCommand.execute(sender, alias, this.defaultSubCommand.getName(), new String[0]);
            } else {
                printHelp(sender, alias);
            }

            return true;
        }

        String[] subCommandArgs = Arrays.copyOf(args, args.length - 1);
        String subCommandAlias = args[0];

        SubCommand toExecute = this.getByNameOrAlias(subCommandAlias);

        if (toExecute == null) {
            sender.sendMessage(ChatColor.RED + "Invalid subcommand - try " + ChatColor.DARK_RED + "/" + alias + " help" + ChatColor.RED + ".");
            return true;
        }

        return toExecute.execute(sender, alias, subCommandAlias, subCommandArgs);
    }

}
