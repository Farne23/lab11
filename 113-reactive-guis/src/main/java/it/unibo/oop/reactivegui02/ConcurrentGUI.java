package it.unibo.oop.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public final class ConcurrentGUI extends JFrame {  

    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("stop");
    private final JButton up= new JButton("up");
    private final JButton down = new JButton("down");

    public ConcurrentGUI(){
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int)(screenSize.getWidth()*WIDTH_PERC),(int)(screenSize.getHeight()*HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);  
        this.setVisible(true); 

        Agent agent = new Agent();
        new Thread(agent).start();

        stop.addActionListener((e)-> agent.stop());
        up.addActionListener((e)-> agent.up());
        down.addActionListener((e)-> agent.down());

    }

    public class Agent implements Runnable{

        public int counter;
        private volatile boolean stop;
        private volatile boolean decrementing;

        @Override
        public void run() {
            while(!this.stop){
                try {
                    final String nextText = Integer.toString(counter);
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextText));
                    if(decrementing == true){
                        counter--;
                    }else{
                        counter++;
                    }
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        public Object down() {
            return decrementing=true;
        }

        public Object up() {
            return decrementing=false;
        }

        public void stop(){
            this.stop = true;
            SwingUtilities.invokeLater(()->ConcurrentGUI.this.stop.setEnabled(false));
            SwingUtilities.invokeLater(()->ConcurrentGUI.this.up.setEnabled(false));
            SwingUtilities.invokeLater(()->ConcurrentGUI.this.down.setEnabled(false));
        }
    }
}
