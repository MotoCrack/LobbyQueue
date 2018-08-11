package me.devnatan.lobbyqueue.api;

import me.devnatan.lobbyqueue.player.QueuePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

@Deprecated
public class QueueAPI {

    private QueueAPI() { }

    /**
     * Obtem um {@link QueuePlayer} através de um {@link Player}
     * @param player = o jogador
     * @return QueuePlayer
     * @deprecated Deprecated
     */
    public static QueuePlayer getFromQueue(Player player) {
        List<QueuePlayer> queue = null;
        return queue.stream().filter(Objects::nonNull)
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

        List<QueuePlayer> queue = null;
        queue.add(new QueuePlayer(player, server));
    }

    /**
     * Remove um jogador da fila.
     * @param player = o jogador
     * @throws IllegalArgumentException se o jogador não estiver na fila
     */
    public static void removeFromQueue(Player player) throws IllegalArgumentException {
        if(isInQueue(player))
            throw new IllegalArgumentException("This player isn't in queue.");

        List<QueuePlayer> queue = null;
        queue.removeIf(queuedPlayer -> queuedPlayer.getPlayer().getName().equals(player.getName()));
    }

    /**
     * Verifica se o jogador está na fila.
     * @param player = o jogador
     * @return se o jogador está na fila
     */
    public static boolean isInQueue(Player player) {
        return false;
        // return LobbyQueue.getQueue().stream().anyMatch(queuedPlayer -> queuedPlayer.getPlayer().getName().equals(player.getName()));
    }

    /**
     * Verifica se o jogador tem prioridade na fila.
     * @param player = o jogador
     * @return se o jogador tem prioridade na fila
     */
    public static boolean hasPriorityInQueue(Player player) {
        return player.hasPermission("lobbyqueue.priority");
    }

}
