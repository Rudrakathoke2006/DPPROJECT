package chain;

import model.ApprovalRequest;

public class DeanApprovalHandler
        extends ApprovalHandler {

    @Override
    public void processRequest(
            ApprovalRequest request,
            String loggedInUsername) {
        if (!request.getApproverRole().equals("DEAN")) {

            if (nextHandler != null) {
                nextHandler.processRequest(
                        request,
                        loggedInUsername
                );
            }

            return;
        }

        if (!request.getApproverUsername()
                .equals(loggedInUsername)) {

            view.ConsoleView.println(
                    "This approval is not assigned to you."
            );

            return;
        }

        view.ConsoleView.println(
                "\nDean approval required."
        );

        view.ConsoleView.println(
                "Event ID   : "
                        + request.getEventId()
        );

        view.ConsoleView.println(
                "Event Name : "
                        + request.getEventName()
        );

        view.ConsoleView.println(
                "Event Type : "
                        + request.getEventType()
        );

        view.ConsoleView.println(
                "Event Level: "
                        + request.getEventLevel()
        );
    }
}