package es.upm.pproject.parkingjam.model.dto;

public class Level {
    private final String name;
    private final int nRows;
    private final int nCols;
    private Board board;

    public Level(String name, int nRows, int nCols, Board board){
        if(name == null || name.isEmpty()){
            throw new IllegalArgumentException("Level name cannot be null or empty");
        }
        if(nRows <= 0 || nCols <= 0){
            throw new IllegalArgumentException("Invalid level dimensions");
        }
        if(board == null){
            throw new IllegalArgumentException("Board cannot be null");
        }
        this.name = name;
        this.nRows = nRows;
        this.nCols = nCols;
        this.board = board;
    }
    
    public String getName(){
        return name;
    }
    public int getnRows(){
        return nRows;
    }
    public int getnCols(){
        return nCols;
    }
    public Board getBoard(){
        return board;
    }
}
