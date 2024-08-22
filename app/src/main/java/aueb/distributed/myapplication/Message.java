package aueb.distributed.myapplication;

import java.io.Serializable;

public abstract class Message implements Serializable {
    private static final long serialVersionUID = -2723363051271966964L;
    protected UserNodeInfo NodeInfo;

    public UserNodeInfo getNodeInfo() {
        return NodeInfo;
    }
}