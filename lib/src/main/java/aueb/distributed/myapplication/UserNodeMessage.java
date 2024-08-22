package aueb.distributed.myapplication;

import java.util.ArrayList;

public class UserNodeMessage extends Message{
    private ArrayList<BrokerImpl> allBrokers = null;

    public UserNodeMessage() {
        this.allBrokers = new ArrayList<BrokerImpl>();
    }

    public void setAllBrokers(ArrayList<BrokerImpl> allBrokers) {
        this.allBrokers = allBrokers;
    }

    public ArrayList<BrokerImpl> getAllBrokers() {
        return allBrokers;
    }
}
