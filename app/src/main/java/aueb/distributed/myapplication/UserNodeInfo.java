package aueb.distributed.myapplication;

import java.io.Serializable;

public class UserNodeInfo implements Serializable {
    private static final long serialVersionUID = -2723363051271966964L;
    protected String profileName;
    protected String ip;
    protected String topic;
    protected int port;
    protected Connection con;

    public UserNodeInfo(String ip, int port, String name, String topic) {
        this.profileName = name;
        this.ip = ip;
        this.port = port;
        this.topic = topic;
    }
    public UserNodeInfo(String ip, int port, String name) {
        this.profileName = name;
        this.ip = ip;
        this.port = port;
    }
    public void setProfileName(String name){profileName = name;}
    public void setIp(String ip){this.ip = ip;}
    public void setPort(int port) {this.port = port;}
    public void setConnection (Connection con){this.con = con;}

    public String getProfileName() {
        return profileName;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getTopic() {
        return topic;
    }
}