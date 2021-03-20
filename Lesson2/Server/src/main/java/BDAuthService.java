import java.sql.*;

public final class BDAuthService implements AuthService{
    private static BDAuthService service;
    private static final String DB_CONNECTION = "jdbc:sqlite:AuthDB.db";
    private static Connection connection;

    private BDAuthService(){}   //скрываем конструктор для реализации синглитона

    static{
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_CONNECTION);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (connection!=null)
            System.out.println("connected to DB");
        else
            System.out.println("fail connection");
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
            e.printStackTrace();
        }
        return null;
    }

    public static void close() {
        try {
            connection.close();
            System.out.println("disconnected from BD");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
