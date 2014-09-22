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

import shidhartha.saikia.common.EditImageFramePanel;
import shidhartha.saikia.image.ImageEffects;
import thirdparty.CannyEdgeDetector;

public class FrameEdgeDetectionPanel extends EditImageFramePanel implements ActionListener,WindowListener,ChangeListener{
	
	/**
	 * 
	 */
	
	static float DEFAULT_THRESHOLD_HIGH=0.5f;
	int delta1=0;    
	float highThresholdUsing=DEFAULT_THRESHOLD_HIGH;
    

	public FrameEdgeDetectionPanel(ImageEditPanel panel){
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
        addSlider(new JSlider(),"HighThreshold",panelSlider ,-15,15,0);
		
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
        
        detectAndDrawEdge(highThresholdUsing, panelImageReadOnly,panelImageReadWrite);
        //this.add(buttonPanel);
        
       
	}
	
	public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        
        if (!source.getValueIsAdjusting()) {
            System.out.println(source.getName()+":"+source.getValue());
                        
            if(source.getName().toLowerCase().trim().equals("highthreshold")){
            	delta1=source.getValue(); 
            	System.out.println("Delta1="+delta1);
            	
            }
            
            highThresholdUsing=(float)(5.0F + delta1);
            detectAndDrawEdge(highThresholdUsing, panelImageReadOnly,panelImageReadWrite);
			         
            repaintReadWriteImage(panelImageReadWrite);
        }        
    }

	private void detectAndDrawEdge(float highThresholdUsing1, ImageEditPanel panelImageReadOnly1, ImageEditPanel panelImageReadWrite1) {
		System.out.println("highThreshold using "+highThresholdUsing1);
		
		CannyEdgeDetector ced=new CannyEdgeDetector();
		ced.setSourceImage(panelImageReadOnly1.getBufferedImage());
		ced.highThreshold=highThresholdUsing1;
//			imagePanel.saveOriginalImage();
		ced.process();			
		BufferedImage img=ced.getEdgesImage();
		ImageEffects.addNegative(img);
		panelImageReadWrite1.setBufferedImage(img);
	}

	private void repaintReadWriteImage(ImageEditPanel p) {
		p.revalidate();
		p.repaint();
	}

	

	

	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.out.println(arg0.getActionCommand());
		
		if(arg0.getActionCommand().toLowerCase().equals("reset")){
			detectAndDrawEdge(DEFAULT_THRESHOLD_HIGH, panelImageReadOnly, panelImageReadWrite);
			repaintReadWriteImage(panelImageReadWrite);
			/*
			sliderContrast.setValue(0);
			sliderBrightness.setValue(0);*/
			
			
		}else if(arg0.getActionCommand().toLowerCase().equals("apply changes")){
			originalPanel.saveOriginalImage();
			detectAndDrawEdge(highThresholdUsing,originalPanel,originalPanel);
			//ImageEffects.doAdjustBrightnessAndContrast (originalPanel.getBufferedImage(),originalPanel.getBufferedImage(),brightnessDelta, contrastDelta);
			repaintReadWriteImage(originalPanel);
			this.setVisible(false);
			this.dispose();
		}
		
	}
}
