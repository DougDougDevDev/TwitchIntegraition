package me.bananababoo.twitchintegration.Commands;

import me.bananababoo.twitchintegration.IRC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;

public class Twitch implements CommandExecutor {
    public static Boolean enabled = false;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length > 1 && args[0].equalsIgnoreCase("start") && sender.isOp()){
            setActive(true);
            try {
                IRC.irc(args[1]);
            } catch (IOException e) {
                Bukkit.getLogger().warning(Arrays.toString(e.getStackTrace()));
                Bukkit.getLogger().warning("Failed to connect to twitch" + e.getCause().toString());
                Bukkit.getLogger().info("Failed to connect to twitch" + e.getMessage() + " " + Arrays.toString(e.getStackTrace()));
            }
            //Twitch4J.twitch4j(args[1]);
        }else if (args.length > 0 && sender.isOp() && args[0].equalsIgnoreCase("stop")){
            setActive(false);
            Bukkit.getLogger().info("Deactivated");
        }else if(args.length == 0){
            IRC.addOrRemovePlayer((Player) sender);
        }

        return true;
    }
    public static boolean isActive(){return enabled;}

    public static void setActive(boolean active){enabled = active;}
}
