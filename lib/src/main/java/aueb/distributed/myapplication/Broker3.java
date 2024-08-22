package aueb.distributed.myapplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

public class Broker3 {
    public static void main(String args[]) throws IOException {

        Properties prop = new Properties();
        FileInputStream cf = new FileInputStream("C:\\WhatsApoVag\\config.properties");
        prop.load(cf);

        String ip = null;
        int port = 0;
        String name = null;
        ip = prop.getProperty("broker3ip");
        port = Integer.parseInt((prop.getProperty("broker3port")));
        name = prop.getProperty("broker3name");

        BrokerImpl broker = new BrokerImpl(ip, port, name);

        broker.ListOfBrokers = broker.writeDownBrokers();
        Collections.sort(broker.ListOfBrokers);
        broker.BrokerTopics = broker.assignTopics();
        broker.init(broker.port);
        broker.connect();

    }
}
