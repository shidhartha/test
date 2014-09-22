package shidhartha.saikia;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Toolkit;
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

import shidhartha.saikia.image.ImageEffects;

public class FrameBrightnessAndContrastCorrection extends JFrame implements ActionListener,WindowListener,ChangeListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ImageEditPanel  originalPanel;
	BufferedImage	scalDownImageReadonly;
	BufferedImage	scalDownImageReadWrite;
	
	ImageEditPanel panelImageReadOnly;
	ImageEditPanel panelImageReadWrite;
	
	JButton buttonApplyChanges;
	JButton buttonResetChanges;
	
	JSlider sliderContrast;
	JSlider sliderBrightness;
	
	int contrastDelta=0;    
    int brightnessDelta=0;

	public FrameBrightnessAndContrastCorrection(ImageEditPanel panel){
		originalPanel=panel;
		//imageOriginal	=	panel.getBufferedImage();
		
		createGUI();
	}

	private void createGUI() {
		int width=Toolkit.getDefaultToolkit().getScreenSize().width/2;
		int height=Toolkit.getDefaultToolkit().getScreenSize().height/2+100;
		
		createScaleDownImages(width, height);
		
		super.setSize(new Dimension (width, height));
		this.setLayout(new GridLayout(0,2));
		
		panelImageReadOnly= new ImageEditPanel(scalDownImageReadonly);
		panelImageReadWrite= new ImageEditPanel(scalDownImageReadWrite);
		
		/*JLabel label;
		label= new JLabel("Original Image");
		this.add(label);
		
		label= new JLabel("Modified Image");
		this.add(label);*/
		
		this.add(panelImageReadOnly);
		this.add(panelImageReadWrite);
		
		//JLabel label;
		
		     
        JPanel panelSlider= new JPanel();
        panelSlider.setLayout(new GridLayout(0,1));
        addBrightnessAndContrsatSliders(panelSlider);
		
        this.add(panelSlider);
        
        //JPanel buttonPanel= new JPanel();
        //buttonPanel.setSize(new Dimension(200,30));
        //panelOtherSlider.setLayout(new GridLayout(0,1));
        
        buttonApplyChanges = new JButton ("Apply Changes");
        buttonApplyChanges.setSize(50, 20);
        buttonApplyChanges.addActionListener(this);
        panelSlider.add(buttonApplyChanges);
        
        buttonResetChanges = new JButton ("Reset");
        buttonResetChanges.setSize(50, 20);
        buttonResetChanges.addActionListener(this);
        panelSlider.add(buttonResetChanges);
        
        //this.add(buttonPanel);
        
       
	}
	
	public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        
        if (!source.getValueIsAdjusting()) {
            System.out.println(source.getName()+":"+source.getValue());
                        
            if(source.getName().toLowerCase().trim().equals("contrast")){
            	contrastDelta=source.getValue();          
            	
            }else if((source.getName().toLowerCase().trim().equals("brightness"))){
            	brightnessDelta=source.getValue();
           	
            }
            
            ImageEffects.doAdjustBrightnessAndContrast (scalDownImageReadonly,scalDownImageReadWrite,brightnessDelta,contrastDelta);
            
            repaintReadWriteImage(panelImageReadWrite);
        }        
    }

	private void repaintReadWriteImage(ImageEditPanel p) {
		p.revalidate();
		p.repaint();
	}

	private void addBrightnessAndContrsatSliders(JPanel panelOtherSlider) {		
		addSlider(sliderContrast,"Contrast",panelOtherSlider ,-100,100,0);
		addSlider(sliderBrightness,"Brightness",panelOtherSlider, -255,255, 0);		
	}

	private void addSlider(JSlider slider, String name, JPanel panel, int min, int max, int value) {
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
	

	private void createScaleDownImages(int width, int height) {
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
		System.out.println(arg0.getActionCommand());
		
		if(arg0.getActionCommand().toLowerCase().equals("reset")){
			ImageEffects.doAdjustRGBandHSB (scalDownImageReadonly,scalDownImageReadWrite,0,0,0,0,0,0);
			repaintReadWriteImage(panelImageReadWrite);
			/*
			sliderContrast.setValue(0);
			sliderBrightness.setValue(0);*/
			
			
		}else if(arg0.getActionCommand().toLowerCase().equals("apply changes")){
			originalPanel.saveOriginalImage();
			ImageEffects.doAdjustBrightnessAndContrast (originalPanel.getBufferedImage(),originalPanel.getBufferedImage(),brightnessDelta, contrastDelta);
			repaintReadWriteImage(originalPanel);
			this.setVisible(false);
			this.dispose();
		}
		
	}
}
