package aueb.distributed.myapplication;

public class ConsumerMessage extends Message {
    private String topicToConnectTo;
    boolean found = false;
    FileInfoTransfer fileTransfer;
    int size;
    String print;
	UserNodeInfo NodeInfo;
    //boolean emptyHistory = false;



    public ConsumerMessage(UserNodeInfo var1, String var2) {
        this.NodeInfo = var1;
        //this.NodeInfo = new UserNodeInfo(var1.getIp(), var1.getPort(), var1.getProfileName());
        this.topicToConnectTo = var2;
        fileTransfer = new FileInfoTransfer();

    }

    public ConsumerMessage(){fileTransfer = new FileInfoTransfer();}

    public UserNodeInfo getNodeInfo(){
        return NodeInfo;
    }

    public String getTopicToConnectTo() {
        return this.topicToConnectTo;
    }

    public int getSize() {
        return size;
    }

    public String getPrint() {
        return print;
    }

    public FileInfoTransfer getFileTransfer() {
        return fileTransfer;
    }

    public void setFileTransfer(FileInfoTransfer fileTransfer) {
        this.fileTransfer = fileTransfer;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public void setPrint(String print) {
        this.print = print;
    }

    public boolean isFound() {
        return found;
    }

    public void setTopicToConnectTo(String name){
        this.topicToConnectTo = name;
    }
}
