package es.upm.pproject.parkingjam.view;

import es.upm.pproject.parkingjam.controller.GameController;
import es.upm.pproject.parkingjam.model.dto.Board;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window.
 *   - Instances a GameController
 *   - Buttons/menu items call controller methods directly
 *   - Exposes update methods so the controller can refresh the UI
 */
public class MainView extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(MainView.class);

    private JLabel levelNameLabel;
    private JLabel levelScoreLabel;
    private JLabel totalScoreLabel;
    private BoardPanel boardPanel;

    private transient GameController controller;

    public MainView() {
        setTitle("Parking Jam");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
        pack();
        setLocationRelativeTo(null);
        logger.info("Main application window initialized");
    }

    // Called by App
    public void setController(GameController controller) {
        this.controller = controller;
        boardPanel.setController(controller);
        logger.debug("Game controller attached to MainView");
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
            logger.trace("UI status updated: {} - Level score: {}, Total score: {}", levelName, levelScore, totalScore);
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

        newItem.addActionListener(e -> {
            if (controller != null) {
                controller.newGame();
            }
        });

        resetItem.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to restart the current level?",
                    "Restart Level", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION && controller != null) {
                controller.restartLevel();
            }
        });

        exitItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                System.exit(0);
            }
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