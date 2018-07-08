package me.devnatan.lobbyqueue.listener;

import me.devnatan.lobbyqueue.LobbyQueue;
import me.devnatan.lobbyqueue.player.QueuedPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.PriorityBlockingQueue;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void on(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        PriorityBlockingQueue<QueuedPlayer> queue = LobbyQueue.getQueue();
        queue.removeIf(queuedPlayer -> queuedPlayer.getPlayer().getName().equals(player.getName()));
    }

}
