import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

public class GameFrame extends JFrame implements WindowListener {
    GameCanvas canvas;
    GameFrame() {
        // frame description
        super("Q");
        // our Canvas
        canvas = new GameCanvas();
        add(canvas, BorderLayout.CENTER);
        // set it's size and make it visible
        setSize(800, 800);
        // have to know when I will be stop by user to stop the thread
        addWindowListener(this);
        // show us
        setVisible(true);       
        // now that is visible we can tell it that we will use 2 buffers to do the repaint
        // before being able to do that, the Canvas as to be visible
        canvas.createBufferStrategy(2);
        // start the thread that upgrades balls position
        IsKeyPressed.init();
        Thread thread = new Thread(canvas);
        thread.start();
    }
    // just to start the application
    public static void main(String[] args) {
        // instance of our stuff
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GameFrame();
            }
        });
    }
    
    //my windows listeners
    // we need at least that one to stop the threads performing the calculation and doing the repaint
    public void windowClosing(WindowEvent e) {
        canvas.threadStop = true;
        dispose();
    }
    public void windowActivated(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}  
}

