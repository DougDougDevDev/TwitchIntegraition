package me.bananababoo.twitchintegration;

import me.bananababoo.twitchintegration.Commands.Twitch;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IRC {
    private static PrintWriter out;
    public static List<Player> players = new ArrayList<>();
    public static void addOrRemovePlayer(Player player){
        if(players.contains(player)){
            players.remove(player);
            player.sendMessage(ChatColor.RED + "You are no longer receiving twitch messages");
        }else{
            players.add(player);
            player.sendMessage(ChatColor.GREEN + "You are now receiving twitch messages");
        }
    }

    public static void irc(String channel) throws IOException {
        try {
            Socket socket = new Socket("irc.chat.twitch.tv", 80);
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            write("PASS oauth:", "9umqyyatjoxzfh2n5mna07hurbj9o4");
            write("NICK ", "bananababoo");
            write("JOIN #", channel);
            int count = 0;

            Bukkit.getLogger().info("Trying to connect" + reader + " " + reader.ready());
            while (reader.ready()) {
                String message = reader.readLine();
                if (message == null) Bukkit.getLogger().info("got null message / line");
                Bukkit.getLogger().info(message);
                if (message.contains("Welcome, GLHF!")) {
                    Bukkit.getLogger().info("Connected");
                    break;
                }
                if (count > 10) {
                    Bukkit.getLogger().info("Failed to connect");
                    return;
                }
                count++;
            }
            Bukkit.getLogger().info("Connected");
            Bukkit.getScheduler().runTaskTimerAsynchronously(TwitchIntegration.getPlugin(), () -> {
                String message = "";
                try {
                    while (reader.ready()) {
                        message = reader.readLine();
                        Bukkit.getLogger().info("<<<" + message);
                        if (!message.equalsIgnoreCase("")) {

                            for (Player p : players) {
                                String username = message.substring(1, message.indexOf("!") - 1);
                                String output = message.substring(message.indexOf(":", message.indexOf(":") + 1));
                                p.sendMessage(ChatColor.BLUE + username + ": " + ChatColor.GRAY + output.substring(1));
//                                p.getScheduler().run(TwitchIntegration.getPlugin(),
//                                        (a) -> p.sendMessage(ChatColor.BLUE + username + ": " + ChatColor.GRAY + output.substring(1)),
//                                        () -> Bukkit.getLogger().info("Failed to send message to " + p.getName()));
                            }
                            if (message.equalsIgnoreCase("PING :tmi.twitch.tv")) {
                                out.print("PONG :tmi.twitch.tv" + "\r\n");

                            }
                            if (!Twitch.isActive()) {
                                for (Player p : players) {
                                    p.sendMessage("Connection Ended");
//                                    p.getScheduler().run(TwitchIntegration.getPlugin(),
//                                            (a) -> p.sendMessage("Connection Ended"),
//                                            () -> Bukkit.getLogger().info("Failed to send message to " + p.getName()));
                                }
                                out.flush();
                                out.close();
                                Bukkit.getScheduler().cancelTasks(TwitchIntegration.getPlugin());
                                try {
                                    socket.close();
                                    reader.close();
                                } catch (IOException e) {
                                    Bukkit.getLogger().warning(Arrays.toString(e.getStackTrace()));
                                    e.printStackTrace();
                                    return;
                                }
                                return;
                            }
                        }
                    }

                } catch (IOException e) {
                    Bukkit.getLogger().warning(e.getMessage());
                    e.printStackTrace();
                    Bukkit.getLogger().warning(Arrays.toString(e.getStackTrace()));
                }

            }, 0, 10);
        }catch (Exception e){
            Bukkit.getLogger().warning(Arrays.toString(e.getStackTrace()) + " got error");
            e.printStackTrace();
        }
        }


    private static void write(String command, String message){
        String fullMessage = command + message;
        Bukkit.getLogger().info(">>> " + fullMessage);
        out.print(fullMessage + "\r\n");
        out.flush();
    }
}


