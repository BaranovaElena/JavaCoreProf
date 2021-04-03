import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {
    private String nickname;
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    public String getNickname() {
        return nickname;
    }

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            logger.setLevel(Level.ALL);
            logger.setUseParentHandlers(false);
            Handler handler = new ConsoleHandler();
            handler.setLevel(Level.ALL);
            logger.addHandler(handler);
        } catch (IOException e) {
            logger.warning("Ошибка создания InputStream/OutputStream");
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String msg = in.readUTF();
                logger.fine("Получено сообщение от нового клиента: " + msg);
                // /auth login1 pass1
                if (msg.startsWith("/auth ")) {
                    String[] tokens = msg.split("\\s");
                    String nick = server.getAuthService().getNicknameByLoginAndPassword(tokens[1], tokens[2]);
                    if (nick != null && !server.isNickBusy(nick)) {
                        sendMsg("/authok " + nick);
                        nickname = nick;
                        server.subscribe(this);
                        logger.info("Клиент " + nick + " прошел аутентификацию");
                        break;
                    }
                    else
                        logger.finer("Аутентификация не пройдена");
                }
            }
            while (true) {
                String msg = in.readUTF();
                logger.fine("Получено сообщение от " + nickname +": " + msg);
                if (msg.startsWith("/")) {
                    if (msg.equals("/end")) {
                        sendMsg("/end");
                        break;
                    }
                    if (msg.startsWith("/w ")) {
                        String[] tokens = msg.split("\\s", 3);
                        server.privateMsg(this, tokens[1], tokens[2]);
                    }
                } else {
                    server.broadcastMsg(nickname + ": " + msg);
                }
            }
        } catch (IOException e) {
            logger.warning("Ошибка получения сообщения");
        } finally {
            ClientHandler.this.disconnect();
        }
    }


    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            logger.warning("Ошибка отправки сообщения");
        }
    }

    public void disconnect() {
        server.unsubscribe(this);
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            logger.warning("Ошибка закрытия потоков чтения/записи или сокета");
        }
    }
}
