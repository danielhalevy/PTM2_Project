package View;

/*
  Sample Skeleton for 'MainWindow.fxml' Controller Class
 */

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;


import Model.SimulatorModel;
import ViewModel.ViewModel;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.util.Pair;

public class Controller implements Initializable, Observer {

    public ViewModel vm;
    public SimulatorModel sim_mod;
    //joystick
    public StringProperty ipJoystick, portJoystick;
    public IntegerProperty connected;
    public DoubleProperty aileronV, elevatorV;
    static double prevX, prevY;
    //Plane:
    public DoubleProperty airplaneX;
    public DoubleProperty airplaneY;
    public DoubleProperty startX;
    public DoubleProperty startY;
    public DoubleProperty markX, markY;
    public DoubleProperty offset;
    public DoubleProperty heading;
//    public DoubleProperty aileron;
//    public DoubleProperty elevator;

    //Map:
    public double mapData[][];
    private Image plane[];
    private Image mark;
    public double lastX;
    public double lastY;
    private String[] solution;
    private BooleanProperty path;
    public BooleanProperty isConnectedToSimulator;
    private boolean mapOn = false;
    public StringProperty ipPath;
    public StringProperty portPath;
    public boolean ConnectedCalcPath = false;


    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML
    private Canvas Airplane;

    @FXML
    private Canvas Mark;

    @FXML
    private MapDisplayer Map;

    @FXML // fx:id="RudderSlider"
    private Slider RudderSlider; // Value injected by FXMLLoader

    @FXML // fx:id="ThrottleSlider"
    private Slider ThrottleSlider; // Value injected by FXMLLoader

    @FXML // fx:id="OuterJoystick"
    private Circle OuterJoystick; // Value injected by FXMLLoader

    @FXML
    private Circle Around;

    @FXML // fx:id="InnerJoystick"
    private Circle InnerJoystick; // Value injected by FXMLLoader

    @FXML // fx:id="AutoPilotButton"
    private RadioButton AutoPilotButton; // Value injected by FXMLLoader

    @FXML // fx:id="ManualButton"
    private RadioButton ManualButton; // Value injected by FXMLLoader

    @FXML // fx:id="LoadButton"
    private Button LoadButton; // Value injected by FXMLLoader

    @FXML // fx:id="ExecuteButton"
    private Button ExecuteButton; // Value injected by FXMLLoader

    @FXML // fx:id="ConnectButton"
    private Button ConnectButton; // Value injected by FXMLLoader

    @FXML // fx:id="LoadMapButton"
    private Button LoadMapButton; // Value injected by FXMLLoader

    @FXML // fx:id="CalculatePathButton"
    private Button CalculatePathButton; // Value injected by FXMLLoader

    @FXML // fx:id="TextArea"
    private TextArea TextArea; // Value injected by FXMLLoader

    @FXML
    private Label ShowLabel;

    public Controller() {
        sim_mod = new SimulatorModel();
        vm = new ViewModel(sim_mod);
        ipJoystick = new SimpleStringProperty();
        portJoystick = new SimpleStringProperty();
        aileronV = new SimpleDoubleProperty();
        elevatorV = new SimpleDoubleProperty();
        Map = new MapDisplayer();
        startX = new SimpleDoubleProperty();
        startY = new SimpleDoubleProperty();
        offset = new SimpleDoubleProperty();
        connected = new SimpleIntegerProperty();
        ShowLabel = new Label("");
        offset = new SimpleDoubleProperty();
        heading = new SimpleDoubleProperty();
        markX = new SimpleDoubleProperty();
        markY = new SimpleDoubleProperty();
        path = new SimpleBooleanProperty();
        airplaneX = new SimpleDoubleProperty();
        airplaneY = new SimpleDoubleProperty();
        startX = new SimpleDoubleProperty();
        startY = new SimpleDoubleProperty();
        isConnectedToSimulator = new SimpleBooleanProperty();
        ipPath = new SimpleStringProperty();
        portPath = new SimpleStringProperty();

        plane = new Image[8];
        try {
            plane[0] = new Image(new FileInputStream("./resources/plane1.png"));
            plane[1] = new Image(new FileInputStream("./resources/plane2.png"));
            plane[2] = new Image(new FileInputStream("./resources/plane3.png"));
            plane[3] = new Image(new FileInputStream("./resources/plane4.png"));
            plane[4] = new Image(new FileInputStream("./resources/plane5.png"));
            plane[5] = new Image(new FileInputStream("./resources/plane6.png"));
            plane[6] = new Image(new FileInputStream("./resources/plane7.png"));
            plane[7] = new Image(new FileInputStream("./resources/plane8.png"));
            mark = new Image(new FileInputStream("./resources/mark.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setVM(ViewModel vm) {
        this.vm = vm;
        ThrottleSlider.valueProperty().bindBidirectional(vm.throttle);
        RudderSlider.valueProperty().bindBidirectional(vm.rudder);
        ipJoystick.bindBidirectional(vm.ipJoystick);
        portJoystick.bindBidirectional(vm.portJoystick);
        aileronV.bindBidirectional(vm.aileronV);
        elevatorV.bindBidirectional(vm.elevatorV);
        elevatorV.bindBidirectional(vm.elevatorV);
        connected.bind(vm.connected);
        airplaneX.bindBidirectional(vm.airplaneX);
        airplaneY.bindBidirectional(vm.airplaneY);
        startX.bindBidirectional(vm.startX);
        startY.bindBidirectional(vm.startY);
        offset.bindBidirectional(vm.offset);
        heading.bindBidirectional(vm.heading);
        markY.bindBidirectional(vm.markY);
        markX.bindBidirectional(vm.markX);
        path.bindBidirectional(vm.path);
        path.setValue(false);
        isConnectedToSimulator.set(false);
        ipPath.bindBidirectional(vm.ipPath);
        portPath.bindBidirectional(vm.portPath);
    }

    @Override
    public void update(Observable o, Object args) {
        if (o == vm) {
            if (args == null)
                drawAirplane();
            else
            {
                solution = (String[]) args;
                this.drawLine();
            }
        }
    }


    @FXML
    void Autopilot(ActionEvent event) {
        ShowLabel.setText("");
        if (ManualButton.isSelected())
            ManualButton.setSelected(false);
        AutoPilotButton.setSelected(true);
        ShowLabel.setText("Autopilot is selected successfully!\nGood for everyone in the plane, am I right?");
    }

    @FXML
    void Calculate_Path(ActionEvent event) {
        if(connected.getValue()==0)
            ShowLabel.setText("Gotta connect first");
        else {
            ShowLabel.setText("");
            String[] ipNport = dialogCreator("Lets connect to the path calculator", "Gotta enter your PORT & IP", "127.0.0.1", "6400");
            ipPath.setValue(ipNport[0]);
            portPath.setValue(ipNport[1]);
            if (ipPath.getValue() != null && portPath.getValue() != null) {
                vm.findPath(Mark.getHeight() / mapData.length, Mark.getWidth() / mapData[0].length);
                path.setValue(true);
            }
        }
    }

    public void drawAirplane() {
        if(connected.getValue()==1) {
            if (airplaneX.getValue() != null && airplaneY.getValue() != null) {
                double height = Airplane.getHeight() / mapData.length;
                double width = Airplane.getWidth() / mapData[0].length;
                GraphicsContext gc = Airplane.getGraphicsContext2D();
                lastX = airplaneX.getValue();
                lastY = airplaneY.getValue() * -1;
                gc.clearRect(0, 0, Airplane.getWidth(), Airplane.getHeight());

                if (heading.getValue() >= 0 && heading.getValue() < 39)
                    gc.drawImage(plane[0], width * lastX, lastY * height, 30, 30);
                if (heading.getValue() >= 39 && heading.getValue() < 80)
                    gc.drawImage(plane[1], width * lastX, lastY * height, 30, 30);
                if (heading.getValue() >= 80 && heading.getValue() < 129)
                    gc.drawImage(plane[2], width * lastX, lastY * height, 30, 30);
                if (heading.getValue() >= 129 && heading.getValue() < 170)
                    gc.drawImage(plane[3], width * lastX, lastY * height, 30, 30);
                if (heading.getValue() >= 170 && heading.getValue() < 219)
                    gc.drawImage(plane[4], width * lastX, lastY * height, 30, 30);
                if (heading.getValue() >= 219 && heading.getValue() < 260)
                    gc.drawImage(plane[5], width * lastX, lastY * height, 30, 30);
                if (heading.getValue() >= 260 && heading.getValue() < 309)
                    gc.drawImage(plane[6], width * lastX, lastY * height, 30, 30);
                if (heading.getValue() >= 309)
                    gc.drawImage(plane[7], width * lastX, lastY * height, 30, 30);
            }
        }
    }

    public void markPressed(MouseEvent mouseEvent) {
        if (mapOn) {
            markX.setValue(mouseEvent.getX());
            markY.setValue(mouseEvent.getY());
            GraphicsContext gc = Mark.getGraphicsContext2D();
            gc.clearRect(0, 0, Mark.getWidth(), Mark.getHeight());
            gc.drawImage(mark, markX.getValue() - 4, markY.getValue() - 4, 15, 15);
            if (path.getValue())
                vm.findPath(Mark.getHeight() / mapData.length, Mark.getWidth()/ mapData[0].length);
        } else
            ShowLabel.setText("Sure.. press on nothing...\nor maybe load a map!");
    }

    public void drawLine() {
        double h = Mark.getHeight() / mapData.length;
        double w = Mark.getWidth() / mapData[0].length;
        GraphicsContext gc = Mark.getGraphicsContext2D();

        String move = solution[1];
        double x = airplaneX.getValue() * w + 10 * w;
        double y = airplaneY.getValue() * -h + 6 * h;
        for (int i = 2; i < solution.length; i++) {
            switch (move) {
                case "Right":
                    gc.setStroke(null);
                    gc.strokeLine(x, y, x + w, y);
                    x += w;
                    break;
                case "Left":
                    gc.setStroke(null);
                    gc.strokeLine(x, y, x - w, y);
                    x -= w;
                    break;
                case "Up":
                    gc.setStroke(null);
                    gc.strokeLine(x, y, x, y - h);
                    y -= h;
                    break;
                case "Down":
                    gc.setStroke(null);
                    gc.strokeLine(x, y, x, y + h);
                    y += h;
            }
            move = solution[i];
        }
    }

    @FXML
    void Connect(ActionEvent event) {
        ShowLabel.setText("");
        String[] ipNport = dialogCreator("Lets connect to the simulator", "Gotta enter your PORT & IP", "127.0.0.1", "5402");

        this.ipJoystick.setValue(ipNport[0]);
        this.portJoystick.setValue(ipNport[1]);
        if (ipJoystick.getValue() != null && portJoystick.getValue() != null)
            vm.connect();
        if (connected.getValue() == 1)
            ShowLabel.setText("Connected");
        else
            ShowLabel.setText("oops");
    }

    @FXML
    void Load(ActionEvent event) {
        ShowLabel.setText("");
        if(connected.getValue()==0)
            ShowLabel.setText("Gotta connect first");
        else if (!(AutoPilotButton.isSelected())) {
            ShowLabel.setText("Dude, select autopilot...");
        } else {
            File theChosenOne = fileC("Load text file", "./resources", "Text Files", "*.txt");
            try {
                if (theChosenOne != null) {
                    TextArea.clear();
                    BufferedReader file = new BufferedReader(new FileReader(theChosenOne.getPath()));
                    Scanner scan = new Scanner(file);
                    while (scan.hasNextLine())
                        TextArea.appendText(scan.nextLine() + "\n");
                    scan.close();
                    file.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    @FXML
    void Execute(ActionEvent event) {
        ShowLabel.setText("");
        if (!TextArea.getText().isEmpty() && AutoPilotButton.isSelected() && connected.getValue() == 1) {
            Scanner scan = new Scanner(TextArea.getText());
            int countLines = 0;
            while (scan.hasNext()) {
                scan.nextLine();
                countLines++;
            }
            scan.close();
            Scanner scan2 = new Scanner(TextArea.getText());
            String[] lines = new String[countLines];
            for (int i = 0; scan2.hasNextLine(); i++)
                lines[i] = scan2.nextLine();
            scan2.close();
            this.vm.AutoPilot(lines);
        } else if (!AutoPilotButton.isSelected())
            ShowLabel.setText("Ummmmm.... how about you select\nautopilot first?... ");
        else if (connected.getValue() == 0)
            ShowLabel.setText("What about connecting?");
        else if (TextArea.getText().isEmpty())
            ShowLabel.setText("Am I supposed to execute nothing?!?");
    }

    @FXML
    void Load_Map(ActionEvent event) {
        ShowLabel.setText("");
        if(connected.getValue()==0)
            ShowLabel.setText("Gotta connect first");
        else {
            File selectFile = fileC("Load map", "./resources", "CSV files (*.csv)", "*.csv");
            if (selectFile != null) {
                BufferedReader br = null;
                String splitter = ",";
                List<String[]> data = new ArrayList<>();
                try {
                    br = new BufferedReader(new FileReader(selectFile));
                    String[] start = br.readLine().split(splitter);
                    startX.setValue(Double.parseDouble(start[0]));
                    startY.setValue(Double.parseDouble(start[1]));
                    start = br.readLine().split(splitter);
                    offset.setValue(Double.parseDouble(start[0]));
                    String Nline;
                    while ((Nline = br.readLine()) != null)
                        data.add(Nline.split(splitter));
                    mapData = new double[data.size()][];
                    double max = 0;
                    for (int i = 0; i < data.size(); i++) {
                        mapData[i] = new double[data.get(i).length];
                        for (int j = 0; j < data.get(i).length; j++) {
                            String tmp = data.get(i)[j];
                            mapData[i][j] = Double.parseDouble(tmp);
                            if (max < mapData[i][j])
                                max = mapData[i][j];
                        }
                    }
                    vm.setMap(mapData);
                    drawAirplane();
                    if (!mapOn)
                        Map.setMapData(mapData, max);
                    mapOn = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    void Manual(ActionEvent event) {
        if (AutoPilotButton.isSelected())
            AutoPilotButton.setSelected(false);
        ManualButton.setSelected(true);
        vm.offAutoPilot();
        ShowLabel.setText("Connected to manual.\nProceed at your own risk");
    }


    public void JoystickDragged(MouseEvent mouseEvent) {
        if (connected.getValue() == 0)
            ShowLabel.setText("Gotta connect first");
        else if (!(ManualButton.isSelected())) {
            ShowLabel.setText("Gotta select Manual first, forgot that?");
        } else {
            if (Around.contains(mouseEvent.getX(), mouseEvent.getY())) {
                prevX = mouseEvent.getX();
                prevY = mouseEvent.getY();
            }
            InnerJoystick.setCenterX(prevX);
            InnerJoystick.setCenterY(prevY);
            aileronV.setValue(prevX / 100);
            elevatorV.setValue(prevY / 100);
            vm.setJoystick();
        }


    }

    public void JoystickPressed(MouseEvent mouseEvent) {
        ShowLabel.setText("");
    }

    public void JoystickReleased(MouseEvent mouseEvent) {
            InnerJoystick.setCenterX(0);
            InnerJoystick.setCenterY(0);
            aileronV.setValue(0);
            elevatorV.setValue(0);
            vm.setJoystick();
    }

    public File fileC(String title, String resource, String desc, String ext) {
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        fc.setInitialDirectory(new File(resource));
        FileChooser.ExtensionFilter fileExtensions = new FileChooser.ExtensionFilter(desc, ext);
        fc.getExtensionFilters().add(fileExtensions);
        return fc.showOpenDialog(null);
    }

    public String[] dialogCreator(String title, String header, String ip, String port) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        ButtonType SubmitButtonType = new ButtonType("Enter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(SubmitButtonType, ButtonType.CANCEL);

        // Create the IP and PORT labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10));

        TextField Nip = new TextField();
        Nip.setPromptText(ip);
        TextField Nport = new TextField();
        Nport.setPromptText(port);

        grid.add(new Label("IP:"), 0, 0);
        grid.add(Nip, 1, 0);
        grid.add(new Label("PORT:"), 0, 1);
        grid.add(Nport, 1, 1);
        Node ipButton = dialog.getDialogPane().lookupButton(SubmitButtonType);
        ipButton.setDisable(true);
        Nport.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!Nip.getText().isEmpty())
                ipButton.setDisable(newValue.trim().isEmpty());
        });
        Nip.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!Nport.getText().isEmpty())
                ipButton.setDisable(newValue.trim().isEmpty());
        });
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == SubmitButtonType) {
                return new Pair<>(Nip.getText(), Nport.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        String[] ipNport = new String[2];
        result.ifPresent(ipPort -> {
            ipNport[0] = ipPort.getKey();
            ipNport[1] = ipPort.getValue();
        });
        return ipNport;
    }

    @FXML
    // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL location, ResourceBundle resources) {
        assert RudderSlider != null : "fx:id=\"RudderSlider\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert ThrottleSlider != null : "fx:id=\"ThrottleSlider\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert OuterJoystick != null : "fx:id=\"OuterJoystick\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert InnerJoystick != null : "fx:id=\"InnerJoystick\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert AutoPilotButton != null : "fx:id=\"AutoPilotButton\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert ManualButton != null : "fx:id=\"ManualButton\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert LoadButton != null : "fx:id=\"LoadButton\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert ExecuteButton != null : "fx:id=\"ExecuteButton\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert ConnectButton != null : "fx:id=\"ConnectButton\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert LoadMapButton != null : "fx:id=\"LoadMapButton\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert CalculatePathButton != null : "fx:id=\"CalculatePathButton\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert TextArea != null : "fx:id=\"TextArea\" was not injected: check your FXML file 'MainWindow.fxml'.";

        RudderSlider.valueProperty().addListener((ChangeListener<Object>) (arg0, arg1, arg2) ->
        {
            //rudderLabel.textProperty().setValue("Rudder: " + (Math.round((rudder.getValue() * 10.00))) / 10.00);
            if (ManualButton.isSelected() && connected.getValue() == 1) {
                vm.setRudder();
            }
        });
        ThrottleSlider.valueProperty().addListener((ChangeListener<Object>) (arg0, arg1, arg2) ->
        {
            // throttleLabel.textProperty().setValue("Throttle: " + (Math.round((throttle.getValue() * 10.00))) / 10.00);
            if (ManualButton.isSelected() && connected.getValue() == 1) {
                vm.setThrottle();
            }
        });
        //Mark.setOnMouseClicked(mapClick);
    }
}

