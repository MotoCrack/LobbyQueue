package me.devnatan.lobbyqueue.hook;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import me.devnatan.lobbyqueue.api.QueueAPI;
import me.devnatan.lobbyqueue.player.QueuedPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlaceholderHook extends EZPlaceholderHook {

    public PlaceholderHook(Plugin plugin) {
        super(plugin, "lobbyqueue");
    }

    @Override
    public String onPlaceholderRequest(Player player, String s) {
        if(player == null)
            return "";

        if(QueueAPI.isInQueue(player)) {
            QueuedPlayer qp = QueueAPI.getFromQueue(player);
            if(s.equals("position")) {
                return String.valueOf(qp.getPosition());
            }

            if(s.equals("server")) {
                return qp.getServer();
            }
        } else {
            if(s.equals("position")) {
                return "0";
            }

            if(s.equals("server")) {
                return "Nenhum";
            }
        }
        return null;
    }

}
