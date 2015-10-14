import com.marasm.VEGA.VEGA;
import com.marasm.VEGA.VEGA_Display;
import com.marasm.ppc.CTRL;
import com.marasm.ppc.PPC;
import com.marasm.ppc.Variable;

/**
 * Created by sr3u on 08.10.2015.
 */

public class Main
{
    static public void main(String args[])
    {
        VEGA_Display dp=new VEGA_Display();
        VEGA d=new VEGA();
        d.connected();
        PPC.out(new Variable(d.ctrlPort),new Variable(32));
        PPC.out(new Variable(d.ctrlPort),CTRL.GETMAN);
        Variable v=new Variable("-1");
        int i=0;
        while (!v.equals(new Variable(0)))
        {
            v=PPC.in(new Variable(d.ctrlPort));
            if(v.intValue()!=0){
            System.out.print((char)v.intValue());}
        }
        System.out.println();
        PPC.out(new Variable(d.ctrlPort),new Variable(VEGA.setcolor));
        PPC.out(new Variable(d.dataPort),new Variable(0x0000FF));
        PPC.out(new Variable(d.ctrlPort),new Variable(VEGA.putpixel));
       for(int y=0;y<255;y++)
       {
           for(int x=0;x<255;x++)
           {
               PPC.out(new Variable(d.ctrlPort),new Variable(VEGA.putpixel));
               PPC.out(new Variable(d.dataPort),new Variable(x));
               PPC.out(new Variable(d.dataPort),new Variable(y));
           }
       }
    }
}
