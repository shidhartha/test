package shidhartha.saikia;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class PanelHistogram extends JPanel{
	
	int[] histogramArray;
	
	PanelHistogram (int[] data){
		//super.setBackground(Color.green);
		super.setForeground(Color.RED);
		histogramArray=data;
		repaint();
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void paint(Graphics g) {
		int max=0;
		for (int i = 0; i < histogramArray.length; i++) {	
			if(max<histogramArray[i])
				max=histogramArray[i];
		}
		
		max=max+20;
		int yAxis=350;
		
		g.setColor(Color.WHITE);
		g.fillRect(5, max+50, histogramArray.length+10, yAxis-(max+50) );
		
		g.setColor(Color.BLACK);
		for (int i = 0; i < histogramArray.length; i++) {			
			//g.drawLine(10 + i, 350, 10 + i, 350 - (histogramArray[i]*3));
			
			g.drawLine(10 + i, yAxis, 10 + i, yAxis - (histogramArray[i]*(350/max)));
		}
		
		g.dispose();
	}
}
