package com.marasm.VEGA;

import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.*;
import java.lang.reflect.Method;

/**
 * Created by sr3u on 12.10.2015.
 */
public class VEGA_GUI
{
    public VEGA_Display screen;
    public VEGA_GUI()
    {
        String jsonLoc=jarLocation() + "VEGA.json";
        jsonLoc=jsonLoc.trim();
        jsonLoc=jsonLoc.replaceAll("[%]20"," ");
        System.out.println(jsonLoc);
        FileReader jsonReader;
        JSONObject config=new JSONObject(defaultSettings);
        try {
            jsonReader = new FileReader(jsonLoc);
            config=new JSONObject(readAsString(jsonReader));
        } catch (IOException | JSONException e) {
            try {
                PrintWriter fw = new PrintWriter(jsonLoc, "UTF8");
                fw.println(defaultSettings);
                fw.close();
                fw.flush();
                config=new JSONObject(defaultSettings);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        int w=320,h=200;
        try {
            w=config.getInt("width");
            h=config.getInt("height");
        }catch (JSONException e){
            try {
                PrintWriter fw = new PrintWriter(jsonLoc, "UTF8");
                fw.println(defaultSettings);
                fw.close();
                fw.flush();
                config=new JSONObject(defaultSettings);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        screen = new VEGA_Display(w,h);
        JFrame frame = new JFrame("VEGA Display");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        enableFullScreenMode(frame);
        frame.add(screen);
        frame.setSize(w, h);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        screen.setVisible(true);
        /*
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                //Component c = (Component)evt.getSource();
                //Dimension d=c.getSize();
                //screen.setSize(d.width,d.height);
            }
        });*/
    }

    private String readAsString(FileReader r) throws IOException {
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = new BufferedReader(r);
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
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
    static final String defaultSettings="{\n" +
            "\t\"width\":320,\n" +
            "\t\"height\":240\n" +
            "}";
    public String jarLocation()
    {
        String path=this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        if(path.endsWith("!/")){path=path.substring(0,path.length()-2);}
        String fileName=path.substring(path.lastIndexOf(File.separatorChar) + 1);
        if(fileName.contains(".jar")){path=path.substring(0, path.lastIndexOf(File.separatorChar)+1).trim();}
        if(path.startsWith("file:")){path=path.substring(5);}
        return path.trim();
    }
    private String readAsString(Reader r) throws IOException {
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = new BufferedReader(r);
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }
}
