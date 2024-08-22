package aueb.distributed.myapplication;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.xml.sax.SAXException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class FileInfo implements Serializable {
	String fileName;

	ArrayList<byte[]> FileChunks;
	String path;
	int theNumberOfChunks;

	public int getTheNumberofChunks(){
		return theNumberOfChunks;
	}

	public FileInfo(String fileName, String path) throws IOException, SAXException {
		//Log.d("Tag","Trying to create file" );
		this.fileName = fileName;
		this.path = path;
		//Log.d("Tag","Filename and path are ok" );

		FileChunks = this.generateChunks(path,fileName);
		Log.d("FileInfo","File created" );
	}

	public ArrayList<byte []> getChunks(){
		return FileChunks;
	}

	//@RequiresApi(api = Build.VERSION_CODES.O)
	@SuppressLint("SetWorldReadable")
	public synchronized ArrayList<byte []> generateChunks(String path, String fileName) throws IOException {
		Log.d("FileInfo","Got in GenerateChunks" );
		ArrayList<byte[]> listOfChunks = new ArrayList<byte[]>();
		File file = new File(path  + fileName );//File read from Source folder to Split.
		Log.d("FileInfo",file.getPath());

		byte[] allBytes =null;
		if (file.exists()){
			allBytes = Files.readAllBytes(file.toPath());
		}

		int sizeOfChunk = 64000;
		theNumberOfChunks = allBytes.length / sizeOfChunk;

		if ((allBytes.length % sizeOfChunk)>0){
			theNumberOfChunks ++;
		}

		byte[] a;

		int counter = 0;
		for(int x = 0; x < theNumberOfChunks; x++) {
			//vale sto kathe chunk, ola ta bytes analoga me to sizeChunk
			a = new byte[sizeOfChunk];
			for (int y = 0; y < sizeOfChunk; y++) {
				if (allBytes.length > counter){
					a[y] = allBytes[counter];
					counter++;
				}
				else
					break;
				
			}
			listOfChunks.add(a);
		}
		return listOfChunks;
	}
	
	public int getNumberofChunks (String path, String fileName) throws IOException{
		//File file = new File(path + "\\" + fileName );//File read from Source folder to Split.
		File file = new File(path  + fileName );//File read from Source folder to Split.
		//file.setReadable(true,false);
		byte[] allBytes = Files.readAllBytes(file.toPath());
		int sizeOfChunk = 64000;
		int numberOfChunks = allBytes.length / sizeOfChunk;
		if ((allBytes.length % sizeOfChunk)>0){
			numberOfChunks ++;
		}
		return numberOfChunks;
	}
	
	public static byte[] toByteArray(ArrayList<byte[]> bytesList) {
		int size = 0;
		for (byte[] bytes : bytesList) {
			size += bytes.length;
		}
		ByteBuffer byteBuffer = ByteBuffer.allocate(size);
		for (byte[] bytes : bytesList) {
			byteBuffer.put(bytes);
		}
		byte[] a = byteBuffer.array();
		return a;
	}

	//eisagwgh tou arxeiou pou stalthike sto fakelo ths sunomilias
	public static String byteArrayToFile(byte[] b, String profilName,String topic, String fileName) throws IOException {
		//Reverse byte[] array to File
		String path = "/storage/self/primary/Download/" + profilName + "/" + topic +"/";

		File splitFile = new File(path);//Destination folder to save.
		if (!splitFile.exists()) {
			splitFile.mkdirs();
		}

		File doc = new File(splitFile, fileName);

		FileOutputStream fos = new FileOutputStream(doc.getPath());
		fos.write(b);
		fos.close();

		path = path  + fileName ;
		Log.d("FileInfo","Path= "+path);
		return path;
	}
	

}