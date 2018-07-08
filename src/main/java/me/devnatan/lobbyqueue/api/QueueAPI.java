package me.devnatan.lobbyqueue.api;

import me.devnatan.lobbyqueue.LobbyQueue;
import me.devnatan.lobbyqueue.player.QueuedPlayer;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.concurrent.PriorityBlockingQueue;

public class QueueAPI {

    private QueueAPI() { }

    /**
     * Obtem um {@link QueuedPlayer} através de um {@link Player}
     * @param player = o jogador
     * @return QueuedPlayer
     */
    public static QueuedPlayer getFromQueue(Player player) {
        PriorityBlockingQueue<QueuedPlayer> queue = LobbyQueue.getQueue();
        return queue.parallelStream().filter(Objects::nonNull)
                .filter(queuedPlayer -> queuedPlayer.getPlayer().getName().equals(player.getName()))
                .filter(queuedPlayer -> queuedPlayer.getPlayer().isOnline())
                .findFirst()
                .orElse(null);
    }

    /**
     * Adiciona um jogador como o ultimo da fila.
     * @param player = o jogador
     * @param server = o servidor
     * @throws IllegalArgumentException se o jogador já estiver na fila
     */
    public static void addToQueue(Player player, String server) throws IllegalArgumentException {
        if(isInQueue(player))
            throw new IllegalArgumentException("This player is already in queue.");

        PriorityBlockingQueue<QueuedPlayer> queue = LobbyQueue.getQueue();
        queue.add(new QueuedPlayer(player, server, queue.size() + 1));
    }

    /**
     * Remove um jogador da fila.
     * @param player = o jogador
     * @throws IllegalArgumentException se o jogador não estiver na fila
     */
    public static void removeFromQueue(Player player) throws IllegalArgumentException {
        if(isInQueue(player))
            throw new IllegalArgumentException("This player isn't in queue.");

        PriorityBlockingQueue<QueuedPlayer> queue = LobbyQueue.getQueue();
        queue.removeIf(queuedPlayer -> queuedPlayer.getPlayer().getName().equals(player.getName()));
    }

    /**
     * Verifica se o jogador está na fila.
     * @param player = o jogador
     * @return se o jogador está na fila
     */
    private static boolean isInQueue(Player player) {
        return LobbyQueue.getQueue().stream().anyMatch(queuedPlayer -> queuedPlayer.getPlayer().getName().equals(player.getName()));
    }

    /**
     * Verifica se o jogador tem prioridade na fila.
     * @param player = o jogador
     * @return se o jogador tem prioridade na fila
     */
    public static boolean hasPriorityInQueue(Player player) {
        return player.hasPermission("lobby.priority");
    }

}
