package davidhickey.bukkit_rtp.command;

import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;

public class SubCommand {

    protected final JavaPlugin plugin;
    protected final PluginCommand parent;
    protected final SubCommandExecutor executor;
    protected final String name;
    protected final String[] aliases;
    protected final String description;
    protected final String permission;
    protected final String usage;

    public SubCommand(JavaPlugin plugin, PluginCommand parent, SubCommandExecutor executor, String name, String description, String permission, String usage, String... aliases) {
        this.plugin = plugin;
        this.parent = parent;
        this.executor = executor;
        this.name = name;
        this.description = description;
        this.permission = permission;
        this.usage = usage;
        this.aliases = aliases;
    }

    public boolean execute(CommandSender sender, String superAlias, String alias, String[] args) {
        if (this.hasPermission(sender)) {
            return this.executor.execute(this, sender, superAlias, alias, args);
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
            return true;
        }
    }

    public PluginCommand getParentCommand() {
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

    public String getUsage(String superAlias) {
        return this.usage.replace("<command>", superAlias);
    }

    public boolean hasPermission(CommandSender sender) {
        return this.permission == null || sender.hasPermission(this.permission);
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

    public boolean hasCommonNamesOrAliases(SubCommand other) {
        if (this.isNameOrAlias(other.getName())) {
            return true;
        }

        for (String alias : other.getAliases()) {
            if (this.isNameOrAlias(alias)) {
                return true;
            }
        }

        return false;
    }
}
