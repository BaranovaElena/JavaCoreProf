import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class Client {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Callback<String> callOnMsgReceived;
    private Callback<String> callOnChangeClientList;
    private Callback<String> callOnAuth;
    private Callback<String> callOnError;
    private Path pathHistory;

    Thread readerMessages;

    private static final int PRELOAD_CHAT_ROWS = 100;

    public void connect() {
        try {
            socket = new Socket("localhost",8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            readerMessages = new Thread(() -> {
                boolean goOn = true;
                try {
                    //авторизация
                    while (goOn) {
                        String message = in.readUTF();
                        if (message.startsWith("/authok")) {
                            callOnAuth.callback("/authok");
                            break;
                        }
                        else if (message.equalsIgnoreCase("/end")) {
                            goOn = false;
                        }
                        else if (message.equalsIgnoreCase("/error timeout")){
                            goOn = false;
                            callOnError.callback("Time to connect has expired");
                        }
                        else {
                            callOnError.callback("Your login or password is wrong");
                        }
                    }
                    while (goOn) {
                        String message = in.readUTF();
                        //сообщение разрыва соединения
                        if (message.equalsIgnoreCase("/end"))
                            break;
                            //сообщение изменения списка клиентов
                        else if (message.startsWith("/clients")){
                            callOnChangeClientList.callback(message.substring(9));
                        }
                        //сообщения об ошибках
                        else if (message.startsWith("/error")){
                        }
                        //не служебные сообщения (в чат) и историю
                        else {
                            callOnMsgReceived.callback(message);
                            //пишем строку в конец файла, не очищая его
                            Files.write(pathHistory, (message+"\n").getBytes(),
                                    StandardOpenOption.CREATE, StandardOpenOption.APPEND,
                                    StandardOpenOption.WRITE);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    disconnect();
                }
            });
            readerMessages.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*private void sendMessages() {
        String outputMessage;
        try {
            do{
                outputMessage = scanner.nextLine();
                out.writeUTF(outputMessage);
                out.flush();
            } while (!outputMessage.equals("/end"));
            readerMessages.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }*/

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCallOnMsgReceived(Callback<String> callOnMsgReceived) {
        this.callOnMsgReceived = callOnMsgReceived;
    }

    public void setCallOnChangeClientList(Callback<String> callOnChangeClientList) {
        this.callOnChangeClientList = callOnChangeClientList;
    }

    public void setCallOnAuth(Callback<String> callOnAuth) {
        this.callOnAuth = callOnAuth;
    }

    public void setCallOnError(Callback<String> callOnError) {
        this.callOnError = callOnError;
    }

    public void setPathHistory(String login) {
        String dir = "Client/history/" + login + ".txt";
        pathHistory = Paths.get(dir);

        //заранее создадим промежуточную директорию history, если ее еще нет
        try {
            Files.createDirectories(pathHistory.getParent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getHistory(String login){
        setPathHistory(login);

        try {
            List<String> lines = Files.readAllLines(pathHistory);
            //обрезаем до 100 последних строк
            if (lines.size() > PRELOAD_CHAT_ROWS)
                lines = lines.subList(lines.size()-PRELOAD_CHAT_ROWS, lines.size());

            //приводим к одной строке, подходящей для вывода в чат
            StringBuilder result = new StringBuilder();
            for (String line : lines) {
                result.append(line).append("\n");
            }
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
