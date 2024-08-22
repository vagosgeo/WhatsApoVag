package aueb.distributed.myapplication;

import java.io.Serializable;
import java.util.ArrayList;


public class Topic implements Serializable {

    String TopicName;
    ArrayList<UserNodeInfo> RegisteredUsers = new ArrayList<UserNodeInfo>();
    ArrayList<byte[]> History = new ArrayList<byte[]>();
    ArrayList<String> FileName = new ArrayList<String>();
    ArrayList<Integer> FileChunkCounter = new ArrayList<Integer>(); // krataei th thesh tou teleutaioy chunk tou arxeiou
                                                                    // autoy
                                                                    
    ArrayList<Connection> BConnections;
    ArrayList<String> Senders = new ArrayList<String>();


    public Topic(String TopicName) {
        this.TopicName = TopicName;
    }

    public String getTopicName() {
        return this.TopicName;
    }


    public ArrayList<UserNodeInfo> getRegisteredUsers() {
        return RegisteredUsers;
    }

    public ArrayList<byte[]> getHistory() {
        return History;
    }


    public ArrayList<Integer> getFileChunkCounter() {
        return FileChunkCounter;
    }

    public ArrayList<String> getFileName() {
        return FileName;
    }

    public ArrayList<String> getSenders() {
        return Senders;
    }
}
