package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public final class AnotherConcurrentGUI extends JFrame {
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.2;
    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");

    public AnotherConcurrentGUI(){
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

        TimerAgent Timercounter = new TimerAgent();
        new Thread(Timercounter).start();

        stop.addActionListener((e)->Timercounter.stop());
        up.addActionListener((e)->Timercounter.up());
        down.addActionListener((e)->Timercounter.down());
    }

    private class CounterAgent implements Runnable{
        
        public int counter;
        private volatile boolean stop;
        private volatile boolean decrementing;

        @Override
        public void run() {
            while(!this.stop){
                try {
                    String nextString = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(()->AnotherConcurrentGUI.this.display.setText(nextString));
                    Thread.sleep(100);
                    if(!decrementing){
                        counter ++;
                    } else{
                        counter --;
                    }
                } catch (InvocationTargetException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stop(){
            this.stop = true;
            SwingUtilities.invokeLater(()->AnotherConcurrentGUI.this.stop.setEnabled(false));
            SwingUtilities.invokeLater(()->AnotherConcurrentGUI.this.up.setEnabled(false));
            SwingUtilities.invokeLater(()->AnotherConcurrentGUI.this.down.setEnabled(false));
        }

        public void up(){
            this.decrementing = false;
        }

        public void down(){
            this.decrementing = true;
        }
    }

    private class TimerAgent implements Runnable{
        
        private CounterAgent counter;
        @Override
        public void run() {
            try {
                counter = new CounterAgent();
                new Thread(counter).start();
                Thread.sleep(10000);
                counter.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void stop(){
            counter.stop();
        }

        public void up(){
            counter.up();
        }

        public void down(){
            counter.down();
        }
    }
}
