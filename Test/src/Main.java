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
            System.out.print((char)v.intValue());
        }PPC.out(new Variable(d.ctrlPort),new Variable(0));
        System.out.println();
    }
}
