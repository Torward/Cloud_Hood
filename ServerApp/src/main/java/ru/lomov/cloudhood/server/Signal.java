package ru.lomov.cloudhood.server;

public enum Signal {
    SEND_FILE_TO_CLIENT((byte) 16),
    WRITE_FILE_TO_CLOUD((byte) 36),
    SEND_FILE_LIST((byte) 52),
    SHARE_FILE_TO_FRIEND((byte) 11),
    DELETE_FILE((byte) 2),
    VOID((byte) -1);

   byte signalByte;

    Signal(byte signalByte) {
        this.signalByte = signalByte;
    }

    static Signal getSignalByte(byte command) {
        if (command == SEND_FILE_TO_CLIENT.signalByte) {
            return SEND_FILE_TO_CLIENT;
        }
        if (command == WRITE_FILE_TO_CLOUD.signalByte) {
            return WRITE_FILE_TO_CLOUD;
        }
        if(command == SEND_FILE_LIST.signalByte){
            return SEND_FILE_LIST;
        }
        if (command == SHARE_FILE_TO_FRIEND.signalByte){
            return SHARE_FILE_TO_FRIEND;
        }
        if (command == DELETE_FILE.signalByte){
            return DELETE_FILE;
        }
        return VOID;
    }
}
