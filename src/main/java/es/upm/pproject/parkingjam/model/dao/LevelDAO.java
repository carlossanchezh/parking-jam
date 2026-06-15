package es.upm.pproject.parkingjam.model.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.upm.pproject.parkingjam.model.dto.*;
import es.upm.pproject.parkingjam.model.exceptions.LevelDAOException;
import es.upm.pproject.parkingjam.model.exceptions.LevelFormatException;
import es.upm.pproject.parkingjam.model.exceptions.LevelNotFoundException;
import es.upm.pproject.parkingjam.model.exceptions.BoardValidationException;

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
        throw new LevelNotFoundException("Level file not found: " + fileName);
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
        try {
            BoardValidation boardValidation = new BoardValidation();
            Board board = boardValidation.buildBoard(rawBoard, nRows, nCols);
            boardValidation.validateBoard(board);
            return new Level(nameLine, nRows, nCols, board);
        } catch (BoardValidationException e) {
            throw new LevelFormatException(e.getMessage(), e);
        }
    }
}
