package es.upm.pproject.parkingjam.model.exceptions;

public class LevelFormatException extends Exception{
    public LevelFormatException(String message){
        super(message);
    }

    // Constructor that allows propagating the cause of an exception
    public LevelFormatException(String message, Throwable cause){
        super(message, cause);
    }
}
