package shidhartha.saikia.common;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import shidhartha.saikia.ImageEditPanel;

public class EditImageFramePanel extends JFrame implements ActionListener,WindowListener,ChangeListener{
	
	private static final long serialVersionUID = 1L;
	public ImageEditPanel  originalPanel;
	public BufferedImage	scalDownImageReadonly;
	public BufferedImage	scalDownImageReadWrite;
	
	public ImageEditPanel panelImageReadOnly;
	public ImageEditPanel panelImageReadWrite;
	
	public JButton buttonApplyChanges;
	public JButton buttonResetChanges;
	
	public void addSlider(JSlider slider, String name, JPanel panel, int min, int max, int value) {
		JLabel label= new JLabel(name);
		panel.add(label);
		if(slider ==null)
			slider = new JSlider(JSlider.HORIZONTAL);
		slider.setMaximum(max);
		slider.setMinimum(min);
		slider.setValue(value);
		slider.setName(name);
		slider.addChangeListener(this);		
		panel.add(slider);
	}
	
	public void createScaleDownImages(int width, int height) {
		double scaleFactor=1;
		scaleFactor = calculateScaleFactor(width, height);
		System.out.println("Scale Factor :"+scaleFactor);
		
		int newImageWidth = new Double(originalPanel.getBufferedImage().getWidth() * scaleFactor).intValue();
		int newImageHeight = new Double(originalPanel.getBufferedImage().getHeight() * scaleFactor).intValue();
		
		scalDownImageReadonly = new BufferedImage(newImageWidth, newImageHeight, originalPanel.getBufferedImage().getType());
		scalDownImageReadWrite = new BufferedImage(newImageWidth, newImageHeight, originalPanel.getBufferedImage().getType());
		
	    Graphics2D gReadOnly = scalDownImageReadonly.createGraphics();
	    gReadOnly.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    gReadOnly.drawImage(originalPanel.getBufferedImage(), 0, 0, newImageWidth, newImageHeight, 0, 0, originalPanel.getBufferedImage().getWidth(), originalPanel.getBufferedImage().getHeight(), null);
	    gReadOnly.dispose();
	    
	    Graphics2D gReadWrite = scalDownImageReadWrite.createGraphics();
	    gReadWrite.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    gReadWrite.drawImage(originalPanel.getBufferedImage(), 0, 0, newImageWidth, newImageHeight, 0, 0, originalPanel.getBufferedImage().getWidth(), originalPanel.getBufferedImage().getHeight(), null);
	    gReadWrite.dispose();
	}

	private double calculateScaleFactor(int width, int height) {
		double scaleFactor;
		if(originalPanel.getBufferedImage().getWidth()>= originalPanel.getBufferedImage().getHeight()){
			scaleFactor=(double)(width/2-20)/(double)originalPanel.getBufferedImage().getWidth();
			System.out.println("Width:"+width+" ImageWidth:"+originalPanel.getBufferedImage().getWidth()+" Scale Factor :"+scaleFactor);
		}else{
			scaleFactor=(double)(height-200)/(double)originalPanel.getBufferedImage().getHeight();
			System.out.println("Height:"+height+" ImageHeight:"+originalPanel.getBufferedImage().getHeight()+" Scale Factor :"+scaleFactor);
		}
		return scaleFactor;
	}

	
	
	@Override
	public void stateChanged(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	

}
