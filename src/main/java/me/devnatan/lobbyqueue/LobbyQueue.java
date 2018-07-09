package me.devnatan.lobbyqueue;

import me.devnatan.lobbyqueue.hook.PlaceholderHook;
import me.devnatan.lobbyqueue.libs.BungeeAPI;
import me.devnatan.lobbyqueue.libs.SocketUtil;
import me.devnatan.lobbyqueue.listener.PlayerQuitListener;
import me.devnatan.lobbyqueue.player.QueuedPlayer;
import me.devnatan.lobbyqueue.player.QueuedPlayerComparator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.PriorityBlockingQueue;

public class LobbyQueue extends JavaPlugin {

    private static PriorityBlockingQueue<QueuedPlayer> queue;
    private static QueuedPlayer last;
    private static final Map<String, Integer> maxPlayers = new HashMap<>();

    public void onEnable() {
        if(!getDataFolder().exists()) getDataFolder().mkdir();
        if(!new File(getDataFolder(), "config.yml").exists())
            saveResource("config.yml", false);

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
                    } catch (Exception e) {
                        getLogger().severe("Falha ao obter quantidade máxima de jogadores para " + sv + ".");
                    }
                });
            }
        }.runTaskTimer(this, 20L * 3, 20L * 60);

        queue = new PriorityBlockingQueue<>(1, new QueuedPlayerComparator());
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderHook(this).hook();
        }

        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
        BungeeAPI api = BungeeAPI.of(this);

        new BukkitRunnable() {
            public void run() {
                if(queue.size() != 0){
                    QueuedPlayer qp = queue.poll();
                    Player p = qp.getPlayer();
                    if(!p.isOnline()) {
                        return;
                    }

                    if (maxPlayers.containsKey(qp.getServer())) {
                        try {
                            int count = api.getPlayerCount(qp.getServer()).get();
                            if(count == maxPlayers.get(qp.getServer())) {
                                p.sendMessage(ChatColor.GOLD + "Você é o primeiro da fila mas o servidor está cheio.");
                                return;
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                    p.sendMessage(ChatColor.GOLD + "Enviando você para o servidor " + qp.getServer() + "...");
                    api.connect(p, qp.getServer());

                    Iterator<QueuedPlayer> iterator = queue.iterator();
                    while(iterator.hasNext()) { // ConcurrentModificationException
                        QueuedPlayer next = iterator.next();
                        Player nextPlayer = next.getPlayer();
                        next.setPosition(next.getPosition() - 1);
                        if(nextPlayer != null && nextPlayer.isOnline()) {
                            nextPlayer.sendMessage(ChatColor.GOLD + "Você agora é o #" + next.getPosition() + " da fila.");
                        }
                    }
                }
            }
        }.runTaskTimer(this, 20L, 20L);
    }

    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    public static PriorityBlockingQueue<QueuedPlayer> getQueue() {
        return queue;
    }

    public static void setQueue(PriorityBlockingQueue<QueuedPlayer> queue) {
        LobbyQueue.queue = queue;
    }

    public static QueuedPlayer getLast() {
        return last;
    }

    public static void setLast(QueuedPlayer last) {
        LobbyQueue.last = last;
    }

    public static Map<String, Integer> getMaxPlayers() {
        return maxPlayers;
    }
}
