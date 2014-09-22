package shidhartha.saikia;

import java.awt.Adjustable;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JFrame;
import javax.swing.JScrollBar;

public class ScrollBar {
  JScrollBar scrollBarVertical = new JScrollBar();
  JScrollBar scrollbarHorizontal = new JScrollBar(Adjustable.HORIZONTAL);

  ScrollBar() {
    JFrame f = new JFrame();
    f.setLayout(new FlowLayout());
    f.setSize(280, 300);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    scrollBarVertical.setPreferredSize(new Dimension(20, 200));
    scrollbarHorizontal.setPreferredSize(new Dimension(200, 20));
    
    scrollbarHorizontal.setMaximum(265);
    scrollbarHorizontal.setMinimum(0);

    scrollbarHorizontal.setValue(50);

    scrollBarVertical.addAdjustmentListener(new AdjustmentListener() {
      public void adjustmentValueChanged(AdjustmentEvent ae) {
        if (scrollBarVertical.getValueIsAdjusting())
          return;
        System.out.println("Value of vertical scroll bar: " + ae.getValue());
      }
    });

    scrollbarHorizontal.addAdjustmentListener(new AdjustmentListener() {
      public void adjustmentValueChanged(AdjustmentEvent ae) {
        System.out.println("Value of horizontal scroll bar: " + ae.getValue());
      }
    });

    f.add(scrollBarVertical);
    f.add(scrollbarHorizontal);

    f.setVisible(true);
  }

  public static void main(String args[]) {
    new ScrollBar();
  }
}