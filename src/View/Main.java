package View;

import Model.Server.*;
import Model.SimulatorModel;
import ViewModel.ViewModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    SimulatorModel sim_mod;
    ViewModel vm;

    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            /*for ide
            Server server = new MySerialServer();
            CacheManager cacheManager = new FileCacheManager();
            MyClientHandler clientHandler = new MyClientHandler(cacheManager);
            server.open(6400 ,new ClientHandlerPath(clientHandler));
            */
            sim_mod=new SimulatorModel();
            vm=new ViewModel(sim_mod);
            sim_mod.setVm(vm);
            vm.addObserver(sim_mod);
            sim_mod.addObserver(vm);
            FXMLLoader fxml = new FXMLLoader();
            Parent root =fxml.load(getClass().getResource("MainWindow.fxml").openStream());
            Controller mwc = fxml.getController();//load the fxml file.
            mwc.setVM(vm);
            vm.addObserver(mwc);
            primaryStage.setTitle("Daniel and Natan's flight awesome controller");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            vm.openServer();

            primaryStage.setOnCloseRequest(event -> {
                vm.closeServer();
                System.out.println("Bye Bye! :)");
            });
        }
        catch(Exception e) {}
    }


    public static void main(String[] args) {
        launch(args);
        Platform.exit();
        System.exit(0);
    }
}
