package es.upm.pproject.parkingjam.model.dto;

public class Level {
    private String name;
    private int nRows;
    private int nCols;
    private char[][] board;

    public Level(String name, int nRows, int nCols, char[][] board){
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
    public char[][] getBoard(){
        return board;
    }
}
