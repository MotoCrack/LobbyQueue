package me.devnatan.lobbyqueue.server;

import me.devnatan.lobbyqueue.LobbyQueue;
import me.devnatan.lobbyqueue.libs.BungeeAPI;
import me.devnatan.lobbyqueue.player.QueuePlayer;

import java.util.TreeSet;

public class ServerRunnable implements Runnable {

    BungeeAPI api = LobbyQueue.INSTANCE.getBungeeApi();

    private final String name;
    private final int maxPlayers;
    private final TreeSet<QueuePlayer> players;

    public ServerRunnable(String name, int maxPlayers) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        players = new TreeSet<>(new QueuePlayer.Comparator());
    }

    public TreeSet<QueuePlayer> getPlayers() {
        return players;
    }

    public void run() {
        try {
            api.getPlayerCount(name).whenCompleteAsync((count, throwable) -> {
                if (count == maxPlayers) {
                    LobbyQueue.INSTANCE.getLogger().warning("Servidor " + name + " está cheio.");
                    return;
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
            });
        } catch (IllegalArgumentException e) {
            LobbyQueue.INSTANCE.getLogger().warning("Nenhum jogador encontrado no servidor " + name + ".");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
