package es.upm.pproject.parkingjam.view;

import es.upm.pproject.parkingjam.controller.GameController;
import es.upm.pproject.parkingjam.model.dto.Board;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Main application window.
 *   - Instances a GameController
 *   - Buttons/menu items call controller methods directly
 *   - Exposes update methods so the controller can refresh the UI
 */
public class MainView extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(MainView.class);

    // Theme colors shared with the board panel for a consistent look
    private static final Color BACKGROUND_COLOR = new Color(37, 37, 38);
    private static final Color PANEL_COLOR      = new Color(50, 50, 52);
    private static final Color BORDER_COLOR     = new Color(70, 70, 72);
    private static final Color TEXT_COLOR       = new Color(230, 230, 230);
    private static final Color ACCENT_COLOR     = new Color(0, 122, 204);

    private JLabel levelNameLabel;
    private JLabel levelScoreLabel;
    private JLabel totalScoreLabel;
    private BoardPanel boardPanel;

    private transient GameController controller;

    public MainView() {
        setTitle("Parking Jam");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BACKGROUND_COLOR);
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
        add(createInfoPanel(), BorderLayout.NORTH);

        // Board
        boardPanel = new BoardPanel();
        JPanel boardWrapper = new JPanel(new GridBagLayout());
        boardWrapper.setBackground(BACKGROUND_COLOR);
        boardWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        boardWrapper.add(boardPanel);
        add(boardWrapper, BorderLayout.CENTER);

        // Menu
        setJMenuBar(createMenuBar());

        // Keyboard shortcuts that should work regardless of which component has focus
        registerGlobalShortcuts();
    }

    // Builds the status bar shown at the top of the window
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 8, 0));
        infoPanel.setBackground(PANEL_COLOR);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_COLOR),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        levelNameLabel  = createStatusLabel("Level: ---");
        levelScoreLabel = createStatusLabel("Level Score: ---");
        totalScoreLabel = createStatusLabel("Total Score: ---");

        infoPanel.add(levelNameLabel);
        infoPanel.add(levelScoreLabel);
        infoPanel.add(totalScoreLabel);
        return infoPanel;
    }

    private JLabel createStatusLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(TEXT_COLOR);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        return label;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(PANEL_COLOR);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        JMenu gameMenu = new JMenu("Game");
        gameMenu.setForeground(TEXT_COLOR);
        gameMenu.setMnemonic(KeyEvent.VK_G);

        JMenuItem newItem   = new JMenuItem("New Game");
        JMenuItem resetItem = new JMenuItem("Reset Level");
        JMenuItem saveItem  = new JMenuItem("Save Game");
        JMenuItem loadItem  = new JMenuItem("Load Game");
        JMenuItem exitItem  = new JMenuItem("Exit");

        // Keyboard shortcuts (accelerators) shown next to the menu items
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        resetItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        loadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));

        for (JMenuItem item : new JMenuItem[]{newItem, resetItem, saveItem, loadItem, exitItem}) {
            item.setBackground(PANEL_COLOR);
            item.setForeground(TEXT_COLOR);
        }

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

        saveItem.addActionListener(e -> {
            if (controller != null) {
                controller.saveGame();
            }
        });

        loadItem.addActionListener(e -> {
            if (controller != null) {
                controller.loadGame();
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

        JButton undoButton = createUndoButton();

        menuBar.add(gameMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(undoButton);
        return menuBar;
    }

    // Creates a styled "Undo" button shown on the menu bar (also bound to Ctrl+Z)
    private JButton createUndoButton() {
        JButton undoButton = new JButton("Undo (Ctrl+Z)");
        undoButton.setFocusable(false);
        undoButton.setForeground(TEXT_COLOR);
        undoButton.setOpaque(true);
        undoButton.setBackground(ACCENT_COLOR);
        undoButton.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

        undoButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                undoButton.setBackground(ACCENT_COLOR.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                undoButton.setBackground(ACCENT_COLOR);
            }
        });

        undoButton.addActionListener(e -> {
            if (controller != null) {
                controller.undoMove();
            }
        });
        return undoButton;
    }

    // Registers application-wide keyboard shortcuts using key bindings on the root pane,
    // so they work no matter which component currently has focus.
    private void registerGlobalShortcuts() {
        JRootPane root = getRootPane();
        InputMap inputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = root.getActionMap();

        bindShortcut(inputMap, actionMap, KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK, "saveGame",
                () -> { if (controller != null) controller.saveGame(); });

        bindShortcut(inputMap, actionMap, KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK, "loadGame",
                () -> { if (controller != null) controller.loadGame(); });

        bindShortcut(inputMap, actionMap, KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK, "undoMove",
                () -> { if (controller != null) controller.undoMove(); });

        bindShortcut(inputMap, actionMap, KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK, "newGame",
                () -> { if (controller != null) controller.newGame(); });
    }

    private void bindShortcut(InputMap inputMap, ActionMap actionMap, int keyCode, int modifiers,
                               String actionName, Runnable action) {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, modifiers);
        inputMap.put(keyStroke, actionName);
        actionMap.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                action.run();
            }
        });
    }
}
