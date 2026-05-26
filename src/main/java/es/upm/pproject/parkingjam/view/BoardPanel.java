package es.upm.pproject.parkingjam.view;

import es.upm.pproject.parkingjam.controller.GameController;
import es.upm.pproject.parkingjam.model.dto.Board;
import es.upm.pproject.parkingjam.model.dto.Direction;
import es.upm.pproject.parkingjam.model.dto.Position;
import es.upm.pproject.parkingjam.model.dto.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Panel that renders the board and converts mouse drag gestures into controller.move() calls.
 * INTERACTION MODEL: click and drag a vehicle to move it one step in the drag direction.
 */
public class BoardPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(BoardPanel.class);

    private static final int CELL_SIZE = 60;
    private transient Board board;
    // Cache of colors assigned to vehicle ids
    private final Map<Character, Color> vehicleColors = new HashMap<>();
    // Controller instance to call services
    private transient GameController controller;
    // Mouse drag state
    private int pressRow;
    private int pressCol;
    private char pressedVehicleId;
    private char selectedVehicleId = '\0';


    public BoardPanel() {
        setBackground(new Color(45, 45, 45));
        setPreferredSize(new Dimension(480, 480));
        setupMouseListeners();
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public void setBoard(Board board) {
        this.board = board;
        if (board != null) {
            setPreferredSize(new Dimension(board.getColumns() * CELL_SIZE, board.getRows() * CELL_SIZE));
        }
        repaint();
    }

    // Mouse listener
    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                mousePressedAux(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mouseReleasedAux(e);
            }
        });
    }

    // Handle mouse pressed
    private void mousePressedAux(MouseEvent e) {
        pressedVehicleId = '\0';
        selectedVehicleId = '\0';

        if (board == null) return;

        // Record cell where press started
        pressCol = e.getX() / CELL_SIZE;
        pressRow = e.getY() / CELL_SIZE;

        Position pos = new Position(pressRow, pressCol);
        if (board.isInBoard(pos)) {
            // Determine if a vehicle was pressed and save its id
            Optional<Vehicle> v = board.getVehicleAtPosition(pos);
            pressedVehicleId = v.map(Vehicle::getId).orElse('\0');
            // Select the clicked vehicle for visual feedback
            if (isVehicle(pressedVehicleId)) {
                selectedVehicleId = pressedVehicleId;
                logger.debug("Vehicle '{}' selected at position ({}, {})", pressedVehicleId, pressRow, pressCol);
                repaint();
            }
        }
    }

    // Handle mouse released
    private void mouseReleasedAux(MouseEvent e) {
        // On release, if a drag occurred compute direction and request a move
        if (controller == null || board == null) return;
        char id = pressedVehicleId;
        if (!isVehicle(id)) return;

        int releaseCol = e.getX() / CELL_SIZE;
        int releaseRow = e.getY() / CELL_SIZE;
        int dc = releaseCol - pressCol;
        int dr = releaseRow - pressRow;
        if (dc == 0 && dr == 0) return;  // no movement

        // Prefer the larger delta to determine primary drag direction
        Direction dir;
        if (Math.abs(dc) >= Math.abs(dr)) {
            dir = dc > 0 ? Direction.EAST : Direction.WEST;
        } else {
            dir = dr > 0 ? Direction.SOUTH : Direction.NORTH;
        }
        logger.debug("User drag detected for vehicle '{}': from ({}, {}) to ({}, {}), direction: {}", id, pressRow, pressCol, releaseRow, releaseCol, dir);
        controller.move(id, dir);
    }

    // Rendering
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (board == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw Walls
        for (Position wallPos : board.getWalls()) {
            drawWall(g2d, wallPos.getX(), wallPos.getY());
        }

        // Draw Exit
        drawExit();

        // Draw Vehicles: each vehicle may occupy multiple cells
        for (Vehicle vehicle : board.getVehicles().values()) {
            for (Position p : vehicle.getPositions()) {
                drawVehiclePart(g2d, p.getX(), p.getY(), vehicle);
            }
        }
    }

    private void drawExit() {
        // No action for rendering exit
    }

    private void drawWall(Graphics2D g, int r, int c) {
        int x = c * CELL_SIZE;
        int y = r * CELL_SIZE;
        g.setColor(new Color(60, 60, 60));
        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        
        // Draw borders for this wall cell where adjacent cell is not a wall
        if (r == 0 || !board.isInWall(new Position(r-1, c))) g.drawLine(x, y, x + CELL_SIZE, y);
        if (r == board.getRows() - 1 || !board.isInWall(new Position(r+1, c))) g.drawLine(x, y + CELL_SIZE, x + CELL_SIZE, y + CELL_SIZE);
        if (c == 0 || !board.isInWall(new Position(r, c-1))) g.drawLine(x, y, x, y + CELL_SIZE);
        if (c == board.getColumns() - 1 || !board.isInWall(new Position(r, c+1))) g.drawLine(x + CELL_SIZE, y, x + CELL_SIZE, y + CELL_SIZE);
    }

    private void drawVehiclePart(Graphics2D g, int r, int c, Vehicle vehicle) {
        char id = vehicle.getId();
        Color base = vehicle.isRedCar() ? Color.RED : getVehicleColor(id);

        // Highlight selected vehicle with a brighter outline
        if (id == selectedVehicleId) {
            base = base.brighter();
        }

        g.setColor(base);
        int x = c * CELL_SIZE;
        int y = r * CELL_SIZE;
        int m = 4;
        g.fillRoundRect(x + m, y + m, CELL_SIZE - m * 2, CELL_SIZE - m * 2, 15, 15);

        // Fill gaps between this cell and adjacent cells of the same vehicle
        // so multi-cell vehicles appear visually connected.
        for (Position p : vehicle.getPositions()) {
            if (p.getX() == r && p.getY() == c + 1) g.fillRect(x + CELL_SIZE / 2, y + m, CELL_SIZE / 2, CELL_SIZE - m * 2);
            if (p.getX() == r && p.getY() == c - 1) g.fillRect(x, y + m, CELL_SIZE / 2, CELL_SIZE - m * 2);
            if (p.getX() == r + 1 && p.getY() == c) g.fillRect(x + m, y + CELL_SIZE / 2, CELL_SIZE - m * 2, CELL_SIZE / 2);
            if (p.getX() == r - 1 && p.getY() == c) g.fillRect(x + m, y, CELL_SIZE - m * 2, CELL_SIZE / 2);
        }
 
    }

    private Color getVehicleColor(char id) {
        vehicleColors.putIfAbsent(id, new Color((id * 30) % 255, (id * 60) % 255, (id * 90) % 255));
        return vehicleColors.get(id);
    }

    // Returns true if the char represents a moveable vehicle (not wall, exit or empty)
    private boolean isVehicle(char id) {
        return id != ' ' && id != '+' && id != '@' && id != '\0' && id != '.';
    }
}