package me.devnatan.lobbyqueue.api;

import me.devnatan.lobbyqueue.LobbyQueue;
import me.devnatan.lobbyqueue.player.QueuePlayer;
import me.devnatan.lobbyqueue.server.ServerRunnable;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map;

public class QueueAPI {

    private QueueAPI() { }

    /**
     * Obtem um {@link QueuePlayer} através de um {@link Player}
     * @param player = o jogador
     * @return QueuePlayer
     */
    public static QueuePlayer getFromQueue(Player player) {
        for (Map.Entry<String, ServerRunnable> entry : LobbyQueue.INSTANCE.getServerRunnableMap().entrySet()) {
            ServerRunnable runnable = entry.getValue();
            for (QueuePlayer next : runnable.getPlayers()) {
                if (next.getPlayer().getName().equalsIgnoreCase(player.getName())) {
                    return next;
                }
            }
        }
        return null;
    }

    /**
     * Adiciona um jogador como o ultimo da fila.
     * @param player = o jogador
     * @param server = o servidor
     */
    public static void addToQueue(Player player, String server) {
        ServerRunnable sr = LobbyQueue.INSTANCE.getServerRunnableMap().get(server);
        if (sr != null) {
            sr.getPlayers().add(new QueuePlayer(player, server));
        }
    }

    /**
     * Remove um jogador da fila.
     * @param player = o jogador
     */
    public static void removeFromQueue(Player player) throws IllegalArgumentException {
        for (Map.Entry<String, ServerRunnable> entry : LobbyQueue.INSTANCE.getServerRunnableMap().entrySet()) {
            ServerRunnable runnable = entry.getValue();
            Iterator<QueuePlayer> iterator = runnable.getPlayers().iterator();
            while (iterator.hasNext()) {
                QueuePlayer next = iterator.next();
                if (next.getPlayer().getName().equals(player.getName())) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    /**
     * Verifica se o jogador está na fila.
     * @param player = o jogador
     * @return se o jogador está na fila
     * @deprecated Não é mais usado.
     */
    @Deprecated
    public static boolean isInQueue(Player player) {
        return false;
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
