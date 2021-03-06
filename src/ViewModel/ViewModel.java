package ViewModel;

import java.util.Observable;
import java.util.Observer;
import Model.SimulatorModel;
import javafx.beans.property.*;

public class ViewModel extends Observable implements Observer
{
	public SimulatorModel sim_mod;
	public StringProperty ipJoystick, portJoystick;
	public IntegerProperty connected;
	public DoubleProperty throttle;
	public DoubleProperty rudder;
	public DoubleProperty aileronV;
	public DoubleProperty elevatorV;
	public DoubleProperty airplaneX;
	public DoubleProperty airplaneY;
	public DoubleProperty heading;
	public StringProperty ipPath;
	public StringProperty portPath;
	public DoubleProperty startX;
	public DoubleProperty startY;
	public DoubleProperty offset;
	public DoubleProperty markX, markY;
	public BooleanProperty path;
	private double[][] Map;

	public ViewModel(SimulatorModel sim_mod)
	{
		this.sim_mod = sim_mod;
		this.ipJoystick=new SimpleStringProperty();
		this.portJoystick=new SimpleStringProperty();
		this.connected=new SimpleIntegerProperty();
		this.throttle=new SimpleDoubleProperty();
		this.rudder=new SimpleDoubleProperty();
		this.aileronV = new SimpleDoubleProperty();
		this.elevatorV = new SimpleDoubleProperty();
		this.airplaneX=new SimpleDoubleProperty();
		this.airplaneY=new SimpleDoubleProperty();
		this.heading=new SimpleDoubleProperty();
		this.ipPath=new SimpleStringProperty();
		this.portPath=new SimpleStringProperty();
		this.startX=new SimpleDoubleProperty();
		this.startY=new SimpleDoubleProperty();
		this.offset=new SimpleDoubleProperty();
		this.markX=new SimpleDoubleProperty();
		this.markY=new SimpleDoubleProperty();
		this.path=new SimpleBooleanProperty();
		this.connected.bind(sim_mod.connected);
	}

	public void setMap(double[][] data)
	{
		this.Map = data;
		sim_mod.airPlanePosition(startX.getValue(), startY.doubleValue(), offset.getValue());
	}

	public void findPath(double h, double w)
	{
		if (!path.getValue())
			sim_mod.connectCP(ipPath.getValue(), Integer.parseInt(portPath.getValue()));
		int[][] parsed=new int[Map.length][];
		for (int i = 0; i < Map.length; i++) {
			parsed[i]=new int[Map[i].length];
			for (int j = 0; j < Map[0].length; j++) {
				parsed[i][j]= (int) Map[i][j];
			}
		}

		sim_mod.findPath((int) (airplaneY.getValue() / -1), (int) (airplaneX.getValue() + 15),
				Math.abs((int) (markY.getValue() / h)), Math.abs((int) (markX.getValue() / w)), parsed);
	}

    public void connect()
    {
    	sim_mod.connect(ipJoystick.get(), portJoystick.get());
    }

    public void openServer()
    {
		sim_mod.openServer();
	}

	public void closeServer()
	{
		sim_mod.closeServer();
	}

	//joystick
    public void setThrottle()
    {
    	sim_mod.setThrottle(throttle.getValue().toString());
    }

    public void setRudder()
    {
    	sim_mod.setRudder(rudder.getValue().toString());
    }

    public void setJoystick()
    {
    	sim_mod.setDirection(aileronV.getValue().toString(),elevatorV.getValue().toString());
    }

    public void AutoPilot(String[] lines)
    {
    	sim_mod.autoPilot(lines);
    }

    public void offAutoPilot()
    {
    	sim_mod.offAutoPilot();
    }

	@Override
	public void update(Observable o, Object args) {

		if(o==sim_mod)
		{
			 String[] arr=(String[])args;
	            if(arr[0].equals("plane"))
	            {
	                double x = Double.parseDouble(arr[1]);
	                double y = Double.parseDouble(arr[2]);
	                x = (x - startX.getValue() + offset.getValue());
	                x /= offset.getValue();
	                y = (y - startY.getValue() + offset.getValue()) / offset.getValue();
	                airplaneX.setValue(x);
	                airplaneY.setValue(y);
	                heading.setValue(Double.parseDouble(arr[3]));
	                setChanged();
	                notifyObservers();
	            }
	            else if(arr[0].equals("path"))
	            {
	                setChanged();
	                notifyObservers(arr);
	            }
		}
	}
}