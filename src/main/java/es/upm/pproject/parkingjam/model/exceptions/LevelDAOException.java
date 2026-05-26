package es.upm.pproject.parkingjam.model.exceptions;

public class LevelDAOException extends Exception{
    public LevelDAOException(String message){
        super(message);
    }

    // Constructor that allows propagating the cause of an exception
    public LevelDAOException(String message, Throwable cause){
        super(message, cause);
    }
}
