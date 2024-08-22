package aueb.distributed.myapplication;

import org.xml.sax.SAXException;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class Publisher extends Thread {
	UserNodeInfo userNode;
	String search;
	String action;
	String path;
	String nameOfFile;
	BrokerImpl rightBroker;
	String topic;
	Connection con;
	private static final long serialVersionUID = -2723363051271966964L;

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public void setrightBroker(BrokerImpl rightBroker) {
		this.rightBroker = rightBroker;
	}

	
	
	public Publisher (UserNodeInfo userNode){
		this.userNode = userNode;
	}
	
	public void run() {

		Scanner sc = new Scanner(System.in);   // Create a Scanner object

		while(true){
			
			
			System.out.print(userNode.getProfileName() + "-> Write the path of the file you want to upload, or press exit to connect to another Topic!");
			if (sc.hasNextLine())
			path = sc.nextLine();  // Read user input

			if(path.equals("exit")){
				try {
					con = new Connection(new Socket(rightBroker.ip, rightBroker.port));
					PublisherMessage message = new PublisherMessage(path,0, userNode.getProfileName() );
					con.out.writeObject(message);// con= connection pou tha ftia3oume
					con.out.flush();
					//System.out.print(userNode.getProfileName() + "-> Message Sent ");
					con.in.close();
					con.out.close();
					return;
				} catch (Exception e) {
					System.out.println("Problemo me exit");
				}

			}
			System.out.print(userNode.getProfileName() + "-> Give the name of the file (include the extension)! ");
			nameOfFile = sc.nextLine();
			
			try {

				
				FileInfo file = null;
				
				file = new FileInfo (nameOfFile,path);
				//System.out.print(userNode.getProfileName() + "-> File created ");

				//steile ston broker message sxetika me to topic, kai to number of chunks toy arxeiou
				con = new Connection(new Socket(rightBroker.ip, rightBroker.port));
				PublisherMessage message = new PublisherMessage(topic,file.getNumberofChunks(path,nameOfFile),nameOfFile);
				con.out.writeObject(message);// con= connection pou tha ftia3oume
				con.out.flush();
				//System.out.print(userNode.getProfileName() + "-> Message Sent ");

				FileInfoTransfer filetransfer = new FileInfoTransfer(nameOfFile, file.getNumberofChunks(path, nameOfFile));
				filetransfer.setSender(userNode.getProfileName());

				message = (PublisherMessage) con.in.readObject();
				if (message.isFound()) {
					//System.out.println("sprwxnwwwww");
					BrokerMessage PublisherMessageForBroker = new BrokerMessage(topic, nameOfFile, filetransfer);
					con.out.writeObject(PublisherMessageForBroker);// con= connection pou tha ftia3oume
					con.out.flush();
					push(con, file, search); // na douke thn push
				}
				else{
					System.out.println("Topic not found");
					continue;
				}

				
				// End of sent to broker the file chunks 
			} catch (IOException | SAXException| ClassNotFoundException e) {
				//e.printStackTrace();
				System.out.println("File or Path not found");
			}
            //sc.close();        	
		}
		//myObj.close();
		
	}
	
	public void push(Connection connection, FileInfo file, String search) throws IOException, ClassNotFoundException {
        for(byte[] chunk: file.getChunks()) {
			BrokerMessage message = (BrokerMessage) connection.in.readObject();
			message.fileTransfer.setChunk(chunk);
			connection.out.writeObject(message);
			connection.out.flush();
        }
		//System.out.println("File sent!");
    }

}