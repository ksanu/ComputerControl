package MyComputerControl;

public interface MessageContent {
    public interface AUTHORIZATION_RESULT{
        String SUCCESS = "SUCCESS";
        String FAILURE = "FAILURE";
    }

    public interface STATE{
        String READY_FOR_REMOTE_CONTROL = "READY_FOR_REMOTE_CONTROL";
        String CLOSING = "CLOSING";
    }
}