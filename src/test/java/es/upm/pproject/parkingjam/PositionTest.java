package es.upm.pproject.parkingjam;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import es.upm.pproject.parkingjam.model.dto.Position;

@Nested
@DisplayName("Position tests")
class PositionTest {

    private Position position;
    private Position samePosition;
    private Position differentPosition;

    @BeforeEach
    void setUp() {
        position = new Position(3, 5);
        samePosition = new Position(3, 5);
        differentPosition = new Position(5, 3);
    }

    @Test
    @DisplayName("PositionTest_01: should create a non-null object when constructor is called")
    void testConstructorCreatesObject() {
        assertNotNull(position);
    }

    @Test
    @DisplayName("PositionTest_02: should return the correct x and y values when getters are called")
    void testConstructorAndGetters() {
        assertEquals(3, position.getX());
        assertEquals(5, position.getY());
    }

    // necessary (?)
    @Test
    @DisplayName("PositionTest_03: should create a new position with offset without modifying the original when addCoordinates is called")
    void testAddCoordinates() {
        Position newPosition = position.addCoordinates(1, -2);

        assertEquals(4, newPosition.getX());
        assertEquals(3, newPosition.getY());

        assertEquals(3, position.getX());
        assertEquals(5, position.getY());
    }

    @Test
    @DisplayName("PositionTest_04: should return true when comparing the same position")
    void testEqualsSamePosition() {
        assertEquals(position, position);
    }

    @Test
    @DisplayName("PositionTest_05: should return true when comparing two positions with same coordinates")
    void testEqualsSameCoordinates() {
        assertEquals(position, samePosition);
        assertEquals(samePosition, position);

    }

    @Test
    @DisplayName("PositionTest_06: should return false when comparing two positions with different coordinates")
    void testEqualsDifferentCoordinates() {
        assertNotEquals(position, differentPosition);
        assertNotEquals(differentPosition, position);
    }

    @Test
    @DisplayName("PositionTest_07: should return false when comparing with null or a different type")
    void testEqualsEdgeCases() {
        assertNotEquals(null, position);
        assertNotEquals("string", position);
    }

    @Test
    @DisplayName("PositionTest_08: should return the same hash code for positions with equal coordinates")
    void testHashCodeConsistency() {
        assertEquals(position.hashCode(), samePosition.hashCode());
    }

    @Test
    @DisplayName("PositionTest_09: should return different hash code for positions with different coordinates")
    void testHashCodeDifferent() {
        assertNotEquals(position.hashCode(), differentPosition.hashCode());
    }

}
