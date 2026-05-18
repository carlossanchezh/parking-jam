package es.upm.pproject.parkingjam.view;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BoardPanel extends JPanel {
    private static final int CELL_SIZE = 60;
    private char[][] board;
    private Map<Character, Color> vehicleColors;

    public BoardPanel() {
        setBackground(new Color(45, 45, 45)); 
        vehicleColors = new HashMap<>();
        setPreferredSize(new Dimension(480, 480));
    }

    public void setBoard(char[][] board) {
        this.board = board;
        if (board != null) {
            setPreferredSize(new Dimension(board[0].length * CELL_SIZE, board.length * CELL_SIZE));
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (board == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                char cell = board[r][c];
                if (cell == '+') {
                    drawWall(g2d, r, c); 
                } else if (cell != ' ' && cell != '@') { 
                    drawVehiclePart(g2d, r, c, cell); 
                }
            }
        }
    }

    private void drawWall(Graphics2D g, int r, int c) {
        int x = c * CELL_SIZE;
        int y = r * CELL_SIZE;
        
        g.setColor(new Color(60, 60, 60)); // Color base del muro
        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));


        if (r == 0 || board[r-1][c] != '+') g.drawLine(x, y, x + CELL_SIZE, y); // Arriba
        if (r == board.length-1 || board[r+1][c] != '+') g.drawLine(x, y + CELL_SIZE, x + CELL_SIZE, y + CELL_SIZE); // Abajo
        if (c == 0 || board[r][c-1] != '+') g.drawLine(x, y, x, y + CELL_SIZE); // Izquierda
        if (c == board[0].length-1 || board[r][c+1] != '+') g.drawLine(x + CELL_SIZE, y, x + CELL_SIZE, y + CELL_SIZE); // Derecha
    }

    private void drawVehiclePart(Graphics2D g, int r, int c, char id) {
        if (id == '*') g.setColor(Color.RED);
        else g.setColor(getVehicleColor(id));

        int x = c * CELL_SIZE;
        int y = r * CELL_SIZE;
        int margin = 4;

        g.fillRoundRect(x + margin, y + margin, CELL_SIZE - (margin * 2), CELL_SIZE - (margin * 2), 15, 15);

        if (c + 1 < board[0].length && board[r][c + 1] == id) {
            g.fillRect(x + CELL_SIZE / 2, y + margin, CELL_SIZE / 2, CELL_SIZE - (margin * 2));
        }
        if (c > 0 && board[r][c - 1] == id) {
            g.fillRect(x, y + margin, CELL_SIZE / 2, CELL_SIZE - (margin * 2));
        }
        if (r + 1 < board.length && board[r + 1][c] == id) {
            g.fillRect(x + margin, y + CELL_SIZE / 2, CELL_SIZE - (margin * 2), CELL_SIZE / 2);
        }
        if (r > 0 && board[r - 1][c] == id) {
            g.fillRect(x + margin, y, CELL_SIZE - (margin * 2), CELL_SIZE / 2);
        }

        g.setColor(Color.WHITE);
    }

    private Color getVehicleColor(char id) {
        vehicleColors.putIfAbsent(id, new Color((id * 30) % 255, (id * 60) % 255, (id * 90) % 255));
        return vehicleColors.get(id);
    }
}