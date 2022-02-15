package ru.lomov.cloudhood.server;

import java.sql.*;

public class JdbcConnect implements AuthProvider{
    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private Savepoint savepoint;

    private static final String CREATE_NEW_USER = "insert into users (nickname, login, password) values (?, ?, ?);";



    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:cloud_hood_db.db");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Подключение к базе данных провалено!");
        }
    }

    public void disconnect() {
        try{
            if (statement != null){
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try{
            if (preparedStatement != null){
                preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void createTableUsers(String UID) throws SQLException {
        statement.executeUpdate(String.format("CREATE TABLE if not exists users (\n" +
                " '%s' TEXT PRIMARY KEY UNIQUE, \n" +
                "nickname TEXT UNIQUE, \n" +
                "login TEXT UNIQUE, \n" +
                "password TEXT\n" +
                ");;", UID));
    }
    public void createPsUpdate(String nickname, String login, String password) throws SQLException {
        preparedStatement = connection.prepareStatement(CREATE_NEW_USER);
        preparedStatement.setString(1, nickname);
        preparedStatement.setString(2, login);
        preparedStatement.setString(3, password);
        preparedStatement.executeUpdate();
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        try(ResultSet rs = statement.executeQuery("select nickname from users where login = '" + login + "' and password = '" + password +"';")){
            if (rs.next()) {
                return rs.getString(2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getPasswordByUID(String UID) {
        try(ResultSet rs = statement.executeQuery("select password from users where UID = '" + UID +"';")){
            if (rs.next()) {
                return rs.getString(4);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void changePasswordByUID(String UID, String newPassword) {

    }

    @Override
    public void changeNicknameIfAuth(String oldNickname, String newNickname) {

    }
}
