package davidhickey.bukkit_rtp.command;

import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;

public class SubCommand {

    private final Command parent;
    private final SubCommandExecutor executor;
    private final String name;
    private final String[] aliases;
    private final String description;
    private final String permission;
    private final String usage;

    public SubCommand(Command parent, SubCommandExecutor executor, String name, String description, String permission, String usage, String... aliases) {
        this.parent = parent;
        this.executor = executor;
        this.name = name;
        this.description = description;
        this.permission = permission;
        this.usage = usage;
        this.aliases = aliases;
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        return this.executor.execute(this, sender, alias, args);
    }

    public Command getParentCommand() {
        return this.parent;
    }

    public String getName() {
        return this.name;
    }

    public String[] getAliases() {
        return this.aliases;
    }

    public String getDescription() {
        return this.description;
    }

    public String getPermission() {
        return this.permission;
    }

    public String getUsage() {
        return this.usage;
    }

    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(this.permission);
    }

    public boolean isNameOrAlias(String command) {
        if (command.equalsIgnoreCase(this.name)) {
            return true;
        }

        for (String alias : this.aliases) {
            if (alias.equalsIgnoreCase(command)) {
                return true;
            }
        }

        return false;
    }
}
