package ru.lomov.cloudhood.server;

public interface AuthProvider {
    String getNicknameByLoginAndPassword(String login, String password);
    String getPasswordByUID(String UID);
    void changePasswordByUID(String UID, String newPassword);
    void changeNicknameIfAuth(String oldNickname, String newNickname);
}
