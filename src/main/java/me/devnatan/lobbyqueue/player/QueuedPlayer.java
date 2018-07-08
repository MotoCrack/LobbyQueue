package me.devnatan.lobbyqueue.player;

import org.bukkit.entity.Player;

public class QueuedPlayer {

    private final Player player;
    private final String server;
    private int position;

    public QueuedPlayer(Player player, String server, int position) {
        this.player = player;
        this.server = server;
        this.position = position;
    }

    public Player getPlayer() {
        return player;
    }

    public String getServer() {
        return server;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
