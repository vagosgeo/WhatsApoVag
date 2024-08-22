package aueb.distributed.myapplication;

public class BrokerMessage extends Message {

    private String topic;
    private String fileName;
    FileInfoTransfer fileTransfer;

    public BrokerMessage(String topic){
        this.topic = topic;
        fileTransfer = new FileInfoTransfer();

    }

    public BrokerMessage(String topic, String filename, FileInfoTransfer fileTransfer ){
        this.topic = topic;
        this.fileTransfer = fileTransfer;
        this.fileName = filename;

    }

    public String getTopic() {
        return topic;
    }

    public String getFileName() {
        return fileName;
    }

    public FileInfoTransfer getFileTransfer() {
        return fileTransfer;
    }
}