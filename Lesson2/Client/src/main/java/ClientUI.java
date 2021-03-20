import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static java.lang.System.exit;

public class ClientUI extends JFrame {
    private JPanel  chatPanel;
    private JButton btnSend;
    private JTextField fieldMessage;
    private JTextArea areaChat;

    private JComboBox<String> clientsBox;
    private String receiver;
    private final String ALL = "all";
    private String[] clients;

    private JPanel  loginPanel;
    private Client clientNetwork = new Client();

    public ClientUI() {
        setCallbacks();
        setMainFrame();
        initAuthUI();
        initChatUI();

        clientNetwork.connect();
        setVisible(true);
    }

    private void setCallbacks() {
        //при получении сообщения от сервера добавляем его в textArea
        this.clientNetwork.setCallOnMsgReceived(
                message -> areaChat.append(message + "\n"));

        // при получении нового списка клиентов
        this.clientNetwork.setCallOnChangeClientList(clientsList -> {
            clients = (ALL + " "+ clientsList).split(" ");
            clientsBox.setModel(new DefaultComboBoxModel(clients));
        });

        // при успешной авторизации мы прячем loginPanel и делаем видимой chatPanel
        this.clientNetwork.setCallOnAuth(s -> {
            loginPanel.setVisible(false);
            add(chatPanel, BorderLayout.CENTER);
            chatPanel.setVisible(true);
        });

        // при сообщении об ошибке показываем pop-up
        this.clientNetwork.setCallOnError(message -> {
            JOptionPane.showMessageDialog(null, message,
                    "We have a problem", JOptionPane.ERROR_MESSAGE);
            //если таймаут, закрываем приложение клиента
            if (message.equalsIgnoreCase("Time to connect has expired")) {
                setVisible(false);
                dispose();
                exit(0);
            }
        });
    }

    private void setMainFrame() {
        setTitle("Чатик");
        setSize(400, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                clientNetwork.sendMessage("/end");
                super.windowClosing(event);
            }
        });
    }

    private void initChatUI() {
        chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());

        //создаем кнопку, строку, список клиентов
        btnSend = new JButton("Отправить сообщение");
        fieldMessage = new JTextField();
        clients = new String[]{ALL};
        clientsBox = new JComboBox<>(clients);
        clientsBox.addActionListener(e -> {
            receiver = clientsBox.getSelectedItem().toString();
        });

        //создаем компоновщик кнопки и комбобокса
        JPanel panelBoxBtn = new JPanel();
        panelBoxBtn.setLayout(new GridLayout(1,2));
        panelBoxBtn.add(btnSend);
        panelBoxBtn.add(clientsBox);

        //создаем компоновщик для поля ввода и panelBoxBtn
        JPanel panelLineBtn = new JPanel();
        panelLineBtn.setLayout(new GridLayout(2, 1));
        panelLineBtn.add(fieldMessage);
        panelLineBtn.add(panelBoxBtn);

        //помещаем все это в низ окна
        chatPanel.add(panelLineBtn, BorderLayout.SOUTH);

        //оставшееся место заполняем текстовым полем
        areaChat = new JTextArea();
        areaChat.setEditable(false);
        areaChat.setBackground(new Color(135, 206, 250));
        areaChat.setLineWrap(true); //перенос длинной строки на след.строку
        JScrollPane scroll = new JScrollPane(areaChat,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatPanel.add(scroll, BorderLayout.CENTER);

        setSendingMessage();
        chatPanel.setVisible(false);
    }

    private void setSendingMessage() {
        //событие для отправки сообщения
        ActionListener actionListener = e -> {
            String message = fieldMessage.getText();
            if (!message.isEmpty()) {
                if (receiver != null && !receiver.equalsIgnoreCase(ALL)) {
                    message = "/w " + receiver + " " + message;
                }
                clientNetwork.sendMessage(message);
                fieldMessage.setText("");
                clientsBox.setSelectedItem(ALL);
                receiver = null;
            }
        };

        btnSend.addActionListener(actionListener);  //на кнопку
        fieldMessage.addActionListener(actionListener); //на Enter
    }

    private void initAuthUI() {
        loginPanel = new JPanel();
        loginPanel.setBackground(Color.white);
        loginPanel.setLayout(new FlowLayout());

        JLabel authLabel = new JLabel("Для входа в чат введите логин и пароль");
        authLabel.setPreferredSize(new Dimension(
                this.getWidth()-20, 25));

        JLabel loginLabel = new JLabel("Логин : ");
        JTextField loginField = new JTextField();
        loginField.setPreferredSize(new Dimension(
                this.getWidth()-loginLabel.getWidth()-50, 25));

        JLabel passwordLabel = new JLabel("Пароль : ");
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(
                this.getWidth()-loginLabel.getWidth()-50, 25));

        JButton submitButton = new JButton("Войти");
        submitButton.addActionListener(e -> {
            clientNetwork.sendMessage("/auth " +
                    loginField.getText() + " " +
                    String.valueOf(passwordField.getPassword()));
            loginField.setText("");
            passwordField.setText("");
        });

        loginPanel.add(authLabel);
        loginPanel.add(loginLabel);
        loginPanel.add(loginField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(submitButton);

        loginPanel.setVisible(true);
        add(loginPanel);
    }
}
