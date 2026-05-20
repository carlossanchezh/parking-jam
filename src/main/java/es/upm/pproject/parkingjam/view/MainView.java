package es.upm.pproject.parkingjam.view;

import es.upm.pproject.parkingjam.controller.GameController;
import es.upm.pproject.parkingjam.model.dto.Board;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window.
 *   - Instances a GameController
 *   - Buttons/menu items call controller methods directly
 *   - Exposes update methods so the controller can refresh the UI
 */
public class MainView extends JFrame {

    private JLabel levelNameLabel;
    private JLabel levelScoreLabel;
    private JLabel totalScoreLabel;
    private BoardPanel boardPanel;

    private GameController controller;

    public MainView() {
        setTitle("Parking Jam");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
        pack();
        setLocationRelativeTo(null);
    }

    // Called by App
    public void setController(GameController controller) {
        this.controller = controller;
        boardPanel.setController(controller);
    }


    //  Methods called by the Controller to update the view
    public void updateBoard(Board board) {
        SwingUtilities.invokeLater(() -> boardPanel.setBoard(board));
    }

    public void updateStatus(String levelName, int levelScore, int totalScore) {
        SwingUtilities.invokeLater(() -> {
            levelNameLabel.setText("Level: " + levelName);
            levelScoreLabel.setText("Level Score: " + levelScore);
            totalScoreLabel.setText("Total Score: " + totalScore);
        });
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }

   

    //  UI construction
    private void initComponents() {
        setLayout(new BorderLayout());

        // Status bar
        JPanel infoPanel = new JPanel(new GridLayout(1, 3));
        levelNameLabel  = new JLabel("Level: ---",       SwingConstants.CENTER);
        levelScoreLabel = new JLabel("Level Score: ---", SwingConstants.CENTER);
        totalScoreLabel = new JLabel("Total Score: ---", SwingConstants.CENTER);
        infoPanel.add(levelNameLabel);
        infoPanel.add(levelScoreLabel);
        infoPanel.add(totalScoreLabel);
        add(infoPanel, BorderLayout.NORTH);

        // Board
        boardPanel = new BoardPanel();
        add(boardPanel, BorderLayout.CENTER);

        // Menu
        setJMenuBar(createMenuBar());
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");

        JMenuItem newItem   = new JMenuItem("New Game");
        JMenuItem resetItem = new JMenuItem("Reset Level");
        JMenuItem saveItem  = new JMenuItem("Save Game");
        JMenuItem loadItem  = new JMenuItem("Load Game");
        JMenuItem exitItem  = new JMenuItem("Exit");
        JButton undoButton  = new JButton("Undo");

        newItem.addActionListener(e -> controller.newGame());

        resetItem.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to restart the current level?",
                    "Restart Level", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                controller.restartLevel();
            }
        });

        exitItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) System.exit(0);
        });

        gameMenu.add(newItem);
        gameMenu.add(resetItem);
        gameMenu.addSeparator();
        gameMenu.add(saveItem);
        gameMenu.add(loadItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);

        menuBar.add(gameMenu);
        menuBar.add(undoButton);
        return menuBar;
    }
}