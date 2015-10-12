package com.marasm.VEGA;

import com.marasm.ppc.PPC;
import com.marasm.ppc.PPCDevice;
import com.marasm.ppc.Variable;
import java.io.File;

/**
 * Created by sr3u on 08.10.2015.
 */

public class VEGA extends PPCDevice
{
    public final String ctrlPort="127.0";
    public final String dataPort="127.2";
    private VEGA_GUI gui;

    public String jarLocation()
    {
        String path=this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        if(path.endsWith("!/")){path=path.substring(0,path.length()-2);}
        String fileName=path.substring(path.lastIndexOf(File.separatorChar) + 1);
        if(fileName.contains(".jar")){path=path.substring(0, path.lastIndexOf(File.separatorChar)+1).trim();}
        if(path.startsWith("file:")){path=path.substring(5);}
        return path.trim();
    }
    @Override public String manufacturer(){return "marasm";}
    public void connected()
    {
        PPC.connect(new Variable(ctrlPort), this);
        PPC.connect(new Variable(dataPort), this);
        gui=new VEGA_GUI();
    }
    @Override
    public Variable in(Variable port)
    {
        switch (port.toString())
        {
            case ctrlPort:
                return ctrlIn();
            case dataPort:
                return new Variable();
        }
        return new Variable();
    }
    @Override
    public void out(Variable port,Variable data)
    {
        switch (port.toString())
        {
            case ctrlPort:
                ctrlOut(data);return;
            case dataPort:
                return;
        }
    }
}
