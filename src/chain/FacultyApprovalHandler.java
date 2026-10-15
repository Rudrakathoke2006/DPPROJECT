package chain;

import model.ApprovalRequest;

public class FacultyApprovalHandler
        extends ApprovalHandler {

    @Override
    public void processRequest(
            ApprovalRequest request,
            String loggedInUsername) {

        if (!request.getApproverRole().equals("FACULTY")) {

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
                "\nFaculty approval required."
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