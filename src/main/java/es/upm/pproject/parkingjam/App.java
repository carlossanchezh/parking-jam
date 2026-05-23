package es.upm.pproject.parkingjam;

import javax.swing.SwingUtilities;

import es.upm.pproject.parkingjam.controller.GameController;
import es.upm.pproject.parkingjam.controller.GameControllerImpl;
import es.upm.pproject.parkingjam.model.dao.LevelDAO;
import es.upm.pproject.parkingjam.model.services.GameService;
import es.upm.pproject.parkingjam.model.services.GameServiceImpl;
import es.upm.pproject.parkingjam.view.MainView;

public class App {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create GUI
            MainView view = new MainView();

            // Instantiate LevelDAO (level loader)
            LevelDAO levelDAO = new LevelDAO();

            // Instantiate level service (business logic)
            GameService gameService = new GameServiceImpl(/*initialState*/);

            // Create Game controller (given the GUI, service and level loader)
            GameController controller = new GameControllerImpl(view, gameService, levelDAO);

            // Connect the view with the controller
            view.setController(controller);

            // Start a new game (from level 1)
            view.setVisible(true);
            controller.newGame();
        });
    }
    
}
