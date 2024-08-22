package aueb.distributed.myapplication;

import android.util.Log;

import org.xml.sax.SAXException;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class Publisher extends Thread {
	UserNodeInfo userNode;
	String search;
	String action;
	String path = "don't send";
	String nameOfFile;
	BrokerImpl rightBroker;
	String topic;
	Connection con;
	boolean flag=false;

	public void setPath(String path){this.path = path;}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

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
	
	/*public void run() {
		Log.d("Tag", "Publisher created");
	}*/
	public void run() {

		Log.d("PUBLISHER", "Created");

		while(true) {
			if (flag) {

				if (path.equals("exit")) {
					try {
						con = new Connection(new Socket(rightBroker.ip, rightBroker.port));
						PublisherMessage message = new PublisherMessage(path, 0, userNode.getProfileName());
						con.out.writeObject(message);// con= connection pou tha ftia3oume
						con.out.flush();
						Log.d("Tag",userNode.getProfileName() + "-> Message Sent ");
						con.in.close();
						con.out.close();
						return;
					} catch (Exception e) {
						Log.d("Tag","Problem with exit");
					}

				}

				//spaw apo to path filename kai path xwris filename
				nameOfFile = path.substring(path.lastIndexOf('/') + 1);
				path = path.replace(nameOfFile,"");

				//nameOfFile = "download.jpeg";
				Log.d("Tag", "File = " + path + nameOfFile);

				try {

					//Log.d("Tag", "At least i TRY");
					FileInfo file = null;

					//Log.d("Tag", "File null");

					file = new FileInfo(nameOfFile, path);

					//Log.d("Tag",userNode.getProfileName() + "-> File created ");

					//steile ston broker message sxetika me to topic, kai to number of chunks toy arxeiou
					con = new Connection(new Socket(rightBroker.ip, rightBroker.port));
					//Log.d("Tag", "con created to send file");
					PublisherMessage message = new PublisherMessage(topic, file.getNumberofChunks(path, nameOfFile), nameOfFile);
					con.out.writeObject(message);// con= connection pou tha ftia3oume
					con.out.flush();
					//Log.d("Tag", userNode.getProfileName() + "Publisher-> Message Sent ");

					FileInfoTransfer filetransfer = new FileInfoTransfer(nameOfFile, file.getNumberofChunks(path, nameOfFile));
					filetransfer.setSender(userNode.getProfileName());

					message = (PublisherMessage) con.in.readObject();
					if (message.isFound()) {
						Log.d("PUBLISHER", "Pushing");
						BrokerMessage PublisherMessageForBroker = new BrokerMessage(topic, nameOfFile, filetransfer);
						con.out.writeObject(PublisherMessageForBroker);// con= connection pou tha ftia3oume
						con.out.flush();
						push(con, file, search); // na douke thn push
					} else {

						Log.d("Tag","Topic not found");
						//continue;
					}
					con.in.close();
					con.out.close();

					// End of sent to broker the file chunks
				} catch (IOException | SAXException | ClassNotFoundException e) {
					e.printStackTrace();
					System.out.println("File or Path not found");
				}

				path = "don't send";
				flag = false;
			}
		}

		
	}
	
	public void push(Connection connection, FileInfo file, String search) throws IOException, ClassNotFoundException {
        for(byte[] chunk: file.getChunks()) {
			BrokerMessage message = (BrokerMessage) connection.in.readObject();
			message.fileTransfer.setChunk(chunk);
			connection.out.writeObject(message);
			connection.out.flush();
        }
    }

}