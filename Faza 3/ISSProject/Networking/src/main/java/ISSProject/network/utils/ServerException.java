package ISSProject.network.utils;


public class ServerException extends Exception{
    /**
     * Constructor with message
     * @param message - the message of the exception
     * @param cause - the cause of the exception
     */
    public ServerException(String message, Throwable cause) {
        super(message, cause);    
    }
}
