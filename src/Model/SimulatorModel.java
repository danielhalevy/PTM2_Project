package Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import Model.interpreter.Interpreter;
import Model.interpreter.Utilities;
import Model.interpreter.Command.ConnectCommand;
import Model.interpreter.Command.DisconnectServerCommand;
import Model.interpreter.Command.OpenServerCommand;
import ViewModel.ViewModel;

public class SimulatorModel extends Observable implements Observer {

    public ViewModel vm;
    public Thread AutoPilot;
    private OpenServerCommand server;
    private DisconnectServerCommand disconnect;
    private ConnectCommand connect;
    public IntegerProperty connected;
    private static Socket socketPath;
    private  static PrintWriter outPath;
    private  static BufferedReader inPath;
    public static volatile boolean stop=false;
    double startX;
    double startY;
    double planeX;
    double planeY;
    double markX;
    double markY;
    int[][] data;
    double offset;

    public  SimulatorModel()
    {
        server=new OpenServerCommand();
        disconnect=new DisconnectServerCommand();
        connect =new ConnectCommand();
        connected= new SimpleIntegerProperty();
        socketPath=null;
        connected.bind(connect.isConnect);
    }

    public void setVm(ViewModel vm)
    {
        this.vm=vm;
    }

    public void airPlanePosition(double startX,double startY, double offset)
    {

        this.offset=offset;
        this.startX=startX;
        this.startY=startY;
        new Thread(() ->
        {
            Socket socket = null;
            try
            {
                socket = new Socket("127.0.0.1", 5402);
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (!stop)
                {
                    out.println("dump /position");
                    out.flush();
                    String line;
                    List<String> lines = new ArrayList<>();
                    while (!(line = br.readLine()).equals("</PropertyList>")){
                        if (!line.equals("")) lines.add(line);
                    }
                    String longitude = lines.get(2);
                    String latitude = lines.get(3);
                    String[] x = longitude.split("[<>]");
                    String[] y = latitude.split("[<>]");
                    br.readLine();
                    out.println("get /instrumentation/heading-indicator/indicated-heading-deg");
                    out.flush();
                    String[] h = br.readLine().split(" ");
                    int temp = h[3].length();
                    String[] data = { "plane", x[2], y[2], h[3].substring(1, temp - 1) };
                    this.setChanged();
                    this.notifyObservers(data);
                    try
                    {Thread.sleep(250);}
                    catch (InterruptedException e) {e.printStackTrace();}
                }
                socket.close();
            }
            catch (IOException e)
            {
                try{
                    assert socket != null;
                    socket.close();
                }catch (Exception ee) {}
            }
        }).start();
    }

    public void connectCP(String ip, int port)
    {
        try
        {
            socketPath = new Socket(ip, port);
            outPath = new PrintWriter(socketPath.getOutputStream());
            inPath = new BufferedReader(new InputStreamReader(socketPath.getInputStream()));
        } catch (Exception e) {}
    }

    public void findPath(int planeX, int planeY, int markX, int markY, int[][] data)
    {
        this.planeX = planeX;
        this.planeY = planeY;
        this.markX = markX;
        this.markY = markY;
        this.data = data;

        new Thread(() ->
        {
            int j=0;
            for ( int i = 0; i < data.length; i++)
            {
                System.out.print("");
                for (j = 0; j < data[i].length - 1; j++)
                    outPath.print(data[i][j] + ",");
                outPath.println(data[i][j]);
            }

            outPath.println("end");
            outPath.println(planeX + "," + planeY);
            outPath.println(markX + "," + markY);
            outPath.flush();
            String solution = null;

            try {
                solution = inPath.readLine();
            }catch (Exception e) {}

            System.out.println("The path is:");
            System.out.println(solution);
            assert solution != null;
            String[] temp = solution.split(",");

            String[] notify = new String[temp.length + 1];
            notify[0] = "path";
            for (int i = 0; i < temp.length; i++)
                notify[i + 1] = temp[i];

            this.setChanged();
            this.notifyObservers(notify);
        }).start();
    }

    //Main Connect
    public void connect(String ip, String port)
    {
        try{
            connect.doCommand(new String[]{"connect",ip,port});
            connected.setValue(1);
        }
        catch (Exception e) {e.printStackTrace();}
    }
    //get values from simulator
    public void openServer()
    {
        try{
            server.doCommand(new String[] {"opedDataServer","5400","10"});
        }
        catch (Exception e) {}
    }

    public void closeServer()
    {
        try{
            disconnect.doCommand(new String[]{""});
        }catch (Exception e) {}
    }
    //autopilot

    //send commands
    public void autoPilot(String[] lines)
    {
        AutoPilot= new Thread(() -> Interpreter.interpret(lines));
        AutoPilot.start();
    }

    public void offAutoPilot()
    {
        if(AutoPilot!=null && AutoPilot.isAlive())
            AutoPilot.interrupt();
        Utilities.resetVariablesTable();
    }
    //manual
    public void setThrottle(String throttle)
    {
        String[] t = { "set /controls/engines/current-engine/throttle " + throttle };
        ConnectCommand.Send(t);
    }

    public void setRudder(String rudder)
    {
        String[] r = { "set /controls/flight/rudder " + rudder };
        ConnectCommand.Send(r);
    }

    public void setDirection(String aileron, String elevator)
    {
        String[] data = { "set /controls/flight/aileron " + aileron, "set /controls/flight/elevator " + elevator };
        ConnectCommand.Send(data);
    }
    @Override
    public void update(Observable arg0, Object arg1) {}
}

