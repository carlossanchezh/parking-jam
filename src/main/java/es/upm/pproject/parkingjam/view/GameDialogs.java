package es.upm.pproject.parkingjam.view;

import javax.swing.JOptionPane;
import java.awt.Component;

public class GameDialogs {

    public static void showLevelError(Component parent, String levelName) {
        JOptionPane.showMessageDialog(parent,
                "Error: The level '" + levelName + "' has an incorrect format.\n" +
                "Attempting to load the next level.",
                "Format Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void showVictory(Component parent, int totalScore) {
        JOptionPane.showMessageDialog(parent,
                "Congratulations! You have completed all levels.\n" +
                "Final score (total moves): " + totalScore,
                "Victory!",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirmReset(Component parent) {
        int response = JOptionPane.showConfirmDialog(parent,
                "Are you sure you want to restart the current level?",
                "Restart Level",
                JOptionPane.YES_NO_OPTION);
        return response == JOptionPane.YES_OPTION;
    }
}