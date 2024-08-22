package aueb.distributed.myapplication;

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
		this.fileName = fileName;
		this.path = path; 

		FileChunks = this.generateChunks(path,fileName);
	}

	
	public ArrayList<byte []> getChunks(){
		return FileChunks;
	}

	
	public synchronized ArrayList<byte []> generateChunks(String path, String fileName) throws IOException {
		ArrayList<byte[]> listOfChunks = new ArrayList<byte[]>();
		File file = new File(path + "\\" + fileName );//File read from Source folder to Split.

		byte[] allBytes = Files.readAllBytes(file.toPath());
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
		File file = new File(path + "\\" + fileName );//File read from Source folder to Split.
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
	
	

	public byte[] fileToByteArray1(String path) throws IOException {
		File file = new File(path);
		byte[] videoFileChunk= Files.readAllBytes(file.toPath());
		return videoFileChunk;
	}

	// dhmiourgia fakelou gia to story
	public static String byteArrayToFileHistory(byte[] b, String profilName, String fileName) throws IOException {
		//Reverse byte[] array to File
		String path = "C:\\distributed-systems\\data\\" + profilName + "\\history";

		File splitFile = new File(path);//Destination folder to save.
		if (!splitFile.exists()) {
			splitFile.mkdirs();
			//System.out.println("History Created -> " + splitFile.getAbsolutePath());
		}
		Path path1 = Paths.get(path + "\\" + fileName);
		Files.write(path1, b);
		path = path + "\\" + fileName;
		return path;
	}
	
	
	//eisagwgh tou arxeiou pou stalthike sto fakelo ths sunomilias
	public static String byteArrayToFile(byte[] b, String profilName,String topic, String fileName) throws IOException {
		//Reverse byte[] array to File
		String path = "C:\\WhatsApoVag\\data\\" +profilName  + "\\" + topic;

		File splitFile = new File(path);//Destination folder to save.
		if (!splitFile.exists()) {
			splitFile.mkdirs();
			//System.out.println("File has been inserted in the topic  -> " + splitFile.getAbsolutePath());
		}
		Path path1 = Paths.get(path + "\\" + fileName);
		Files.write(path1, b);
		path = path + "\\" + fileName ;
		return path;
	}
	

}