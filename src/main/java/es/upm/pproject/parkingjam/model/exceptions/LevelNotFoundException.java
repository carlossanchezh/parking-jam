package es.upm.pproject.parkingjam.model.exceptions;

public class LevelNotFoundException extends LevelDAOException{
    public LevelNotFoundException(String message){
        super(message);
    }
}
