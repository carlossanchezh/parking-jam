package es.upm.pproject.parkingjam.unit.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import es.upm.pproject.parkingjam.model.dto.Board;
import es.upm.pproject.parkingjam.model.dto.Orientation;
import es.upm.pproject.parkingjam.model.dto.Position;
import es.upm.pproject.parkingjam.model.dto.Vehicle;

import es.upm.pproject.parkingjam.model.services.CollisionService;
import es.upm.pproject.parkingjam.model.services.CollisionServiceImpl;

@Nested
@DisplayName("CollisionService tests")
class CollisionServiceImplTest {

    private CollisionService collisionService;

    @BeforeEach
    void setUp() {
        collisionService = new CollisionServiceImpl();
    }

    @Test
    @DisplayName("CollisionTest_01: should create a non-null object when constructor is called")
    void testConstructorCreatesObject() {
        assertNotNull(collisionService);
    }

    @Test
    @DisplayName("CollisionTest_02: should throw exception when board is null")
    void testNullBoard() {
        Position position = new Position(0, 0);
        assertThrows(IllegalArgumentException.class, () -> collisionService.collision(null, position, 'R'));
    }

    @Test
    @DisplayName("CollisionTest_03: should throw exception when position is null")
    void testNullPosition() {
        Set<Position> walls = new HashSet<>();
        Position exit = new Position(6, 3);

        List<Position> redCarPositions = new ArrayList<>();
        redCarPositions.add(new Position(3, 2));
        redCarPositions.add(new Position(3, 3));

        Vehicle redCar = new Vehicle('R', redCarPositions, true, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('R', redCar);

        Board board = new Board(7, 7, walls, exit, vehicles);

        assertThrows(IllegalArgumentException.class, () -> collisionService.collision(board, null, 'R'));
    }

    @Test
    @DisplayName("CollisionTest_04: should return true when position is a wall")
    void testCollisionWithWall() {

        Set<Position> walls = new HashSet<>();
        walls.add(new Position(0, 0));
        walls.add(new Position(0, 1));

        Position exit = new Position(6, 3);

        List<Position> redCarPositions = new ArrayList<>();
        redCarPositions.add(new Position(3, 2));
        redCarPositions.add(new Position(3, 3));

        Vehicle redCar = new Vehicle('R', redCarPositions, true, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('R', redCar);

        Board board = new Board(7, 7, walls, exit, vehicles);

        assertTrue(collisionService.collision(board, new Position(0, 0), 'R'));
        assertTrue(collisionService.collision(board, new Position(0, 1), 'R'));
    }

    @Test
    @DisplayName("CollisionTest_05: should return false when position is not a wall and not occupied")
    void testNoCollisionWithEmptyPosition() {

        Set<Position> walls = new HashSet<>();
        walls.add(new Position(0, 0));

        Position exit = new Position(6, 3);

        List<Position> redCarPositions = new ArrayList<>();
        redCarPositions.add(new Position(3, 2));
        redCarPositions.add(new Position(3, 3));

        Vehicle redCar = new Vehicle('R', redCarPositions, true, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('R', redCar);

        Board board = new Board(7, 7, walls, exit, vehicles);

        assertFalse(collisionService.collision(board, new Position(1, 1), 'R'));
        assertFalse(collisionService.collision(board, new Position(5, 5), 'R'));
    }

    @Test
    @DisplayName("CollisionTest_06: should return true when position is occupied by another vehicle")
    void testCollisionWithOtherVehicle() {
        // Given
        Set<Position> walls = new HashSet<>();

        Position exit = new Position(6, 3);

        List<Position> redCarPositions = new ArrayList<>();
        redCarPositions.add(new Position(3, 2));
        redCarPositions.add(new Position(3, 3));

        Vehicle redCar = new Vehicle('R', redCarPositions, true, Orientation.HORIZONTAL);

        List<Position> vehicleAPositions = new ArrayList<>();
        vehicleAPositions.add(new Position(1, 1));
        vehicleAPositions.add(new Position(1, 2));
        vehicleAPositions.add(new Position(1, 3));

        Vehicle vehicleA = new Vehicle('A', vehicleAPositions, false, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('R', redCar);
        vehicles.put('A', vehicleA);

        Board board = new Board(7, 7, walls, exit, vehicles);

        // When & Then - position occupied by vehicle A
        assertTrue(collisionService.collision(board, new Position(1, 1), 'R'));
        assertTrue(collisionService.collision(board, new Position(1, 2), 'R'));
        assertTrue(collisionService.collision(board, new Position(1, 3), 'R'));
    }

    @Test
    @DisplayName("CollisionTest_07: should return false when position is occupied by the same vehicle")
    void testNoCollisionWithSameVehicle() {
        // Given
        Set<Position> walls = new HashSet<>();

        Position exit = new Position(6, 3);

        List<Position> redCarPositions = new ArrayList<>();
        redCarPositions.add(new Position(3, 2));
        redCarPositions.add(new Position(3, 3));

        Vehicle redCar = new Vehicle('R', redCarPositions, true, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('R', redCar);

        Board board = new Board(7, 7, walls, exit, vehicles);


        assertFalse(collisionService.collision(board, new Position(3, 2), 'R'));
        assertFalse(collisionService.collision(board, new Position(3, 3), 'R'));
    }

    @Test
    @DisplayName("CollisionTest_08: should return false when red car leaves through exit")
    void testRedCarLeavingThroughExit() {

        Set<Position> walls = new HashSet<>();

        Position exit = new Position(3, 0);

        List<Position> redCarPositions = new ArrayList<>();
        redCarPositions.add(new Position(3, -2));
        redCarPositions.add(new Position(3, -1));

        Vehicle redCar = new Vehicle('R', redCarPositions, true, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('R', redCar);

        Board board = new Board(7, 7, walls, exit, vehicles);

        assertFalse(collisionService.collision(board, new Position(3, -1), 'R'));
    }

    @Test
    @DisplayName("CollisionTest_09: should return true when non-red car tries to leave board")
    void testNonRedCarLeavingBoard() {

        Set<Position> walls = new HashSet<>();

        Position exit = new Position(6, 3);

        List<Position> vehicleAPositions = new ArrayList<>();
        vehicleAPositions.add(new Position(1, 1));
        vehicleAPositions.add(new Position(1, 2));

        Vehicle vehicleA = new Vehicle('A', vehicleAPositions, false, Orientation.HORIZONTAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('A', vehicleA);

        Board board = new Board(7, 7, walls, exit, vehicles);

        assertTrue(collisionService.collision(board, new Position(1, -1), 'A'));
    }

    @Test
    @DisplayName("CollisionTest_10: should return false for position outside board when red car leaves aligned with exit")
    void testRedCarOutsideBoardAlignedWithExit() {

        Set<Position> walls = new HashSet<>();

        Position exit = new Position(0, 3);

        List<Position> redCarPositions = new ArrayList<>();
        redCarPositions.add(new Position(-2, 3));
        redCarPositions.add(new Position(-1, 3));

        Vehicle redCar = new Vehicle('R', redCarPositions, true, Orientation.VERTICAL);

        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put('R', redCar);

        Board board = new Board(7, 7, walls, exit, vehicles);

        assertFalse(collisionService.collision(board, new Position(-1, 3), 'R'));
    }

}
