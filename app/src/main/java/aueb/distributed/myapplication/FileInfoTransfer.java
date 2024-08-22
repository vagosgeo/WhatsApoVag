package aueb.distributed.myapplication;

import java.io.Serializable;
import java.util.ArrayList;

public class FileInfoTransfer implements Serializable {
    private byte[] chunk;

    private ArrayList<Integer> chunkCounter;
    private ArrayList<String> fileNames;
    private ArrayList<String> senderNames;
    private String name;
    private String sender;
    private int counter;


    public FileInfoTransfer() {
        this.chunkCounter = new ArrayList<Integer>();
        this.fileNames = new ArrayList<String>();
    }

    public FileInfoTransfer(String name, int counter) {
        this.counter = counter;
        this.name = name;
        this.chunkCounter = new ArrayList<Integer>();
        this.chunkCounter.add(counter);
    }

    public ArrayList<String> getFileNames() {
        return fileNames;
    }

    public ArrayList<String> getSenderNames() {
        return senderNames;
    }

    public void setChunkCounter(ArrayList<Integer> chunkCounter) {
        this.chunkCounter = chunkCounter;
    }

    public byte[] getChunk() {
        return chunk;
    }

    public void setChunk(byte[] chunk) {
        this.chunk = chunk;
    }

    public void setFileNames(ArrayList<String> fileNames) {
        this.fileNames = fileNames;
    }

    public void setSenderNames(ArrayList<String> senderNames) {
        this.senderNames = senderNames;
    }

    public ArrayList<Integer> getChunkCounter() {
        return chunkCounter;
    }

    public int getSingleChunkCounter() {
        return counter;
    }

    public String getSingleFileName(){
        return name;
    }

    public String getSender(){
        return sender;
    }

    public void setSingleFileNames(String fileNames) {
        this.name = fileNames;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setSingleChunkCounter(int chunkCounter) {
        this.counter = chunkCounter;
    }
}
