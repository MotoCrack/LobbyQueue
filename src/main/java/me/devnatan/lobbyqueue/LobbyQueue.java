package me.devnatan.lobbyqueue;

import me.devnatan.lobbyqueue.command.TestCommand;
import me.devnatan.lobbyqueue.libs.BungeeAPI;
import me.devnatan.lobbyqueue.libs.SocketUtil;
import me.devnatan.lobbyqueue.server.ServerRunnable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class LobbyQueue extends JavaPlugin {

    public static LobbyQueue INSTANCE;

    private Map<String, ServerRunnable> serverRunnableMap = new HashMap<>();
    private Map<String, Integer> maxPlayers = new HashMap<>();
    private BungeeAPI bungeeApi;

    public void onLoad() {
        INSTANCE = this;
    }

    public void onEnable() {
        serverRunnableMap.clear();
        maxPlayers.clear();
        bungeeApi = null;

        path();
        config(this::runnable);
        setup();
    }

    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    private void path() {
        if(!getDataFolder().exists()) getDataFolder().mkdir();
        if(!new File(getDataFolder(), "config.yml").exists())
            saveResource("config.yml", false);
    }

    private void config(BiConsumer<String, Integer> consumer) {
        new BukkitRunnable() {
            public void run() {
                ConfigurationSection cs = getConfig().getConfigurationSection("servers");
                cs.getKeys(false).forEach(sv -> {
                    String ip = cs.getString(sv);
                    String host = ip.contains(":") ? ip.split(":")[0] : ip;
                    int port = ip.contains(":") ? Integer.parseInt(ip.split(":")[1]) : 25565;
                    InetSocketAddress addr = new InetSocketAddress(host, port);

                    try {
                        int max = SocketUtil.getMaxPlayers(addr);
                        maxPlayers.put(sv, max);
                        consumer.accept(sv, max);
                    } catch (Exception e) {
                        getLogger().severe("Falha ao obter quantidade máxima de jogadores para " + sv + ".");
                    }
                });
            }
        }.runTaskTimer(this, 20L, 20L * 30);
    }

    private void setup() {
        bungeeApi = BungeeAPI.of(this);
        getCommand("testqueue").setExecutor(new TestCommand());
    }

    private void runnable(String server, int max) {
        if (!serverRunnableMap.containsKey(server)) {
            ServerRunnable runnable = new ServerRunnable(server, max);
            serverRunnableMap.put(server, runnable);
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, runnable, 20L, 20L);
        }
    }

    public Map<String, ServerRunnable> getServerRunnableMap() {
        return serverRunnableMap;
    }

    public Map<String, Integer> getMaxPlayers() {
        return maxPlayers;
    }

    public BungeeAPI getBungeeApi() {
        return bungeeApi;
    }

}
