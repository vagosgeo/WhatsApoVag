package aueb.distributed.myapplication;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import org.xml.sax.SAXException;

interface Broker extends Serializable {



	void init(int a) throws IOException, ClassNotFoundException, SAXException;
	void connect() throws IOException, ClassNotFoundException, SAXException, InterruptedException;
	void disconnect() throws IOException;
	void receiveMessages() throws InterruptedException;
	//void pull(Connection connection, BrokerMessage message, Topic top, ArrayList<byte[]> queue) throws IOException, ClassNotFoundException;
	
}






