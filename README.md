# Bukkit-RTP

This plugin enables players to teleport to a random location in the wilderness. Handy for stopping griefers!

### Commands and Permissions

All of the commands added by this plugin are grouped under the `/rtp` command. Here is a list:
* `/rtp` - the parent command under which all other commands are grouped
  - Usage: `/rtp [subcommand] [args]`
  - Aliases: `/randtp`
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
* `/rtp help` - display help message
  - Usage: `/rtp help`
  - Aliases: `/rtp h`
  - Permissions: Enabled for everyone who has access to `/rtp`
  
By default, normal players have `rtp.rtp` and `rtp.tp`. Ops get every permission listed above.

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

### Installing the Easy Way

1. Download the `.jar` file from the [latest release](https://github.com/David-Hickey/Bukkit-RTP/releases/latest).
1. Put the `.jar` file into your server's `plugins` directory.


### Building from Source (Advanced users only)

This plugin builds with [Maven](https://maven.apache.org/) - if you don't have that, you'll need to install it before continuing. To install, open a terminal or command prompt and do the following:
1. Run `git clone https://github.com/David-Hickey/Bukkit-RTP.git` or download and extract the zip folder containing the source code from [here](https://github.com/David-Hickey/Bukkit-RTP/archive/master.zip).
1. Navigate to the folder containing `pom.xml` and then run `mvn clean install`.
1. A directory called `target` will appear in the same directory. Inside that directory will be a file called something like `bukkit_rtp-1.0.0.jar` - the number at the end might be different.
1. Copy `bukkit_rtp-1.0.0.jar` into your server's `plugins` directory.
