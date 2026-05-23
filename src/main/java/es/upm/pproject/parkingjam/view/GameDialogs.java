package es.upm.pproject.parkingjam.view;

import javax.swing.JOptionPane;
import java.awt.Component;

public class GameDialogs {

    private GameDialogs() {
        // This utility class will not be instantiated
    }

    public static void showLevelError(Component parent, String levelName) {
        JOptionPane.showMessageDialog(parent,
                "Error: The level '" + levelName + "' has an incorrect format.\n" +
                "Attempting to load the next level.",
                "Format Error",
                JOptionPane.ERROR_MESSAGE);
    }
    public static void showLevelVictory(Component parent, String levelName, int levelScore, int totalScore) {
        JOptionPane.showMessageDialog(parent,
                "Congratulations! You completed '" + levelName + "'.\n" +
                "Level score : " + levelScore + "\n" +
                "Total score : " + totalScore,
                "Level Completed!",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showVictory(Component parent, int totalScore) {
        JOptionPane.showMessageDialog(parent,
                "Congratulations! You have completed all levels.\n" +
                "Final score (total moves): " + totalScore,
                "Victory!",
                JOptionPane.INFORMATION_MESSAGE);
    }

}