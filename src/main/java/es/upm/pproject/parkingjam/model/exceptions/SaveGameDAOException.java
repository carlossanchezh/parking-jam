package es.upm.pproject.parkingjam.model.exceptions;

public class SaveGameDAOException extends Exception{
    public SaveGameDAOException(String message){
        super(message);
    }
    public SaveGameDAOException(String message, Throwable cause){
        super(message, cause);
    }
}
