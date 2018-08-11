package me.devnatan.lobbyqueue.command;

import me.devnatan.lobbyqueue.api.QueueAPI;
import me.devnatan.lobbyqueue.player.QueuePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;
        if (args.length == 0) {
            p.sendMessage("Modo correto: /" + label + " (servidor)");
            return true;
        }

        QueuePlayer qp = QueueAPI.getFromQueue(p);
        if (qp != null) {
            p.sendMessage("Você já está na fila para entrar em " + qp.getServer() + ".");
        } else {
            QueueAPI.addToQueue(p, args[0]);
            p.sendMessage("Adicionado à fila de " + args[0] + ".");
        }
        return true;
    }
}
