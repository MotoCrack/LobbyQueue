package me.devnatan.lobbyqueue;

import me.devnatan.lobbyqueue.libs.BungeeAPI;
import me.devnatan.lobbyqueue.listener.PlayerQuitListener;
import me.devnatan.lobbyqueue.player.QueuedPlayer;
import me.devnatan.lobbyqueue.player.QueuedPlayerComparator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;

public class LobbyQueue extends JavaPlugin {

    private static PriorityBlockingQueue<QueuedPlayer> queue;
    private static QueuedPlayer last;

    public void onEnable() {
        queue = new PriorityBlockingQueue<>(1, new QueuedPlayerComparator());
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
        BungeeAPI api = BungeeAPI.of(this);

        new BukkitRunnable() {
            public void run() {
                if(queue.size() != 0){
                    QueuedPlayer qp = queue.poll();
                    Player p = qp.getPlayer();
                    if(!p.isOnline()) {
                        Bukkit.broadcastMessage(ChatColor.GOLD + p.getName() + " saiu da fila forçadamente.");
                        return;
                    }

                    Bukkit.broadcastMessage(ChatColor.GOLD + p.getName() + " foi enviado para o servidor " + qp.getServer() + ".");
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
}
