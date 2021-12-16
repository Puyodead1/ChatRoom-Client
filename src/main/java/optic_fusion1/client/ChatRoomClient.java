package optic_fusion1.client;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import optic_fusion1.common.protos.Packet;
import optic_fusion1.common.protos.ProtocolVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fusesource.jansi.AnsiConsole;

public class ChatRoomClient {

    private static final Logger LOGGER = LogManager.getLogger(ChatRoomClient.class);

    public static void main(String[] args) {
        AnsiConsole.systemInstall();

        OptionParser optionParser = new OptionParser();
        OptionSpec<Void> helpSpec = optionParser.accepts("help").forHelp();
        OptionSpec<String> hostSpec = optionParser.accepts("host", "Server host address").withOptionalArg().defaultsTo("localhost");
        OptionSpec<Integer> portSpec = optionParser.accepts("port", "Server port").withOptionalArg().ofType(Integer.class).defaultsTo(8888);
        OptionSpec<String> usernameSpec = optionParser.accepts("username", "Server account username").withOptionalArg();
        OptionSpec<String> passwordSpec = optionParser.accepts("password", "Server account password").withOptionalArg();

        try {
            OptionSet optionSet = optionParser.parse(args);

            if (optionSet.has(helpSpec)) {
                optionParser.printHelpOn(System.out);
                return;
            }

            String serverHost = optionSet.valueOf(hostSpec);
            int serverPort = optionSet.valueOf(portSpec);
            String username = optionSet.valueOf(usernameSpec);
            String password = optionSet.valueOf(passwordSpec);

            Client client = new Client(serverHost, serverPort);
            client.start();

            Packet.Builder packet = Packet.newBuilder();

            packet.setPacketType(Packet.PacketType.HANDSHAKE);
            packet.setProtocolVersion(ProtocolVersion.VERSION_000);
            packet.setUseEncryption(true);

            client.sendPacket(packet.build());

            client.stop();
        } catch (Exception e) {
            LOGGER.fatal(String.format("Error parsing options: %s", e.getLocalizedMessage()));
        }
    }
}
