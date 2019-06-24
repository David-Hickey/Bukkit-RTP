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
import org.bukkit.scheduler.BukkitRunnable;
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
                    if (plugin.getDataStorage().onWaitingList(player)) {
                        player.sendMessage(ChatColor.RED + "Still trying to find a safe location, be patient!");
                    } else {
                        player.sendMessage(ChatColor.RED + "Wait "
                            + ChatColor.DARK_RED + plugin.getDataStorage().secondsUntilPlayerCanTeleport(player, System.currentTimeMillis()) + " seconds "
                            + ChatColor.RED + "before trying to teleport again.");
                    }

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

                plugin.getDataStorage().addToWaitingList(player);
                player.sendMessage(ChatColor.GRAY + "Searching for a safe location...");

                // Break the checking up - now it checks one possible placer per tick,
                // massively reducing lag.
                BukkitRunnable findSafePlaceWorker = new FindSafeLocationRunnable(
                    plugin,
                    player,
                    player.getWorld(),
                    worldInfo.getCentre(),
                    radius,
                    plugin.getDataStorage().getMaxChecks()
                );
                findSafePlaceWorker.runTaskTimer(plugin, 1, 1);

                return true;
            }
        };
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

    private static class FindSafeLocationRunnable extends BukkitRunnable {

        private final RTPDataStore config;
        private final Player player;
        private final World world;
        private final Location centre;
        private final int radius;

        private int checksLeft;

        public FindSafeLocationRunnable(RTPPlugin plugin, Player player, World world, Location centre, int radius, int maxChecks) {
            this.config = plugin.getDataStorage();
            this.player = player;
            this.world = world;
            this.centre = centre;
            this.radius = radius;

            this.checksLeft = maxChecks;
        }

        @Override
        public void run() {
            if (this.checksLeft-- <= 0) {
                this.player.sendMessage(ChatColor.RED + "Couldn't find a safe place to teleport. Try again later.");
                this.config.removeFromWaitingList(this.player);
                this.cancel();
            }

            Location testLocation = RTPTeleportCommand.getRandomLocation(
                this.world,
                this.radius,
                this.centre
            );

            if (RTPTeleportCommand.isSafe(testLocation)) {
                this.player.teleport(testLocation);
                this.config.playerTeleported(this.player, System.currentTimeMillis());
                this.cancel();
            }
        }
    }

}
