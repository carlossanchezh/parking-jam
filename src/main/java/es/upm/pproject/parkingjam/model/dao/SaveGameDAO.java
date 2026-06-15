package es.upm.pproject.parkingjam.model.dao;

import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.upm.pproject.parkingjam.model.dto.*;
import es.upm.pproject.parkingjam.model.exceptions.SaveGameDAOException;
import es.upm.pproject.parkingjam.model.exceptions.BoardValidationException;

public class SaveGameDAO {
    private static final Logger logger = LoggerFactory.getLogger(SaveGameDAO.class);

    public void saveGame(GameState gameState, Path saveFile) throws SaveGameDAOException{
        if(gameState == null) throw new SaveGameDAOException("Game state cannot be null");
        if(saveFile == null) throw new SaveGameDAOException("Save file cannot be null");

        logger.info("Saving game in {}", saveFile);
        try {
            Path parent = saveFile.getParent();
            if(parent != null){
                Files.createDirectories(parent);
            }
            try(BufferedWriter bw = Files.newBufferedWriter(saveFile)){
                writeGameState(gameState, bw);
            }
            logger.info("Game saved successfully");
        } catch (IOException e) {
            logger.error("Error saving game", e);
            throw new SaveGameDAOException("Error saving game", e);
        }
    }
    private void writeGameState(GameState gameState, BufferedWriter bw) throws IOException{
        bw.write("CURRENT_LEVEL=" + gameState.getCurrentLevel());
        bw.newLine();

        bw.write("CURRENT_LEVEL_NAME=" + gameState.getCurrentLevelName());
        bw.newLine();

        bw.write("LEVEL_SCORE=" + gameState.getLevelScore());
        bw.newLine();

        bw.write("TOTAL_SCORE=" + gameState.getTotalScore());
        bw.newLine();

        bw.write("FINISHED=" + gameState.isFinished());
        bw.newLine();
        bw.newLine();

        bw.write("MOVES");
        bw.newLine();
        for(Move move : gameState.getMovementHistory()){
            bw.write(move.getVehicleId()+ " " +move.getDirection());
            bw.newLine();
        }
        bw.newLine();

        bw.write("BOARD");
        bw.newLine();
        char[][] boardMatrix = boardToChar(gameState.getBoard());
        bw.write(gameState.getBoard().getRows()+ " " +gameState.getBoard().getColumns());
        bw.newLine();
        for(int i = 0; i < boardMatrix.length; i++){
            bw.write(new String(boardMatrix[i]));
            bw.newLine();
        }
    }

    public GameState loadGame(Path saveFile) throws SaveGameDAOException{
        if(saveFile == null) throw new SaveGameDAOException("Save file cannot be null");
        logger.info("Loading game from {}", saveFile);
        if(!Files.exists(saveFile)) throw new SaveGameDAOException("Save file does not exist");

        try(BufferedReader br = Files.newBufferedReader(saveFile)){
            int currentLevel = Integer.parseInt(readValue(br, "CURRENT_LEVEL"));
            String currentLevelName = readValue(br, "CURRENT_LEVEL_NAME");
            int levelScore = Integer.parseInt(readValue(br, "LEVEL_SCORE"));
            int totalScore = Integer.parseInt(readValue(br, "TOTAL_SCORE"));
            boolean finished = Boolean.parseBoolean(readValue(br, "FINISHED"));
            String separator = br.readLine();
            if(separator == null || !separator.isBlank()){
                throw new SaveGameDAOException("Expected empty line after finished");
            }

            String movesHeader = br.readLine();
            if(!"MOVES".equals(movesHeader)) throw new SaveGameDAOException("Missing MOVES section");
            List<Move> moves = readMoves(br);

            String boardHeader = br.readLine();
            if(!"BOARD".equals(boardHeader)) throw new SaveGameDAOException("Missing BOARD section");
            Board board = readBoard(br);

            GameState gameState = new GameState(board, currentLevel, currentLevelName);
            gameState.setLevelScore(levelScore);
            gameState.setTotalScore(totalScore);
            gameState.setFinished(finished);
            for(Move move : moves){
                gameState.addMovement(move);
            }
            
            logger.info("Game loaded successfully");
            return gameState;
        } catch(IOException | NumberFormatException e){
            logger.error("Error loading game", e);
            throw new SaveGameDAOException("Error loading game", e);
        }        
    }

    private char[][] boardToChar(Board board){
        char[][] saveMatrix = new char[board.getRows()][board.getColumns()];

        for(int i = 0; i <board.getRows(); i++){
            for(int j = 0; j < board.getColumns(); j++){
                saveMatrix[i][j] = '.';
            }
        }
        for(Position wall : board.getWalls()){
            saveMatrix[wall.getX()][wall.getY()] = '+';
        }
        Position exit = board.getExit();
        saveMatrix[exit.getX()][exit.getY()] = '@';
        for(Vehicle vehicle : board.getVehicles().values()){
            char symbol;
            if(vehicle.isRedCar()) symbol = '*';
            else{
                symbol = vehicle.getId();
            }
            for(Position position : vehicle.getPositions()){
                saveMatrix[position.getX()][position.getY()] = symbol;
            }
        }
        return saveMatrix;
    }

    private String readValue(BufferedReader br, String expectedWord) throws IOException, SaveGameDAOException{
        String line = br.readLine();
        if(line == null) throw new SaveGameDAOException("Missing value for: " + expectedWord);
        String prefix = expectedWord + "=";
        if(!line.startsWith(prefix)) throw new SaveGameDAOException("Expected" + expectedWord);
        return line.substring(prefix.length());
    }

    private List<Move> readMoves(BufferedReader br) throws IOException, SaveGameDAOException{
        List<Move> moves = new ArrayList<>();
        String line;
        while((line = br.readLine()) != null){
            if(line.isBlank()){
                break;
            }
            String[] parts = line.split("\\s+");
            if(parts.length != 2) throw new SaveGameDAOException("Invalid move format: " +line);

            char vehicleId = parts[0].charAt(0);
            Direction direction;
            try{
                direction = Direction.valueOf(parts[1]);
            } catch(IllegalArgumentException e){
                throw new SaveGameDAOException("Invalid direction in move: " + line, e);
            }

            moves.add(new Move(vehicleId, direction));
        }
        return moves;
    }

    private Board readBoard(BufferedReader br) throws IOException, SaveGameDAOException{
        String dimensionLine = br.readLine();
        if(dimensionLine == null) throw new SaveGameDAOException("Missing board dimensions");

        String[] dims = dimensionLine.split("\\s+");
        if(dims.length != 2) throw new SaveGameDAOException("Invalid board dimensions");

        int nRows;
        int nCols;
        try{
            nRows = Integer.parseInt(dims[0]);
            nCols = Integer.parseInt(dims[1]);
        } catch(NumberFormatException e){
            throw new SaveGameDAOException("Invalid board dimensions", e);
        }

        char[][] rawBoard = new char[nRows][nCols];
        for(int i = 0; i < nRows; i++){
            String row = br.readLine();
            if(row == null) throw new SaveGameDAOException("Missing board row");
            if(row.length() != nCols) throw new SaveGameDAOException("Invalid board row length");
            rawBoard[i] = row.toCharArray();
        }
        try {
            BoardValidation boardValidation = new BoardValidation();
            Board board = boardValidation.buildBoard(rawBoard, nRows, nCols);
            boardValidation.validateBoard(board);
            return board;
        } catch (BoardValidationException e) {
            throw new SaveGameDAOException(e.getMessage(), e);
        }
    }
}
