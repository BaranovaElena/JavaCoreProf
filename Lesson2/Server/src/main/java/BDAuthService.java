import java.sql.*;
import java.util.logging.Logger;

public final class BDAuthService implements AuthService{
    private static BDAuthService service;
    private static final String DB_CONNECTION = "jdbc:sqlite:AuthDB.db";
    private static Connection connection;
    private static final Logger logger = Logger.getLogger(BDAuthService.class.getName());

    private BDAuthService(){}   //скрываем конструктор для реализации синглитона

    static{
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_CONNECTION);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (connection!=null)
            logger.info("Сервер подключился к БД");
        else
            logger.warning("Ошибка подключения к БД");
    }

    //вместо конструктора статический метод, возвращающий статический экземпляр класса
    public static BDAuthService getInstance() {
        if (service == null)
            service = new BDAuthService();
        return service;
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        try(PreparedStatement stm = connection.prepareStatement(
                "SELECT * FROM 'Clients' WHERE login='"+login+"' AND password='"+password+"'");
            ResultSet resultSet = stm.executeQuery()){
            if (resultSet.next()){
                return resultSet.getString("nickname");
            }
        } catch (SQLException e) {
            logger.severe("Произошла ошибка запроса к БД");
        }
        return null;
    }

    public static void close() {
        try {
            connection.close();
            logger.info("Закрытие соединения с БД");
        } catch (SQLException throwables) {
            logger.severe("Произошла ошибка при закрытии соединения с БД");
        }
    }
}
