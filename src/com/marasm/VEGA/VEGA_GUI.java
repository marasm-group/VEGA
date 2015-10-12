package com.marasm.VEGA;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.reflect.Method;

/**
 * Created by sr3u on 12.10.2015.
 */
public class VEGA_GUI
{
    VEGA_Display screen;
    public VEGA_GUI()
    {
        screen = new VEGA_Display(800,600);
        JFrame frame = new JFrame("VEGA Display");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        enableFullScreenMode(frame);
        frame.add(screen);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        screen.setVisible(true);
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                Component c = (Component)evt.getSource();
                Dimension d=c.getSize();
                screen.setSize(d.width,d.height);
            }
        });
    }
    static void enableFullScreenMode(Window window) {
        if(isMacOSX()) {
            String className = "com.apple.eawt.FullScreenUtilities";
            String methodName = "setWindowCanFullScreen";
            try {
                Class t = Class.forName(className);
                Method method = t.getMethod(methodName, new Class[]{Window.class, Boolean.TYPE});
                method.invoke((Object)null, new Object[]{window, Boolean.valueOf(true)});
            } catch (Throwable var5) {
                System.err.println("Full screen mode is not supported");
                var5.printStackTrace();
            }

        }
    }
    private static boolean isMacOSX() {
        return System.getProperty("os.name").indexOf("Mac OS X") >= 0;
    }
}
