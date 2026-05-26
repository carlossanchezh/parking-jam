package es.upm.pproject.parkingjam.model.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.upm.pproject.parkingjam.model.dto.*;
import es.upm.pproject.parkingjam.model.exceptions.LevelDAOException;
import es.upm.pproject.parkingjam.model.exceptions.LevelFormatException;

public class LevelDAO {         

    private static final Logger logger = LoggerFactory.getLogger(LevelDAO.class);

    // Loads, parses, and validates a level file
    public Level loadLevel(String fileName) throws LevelDAOException{
        Path path = resolveLevelPath(fileName);
        logger.info("Loading level from file: {}", path);

        try(BufferedReader br = Files.newBufferedReader(path)){
            String nameLine = br.readLine();
            if(nameLine == null || nameLine.trim().isEmpty()){
                throw new LevelFormatException("Missing level name");
            }
            Level level = readLevel(br, nameLine);
            logger.info("Level '{}' loaded succesfully", level.getName());
            return level;

        } catch(LevelFormatException e){
            logger.warn("Level '{}': {}", fileName, e.getMessage());
            throw new LevelDAOException("Invalid level: " + fileName, e);
        } catch(IOException e){
            logger.error("Error in reading level'{}'", fileName, e);
            throw new LevelDAOException("Error in reading level:" + fileName, e);
        }
    }


    // Helper to get the level files path
    private Path resolveLevelPath(String fileName) throws LevelDAOException {
        Path resourcesPath = Paths.get("resources", "levels", fileName);
        if (Files.exists(resourcesPath)) {
            return resourcesPath;
        }
        throw new LevelDAOException("Level file not found: " + fileName);
    }

    // Helper that parses a level from a .txt file
    private Level readLevel(BufferedReader br, String nameLine) throws IOException, LevelFormatException{
        if(nameLine.trim().isEmpty()){
            throw new LevelFormatException("Level name cannot be empty");
        }
        String dimensionLine = br.readLine();
        if(dimensionLine == null){
            throw new LevelFormatException("Missing dimensions line");
        }
        String[] dims = dimensionLine.trim().split("\\s+");
        if(dims.length != 2){
            throw new LevelFormatException("Invalid dimensions format");
        }
        int nRows;
        int nCols;
        try{
            nRows = Integer.parseInt(dims[0]);
            nCols = Integer.parseInt(dims[1]);
        } catch(NumberFormatException e){
            throw new LevelFormatException("Dimensions must be numbers", e);
        }
        if(nRows <= 0 || nCols <= 0){
            throw new LevelFormatException("Invalid dimensions");
        }

        char[][] rawBoard = new char[nRows][nCols];
        for(int i = 0; i < nRows; i++){
            String row = br.readLine();
            if(row == null){
                throw new LevelFormatException("Missing board row "+ i);
            }
            if (row.length() != nCols) {
                throw new LevelFormatException("Row with incorrect size");
            }
            rawBoard[i] = row.toCharArray();
        }
        Board board = buildBoard(rawBoard, nRows, nCols);
        validateBoard(board);
        return new Level(nameLine, nRows, nCols, board);
    }


    // Converts the raw character grid into walls, exit, and vehicle objects.
    private Board buildBoard(char[][] rawBoard, int nRows, int nCols) throws LevelFormatException{
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
                                 Map<Character, List<Position>> vehiclesPositions) throws LevelFormatException {

        if(!isValidBoardCharacter(c)) throw new LevelFormatException("Invalid character "+c);

        if(c == '+') {
            walls.add(pos);
        } else if(c == '@') {
            if(exit != null) throw new LevelFormatException("There must be one exit");
            exit = pos;
        } else if(c == '*') {
            redCarPositions.add(pos);
        } else if(c >= 'a' && c <= 'z') {
            vehiclesPositions.computeIfAbsent(c, k -> new ArrayList<>()).add(pos);
        }
        return exit;
    }


    // Maps the positions from each vehicleId to a Vehicle
    private Map<Character, Vehicle> buildVehicles(Map<Character, List<Position>> vehiclesPositions, List<Position> redCarPositions) throws LevelFormatException {
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
    private Orientation determineOrientation(List<Position> positions) throws LevelFormatException{
        if(positions == null || positions.size() < 2){
            throw new LevelFormatException("Vehicle must have at least 2 positions");
        }
        Position first = positions.get(0);
        Position second = positions.get(1);
        if(first.getX() == second.getX()) return Orientation.HORIZONTAL;
        else if(first.getY() == second.getY()) return Orientation.VERTICAL;
        else throw new LevelFormatException("Orientation invalid");
    }


    // Ensures the board contains the mandatory pieces
    private void validateBoard(Board board) throws LevelFormatException{
        if(board.getExit() == null){
            throw new LevelFormatException("There must be exactly one exit");
        }
        Map<Character, Vehicle> vehicles = board.getVehicles();
        Vehicle redCar = vehicles.get('*');
        if(redCar == null){
            throw new LevelFormatException("There must be one red car");
        }
        validateRedCar(redCar);
        for(Vehicle vehicle : vehicles.values()){
            validateVehicle(vehicle);
        }
    }


    // Applies the extra rules for the red car (exact size, id, and continuity)
    private void validateRedCar(Vehicle redCar) throws LevelFormatException{
        if(!redCar.isRedCar()) throw new LevelFormatException("Red car vehicle is not marked as red car");
        if(redCar.getId() != '*') throw new LevelFormatException("Red car must have '*' as id");

        List<Position> positions = redCar.getPositions();
        if(positions.size() != 2) throw new LevelFormatException("Red car must occupy 2 cells");
        Orientation orientation = redCar.getOrientation();
        if(orientation == null) throw new LevelFormatException("Red car must have orientation and be 1x2 or 2x1");
        validateContinuity(positions, redCar.getId(), orientation);
    }


    // Helper that checks all the vehicles (except the red car) are created according the norms
    private void validateVehicle(Vehicle vehicle) throws LevelFormatException{
        if(vehicle == null) throw new LevelFormatException("Vehicle cannot be null");
        if(vehicle.isRedCar()) return;
        if(vehicle.getId() < 'a' || vehicle.getId() > 'z') throw new LevelFormatException("Vehicle "+ vehicle.getId() +" wrong identifier");
        
        List<Position> positions = vehicle.getPositions();
        if(positions == null || positions.size() < 2) throw new LevelFormatException("Vehicle "+ vehicle.getId() +" must occupy 2 cells");
        if(vehicle.getOrientation() == null) throw new LevelFormatException("Vehicle "+ vehicle.getId() +" must have orientation");

        validateContinuity(positions, vehicle.getId(), vehicle.getOrientation());
    }


    // Helper that checks positions assigned to the same car are adjacent (no holes in between)
    private void validateContinuity(List<Position> positions, char id, Orientation orientation) throws LevelFormatException{
        List<Integer> values = new ArrayList<>();
        for(Position position : positions){
            if(orientation == Orientation.HORIZONTAL) values.add(position.getY());
            else if(orientation == Orientation.VERTICAL) values.add(position.getX());
            else throw new LevelFormatException("Vehicle "+ id +" invalid orientation");
        }
        values.sort(Integer::compareTo);
        for(int i = 1; i < values.size(); i++){
            if(values.get(i) != values.get(i-1)+1) throw new LevelFormatException("Vehicle "+ id +" has holes");
        }
    }

}
