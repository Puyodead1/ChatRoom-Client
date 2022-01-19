package optic_fusion1.client;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.lenni0451.asmevents.EventManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.util.Properties;

public class ChatRoomClient {

    private static final Logger LOGGER = LogManager.getLogger(ChatRoomClient.class);
    public static final Properties properties = new Properties();

    public static void main(String[] args) throws IOException {
        AnsiConsole.systemInstall();

        properties.load(ChatRoomClient.class.getClassLoader().getResourceAsStream("project.properties"));

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

            try {
                Client client = new Client(serverHost, serverPort, username, password);

                ClientEventListener eventListener = new ClientEventListener(client);
                EventManager.register(eventListener);

                client.start();
//
//                client.stop();
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.fatal(String.format("Error starting ChatRoom client: %s", e.getLocalizedMessage()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.fatal(String.format("Error parsing options: %s", e.getLocalizedMessage()));
        }
    }
}
