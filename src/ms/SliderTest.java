package ms;

//JSlider Example
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

class SliderTest {

   public static void main(String[] args) {

      final JSlider sa = new JSlider();
      final JSlider sb = new JSlider(JSlider.VERTICAL, 0, 100, 50);

      sa.addChangeListener (new ChangeListener() {
         public void stateChanged (ChangeEvent e) {
            System.out.println(sa.getValue());
         }
      });

      sb.addChangeListener (new ChangeListener() {
         public void stateChanged (ChangeEvent e) {
            System.out.println(sb.getValue());
         }
      });

      sb.setPaintTicks(true);
      sb.setMajorTickSpacing(25);
      sb.setMinorTickSpacing(5);
      sb.setPaintLabels(true);

      JFrame frame = new JFrame();
      Container cp = frame.getContentPane();
      cp.setLayout(new FlowLayout());
      cp.add(sa);
      cp.add(sb);

      frame.pack();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true);
   }
}