package es.upm.pproject.parkingjam.unit.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import es.upm.pproject.parkingjam.model.dto.Direction;

@Nested
@DisplayName("Direction tests")
class DirectionTest {

    @Test
    @DisplayName("DirectionTest_01: should return opposite direction correctly for NORTH")
    void testGetOppositeDirectionNorth() {
        assertEquals(Direction.SOUTH, Direction.NORTH.getOppositeDirection());
    }

    @Test
    @DisplayName("DirectionTest_02: should return opposite direction correctly for SOUTH")
    void testGetOppositeDirectionSouth() {
        assertEquals(Direction.NORTH, Direction.SOUTH.getOppositeDirection());
    }

    @Test
    @DisplayName("DirectionTest_03: should return opposite direction correctly for EAST")
    void testGetOppositeDirectionEast() {
        assertEquals(Direction.WEST, Direction.EAST.getOppositeDirection());
    }

    @Test
    @DisplayName("DirectionTest_04: should return opposite direction correctly for WEST")
    void testGetOppositeDirectionWest() {
        assertEquals(Direction.EAST, Direction.WEST.getOppositeDirection());
    }
}
