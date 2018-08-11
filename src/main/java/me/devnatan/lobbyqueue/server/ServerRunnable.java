package me.devnatan.lobbyqueue.server;

import me.devnatan.lobbyqueue.LobbyQueue;
import me.devnatan.lobbyqueue.libs.BungeeAPI;
import me.devnatan.lobbyqueue.player.QueuePlayer;

import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

public class ServerRunnable implements Runnable {

    private final String name;
    private final int maxPlayers;
    private final TreeSet<QueuePlayer> players;
    BungeeAPI api = LobbyQueue.INSTANCE.getBungeeApi();

    public ServerRunnable(String name, int maxPlayers) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        players = new TreeSet<>(new QueuePlayer.Comparator());
    }

    public void run() {
        try {
            int count = api.getPlayerCount(name).get();
            if (count == maxPlayers) {
                LobbyQueue.INSTANCE.getLogger().warning("Servidor " + name + " está cheio.");
                return;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        QueuePlayer player = players.pollFirst();
        if (player == null) {
            LobbyQueue.INSTANCE.getLogger().info("Fila do servidor " + name + " vazia.");
            return;
        }

        if (player.getPlayer() == null || !player.getPlayer().isOnline()) {
            LobbyQueue.INSTANCE.getLogger().info("Jogador " + player.getPlayer().getName() + " removido da fila.");
            return;
        }

        api.connect(player.getPlayer(), name);
        LobbyQueue.INSTANCE.getLogger().info("Jogador " + player.getPlayer().getName() + " conectou-se à " + name + ".");
    }

}
