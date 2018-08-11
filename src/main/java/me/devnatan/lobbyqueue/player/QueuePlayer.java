package me.devnatan.lobbyqueue.player;

import org.bukkit.entity.Player;

public class QueuePlayer {

    private final Player player;
    private final String server;

    public QueuePlayer(Player player, String server) {
        this.player = player;
        this.server = server;
    }

    public Player getPlayer() {
        return player;
    }

    public String getServer() {
        return server;
    }

    public static class Comparator implements java.util.Comparator<QueuePlayer> {

        public int compare(QueuePlayer qp1, QueuePlayer qp2) {
            String permission = "lobbyqueue.priority";
            if (qp1.getPlayer().hasPermission(permission) &&
                    !qp2.getPlayer().hasPermission(permission))
                return 1;

            if (!qp1.getPlayer().hasPermission(permission) &&
                    qp2.getPlayer().hasPermission(permission))
                return -1;

            return 0;
        }

    }

}
