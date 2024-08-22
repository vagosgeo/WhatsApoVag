package aueb.distributed.myapplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class BrokerImpl extends Thread implements Broker, Serializable, Comparable<BrokerImpl> {

    String brokerName;
    String ip;
    int port;
    ServerSocket providerSocket;
    Socket connection = null;
    BigInteger brokerHash;

    ArrayList<BrokerImpl> ListOfBrokers;
    ArrayList<UserNodeImpl> registeredUsers = new ArrayList<UserNodeImpl>();
    ArrayList<Topic> BrokerTopics;

    private static final long serialVersionUID = 2683471646486152803L;

    //private static final long serialVersionUID = -2723363051271966964L;

    public BrokerImpl(String ip, int port, String name) {
        this.brokerName = name;
        this.ip = ip;
        this.port = port;
        brokerHash = MD5.hashText(ip + port);
    }

    @Override
    public void init(int port) throws IOException {
        providerSocket = new ServerSocket(port, 10); // backlog einai posa connections taytoxrona
    }

    @Override
    public void connect() {
        System.out.println("I am " + this.brokerName + " and waiting for a connection.");
        try {
            while (true) {
                connection = providerSocket.accept();
                this.receiveMessages();
            }
        } catch (IOException | InterruptedException ioException) {
            ioException.printStackTrace();
        } finally {
            this.disconnect();
        }
    }

    @Override
    public void receiveMessages() throws InterruptedException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connectWithSomeone = null;
                try {
                    connectWithSomeone = new Connection(connection);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Message message;
                try {
                    message = (Message) connectWithSomeone.in.readObject();
                    if (message instanceof UserNodeMessage) {
                        UserNodeMessage messageFromUserNode = (UserNodeMessage) message;
                        messageFromUserNode.setAllBrokers(ListOfBrokers);

                        connectWithSomeone.out.writeObject(messageFromUserNode);
                        connectWithSomeone.out.flush();
                        connectWithSomeone.out.close();
                        connectWithSomeone.in.close();
                    }
                    if (message instanceof ConsumerMessage) {
                        ConsumerMessage messageFromConsumer = (ConsumerMessage) message;
                        System.out.println( messageFromConsumer.getNodeInfo().getProfileName());
                        // search if topic exists on broker and if user is registered
                        boolean foundTopic = false;
                        boolean foundUser = false;
                        Topic tempTopic =null;
                        for (Topic btopic : BrokerTopics) {
                            if (messageFromConsumer.getTopicToConnectTo().equals(btopic.getTopicName())) {

                                tempTopic = btopic;

                                for (UserNodeInfo N : btopic.getRegisteredUsers()) {
                                    if (messageFromConsumer.getNodeInfo().getProfileName().equals(N.getProfileName())) {
                                        foundUser = true;
                                        break;
                                    }
                                }
                                if (foundUser == false) {
                                    //messageFromConsumer.getNodeInfo().setConnection(connectWithSomeone);
                                    btopic.getRegisteredUsers().add(messageFromConsumer.getNodeInfo());
                                    System.out.println("Registered User: "+ messageFromConsumer.getNodeInfo().getProfileName() + " to chatroom: "+ messageFromConsumer.getTopicToConnectTo() + ".");
                                }

                                foundTopic = true;
                                break;
                            }
                        }
                        messageFromConsumer.setFound(foundTopic);

                        
                        if (foundTopic) {
                            if (!tempTopic.getHistory().isEmpty()) {
                                messageFromConsumer.setSize(tempTopic.getFileName().size());
                                messageFromConsumer.getFileTransfer().setFileNames(tempTopic.getFileName());
                                messageFromConsumer.getFileTransfer().setChunkCounter(tempTopic.getFileChunkCounter());
                                messageFromConsumer.getFileTransfer().setSenderNames(tempTopic.getSenders());
                                connectWithSomeone.out.writeObject(messageFromConsumer);
                                connectWithSomeone.out.flush();

                                ArrayList<byte[]> clone = new ArrayList<byte[]>();
                                for (byte[] p : tempTopic.getHistory())
                                    clone.add(p.clone());

                                // Send to consumer the file's chunks from History
                                while (!clone.isEmpty()) {
                                    messageFromConsumer = (ConsumerMessage) connectWithSomeone.in.readObject();
                                    messageFromConsumer.getFileTransfer().setChunk(clone.remove(0));
                                    connectWithSomeone.out.writeObject(messageFromConsumer);
                                    connectWithSomeone.out.flush();
                                }

                            } else {
                                messageFromConsumer.setSize(0);
                                messageFromConsumer.setPrint(
                                    "You are the first one in the chatroom.");
                                connectWithSomeone.out.writeObject(messageFromConsumer);
                                connectWithSomeone.out.flush();
                            }
                        } else {
                            messageFromConsumer.setPrint(
                                    "The topic that you are searching on doesn't exist! Try again with something else.");
                            connectWithSomeone.out.writeObject(messageFromConsumer);
                            connectWithSomeone.out.flush();
                        }
                        Thread.sleep(2000);
                        connectWithSomeone.in.close();
                        connectWithSomeone.out.close();
                        

                    }
                    if (message instanceof PublisherMessage) {
                        PublisherMessage messageFromPublisher = (PublisherMessage) message;

                        if ( messageFromPublisher.getTopicToUploadTo().equals("exit") ) {
                            //System.out.println("Oxw apo edw");
                            for (Topic btopic : BrokerTopics) {
                                for( UserNodeInfo user : btopic.getRegisteredUsers()){
                                    if (messageFromPublisher.getfileName().equals(user.getProfileName()) ) {
                                        
                                        Connection connectWithConsumer = new Connection(new Socket(user.getIp(), user.getPort()));
                                        ConsumerMessage messageForConsumer = new ConsumerMessage();
                                        
                                        messageForConsumer.setTopicToConnectTo("exit");
                                        
                                        connectWithConsumer.out.writeObject(messageForConsumer);
                                        connectWithConsumer.out.flush();

                                        connectWithConsumer.in.close();
                                        connectWithConsumer.out.close();

                                        btopic.getRegisteredUsers().remove(user);
                                        connectWithSomeone.out.close();
                                        connectWithSomeone.in.close();
                                        return;
                                    }
                                }
                            }

                            

                        }

                        final ArrayList<byte[]> queue = new ArrayList<byte[]>();

                        // search for topic
                        boolean foundTopic = false;
                        // boolean foundUser = false;
                        Topic tempTopic = null;
                        for (Topic btopic : BrokerTopics) {
                            if (messageFromPublisher.getTopicToUploadTo().equals(btopic.getTopicName())) {

                                tempTopic = btopic;

                                foundTopic = true;
                                break;
                            }
                        }
                        messageFromPublisher.setFound(foundTopic);
                        connectWithSomeone.out.writeObject(messageFromPublisher);
                        connectWithSomeone.out.flush();
                        if (!foundTopic) {
                            
                            connectWithSomeone.out.writeObject(messageFromPublisher);
                            connectWithSomeone.out.flush();
                        } else {


                            BrokerMessage messageForPublisher = (BrokerMessage) connectWithSomeone.in.readObject();

                            final String fileName = messageForPublisher.getFileTransfer().getSingleFileName();
                            final int chunkCounter = messageForPublisher.getFileTransfer().getSingleChunkCounter();
                            final String senderName = messageForPublisher.getFileTransfer().getSender();

                            tempTopic.getFileName().add(fileName);
                            tempTopic.getFileChunkCounter().add(chunkCounter);
                            tempTopic.getSenders().add(senderName);

                            tempTopic.pull(connectWithSomeone, messageForPublisher, tempTopic, queue);

                            connectWithSomeone.in.close();
                            connectWithSomeone.out.close();

                            final String topicname = tempTopic.getTopicName();
                            System.out.println("5");
                            for (final UserNodeInfo myconsumer : tempTopic.getRegisteredUsers()) {
                                Thread t = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try{
                                            System.out.println(myconsumer.getProfileName());
//                                            System.out.println("paei na ginei");
//                                            Socket s = new Socket(myconsumer.getIp(), myconsumer.getPort());
                                            Socket s = new Socket(myconsumer.getIp(), 9999);
//                                            Socket s = new Socket("127.0.0.1", 9999);
//                                            System.out.println("socket komple");
                                            Connection connectWithConsumer = new Connection(s);
//                                            System.out.println("egine to connection");
                                            ConsumerMessage messageForConsumer = new ConsumerMessage();
                                            messageForConsumer.setSize(1);
                                            messageForConsumer.getFileTransfer().setSingleFileNames(fileName);
                                            messageForConsumer.getFileTransfer().setSingleChunkCounter(chunkCounter);
                                            messageForConsumer.getFileTransfer().setSender(senderName);
                                            messageForConsumer.setTopicToConnectTo(topicname);
//                                            System.out.println("esteila plhr");
                                            connectWithConsumer.out.writeObject(messageForConsumer);
                                            connectWithConsumer.out.flush();
//                                            System.out.println("flush");
                                           

                                            ArrayList<byte[]> clone = new ArrayList<byte[]>();
                                            for (byte[] p : queue)
                                                clone.add(p.clone());

                                            // Send to consumer the file's chunks from History
                                            while (!clone.isEmpty()) {
//                                                System.out.println("mphka sth while");
                                                messageForConsumer = (ConsumerMessage) connectWithConsumer.in.readObject();
//                                                System.out.println("phra mhnyma apo consumer");
                                                messageForConsumer.getFileTransfer().setChunk(clone.remove(0));
//                                                System.out.println("papap");
                                                connectWithConsumer.out.writeObject(messageForConsumer);
//                                                System.out.println("egrapsa mhnyma");
                                                connectWithConsumer.out.flush();
//                                                System.out.println("flush");
                                            }
                                            Thread.sleep(2000);
                                            connectWithConsumer.out.close();
                                            connectWithConsumer.in.close();
//                                            System.out.println("CLOSED CONNECTION");
                                        } catch (IOException | ClassNotFoundException | InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                t.start();
                            }

                        }
                    }
                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    connectWithSomeone.out.close();
                    connectWithSomeone.in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

//    public synchronized void pull(Connection connection, BrokerMessage message, Topic top, ArrayList<byte[]> queue)
//            throws IOException, ClassNotFoundException {
//
//        // Received from publisher the video chunks 3
//        for (int hmc : message.getFileTransfer().getChunkCounter()) {
//
//            for (int ii = 0; ii < hmc; ii++) {
//
//                connection.out.writeObject(message);
//                connection.out.flush();
//                message = (BrokerMessage) connection.in.readObject();
//                top.getHistory().add(message.getFileTransfer().getChunk());
//                queue.add(message.getFileTransfer().getChunk());
//            }
//        }
//
//    }


    @Override
    public void disconnect() {
        try {
            providerSocket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public ArrayList<BrokerImpl> writeDownBrokers() throws IOException {
        Properties prop = new Properties();
        FileInputStream cf = new FileInputStream("C:\\WhatsApoVag\\config.properties");
        prop.load(cf);

        ArrayList<BrokerImpl> listOfAllBrokers = new ArrayList<BrokerImpl>();
        String ip = null;
        int port = 0;
        String name = null;
        for (int i = 1; i <= 3; i++) {
            ip = prop.getProperty("broker" + i + "ip");
            port = Integer.parseInt((prop.getProperty("broker" + i + "port")));
            name = prop.getProperty("broker" + i + "name");

            listOfAllBrokers.add(new BrokerImpl(ip, port, name));
        }
        return listOfAllBrokers;
    }

    public ArrayList<Topic> assignTopics() throws IOException {
        Properties prop = new Properties();
        FileInputStream cf = new FileInputStream("C:\\WhatsApoVag\\configchat.properties");
        prop.load(cf);

        ArrayList<Topic> listOfAllTopics = new ArrayList<Topic>();
        ArrayList<Topic> listOfMyTopics = new ArrayList<Topic>();
        String name = null;
        for (int i = 1; i <= 4; i++) {
            name = prop.getProperty("topic" + i + "name");

            listOfAllTopics.add(new Topic(name));
        }

        for (Topic topic : listOfAllTopics) {

            //if (this.equals(this.findTheRightBroker(MD5.hashText(topic.getTopicName())))) {
            if(this.brokerName.equals(this.findTheRightBroker(MD5.hashText(topic.getTopicName())).brokerName))  {  
                listOfMyTopics.add(topic);
            }
        }

        for (Topic topic : listOfAllTopics) {
            System.out.println("Topic: " + topic.getTopicName() + " Broker: " + this.findTheRightBroker(MD5.hashText(topic.getTopicName())).brokerName);
        }



        return listOfMyTopics;
    }

    public BrokerImpl findTheRightBroker(BigInteger hashS) throws IOException {
        int size = ListOfBrokers.size();
        int count = 0;
        for (BrokerImpl b : ListOfBrokers) {
            count++;
            if ((hashS.compareTo(b.brokerHash) <= 0)) {
                return b;
            } else if ((hashS.compareTo(b.brokerHash) == 1 && count < size)) {
                continue;
            } else {
                BigInteger modValue = BigInteger.valueOf(size);
                BigInteger indexOfBr = hashS.mod(modValue);
                return ListOfBrokers.get(indexOfBr.intValue());
            }
        }
        return null;
    }

    public int compareTo(BrokerImpl st) {
        return this.brokerHash.compareTo(st.brokerHash);
    }

    public static void main(String args[]) throws IOException {

        Properties prop = new Properties();
        FileInputStream cf = new FileInputStream("C:\\WhatsApoVag\\config.properties");
        prop.load(cf);

        String ip = null;
        int port = 0;
        String name = null;
        switch (Integer.parseInt(args[0])) {
            case 1:
                ip = prop.getProperty("broker1ip");
                port = Integer.parseInt((prop.getProperty("broker1port")));
                name = prop.getProperty("broker1name");
                break;
            case 2:
                ip = prop.getProperty("broker2ip");
                port = Integer.parseInt((prop.getProperty("broker2port")));
                name = prop.getProperty("broker2name");
                break;
            case 3:
                ip = prop.getProperty("broker3ip");
                port = Integer.parseInt((prop.getProperty("broker3port")));
                name = prop.getProperty("broker3name");
                break;
        }

        BrokerImpl broker = new BrokerImpl(ip, port, name);

        broker.ListOfBrokers = broker.writeDownBrokers();
        Collections.sort(broker.ListOfBrokers);
        broker.BrokerTopics = broker.assignTopics();
        broker.init(broker.port);
        broker.connect();

    }
}
