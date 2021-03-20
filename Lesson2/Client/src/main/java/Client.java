import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Callback<String> callOnMsgReceived;
    private Callback<String> callOnChangeClientList;
    private Callback<String> callOnAuth;
    private Callback<String> callOnError;

    Thread readerMessages;

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
                        //не служебные сообщения (в чат)
                        else {
                            callOnMsgReceived.callback(message);
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
}
