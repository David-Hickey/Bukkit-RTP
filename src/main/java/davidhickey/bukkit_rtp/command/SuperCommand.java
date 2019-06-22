package davidhickey.bukkit_rtp.command;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.PluginCommand;

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
                printHelp(sender);
                return true;
            }
        }, "help", "display help", null, "help", "h"));
    }

    protected void printHelp(CommandSender sender) {
        for (SubCommand command : commands) {
            sender.sendMessage(command.getName() + ":");
            sender.sendMessage("    " + command.getDescription());
            sender.sendMessage("    " + command.getUsage());
            sender.sendMessage("    " + command.getPermission());
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
                printHelp(sender);
            }

            return true;
        }

        String[] subCommandArgs = Arrays.copyOf(args, args.length - 1);
        String subCommandAlias = args[0];

        SubCommand toExecute = this.getByNameOrAlias(subCommandAlias);

        return toExecute.execute(sender, alias, subCommandAlias, subCommandArgs);
    }

}
