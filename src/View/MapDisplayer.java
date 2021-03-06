package View;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class MapDisplayer extends Canvas
{
    double[][] mapData;

    public void setMapData(double[][] mapData,double max)
    {
        this.mapData = mapData;

        for (int i=0;i<mapData.length;i++)
            for (int j=0;j<mapData[i].length;j++) {
                if(mapData[i][j]!=0)
                    mapData[i][j] = mapData[i][j]/(max);
                else
                    mapData[i][j]=0;
            }
        draw();
    }

    //redraw the map:
    public void draw()
    {
        if(mapData!=null)
        {
            double H=getHeight();
            double W=getWidth();
            double h=H/mapData.length;
            double w=W/mapData[0].length;
            GraphicsContext gc=getGraphicsContext2D();

            for (int i=0;i<mapData.length;i++)
                for (int j=0;j<mapData[i].length;j++)
                {
                    double temp=mapData[i][j];
                    if(temp!=0)
                        gc.setFill(Color.rgb((int) (161*temp),79, 2));
                    else
                        gc.setFill(Color.rgb(241,222, 101));
                    gc.fillRect(j*w,i*h,w,h);
                }
        }
    }
}
