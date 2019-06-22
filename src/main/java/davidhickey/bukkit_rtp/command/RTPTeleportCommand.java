package davidhickey.bukkit_rtp.command;

import davidhickey.bukkit_rtp.RTPPlugin;
import davidhickey.bukkit_rtp.storage.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.Material;
import java.util.Random;

public class RTPTeleportCommand extends SubCommand {

    public RTPTeleportCommand(RTPPlugin plugin) {
        super(
            plugin,
            plugin.getCommand("rtp"),
            RTPTeleportCommand.makeExecutor(plugin),
            "tp",
            "teleport to a random wilderness location",
            "rtp.tp",
            "/<command> tp [radius]",
            "teleport", "tele", "t"
        );
    }

    private static SubCommandExecutor makeExecutor(final RTPPlugin plugin) {
        return new SubCommandExecutor() {
            public boolean execute(SubCommand command, CommandSender sender, String superAlias, String alias, String[] args) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can do that.");
                    return true;
                }

                Player player = (Player) sender;

                if (!plugin.getDataStorage().isEnabledForWorld(player.getWorld())) {
                    player.sendMessage(ChatColor.RED + "RTP is not enabled in this world.");
                    return true;
                }

                if (!plugin.getDataStorage().canPlayerTeleportNow(player, System.currentTimeMillis())) {
                    player.sendMessage(ChatColor.RED + "Wait a while before trying to teleport again.");
                    return true;
                }

                RTPWorldInfo worldInfo = plugin.getDataStorage().getWorldInfo(player.getWorld());
                int radius = worldInfo.getRadius();
                if (args.length >= 1) {
                    if (!player.hasPermission("rtp.tp.radius")) {
                        player.sendMessage(ChatColor.RED + "You don't have permission to set a custom radius.");
                        return true;
                    }

                    try {
                        radius = Integer.parseInt(args[0]);
                    } catch (NumberFormatException ex) {
                        player.sendMessage(ChatColor.RED + "That's an invalid radius.");
                        return true;
                    }

                    if (radius <= 1) {
                        player.sendMessage(ChatColor.RED + "Given radius is too small.");
                        return true;
                    }
                }

                Location destination = RTPTeleportCommand.getRandomSafeLocation(
                    player.getWorld(),
                    radius,
                    worldInfo.getCentre(),
                    plugin.getDataStorage().getMaxChecks()
                );

                player.teleport(destination);

                plugin.getDataStorage().playerTeleported(player, System.currentTimeMillis());

                return true;
            }
        };
    }

    private static Location getRandomSafeLocation(World world, int radius, Location centre, int maxChecks) {
        Location loc = null;
        int checksSoFar = 0;
        do {
            loc = getRandomLocation(world, radius, centre);
            checksSoFar++;

            if (checksSoFar > maxChecks) {
                return null;
            }
        } while (!isSafe(loc));

        return loc;
    }

    private static boolean isSafe(Location loc) {
        World world = loc.getWorld();

        Block legs = world.getBlockAt(loc);
        Block below = world.getBlockAt(loc.add(0, -1, 0));
        Block above = world.getBlockAt(loc.add(0, 2, 0));

        return below.getType().isSolid()
            && below.getType().isOccluding()
            && (above.getType() == Material.AIR)
            && (legs.getType() == Material.AIR);
    }

    private static Location getRandomLocation(World world, int radius, Location centre) {
        int maxX = centre.getBlockX() + radius;
        int minX = centre.getBlockX() - radius;

        int maxZ = centre.getBlockZ() + radius;
        int minZ = centre.getBlockZ() - radius;

        Random r = new Random();

        int ix = r.nextInt(Math.max(Math.abs(maxX - minX), 1)) + minX;
        double x = ix + 0.5;
        int iz = r.nextInt(Math.max(Math.abs(maxZ - minZ), 1)) + minZ;
        double z = iz + 0.5;

        return new Location(world, x, world.getHighestBlockYAt(ix, iz), z);
    }

}
