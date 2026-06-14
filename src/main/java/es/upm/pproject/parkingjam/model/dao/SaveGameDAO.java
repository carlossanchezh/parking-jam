package es.upm.pproject.parkingjam.model.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.upm.pproject.parkingjam.model.dto.*;
import es.upm.pproject.parkingjam.model.exceptions.SaveGameDAOException;

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
        Board board = buildBoard(rawBoard, nRows, nCols);
        validateBoard(board);
        return board;
    }



    private Board buildBoard(char[][] rawBoard, int nRows, int nCols) throws SaveGameDAOException{
        Set<Position> walls = new HashSet<>();
        Position exit = null;
        Map<Character, List<Position>> vehiclesPositions = new HashMap<>();
        List<Position> redCarPositions = new ArrayList<>();

        // Traverse the whole grid
        for(int i = 0; i < nRows; i++){
            for(int j = 0; j < nCols; j++){
                char c = rawBoard[i][j];
                Position pos = new Position(i, j);
                exit = processCell(c, pos, walls, exit, redCarPositions, vehiclesPositions); // Call aux function to process the cell
            }
        }
        Map<Character, Vehicle> vehicles = buildVehicles(vehiclesPositions, redCarPositions);
        return new Board(nRows, nCols, walls, exit, vehicles);
    }

    // Process a single cell in the grid and update the Board state and elements
    private Position processCell(char c, Position pos,
                                 Set<Position> walls,
                                 Position exit,
                                 List<Position> redCarPositions,
                                 Map<Character, List<Position>> vehiclesPositions) throws SaveGameDAOException {

        if(!isValidBoardCharacter(c)) throw new SaveGameDAOException("Invalid character "+c);

        if(c == '+') {
            walls.add(pos);
        } else if(c == '@') {
            if(exit != null) throw new SaveGameDAOException("There must be one exit");
            exit = pos;
        } else if(c == '*') {
            redCarPositions.add(pos);
        } else if(c >= 'a' && c <= 'z') {
            vehiclesPositions.computeIfAbsent(c, k -> new ArrayList<>()).add(pos);
        }
        return exit;
    }


    // Maps the positions from each vehicleId to a Vehicle
    private Map<Character, Vehicle> buildVehicles(Map<Character, List<Position>> vehiclesPositions, List<Position> redCarPositions) throws SaveGameDAOException {
        Map<Character, Vehicle> vehicles = new HashMap<>();

        //RED CAR
        if(!redCarPositions.isEmpty()){
            vehicles.put('*', new Vehicle('*', redCarPositions, true, determineOrientation(redCarPositions)));
        }

        //Normal cars
        for(Map.Entry<Character, List<Position>> entry : vehiclesPositions.entrySet()){
            char id = entry.getKey();
            List<Position> positions = entry.getValue();
            vehicles.put(id, new Vehicle(id, positions, false, determineOrientation(positions)));
        }
        return vehicles;
    }


    // Helper that checks if a character in a .txt file is a valid character
    private boolean isValidBoardCharacter(char c){
        return c == '+' || c == '.' || c == '@' || c == '*' || (c >= 'a' && c <= 'z');
    }


    // Helper that returns the orientation os a vehicle in a board
    private Orientation determineOrientation(List<Position> positions) throws SaveGameDAOException{
        if(positions == null || positions.size() < 2){
            throw new SaveGameDAOException("Vehicle must have at least 2 positions");
        }
        Position first = positions.get(0);
        Position second = positions.get(1);
        if(first.getX() == second.getX()) return Orientation.HORIZONTAL;
        else if(first.getY() == second.getY()) return Orientation.VERTICAL;
        else throw new SaveGameDAOException("Orientation invalid");
    }


    // Ensures the board contains the mandatory pieces
    private void validateBoard(Board board) throws SaveGameDAOException{
        if(board.getExit() == null){
            throw new SaveGameDAOException("There must be exactly one exit");
        }
        Map<Character, Vehicle> vehicles = board.getVehicles();
        Vehicle redCar = vehicles.get('*');
        if(redCar == null){
            throw new SaveGameDAOException("There must be one red car");
        }
        validateRedCar(redCar);
        for(Vehicle vehicle : vehicles.values()){
            validateVehicle(vehicle);
        }
    }


    // Applies the extra rules for the red car (exact size, id, and continuity)
    private void validateRedCar(Vehicle redCar) throws SaveGameDAOException{
        if(!redCar.isRedCar()) throw new SaveGameDAOException("Red car vehicle is not marked as red car");
        if(redCar.getId() != '*') throw new SaveGameDAOException("Red car must have '*' as id");

        List<Position> positions = redCar.getPositions();
        if(positions.size() != 2) throw new SaveGameDAOException("Red car must occupy 2 cells");
        Orientation orientation = redCar.getOrientation();
        if(orientation == null) throw new SaveGameDAOException("Red car must have orientation and be 1x2 or 2x1");
        validateContinuity(positions, redCar.getId(), orientation);
    }


    // Helper that checks all the vehicles (except the red car) are created according the norms
    private void validateVehicle(Vehicle vehicle) throws SaveGameDAOException{
        if(vehicle == null) throw new SaveGameDAOException("Vehicle cannot be null");
        if(vehicle.isRedCar()) return;
        if(vehicle.getId() < 'a' || vehicle.getId() > 'z') throw new SaveGameDAOException("Vehicle "+ vehicle.getId() +" wrong identifier");
        
        List<Position> positions = vehicle.getPositions();
        if(positions == null || positions.size() < 2) throw new SaveGameDAOException("Vehicle "+ vehicle.getId() +" must occupy 2 cells");
        if(vehicle.getOrientation() == null) throw new SaveGameDAOException("Vehicle "+ vehicle.getId() +" must have orientation");

        validateContinuity(positions, vehicle.getId(), vehicle.getOrientation());
    }


    // Helper that checks positions assigned to the same car are adjacent (no holes in between)
    private void validateContinuity(List<Position> positions, char id, Orientation orientation) throws SaveGameDAOException{
        List<Integer> values = new ArrayList<>();
        for(Position position : positions){
            if(orientation == Orientation.HORIZONTAL) values.add(position.getY());
            else if(orientation == Orientation.VERTICAL) values.add(position.getX());
            else throw new SaveGameDAOException("Vehicle "+ id +" invalid orientation");
        }
        values.sort(Integer::compareTo);
        for(int i = 1; i < values.size(); i++){
            if(values.get(i) != values.get(i-1)+1) throw new SaveGameDAOException("Vehicle "+ id +" has holes");
        }
    }
}
