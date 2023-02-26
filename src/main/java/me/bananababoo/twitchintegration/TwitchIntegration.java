package me.bananababoo.twitchintegration;


import me.bananababoo.twitchintegration.Commands.Twitch;
import org.bukkit.plugin.java.JavaPlugin;

public final class TwitchIntegration extends JavaPlugin {
    private static TwitchIntegration instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        getCommand("twitch").setExecutor(new Twitch());
        

    }

    public static TwitchIntegration getPlugin(){
        return instance;
    }
}
