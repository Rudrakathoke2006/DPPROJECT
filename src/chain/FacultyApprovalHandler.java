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

            System.out.println(
                    "This approval is not assigned to you."
            );

            return;
        }

        System.out.println(
                "\nFaculty approval required."
        );

        System.out.println(
                "Event ID   : "
                        + request.getEventId()
        );

        System.out.println(
                "Event Name : "
                        + request.getEventName()
        );

        System.out.println(
                "Event Type : "
                        + request.getEventType()
        );

        System.out.println(
                "Event Level: "
                        + request.getEventLevel()
        );
    }
}