package optic_fusion1.client;

import com.sun.tools.javac.Main;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fusesource.jansi.AnsiConsole;

public class ChatRoomClient {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        AnsiConsole.systemInstall();

        OptionParser optionParser = new OptionParser();
        OptionSpec<Void> helpSpec = optionParser.accepts("help").forHelp();
        OptionSpec<String> hostSpec = optionParser.accepts("host", "Server host address").withOptionalArg().defaultsTo("localhost");
        OptionSpec<Integer> portSpec = optionParser.accepts("port", "Server port").withOptionalArg().ofType(Integer.class).defaultsTo(8888);
        OptionSpec<String> usernameSpec = optionParser.accepts("username", "Server account username").withOptionalArg();
        OptionSpec<String> passwordSpec = optionParser.accepts("password", "Server account password").withOptionalArg();

        EventLoopGroup group = new NioEventLoopGroup();

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

            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientChannelInitializer());

            b.connect(serverHost, serverPort).sync().channel();

        } catch (Exception e) {
            LOGGER.fatal(String.format("Failed to start ChatRoom client: %s", e.getLocalizedMessage()));
        } finally {
            group.shutdownGracefully();
        }
    }
}
