package es.upm.pproject.parkingjam;

import javax.swing.SwingUtilities;

import es.upm.pproject.parkingjam.controller.GameController;
import es.upm.pproject.parkingjam.controller.GameControllerImpl;
import es.upm.pproject.parkingjam.model.dao.LevelDAO;
import es.upm.pproject.parkingjam.model.services.GameService;
import es.upm.pproject.parkingjam.model.services.GameServiceImpl;
import es.upm.pproject.parkingjam.view.MainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        logger.info("Parking Jam application starting...");
        SwingUtilities.invokeLater(() -> {
            // Create GUI
            logger.debug("Initializing GUI...");
            MainView view = new MainView();

            // Instantiate LevelDAO (level loader)
            logger.debug("Initializing LevelDAO...");
            LevelDAO levelDAO = new LevelDAO();

            // Instantiate level service (business logic)
            logger.debug("Initializing GameService...");
            GameService gameService = new GameServiceImpl();

            // Create Game controller (given the GUI, service and level loader)
            logger.debug("Initializing GameController...");
            GameController controller = new GameControllerImpl(view, gameService, levelDAO);

            // Connect the view with the controller
            view.setController(controller);

            // Start a new game (from level 1)
            logger.info("Starting new game...");
            view.setVisible(true);
            controller.newGame();
        });
    }
    
}
