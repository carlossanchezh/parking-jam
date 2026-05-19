package es.upm.pproject.parkingjam.model.dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import es.upm.pproject.parkingjam.model.dto.Level;
import es.upm.pproject.parkingjam.model.exceptions.LevelDAOException;
import es.upm.pproject.parkingjam.model.exceptions.LevelFormatException;

public class LevelDAO {         //Falta logger y revisar skiplevl

    public List<Level> loadLevels(String filePath) throws LevelDAOException{
        List<Level> levels = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(filePath))){
            String line;

            while((line = br.readLine()) != null){
                if(line.trim().isEmpty()) continue;
                try{
                    Level level = readLevel(br, line);
                    levels.add(level);
                } catch(LevelFormatException e){
                    System.out.println("Error in level: " + e.getMessage());
                }
            }
        } catch(IOException e){
            throw new LevelDAOException();
        }
        return levels;
    }

    private Level readLevel(BufferedReader br, String nameLine) throws IOException, LevelFormatException{
        String name = nameLine;
        if(name.trim().isEmpty()){
            throw new LevelFormatException("Level name cannot be empty");
        }
        String dimensionLine = br.readLine();
        if(dimensionLine == null){
            throw new LevelFormatException("Missing dimensions line");
        }
        String[] dims = dimensionLine.split("\\s+");
        if(dims.length != 2){
            throw new LevelFormatException("Invalid dimensions format");
        }
        int nRows;
        int nCols;
        try{
            nRows = Integer.parseInt(dims[0]);
            nCols = Integer.parseInt(dims[1]);
        } catch(NumberFormatException e){
            throw new LevelFormatException("Dimensions must be numbers");
        }
        if(nRows <= 0 || nCols <= 0){
            throw new LevelFormatException("Invalid dimensions");
        }

        char[][] board = new char[nRows][nCols];
        for(int i = 0; i < nRows; i++){
            String row = br.readLine();
            if(row == null){
                throw new LevelFormatException("Missing board rows");
            }
            if (row.length() != nCols) {
                throw new LevelFormatException("Row with incorrect size");
            }
            board[i] = row.toCharArray();
        }

        Level level = new Level(name, nRows, nCols, board);
        validateLevel(level);
        return level;
    }
    
    //posible separacion de las validaciones y convertir int[] en clase Position
    private void validateLevel(Level level) throws LevelFormatException{
        char[][] board = level.getBoard();
        int nRows = level.getnRows();
        int nCols = level.getnCols();

        int exitCount = 0;
        List<int[]> redPositions = new ArrayList<>();
        Map<Character, List<int[]>> vehicles = new HashMap<>();
        for(int i = 0; i < nRows; i++){
            for(int j = 0; j < nCols; j++){
                char c = board[i][j];
                if(!(c == '+' || c == '.' || c == '@' || c == '*' || (c >= 'a' && c <= 'z'))){
                    throw new LevelFormatException("Invalid character:" + c);
                }
                if(c == '@') exitCount++;
                if(c == '*'){
                    redPositions.add(new int[]{i,j});
                }
                if(c >= 'a' && c <= 'z'){
                    vehicles.computeIfAbsent(c, k-> new ArrayList<>()).add(new int[]{i,j});
                }
            }
        }
        if(exitCount != 1){
            throw new LevelFormatException("There must be exactly one exit");
        }
        if(redPositions.size() != 2){
            throw new LevelFormatException("Red car must occupy exactly two cells");
        }
        int r1 = redPositions.get(0)[0];
        int c1 = redPositions.get(0)[1];
        int r2 = redPositions.get(1)[0];
        int c2 = redPositions.get(1)[1];

        boolean horizontal = (r1 == r2 && Math.abs(c1-c2) == 1);
        boolean vertical = (c1 == c2 && Math.abs(r1-r2) == 1);
        if(!horizontal && !vertical){
            throw new LevelFormatException("Red car must be 1x2 or 2x1");
        }
        for(Map.Entry<Character, List<int[]>> entry : vehicles.entrySet()){
            char vehicle = entry.getKey();
            List<int[]> positions = entry.getValue();
            if(positions.size() < 2){
                throw new LevelFormatException("Vehicle " + vehicle + " must occupy at least 2 cells");
            }

            boolean sameRow = true;
            boolean sameCol = true;
            int row0 = positions.get(0)[0];
            int col0 = positions.get(0)[1];
            for(int i = 1; i < positions.size(); i++){
                int r = positions.get(i)[0];
                int c = positions.get(i)[1];
                if(r != row0){
                    sameRow = false;
                }
                if(c != col0){
                    sameCol = false;
                }
            }
            if(!sameRow && !sameCol){
                throw new LevelFormatException("Vehicle " + vehicle +" is not linear");
            }
            validateContinuity(positions, vehicle, sameRow);
        }
        validateVehiclesConnectivity(board, nRows, nCols);
    }

    private void validateContinuity(List<int[]> positions, char vehicle, boolean horizontal) throws LevelFormatException{
        List<Integer> values = new ArrayList<>();
        for(int[] pos : positions){
            if(horizontal){
                values.add(pos[1]);
            }
            else{
                values.add(pos[0]);
            }
        }
        values.sort(Integer::compareTo);
        for(int i = 1; i < values.size(); i++){
            if(values.get(i) != values.get(i -1) + 1){
                throw new LevelFormatException("Vehicle "+ vehicle + " has holes");
            }
        }
    }

    private void validateVehiclesConnectivity(char[][] board, int nRows, int nCols) throws LevelFormatException{
        boolean[][] visited = new boolean[nRows][nCols];

        for(int i = 0; i < nRows; i++){
            for(int j = 0; j < nCols; j++){
                char c = board[i][j];
                if(c == '+' || c == '.' || c == '@' || c == '*'){
                    continue;
                }
                if(!visited[i][j]){
                    int size = dfs(board, visited, i, j, c);
                    if(size < 2){
                        throw new LevelFormatException("Vehicle " + c + " is disconnected");
                    }
                }
            }
        }
    }

    private int dfs(char[][] board, boolean[][] visited, int i, int j, char target){
        if(i < 0 || i >= board.length || j < 0 || j >= board[0].length ||
            visited[i][j] ||
            board[i][j] != target){
            return 0;
        }
        visited[i][j] = true;
        return 1 + dfs(board, visited, i + 1, j, target) + dfs(board, visited, i - 1, j, target)
                 + dfs(board, visited, i, j + 1, target) + dfs(board, visited, i, j - 1, target);
    }

}
