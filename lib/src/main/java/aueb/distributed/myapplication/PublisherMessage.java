package aueb.distributed.myapplication;

public class PublisherMessage extends Message {
    private String topicToUploadTo;
	int ChunksofFile;
	String fileName;
    boolean found;



    public PublisherMessage(String topic, int numberOfChunks, String fileName){
        topicToUploadTo = topic;
		ChunksofFile = numberOfChunks;
		this.fileName = fileName;
		
    }

    public String getTopicToUploadTo() {
        return topicToUploadTo;
    }
	
	public int getnumberOfChunks() {
        return ChunksofFile;
    }
	
	public String getfileName() {
        return fileName;
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }
}


