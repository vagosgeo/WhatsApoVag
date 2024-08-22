package aueb.distributed.myapplication;

import android.util.Log;

import org.xml.sax.SAXException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class UserNodeImpl extends Thread{

	String ipForTheFirstBroker;
    int portForTheFirstBroker;
	UserNodeInfo NodeInfo ;
	String path;
    Message message;
	ServerSocket providerSocket;
    Socket connection = null;
	String register;
	BrokerImpl connectedBroker; //to xreiazomaste ston publisher klp
	ArrayList<BrokerImpl> allBrokers = new ArrayList<BrokerImpl>();
    Consumer cons;

    public Consumer getCons() {
        return cons;
    }

    private static final long serialVersionUID = -2723363051271966964L;

	
	
	public UserNodeImpl( String ipBroker, int portBroker, UserNodeInfo NodeInfo) throws UnknownHostException {
        this.NodeInfo = NodeInfo;
        path = "C:\\WhatsApoVag\\data\\" + NodeInfo.getProfileName();
        this.ipForTheFirstBroker = ipBroker;
        this.portForTheFirstBroker = portBroker;
        Log.d("Tag","UserNode constructed: Name: " +NodeInfo.getProfileName()+" ipBroker: "+ipForTheFirstBroker+" port: "+portForTheFirstBroker);

    }
	
	@Override
    public void run() {
        try {
            this.init();
            this.connect();

        } catch (IOException | ClassNotFoundException  | SAXException | InterruptedException e) {
            e.printStackTrace();
        }
    }
	
	public void init() throws IOException, ClassNotFoundException, SAXException {
        this.initBrokers();
        //Collections.sort(allBrokers);
        //Log.d("Tag","UserNodeImpl Init");
        providerSocket = new ServerSocket(NodeInfo.getPort(), 10);
        //Log.d("Tag","eftiaksa socket");
	}
	
	public void connect() throws IOException, ClassNotFoundException, SAXException, InterruptedException {
		cons = new Consumer(new UserNodeInfo(NodeInfo.getIp(), NodeInfo.getPort() + 1, NodeInfo.getProfileName(), NodeInfo.getTopic()));
        cons.setAllBrokers(allBrokers);
        Log.d("Tag","Brokers set");
		cons.start();
    }
		
	public void initBrokers() throws IOException, ClassNotFoundException {
        Log.d("Tag","initBrokers called");
        message = new UserNodeMessage();
        Socket soc = new Socket(ipForTheFirstBroker, portForTheFirstBroker);
        Connection giveMeTheListOfBroker = new Connection(soc);
        //Log.d("Tag","initBrokers connection created");
        giveMeTheListOfBroker.out.writeObject(message);
        giveMeTheListOfBroker.out.flush();
        //Log.d("Tag","initBrokers message sent");
        //o broker exei grapsei sto connection ta info twn brokers
        UserNodeMessage m = (UserNodeMessage) giveMeTheListOfBroker.in.readObject();
        //Log.d("Tag","Message received from Broker");
        allBrokers = m.getAllBrokers();
        giveMeTheListOfBroker.in.close();
        giveMeTheListOfBroker.out.close();
    }
	
	public BrokerImpl findTheRightBroker(BigInteger hashS) throws IOException {
        int size = allBrokers.size();
        int count=0;
        for(BrokerImpl b: allBrokers) {
            count++;
            if ((hashS.compareTo(b.brokerHash) <= 0)) {
                return b;
            }
            else if ((hashS.compareTo(b.brokerHash) == 1 && count<size)){
                continue;
            }
            else{
                BigInteger modValue = BigInteger.valueOf(size);
                BigInteger indexOfBr = hashS.mod(modValue);
                return allBrokers.get(indexOfBr.intValue());
            }
        }
        return null;
    }

	public List<BrokerImpl> getBroker() {
        return allBrokers;
    }


    public void disconnect() throws IOException {
        System.out.println("I am closing the connection. BYE!");
    }
	
	 
	
//	public static void main(String args[]) throws UnknownHostException {
//        String ipBroker = args[0];
//        int portBroker = Integer.parseInt(args[1]);
//
//		// UserNodeInfo user11 = new UserNodeInfo ("localhost", 22222, "Apostolis");
//        // UserNodeImpl user1 = new UserNodeImpl(ipBroker, portBroker, user11);
//        // user1.start();
//
//         UserNodeInfo user22 = new UserNodeInfo("localhost", 44444, "Vaggelakos");
//         UserNodeImpl user2 = new UserNodeImpl(ipBroker, portBroker, user22);
//         user2.start();
//
//        // UserNodeInfo user33 = new UserNodeInfo ("localhost", 33333, "RandomName");
//        // UserNodeImpl user3 = new UserNodeImpl(ipBroker, portBroker, user33);
//        // user3.start();
//
//
//
//    }
	
}