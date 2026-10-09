package chain;

import model.ApprovalRequest;

public abstract class ApprovalHandler {

    protected ApprovalHandler nextHandler;

    public void setNextHandler(ApprovalHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public abstract void processRequest(
            ApprovalRequest request,
            String loggedInUsername
    );
}