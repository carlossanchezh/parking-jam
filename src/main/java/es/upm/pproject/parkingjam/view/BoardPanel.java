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
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Panel that renders the board and converts mouse drag gestures into controller.move() calls.
 * INTERACTION MODEL: click and drag a vehicle to move it one step in the drag direction.
 *
 * Vehicles are drawn as different kinds of "cars" depending on their size and color:
 *   - size 2, red  -> Formula 1 car
 *   - size 2, other colors -> regular car
 *   - size 3 -> van
 *   - size 4 -> bus
 *   - size 5+ -> truck (cab + cargo box)
 */
public class BoardPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(BoardPanel.class);

    private static final int CELL_SIZE = 60;

    // Palette used to give the board a more polished, consistent look
    private static final Color BOARD_BACKGROUND = new Color(45, 45, 45);
    private static final Color FLOOR_COLOR       = new Color(58, 58, 60);
    private static final Color GRID_LINE_COLOR   = new Color(45, 45, 45);
    private static final Color WALL_COLOR        = new Color(65, 65, 67);
    private static final Color WALL_BORDER_COLOR = new Color(20, 20, 20);
    private static final Color EXIT_COLOR        = new Color(60, 200, 110);
    private static final Color SELECTION_COLOR   = new Color(255, 215, 0);
    private static final Color RED_CAR_COLOR     = new Color(214, 48, 49);
    private static final Color WINDOW_COLOR      = new Color(214, 234, 248, 230);
    private static final Color WHEEL_COLOR       = new Color(25, 25, 28);
    private static final Color HEADLIGHT_COLOR   = new Color(255, 244, 180);
    private static final Color TAILLIGHT_COLOR   = new Color(255, 90, 90);

    // Transform that swaps the x and y axes, used to re-use the "horizontal" drawing
    // routines for vertically oriented vehicles.
    private static final AffineTransform SWAP_XY = new AffineTransform(0, 1, 1, 0, 0, 0);

    // Pleasant set of colors assigned to vehicles in order of appearance
    private static final Color[] CAR_PALETTE = {
            new Color(52, 152, 219),  // blue
            new Color(241, 196, 15),  // yellow
            new Color(46, 204, 113),  // green
            new Color(155, 89, 182),  // purple
            new Color(230, 126, 34),  // orange
            new Color(26, 188, 156),  // turquoise
            new Color(149, 165, 166), // grey
            new Color(0, 184, 217),   // cyan
    };

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
        setBackground(BOARD_BACKGROUND);
        setPreferredSize(new Dimension(480, 480));
        setupMouseListeners();
        setFocusable(true); // Allow receiving key events
        setupKeyBindings();
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public void setBoard(Board board) {
        this.board = board;
        if (board != null) {
            setPreferredSize(new Dimension(board.getColumns() * CELL_SIZE, board.getRows() * CELL_SIZE));
        }
        revalidate();
        repaint();
    }

    // Key listeners
    private void setupKeyBindings() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        inputMap.put(KeyStroke.getKeyStroke("UP"), "moveUp");
        inputMap.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");

        actionMap.put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveSelectedVehicle(Direction.WEST);
            }
        });
        actionMap.put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveSelectedVehicle(Direction.EAST);
            }
        });
        actionMap.put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveSelectedVehicle(Direction.NORTH);
            }
        });
        actionMap.put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveSelectedVehicle(Direction.SOUTH);
            }
        });
    }

    // Helper
    private void moveSelectedVehicle(Direction direction) {
        if (controller == null || !isVehicle(selectedVehicleId)) {
            return;
        }
        controller.move(selectedVehicleId, direction);
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
            if (v.isPresent()) {
                pressedVehicleId = v.get().getId();
                // Select the clicked vehicle for visual feedback
                if (isVehicle(pressedVehicleId)) {
                    selectedVehicleId = pressedVehicleId;
                    logger.debug("Vehicle '{}' selected at position ({}, {})", pressedVehicleId, pressRow, pressCol);
                    repaint();
                } else {
                    pressedVehicleId = '\0';
                }
            } else  {
                pressedVehicleId = '\0';
            }
        }  else {
            pressedVehicleId = '\0';
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

        // Draw the floor grid first so every other element is layered on top
        drawFloor(g2d);

        // Draw Walls
        for (Position wallPos : board.getWalls()) {
            drawWall(g2d, wallPos.getX(), wallPos.getY());
        }

        // Draw Exit
        drawExit(g2d);

        // Draw Vehicles as cars
        for (Vehicle vehicle : board.getVehicles().values()) {
            drawCar(g2d, vehicle);
        }
    }

    // Draws a subtle grid over the playable area so empty cells are easy to read
    private void drawFloor(Graphics2D g) {
        int rows = board.getRows();
        int cols = board.getColumns();

        g.setColor(FLOOR_COLOR);
        g.fillRect(0, 0, cols * CELL_SIZE, rows * CELL_SIZE);

        g.setColor(GRID_LINE_COLOR);
        for (int r = 0; r <= rows; r++) {
            g.drawLine(0, r * CELL_SIZE, cols * CELL_SIZE, r * CELL_SIZE);
        }
        for (int c = 0; c <= cols; c++) {
            g.drawLine(c * CELL_SIZE, 0, c * CELL_SIZE, rows * CELL_SIZE);
        }
    }

    // Draws the exit cell as a glowing opening with an arrow pointing outwards from the board
    private void drawExit(Graphics2D g) {
        Position exit = board.getExit();
        int x = exit.getY() * CELL_SIZE;
        int y = exit.getX() * CELL_SIZE;

        g.setColor(EXIT_COLOR);
        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);

        // Brighter inset glow to make the exit stand out
        g.setColor(EXIT_COLOR.brighter());
        int inset = 8;
        g.fillRect(x + inset, y + inset, CELL_SIZE - inset * 2, CELL_SIZE - inset * 2);

        drawExitArrow(g, exit, x, y);
    }

    // Draws an arrow inside the exit cell that points towards the edge of the board it sits on
    private void drawExitArrow(Graphics2D g, Position exit, int x, int y) {
        int cx = x + CELL_SIZE / 2;
        int cy = y + CELL_SIZE / 2;
        int armSize = CELL_SIZE / 5;

        Polygon arrow = new Polygon();
        if (exit.getX() == 0) {
            arrow.addPoint(cx - armSize, cy + armSize);
            arrow.addPoint(cx + armSize, cy + armSize);
            arrow.addPoint(cx, cy - armSize);
        } else if (exit.getX() == board.getRows() - 1) {
            arrow.addPoint(cx - armSize, cy - armSize);
            arrow.addPoint(cx + armSize, cy - armSize);
            arrow.addPoint(cx, cy + armSize);
        } else if (exit.getY() == 0) {
            arrow.addPoint(cx + armSize, cy - armSize);
            arrow.addPoint(cx + armSize, cy + armSize);
            arrow.addPoint(cx - armSize, cy);
        } else {
            arrow.addPoint(cx - armSize, cy - armSize);
            arrow.addPoint(cx - armSize, cy + armSize);
            arrow.addPoint(cx + armSize, cy);
        }

        g.setColor(new Color(20, 60, 35));
        g.fillPolygon(arrow);
    }

    // Draws a wall cell as a flat block; borders are only drawn where the wall meets
    // the playable area, so adjacent wall cells form one continuous block.
    private void drawWall(Graphics2D g, int r, int c) {
        int x = c * CELL_SIZE;
        int y = r * CELL_SIZE;

        g.setColor(WALL_COLOR);
        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);

        g.setColor(WALL_BORDER_COLOR);
        g.setStroke(new BasicStroke(2));

        // Draw borders for this wall cell where adjacent cell is not a wall
        if (r == 0 || !board.isInWall(new Position(r-1, c))) g.drawLine(x, y, x + CELL_SIZE, y);
        if (r == board.getRows() - 1 || !board.isInWall(new Position(r+1, c))) g.drawLine(x, y + CELL_SIZE, x + CELL_SIZE, y + CELL_SIZE);
        if (c == 0 || !board.isInWall(new Position(r, c-1))) g.drawLine(x, y, x, y + CELL_SIZE);
        if (c == board.getColumns() - 1 || !board.isInWall(new Position(r, c+1))) g.drawLine(x + CELL_SIZE, y, x + CELL_SIZE, y + CELL_SIZE);
    }

    // Draws a vehicle, dispatching to a shape that matches its size/color:
    //   size 2 + red  -> Formula 1 car
    //   size 2        -> regular car
    //   size 3        -> van
    //   size 4        -> bus
    //   size 5+       -> truck
    private void drawCar(Graphics2D g, Vehicle vehicle) {
        BoundingBox bounds = boundingBox(vehicle);
        int minRow = bounds.minRow();
        int minCol = bounds.minCol();
        int maxRow = bounds.maxRow();
        int maxCol = bounds.maxCol();

        int x = minCol * CELL_SIZE;
        int y = minRow * CELL_SIZE;
        int w = (maxCol - minCol + 1) * CELL_SIZE;
        int h = (maxRow - minRow + 1) * CELL_SIZE;

        Color base = vehicle.isRedCar() ? RED_CAR_COLOR : getVehicleColor(vehicle.getId());

        int m = 6;
        double bx = x + (double) m;
        double by = y + (double) m;
        double bw = w - 2.0 * m;
        double bh = h - 2.0 * m;

        // Soft drop shadow for a bit of depth
        g.setColor(new Color(0, 0, 0, 70));
        double shadowArc = Math.min(bw, bh) * 0.3;
        g.fill(new RoundRectangle2D.Double(bx + 3, by + 4, bw, bh, shadowArc, shadowArc));

        boolean horizontal = bw >= bh;
        double length = Math.max(bw, bh);   // long side of the vehicle
        double thickness = Math.min(bw, bh); // short side of the vehicle

        // Draw the vehicle shape in "horizontal" local coordinates (0,0)-(length,thickness),
        // then transform it back into the real orientation of the cell.
        Graphics2D gc = (Graphics2D) g.create();
        gc.translate(bx, by);
        if (!horizontal) {
            gc.transform(SWAP_XY);
        }
        gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = vehicle.getSize();
        if (size == 2 && vehicle.isRedCar()) {
            drawF1Car(gc, length, thickness, base);
        } else if (size == 2) {
            drawNormalCar(gc, length, thickness, base);
        } else if (size == 3) {
            drawVan(gc, length, thickness, base);
        } else if (size == 4) {
            drawBus(gc, length, thickness, base);
        } else {
            drawTruck(gc, length, thickness, base);
        }
        gc.dispose();

        // Highlight the selected vehicle with a bright glowing border
        if (vehicle.getId() == selectedVehicleId) {
            double arc = Math.min(bw, bh) * 0.25;
            g.setColor(SELECTION_COLOR);
            g.setStroke(new BasicStroke(3));
            g.drawRoundRect(x + 2, y + 2, w - 4, h - 4, (int) arc + 4, (int) arc + 4);
        }
    }

    // Regular passenger car: rounded body, two window bands, four wheels, head/tail lights
    private void drawNormalCar(Graphics2D g, double l, double t, Color base) {
        double arc = t;

        GradientPaint grad = new GradientPaint(0, 0, base.brighter(), 0, (float) t, base.darker());
        g.setPaint(grad);
        g.fill(new RoundRectangle2D.Double(0, 0, l, t, arc, arc));
        g.setPaint(null);

        // Windows near each end, leaving a hood/trunk gap in the middle
        double winW = l * 0.26;
        double winH = t * 0.62;
        double winY = (t - winH) / 2;
        g.setColor(WINDOW_COLOR);
        g.fill(new RoundRectangle2D.Double(l * 0.08, winY, winW, winH, 8, 8));
        g.fill(new RoundRectangle2D.Double(l - l * 0.08 - winW, winY, winW, winH, 8, 8));

        // Wheels
        g.setColor(WHEEL_COLOR);
        drawWheelPair(g, l * 0.24, t, l * 0.18, 7);
        drawWheelPair(g, l * 0.76, t, l * 0.18, 7);

        // Lights at both ends
        double lightSize = t * 0.22;
        g.setColor(HEADLIGHT_COLOR);
        g.fill(new Ellipse2D.Double(l - lightSize - 2, (t - lightSize) / 2, lightSize, lightSize));
        g.setColor(TAILLIGHT_COLOR);
        g.fill(new Ellipse2D.Double(2, (t - lightSize) / 2, lightSize, lightSize));

        // Outline
        g.setColor(base.darker().darker());
        g.setStroke(new BasicStroke(2f));
        g.draw(new RoundRectangle2D.Double(0, 0, l, t, arc, arc));
    }

    // Formula 1 car: narrow chassis, pointed nose, front/rear wings, exposed wheels and cockpit
    private void drawF1Car(Graphics2D g, double l, double t, Color base) {
        double chassisH = t * 0.46;
        double chassisY = (t - chassisH) / 2;
        double chassisX0 = l * 0.16;
        double chassisX1 = l * 0.80;
        double chassisArc = chassisH * 0.5;

        // Rear wing (tall thin bar near the back)
        g.setPaint(new GradientPaint(0, 0, base.darker(), 0, (float) t, base.darker().darker()));
        g.fill(new RoundRectangle2D.Double(0, t * 0.06, l * 0.07, t * 0.88, 3, 3));

        // Main chassis
        GradientPaint chassisGrad = new GradientPaint(0, (float) chassisY, base.brighter(), 0, (float) (chassisY + chassisH), base.darker());
        g.setPaint(chassisGrad);
        RoundRectangle2D.Double chassis = new RoundRectangle2D.Double(chassisX0, chassisY, chassisX1 - chassisX0, chassisH, chassisArc, chassisArc);
        g.fill(chassis);

        // Nose cone tapering to a point
        g.setPaint(chassisGrad);
        Path2D.Double nose = new Path2D.Double();
        nose.moveTo(chassisX1 - chassisArc * 0.3, chassisY + chassisH * 0.12);
        nose.lineTo(chassisX1 - chassisArc * 0.3, chassisY + chassisH * 0.88);
        nose.lineTo(l * 0.97, t / 2.0);
        nose.closePath();
        g.fill(nose);
        g.setPaint(null);

        // Front wing (thin wide bar near the nose)
        g.setColor(base.darker());
        double fwW = l * 0.045;
        g.fill(new RoundRectangle2D.Double(l - fwW - 1, t * 0.06, fwW, t * 0.88, 2, 2));

        // Cockpit opening
        g.setColor(new Color(20, 20, 22));
        double cockH = chassisH * 0.62;
        double cockW = chassisH * 0.95;
        double cockX = chassisX0 + chassisH * 0.55;
        g.fill(new Ellipse2D.Double(cockX, t / 2.0 - cockH / 2.0, cockW, cockH));

        // Driver's helmet
        g.setColor(new Color(235, 235, 235));
        double helD = cockH * 0.55;
        g.fill(new Ellipse2D.Double(cockX + (cockW - helD) / 2.0, t / 2.0 - helD / 2.0, helD, helD));

        // Big, partially exposed wheels at each corner
        g.setColor(WHEEL_COLOR);
        double wheelD = t * 0.5;
        g.fill(new Ellipse2D.Double(l * 0.22 - wheelD / 2.0, -wheelD / 2.0, wheelD, wheelD));
        g.fill(new Ellipse2D.Double(l * 0.22 - wheelD / 2.0, t - wheelD / 2.0, wheelD, wheelD));
        g.fill(new Ellipse2D.Double(l * 0.70 - wheelD / 2.0, -wheelD / 2.0, wheelD, wheelD));
        g.fill(new Ellipse2D.Double(l * 0.70 - wheelD / 2.0, t - wheelD / 2.0, wheelD, wheelD));

        // Outline of the chassis
        g.setColor(base.darker().darker());
        g.setStroke(new BasicStroke(2f));
        g.draw(chassis);
    }

    // Van: boxy body, windshield, side windows and a sliding door line
    private void drawVan(Graphics2D g, double l, double t, Color base) {
        double arc = t * 0.18;

        GradientPaint grad = new GradientPaint(0, 0, base.brighter(), 0, (float) t, base.darker());
        g.setPaint(grad);
        RoundRectangle2D.Double body = new RoundRectangle2D.Double(0, 0, l, t, arc, arc);
        g.fill(body);
        g.setPaint(null);

        // Windshield near the front
        double wsW = l * 0.16;
        double wsH = t * 0.6;
        double wsY = (t - wsH) / 2;
        g.setColor(WINDOW_COLOR);
        g.fill(new RoundRectangle2D.Double(l - wsW - l * 0.05, wsY, wsW, wsH, 6, 6));

        // Side windows for the passenger area
        double sw = t * 0.32;
        double swY = (t - sw) / 2;
        g.fill(new RoundRectangle2D.Double(l * 0.40, swY, sw, sw, 5, 5));
        g.fill(new RoundRectangle2D.Double(l * 0.56, swY, sw, sw, 5, 5));

        // Sliding door line separating passenger area from cargo area
        g.setColor(base.darker());
        g.setStroke(new BasicStroke(1.5f));
        g.draw(new Line2D.Double(l * 0.36, t * 0.08, l * 0.36, t * 0.92));

        // Wheels
        g.setColor(WHEEL_COLOR);
        drawWheelPair(g, l * 0.22, t, l * 0.15, 7);
        drawWheelPair(g, l * 0.78, t, l * 0.15, 7);

        // Lights
        double lightSize = t * 0.2;
        g.setColor(HEADLIGHT_COLOR);
        g.fill(new Ellipse2D.Double(l - lightSize - 2, (t - lightSize) / 2, lightSize, lightSize));
        g.setColor(TAILLIGHT_COLOR);
        g.fill(new Rectangle2D.Double(1, (t - lightSize) / 2, lightSize * 0.6, lightSize));

        // Outline
        g.setColor(base.darker().darker());
        g.setStroke(new BasicStroke(2f));
        g.draw(body);
    }

    // Bus: long boxy body with a row of windows and a decorative stripe
    private void drawBus(Graphics2D g, double l, double t, Color base) {
        double arc = t * 0.15;

        GradientPaint grad = new GradientPaint(0, 0, base.brighter(), 0, (float) t, base.darker());
        g.setPaint(grad);
        RoundRectangle2D.Double body = new RoundRectangle2D.Double(0, 0, l, t, arc, arc);
        g.fill(body);
        g.setPaint(null);

        // Decorative stripe along the side
        g.setColor(new Color(255, 255, 255, 90));
        g.fill(new Rectangle2D.Double(0, t * 0.62, l, t * 0.08));

        // Row of evenly spaced passenger windows
        int numWindows = 5;
        double margin = l * 0.06;
        double available = l - 2 * margin;
        double slot = available / numWindows;
        double winW = slot * 0.68;
        double winH = t * 0.42;
        double winY = t * 0.14;
        g.setColor(WINDOW_COLOR);
        for (int i = 0; i < numWindows; i++) {
            double wx = margin + i * slot + (slot - winW) / 2;
            g.fill(new RoundRectangle2D.Double(wx, winY, winW, winH, 5, 5));
        }

        // Wheels
        g.setColor(WHEEL_COLOR);
        drawWheelPair(g, l * 0.22, t, l * 0.11, 8);
        drawWheelPair(g, l * 0.78, t, l * 0.11, 8);

        // Lights
        double lightSize = t * 0.18;
        g.setColor(HEADLIGHT_COLOR);
        g.fill(new Ellipse2D.Double(l - lightSize - 2, (t - lightSize) / 2, lightSize, lightSize));
        g.setColor(TAILLIGHT_COLOR);
        g.fill(new Ellipse2D.Double(2, (t - lightSize) / 2, lightSize, lightSize));

        // Outline
        g.setColor(base.darker().darker());
        g.setStroke(new BasicStroke(2f));
        g.draw(body);
    }

    // Truck: a darker cab at one end plus a cargo box with ridge lines, wheels along the body
    private void drawTruck(Graphics2D g, double l, double t, Color base) {
        double arc = t * 0.15;
        double cabLen = Math.min(t * 1.15, l * 0.32);
        double cargoLen = l - cabLen;

        // Cargo box
        GradientPaint cargoGrad = new GradientPaint(0, 0, base.brighter(), 0, (float) t, base.darker());
        g.setPaint(cargoGrad);
        RoundRectangle2D.Double cargo = new RoundRectangle2D.Double(0, 0, cargoLen, t, arc, arc);
        g.fill(cargo);
        g.setPaint(null);

        // Ridge lines giving the cargo box a paneled/container look
        g.setColor(base.darker());
        g.setStroke(new BasicStroke(1f));
        int ridges = Math.max(2, (int) (cargoLen / 18));
        for (int i = 1; i < ridges; i++) {
            double rx = cargoLen * i / ridges;
            g.draw(new Line2D.Double(rx, t * 0.12, rx, t * 0.88));
        }

        // Cab
        Color cabColor = base.darker();
        GradientPaint cabGrad = new GradientPaint((float) cargoLen, 0, cabColor.brighter(), (float) cargoLen, (float) t, cabColor.darker());
        g.setPaint(cabGrad);
        RoundRectangle2D.Double cab = new RoundRectangle2D.Double(cargoLen, 0, cabLen, t, arc, arc);
        g.fill(cab);
        g.setPaint(null);

        // Cab window
        double winW = cabLen * 0.55;
        double winH = t * 0.55;
        double winY = (t - winH) / 2;
        g.setColor(WINDOW_COLOR);
        g.fill(new RoundRectangle2D.Double(cargoLen + cabLen * 0.32, winY, winW, winH, 5, 5));

        // Wheels spread along the whole length, roughly one pair per cell
        g.setColor(WHEEL_COLOR);
        int pairCount = Math.max(2, Math.round((float) (l / CELL_SIZE)));
        for (int i = 0; i < pairCount; i++) {
            double cx = (i + 0.5) * l / pairCount;
            drawWheelPair(g, cx, t, l * 0.08, 8);
        }

        // Lights
        double lightSize = t * 0.18;
        g.setColor(HEADLIGHT_COLOR);
        g.fill(new Ellipse2D.Double(l - lightSize - 2, (t - lightSize) / 2, lightSize, lightSize));
        g.setColor(TAILLIGHT_COLOR);
        g.fill(new Ellipse2D.Double(2, (t - lightSize) / 2, lightSize, lightSize));

        // Outlines
        g.setColor(base.darker().darker());
        g.setStroke(new BasicStroke(2f));
        g.draw(cargo);
        g.draw(cab);
    }

    // Draws a pair of wheels (one on the top edge, one on the bottom edge) centered at cx
    private void drawWheelPair(Graphics2D g, double cx, double t, double wheelLen, double wheelThick) {
        g.fill(new RoundRectangle2D.Double(cx - wheelLen / 2, -wheelThick / 2, wheelLen, wheelThick, 3, 3));
        g.fill(new RoundRectangle2D.Double(cx - wheelLen / 2, t - wheelThick / 2, wheelLen, wheelThick, 3, 3));
    }

    // Computes the [minRow, minCol, maxRow, maxCol] bounding box occupied by a vehicle
    private BoundingBox boundingBox(Vehicle vehicle) {
        int minRow = Integer.MAX_VALUE;
        int minCol = Integer.MAX_VALUE;
        int maxRow = Integer.MIN_VALUE;
        int maxCol = Integer.MIN_VALUE;
        for (Position p : vehicle.getPositions()) {
            minRow = Math.min(minRow, p.getX());
            maxRow = Math.max(maxRow, p.getX());
            minCol = Math.min(minCol, p.getY());
            maxCol = Math.max(maxCol, p.getY());
        }
        return new BoundingBox(minRow, minCol, maxRow, maxCol);
    }

    // Simple value holder for a vehicle's bounding box, avoiding constant-index array access
    private static final class BoundingBox {
        private final int minRow;
        private final int minCol;
        private final int maxRow;
        private final int maxCol;

        private BoundingBox(int minRow, int minCol, int maxRow, int maxCol) {
            this.minRow = minRow;
            this.minCol = minCol;
            this.maxRow = maxRow;
            this.maxCol = maxCol;
        }

        int minRow() { return minRow; }
        int minCol() { return minCol; }
        int maxRow() { return maxRow; }
        int maxCol() { return maxCol; }
    }

    // Assigns each vehicle a color from the palette the first time it is drawn
    private Color getVehicleColor(char id) {
        return vehicleColors.computeIfAbsent(id, k -> CAR_PALETTE[vehicleColors.size() % CAR_PALETTE.length]);
    }

    // Returns true if the char represents a moveable vehicle (not wall, exit or empty)
    private boolean isVehicle(char id) {
        return id != ' ' && id != '+' && id != '@' && id != '\0' && id != '.';
    }
}