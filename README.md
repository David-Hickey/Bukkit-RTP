# Bukkit-RTP

This plugin enables players to teleport to a random location in the wilderness. Handy for stopping griefers!

### Contents of this README

* [Commands](#commands)
* [Permissions](#permissions)
* [Configuring](#configuring)
* [Installing](#installing)

### Commands

All of the commands added by this plugin are grouped under the `/rtp` command. Here is a list:
* `/rtp` - the parent command under which all other commands are grouped
  - Usage: `/rtp [subcommand] [args]`
  - Aliases: `/rtele`, `/rteleport`, `/randtp`, `/randtele`, `/randteleport`, `/randomtp`, `/randomtele`, `/randomteleport`
  - Permissions:
    - `rtp.rtp` to allow use of this command. If a player doesn't have this permissions, they can't use `/rtp` at all, including subcommands!
* `/rtp tp` - teleports the user to a random wilderness location
  - Usage: `/rtp tp [radius]`
  - Aliases: `/rtp tele`, `/rtp teleport`
  - Permissions:
    - `rtp.tp` to allow use of the command
    - `rtp.tp.radius` to allow specifying a custom radius
    - `rtp.tp.nocooldown` to allow teleporting without having to wait in between
* `/rtp config` - view the plugin configuration
  - Usage: `/rtp config`
  - Aliases: `/rtp conf`, `/rtp c`, `/rtp configuration`
  - Permissions:
    - `rtp.config` to allow use of the command
* `/rtp reload` - reload the plugin configuration
  - Usage: `/rtp reload`
  - Aliases: `/rtp rl`
  - Permissions:
    - `rtp.reload` to allow use of the command
* `/rtp help` - display help message
  - Usage: `/rtp help`
  - Aliases: `/rtp h`
  - Permissions: Enabled for everyone who has access to `/rtp`


### Permissions

* `rtp.rtp` - anyone without this permission can't use `/rtp` at all!
* `rtp.tp` - allow use of `/rtp tp`
* `rtp.tp.radius` - allow use of `/rtp tp` with a radius specified
* `rtp.tp.nocooldown` - allow repeated use of `/rtp tp` without any cooldown
* `rtp.config` - allow use of `/rtp config` to view the configuration
* `rtp.reload` - allow use of `/rtp reload` to reload the configuration
* `rtp.*` - give every permission listed above. Use with caution!

By default, normal players have `rtp.rtp` and `rtp.tp`. Ops get every permission listed above except for `rtp.*`.

### Configuring

A normal `config.yml` file looks like this (note that the indentation is important, and you must use spaces instead of tabs):

```
cooldown-seconds: 60
maximum-checks: 100

enabled-worlds:
    world:
        radius: 20000
        centre:
            x: 0
            z: 0
    world_the_end:
        radius: 20000
        centre:
            x: 0
            z: 0
```
I'll explain what each bit does:
* `cooldown-seconds` is the length of time after someone uses `/rtp tp` for which they can't use the command again. This prevents people spamming the command and lagging the server. If you want a particular player to bypass this cooldown, give them the `rtp.nocooldown` permission.
* `maximum-checks` is the maximum number of random locations generated and examined by the `/rtp tp` command. If this number is too high, the command may check too many locations and lag the server. If this number is too low, the command may fail to find a safe location and the player will not be teleported. Adjust depending on how powerful your server is and how many players use the command.
* `enabled-worlds` tells the plugin which worlds players can use `/rtp tp` in. If you don't want a particular world to have `/rtp tp` enabled, don't include it here. `radius` is the maximum distance from the point `centre` that the command can teleport someone. I generally don't recommend enabling this command in any nether worlds, since it'll just drop someone in a random location on top of the nether. In End worlds, there's a lot of empty void space so the plugin will have to do a lot of checks, but it will at least work.

### Installing

1. Download the `.jar` file from the [latest release](https://github.com/David-Hickey/Bukkit-RTP/releases/latest).
1. Put the `.jar` file into your server's `plugins` directory.
