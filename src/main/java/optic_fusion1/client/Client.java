package optic_fusion1.client;

import optic_fusion1.client.network.SocketClient;
import optic_fusion1.client.network.listeners.PacketListener;
import optic_fusion1.packets.impl.HeartbeatPacket;
import optic_fusion1.packets.impl.MessagePacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.net.ConnectException;

public class Client {

    private static final Logger LOGGER = LogManager.getLogger();

    private final SocketClient socketClient;

    public Client(final String host, final int port, final String username, final String password) throws ConnectException {
        AnsiConsole.systemInstall();

        socketClient = new SocketClient(this, host, port, username, password);
        socketClient.getPacketRegister().addPacket("message", MessagePacket.class);
        socketClient.getPacketRegister().addPacket("heartbeat", HeartbeatPacket.class);
        socketClient.addEventListener(new PacketListener());

        try {
            socketClient.connect();
        } catch (IOException e) {
            throw new ConnectException(e.getLocalizedMessage());
        }
    }

    public SocketClient getSocketClient() {
        return socketClient;
    }
}
