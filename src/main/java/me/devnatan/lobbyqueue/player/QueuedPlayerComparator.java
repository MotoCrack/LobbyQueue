package me.devnatan.lobbyqueue.player;

import java.util.Comparator;

public class QueuedPlayerComparator implements Comparator<QueuedPlayer> {

    public int compare(QueuedPlayer qp1, QueuedPlayer qp2) {
        String permission = "lobbyqueue.priority";
        if(qp1.getPlayer().hasPermission(permission) &&
                !qp2.getPlayer().hasPermission(permission))
            return 1;

        if(!qp1.getPlayer().hasPermission(permission) &&
                qp2.getPlayer().hasPermission(permission))
            return -1;

        return 0;
    }

}
