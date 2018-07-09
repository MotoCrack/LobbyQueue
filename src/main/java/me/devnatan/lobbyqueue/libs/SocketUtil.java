package me.devnatan.lobbyqueue.libs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketUtil {

    public static int getMaxPlayers(InetSocketAddress address) {
        Socket socket = new Socket();
        try {
            socket.connect(address);

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            dos.write(0xFE);

            StringBuilder sb = new StringBuilder();
            int i;
            while((i = dis.read()) != -1) {
                if(i != 0 && i > 16 && i != 255 && i != 23 && i != 24) {
                    sb.append((char) i);
                }
            }

            return Integer.valueOf(sb.toString().split("§")[2]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

}
