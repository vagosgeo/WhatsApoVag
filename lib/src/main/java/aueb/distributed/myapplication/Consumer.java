package aueb.distributed.myapplication;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Consumer extends Thread {
    UserNodeInfo userNode ;
    String filename;
    String sender;
    ArrayList<String> fileNames;
    ArrayList<String> senders;
    ArrayList<Integer> chunkCounter;
    int ccounter;
    Publisher publisher;
    String topic;
    ArrayList<BrokerImpl> allBrokers;
    ServerSocket providerSocket;
    Socket connection = null;
    private static final long serialVersionUID = -2723363051271966964L;


    public void setAllBrokers(ArrayList<BrokerImpl> allBrokers) {
        this.allBrokers = allBrokers;
    }

    public Consumer(UserNodeInfo userNode) {
        this.userNode = userNode;
    }

    public BrokerImpl findTheRightBroker(BigInteger hashS) throws IOException {
        int size = allBrokers.size();
        //System.out.println("size of all brokers: " + allBrokers.size());
        int count = 0;
        for (BrokerImpl b : allBrokers) {
            count++;
            if ((hashS.compareTo(b.brokerHash) <= 0)) {
                return b;
            } else if ((hashS.compareTo(b.brokerHash) == 1 && count < size)) {
                continue;
            } else {
                BigInteger modValue = BigInteger.valueOf(size);
                BigInteger indexOfBr = hashS.mod(modValue);
                return allBrokers.get(indexOfBr.intValue());
            }
        }
        return null;
    }

    public void init(int port) throws IOException {
        providerSocket = new ServerSocket(port, 10); // backlog einai posa connections taytoxrona
    }

    public void run() {

        try {
            //int nothing = 0;
            this.init(userNode.getPort());

        } catch (IOException e) {
            e.printStackTrace();
        }
        BrokerImpl connectedBroker;
        Connection con = null;
        ConsumerMessage messageForBroker;
        String search;
        //UserNodeInfo userNode;

        Scanner myObj = new Scanner(System.in); // Create a Scanner object
        System.out.print(userNode.getProfileName() + "-> Search Topic or press -end- for exit: ");
        search = myObj.nextLine(); // Read user input
        //myObj.close();

       while (!search.equals("-end-")) {
            try{    
            // sundesoy sto swsto broker

                connectedBroker = this.findTheRightBroker(MD5.hashText(search));
               
                con = new Connection(new Socket(connectedBroker.ip, connectedBroker.port));

                // Send message 
                messageForBroker = new ConsumerMessage(userNode, search); // profilename h object of user???????
                
                con.out.writeObject(messageForBroker);
                con.out.flush();


                ConsumerMessage consumerMessageFromBroker = (ConsumerMessage) con.in.readObject();
                // Received from broker size of history arraylist,filenames,chunkCounters
                if (consumerMessageFromBroker.isFound() && consumerMessageFromBroker.getSize()>0) {
                    //int size = consumerMessageFromBroker.getSize();
                    fileNames = consumerMessageFromBroker.getFileTransfer().getFileNames();
                    chunkCounter = consumerMessageFromBroker.getFileTransfer().getChunkCounter();
                    senders = consumerMessageFromBroker.getFileTransfer().getSenderNames();
   
                    for (int j = 0; j < chunkCounter.size(); j++) {

                        ArrayList<byte[]> file = new ArrayList<byte[]>();
                        for (int i = 0; i < chunkCounter.get(j); i++) {
                            // add the chunks one by one for every file in history in an ArrayList
                            con.out.writeObject(consumerMessageFromBroker);
                            con.out.flush();
                            consumerMessageFromBroker = (ConsumerMessage) con.in.readObject();
                            file.add(consumerMessageFromBroker.getFileTransfer().getChunk());
                        }
                        //x++;
                        // prepei consumer na pairnei to path
                        // FileInfo receivedFile = new FileInfo()
                        //FileInfo.byteArrayToFileHistory(FileInfo.toByteArray(file),userNode.getProfileName(), fileNames.get(j));
                        FileInfo.byteArrayToFile(FileInfo.toByteArray(file), userNode.getProfileName(), search, fileNames.get(j));

                        // consumerMessageForBroker.videoTransfer.getAssociatedHashTags(), path);
                        System.out.println("\n User: "+senders.get(j)+" sent -> "+ fileNames.get(j));
                    }

                }
                else if(!consumerMessageFromBroker.isFound())
                {
                    System.out.println(consumerMessageFromBroker.getPrint());
                    System.out.print(userNode.getProfileName() + "-> Search Topic or press -end- for exit: ");
                    search = myObj.nextLine(); // Read user input
                    con.out.close();
                    con.in.close();
                    continue;

                }

                Publisher pub = new Publisher(new UserNodeInfo(userNode.getIp(), userNode.getPort() + 1, userNode.getProfileName()));
                pub.setrightBroker(connectedBroker);
                pub.setTopic(search);
                pub.start();

                while (true) {// theloume to socket ths syndeshs
                    // perimene gia minimata
                    connection = providerSocket.accept();

                    con = new Connection(connection);                   

                    ConsumerMessage consumerMessageBroker = (ConsumerMessage) con.in.readObject();
                    
                    topic = consumerMessageBroker.getTopicToConnectTo();

                    if(topic.equals("exit")){
                        con.out.close();
                        con.in.close();
                        break;
                    }
                    filename = consumerMessageBroker.getFileTransfer().getSingleFileName();
                    ccounter = consumerMessageBroker.getFileTransfer().getSingleChunkCounter();
                    sender = consumerMessageBroker.getFileTransfer().getSender();



                    ArrayList<byte[]> listOfChunks = new ArrayList<byte[]>();
                    for (int i = 0; i < ccounter; i++) {
                        con.out.writeObject(consumerMessageBroker);
                        con.out.flush();
                        consumerMessageBroker = (ConsumerMessage) con.in.readObject();
                        listOfChunks.add(consumerMessageBroker.fileTransfer.getChunk());
                    }
                    FileInfo.byteArrayToFile(FileInfo.toByteArray(listOfChunks), userNode.getProfileName(), topic, filename);
                    System.out.println("\n User: "+sender+" sent -> "+ filename);
                    
                }
                
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            System.out.print(userNode.getProfileName() + "-> Search Topic or press -end- for exit: ");
            search = myObj.nextLine(); // Read user input
            
        }
        

    }
}