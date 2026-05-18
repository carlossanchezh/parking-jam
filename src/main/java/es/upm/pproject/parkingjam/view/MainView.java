package es.upm.pproject.parkingjam.view;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {
    private JLabel levelNameLabel, levelScoreLabel, totalScoreLabel;
    private BoardPanel boardPanel;
    public MainView(){
        setTitle("Parkin Jam");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel infoPanel = new JPanel(new GridLayout(1, 3));
        levelNameLabel = new JLabel("Level: ---", SwingConstants.CENTER);
        levelScoreLabel = new JLabel("Level Score: ---", SwingConstants.CENTER);
        totalScoreLabel = new JLabel("Total Score: ---", SwingConstants.CENTER);

        infoPanel.add(levelNameLabel);
        infoPanel.add(levelScoreLabel);
        infoPanel.add(totalScoreLabel);
        add(infoPanel, BorderLayout.NORTH);
        
        boardPanel = new BoardPanel();
        add(boardPanel, BorderLayout.CENTER);

        setJMenuBar(createMenuBar());
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");

        JMenuItem newItem = new JMenuItem("New Game");
        JMenuItem resetItem = new JMenuItem("Reset Level");
        JButton undoItem = new JButton("Undo");
        JButton redoItem = new JButton("Redo");

        JMenuItem saveItem = new JMenuItem("Save game");
        JMenuItem loadItem = new JMenuItem("Load game");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        resetItem.addActionListener(e -> {
            if (GameDialogs.confirmReset(this)) {
                System.out.println("Restarting level...");
                // Aquí irá la llamada al controlador
            }
        });

        exitItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "¿Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        
        saveItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                System.out.println("Saving to: " + fileChooser.getSelectedFile().getPath());
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
        menuBar.add(undoItem);
        menuBar.add(redoItem);

        return menuBar;
    }
    
    public BoardPanel getBoardPanel() {
        return boardPanel;
    }
    public void updateStatus(String levelName, int levelScore, int totalScore) {
        levelNameLabel.setText("Level: " + levelName);
        levelScoreLabel.setText("Level Score: " + levelScore);
        totalScoreLabel.setText("Total Score: " + totalScore);
    }
}