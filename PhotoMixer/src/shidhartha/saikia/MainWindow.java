package shidhartha.saikia;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.filechooser.FileSystemView;

import shidhartha.saikia.image.ImageEffects;

public class MainWindow implements ActionListener , KeyListener{
	
	File userOpenDirectory=null;
	File currentFile=null;
	
	//String[] imageFileExtensions;
	JFrame mainFrame;
	JFrame preferenceFrame;
	JFrame frameHistogram;
	JFrame frameColorCorrection;
	JFrame frameEdgeDetection;
	JFrame frameBrightnessAndContrastCorrection;
	JFrame frameSepiaImage;
	
	ImageEditPanel imagePanel;
	JScrollPane pane;
	
	//menus
	JMenuBar menuBar;
	
	JMenu fileMenu;
	JMenu editMenu;
	JMenu toolMenu;
	JMenu toolEffectSubMenu;
	JMenu imageMenu;
	JMenu specialEffectsMenu;
	
	JMenuItem menuItem;
	
	JPopupMenu rightClickMenu;
	
	JFormattedTextField inputBW;
	
	int thresholdBlackAndWhite=125;
	
	String defaultFileName=null;
	
	
	
	private MainWindow(){
		
		//for testing only
		defaultFileName="/Users/ssaikia/Pictures/butterfly-18a.jpg";
		
		this.mainFrame=new JFrame("PhotoMixer by Shidhartha...");
		this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screenHight = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.mainFrame.setSize(screenWidth,screenHight);
		
		userOpenDirectory=FileSystemView.getFileSystemView().getDefaultDirectory();
		
		addGUIComponent();	
		this.mainFrame.addKeyListener(this);
		
		if(defaultFileName!=null){
			//open the default image file for testing
			openImageFile(new File(defaultFileName));
			calculateAndSetImageBestFitTransformRatio();
		}
		
	}
	
	private void addGUIComponent(){			
		addMenu();
//		addToolBox();
		// build poup menu
//		rightClickMenu= new JPopupMenu();
//		 JMenuItem menuItem = new JMenuItem("New Project...",
//	                new ImageIcon("images/newproject.png"));
//	        menuItem.setMnemonic(KeyEvent.VK_P);
//	        menuItem.getAccessibleContext().setAccessibleDescription(
//	                "New Project");
//	        menuItem.addActionListener(new ActionListener() {
//	 
//	            public void actionPerformed(ActionEvent e) {
//	                System.out.println("New Project clicked!");
//	            }
//	        });
//	        rightClickMenu.add(menuItem);
//	        // New File menu item
//	        menuItem = new JMenuItem("New File...",
//	                new ImageIcon("images/newfile.png"));
//	        menuItem.setMnemonic(KeyEvent.VK_F);
//	        menuItem.addActionListener(new ActionListener() {
//	 
//	            public void actionPerformed(ActionEvent e) {
//	                System.out.println("New File clicked!");
//	            }
//	        });
//	        rightClickMenu.add(menuItem);
		
	}
	
	private void addImageEditPanel(String imageName) {
		
		
		//imageName="C:\\Users\\ssaikia\\Pictures\\Blue Morpho.jpg";
		//imageName="C:\\Users\\ssaikia\\Pictures\\my card.jpg";
		
		try {
			//ImageEditPanel imagePanel= 
			
			if(imagePanel!=null)
				pane.getViewport().remove(imagePanel);
			if(pane!=null)
				this.mainFrame.getContentPane().remove(pane);
			
			
			pane = new JScrollPane();
			imagePanel=new ImageEditPanel(imageName);
			//imagePanel=ImageEditPanel.getInstance(imageName);
			//pane.setPreferredSize(new Dimension(500, 500));
			
			pane.getViewport().add(imagePanel);
			
	
			pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			//pane.setWheelScrollingEnabled(true);
			//pane.validate();
			//this.mainFrame.setContentPane(pane);
			this.mainFrame.getContentPane().add(pane);
			
			//imagePanel.repaint();
			
			//this.mainFrame.add(pane);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Exception",  e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
		
		
	}

	private void addMenu() {
		menuBar	=	new	JMenuBar();
		
		addFileMenu();
		addEditMenu();
		addImageMenu();
		addToolsMenu();
		addSpecialEffectsMenu();
		addAboutMenu();
		
		mainFrame.setJMenuBar(menuBar);
	}

	private void addAboutMenu() {
		// TODO Auto-generated method stub
		
	}

	private void addToolsMenu() {
		
		toolMenu	=	new	JMenu("Tools");		
		toolMenu.setMnemonic(KeyEvent.VK_T);
		
		addEffectMenu();
		addHistogram();
		addColorCorrection();
		addBrightnessAndConstrastCorrection();
		menuBar.add(toolMenu);
	}
	
	private void addSpecialEffectsMenu() {
		
		specialEffectsMenu	=	new	JMenu("Special Effects");		
		toolMenu.setMnemonic(KeyEvent.VK_E);
		
		addResetSepecialEffectSelection();
		addSelectiveFocus();
		addSelectiveBlur();
		addSelectiveGray();
		
		menuBar.add(specialEffectsMenu);
	}

	

	private void addBrightnessAndConstrastCorrection() {
		
		menuItem = new JMenuItem("Brightness And Contrast Correction");
		menuItem.addActionListener(this);		
				
		toolMenu.add(menuItem);
	}

	private void addColorCorrection() {
		menuItem = new JMenuItem("Color Correction");
		menuItem.addActionListener(this);		
				
		toolMenu.add(menuItem);
		
	}

	private void addHistogram() {
		menuItem = new JMenuItem("Histogram");
		menuItem.addActionListener(this);		
		//toolSubMenu.add(menuItem);
		
		toolMenu.add(menuItem);
		
	}
	
	private void addSelectiveFocus() {
		menuItem = new JMenuItem("Selective Focus");
		menuItem.addActionListener(this);		
		//toolSubMenu.add(menuItem);
		
		specialEffectsMenu.add(menuItem);
		
	}
	
	private void addSelectiveBlur() {
		menuItem = new JMenuItem("Selective Blur");
		menuItem.addActionListener(this);		
		//toolSubMenu.add(menuItem);
		
		specialEffectsMenu.add(menuItem);
		
	}
	private void addSelectiveGray() {
		menuItem = new JMenuItem("Selective Gray");
		menuItem.addActionListener(this);		
		//toolSubMenu.add(menuItem);
		
		specialEffectsMenu.add(menuItem);
		
	}
	
	private void addResetSepecialEffectSelection() {
		menuItem = new JMenuItem("Reset Special Effect Selcection");
		menuItem.addActionListener(this);		
		//toolSubMenu.add(menuItem);
		
		specialEffectsMenu.add(menuItem);
		
	}

	private void addImageMenu() {
		
		imageMenu = new JMenu("Image");
		imageMenu.setMnemonic(KeyEvent.VK_I);
		
		menuItem = new JMenuItem("Best Fit");
		menuItem.addActionListener(this);
		imageMenu.add(menuItem);
		
		menuItem = new JMenuItem("Actual Size");
		menuItem.addActionListener(this);
		imageMenu.add(menuItem);
		
		imageMenu.add(new JSeparator());
		
		
		menuItem = new JMenuItem("Crop");
		menuItem.addActionListener(this);
		imageMenu.add(menuItem);
		
		menuBar.add(imageMenu);
		
	}

	private void addEditMenu() {
		editMenu = new JMenu ("Edit");
		editMenu.setMnemonic(KeyEvent.VK_F);
		
		menuItem= new JMenuItem("Undo");
		menuItem.addActionListener(this);
		
		editMenu.add(menuItem);
		menuBar.add(editMenu);
		
	}

	private void addFileMenu() {	
		
		fileMenu	=	new	JMenu("File");		
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		menuItem	=	new JMenuItem("Open");
		menuItem.addActionListener(this);
		fileMenu.add(menuItem);
		
		menuItem	=	new	JMenuItem("Save");
		menuItem.addActionListener(this);
		fileMenu.add(menuItem);
		
		fileMenu.add(new JSeparator());
		
		menuItem	=	new	JMenuItem("Preference");
		menuItem.addActionListener(this);
		fileMenu.add(menuItem);
		
		fileMenu.add(new JSeparator());
		
		menuItem	=	new	JMenuItem("Exit");
		menuItem.addActionListener(this);
		fileMenu.add(menuItem);
		
		
		
				
		menuBar.add(fileMenu);		
	}

	public static void main(String[] args){
		
		MainWindow mainW=new MainWindow();
		
		/*
		//disable the close button
		mainW.mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//create custom close operation
		mainW.mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        System.exit(0);
		    }
		});*/
		
		mainW.mainFrame.setVisible(true);
		
		System.out.println("Total/Free Memory: (MB) "+Runtime.getRuntime().totalMemory()/1000000+"/"+Runtime.getRuntime().freeMemory()/1000000);
		
		
	}
	private void addEffectMenu() {
		
		toolEffectSubMenu	=	new JMenu("Effects");
		
		menuItem	=	new JMenuItem("Negative");
		menuItem.addActionListener(this);
		toolEffectSubMenu.add(menuItem);
		
		menuItem	=	new JMenuItem("GrayScale");
		menuItem.addActionListener(this);		
		toolEffectSubMenu.add(menuItem);
		
		menuItem	=	new JMenuItem("Sepia");
		menuItem.addActionListener(this);		
		toolEffectSubMenu.add(menuItem);
		
		menuItem	=	new JMenuItem("Black & White");
		menuItem.addActionListener(this);		
		toolEffectSubMenu.add(menuItem);
		
		menuItem	=	new JMenuItem("EdgeDetection");
		menuItem.addActionListener(this);		
		toolEffectSubMenu.add(menuItem);
		
		menuItem	=	new JMenuItem("BlurImage");
		menuItem.addActionListener(this);		
		toolEffectSubMenu.add(menuItem);
		
		toolMenu.add(toolEffectSubMenu);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String item = e.getActionCommand();
		
		if(item.equalsIgnoreCase("Open")){
			openImageFile();
			calculateAndSetImageBestFitTransformRatio();
		}
		else if(item.equalsIgnoreCase("Save")){
			try {
				saveImageFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "Exception",  e1.getMessage(), JOptionPane.ERROR_MESSAGE);
			}
			//doImageBestFit();
		}
		else if(item.equalsIgnoreCase("Exit")){
			System.exit(0);
		}
		else if(item.equalsIgnoreCase("Negative")){
			imagePanel.saveOriginalImage();
			ImageEffects.addNegative(imagePanel.getBufferedImage());
			refreshImagePanel();
		}
		else if(item.equalsIgnoreCase("GrayScale")){
			imagePanel.saveOriginalImage();
			ImageEffects.addGrayScale(imagePanel.getBufferedImage());
			refreshImagePanel();
		}else if(item.equalsIgnoreCase("Sepia")){
			imagePanel.saveOriginalImage();
			displaySepiaEffectFrame();
			//ImageEffects.addSepiaEffect(imagePanel.getBufferedImage(),20);
			//refreshImagePanel();
		}else if(item.equalsIgnoreCase("Black & White")){
			imagePanel.saveOriginalImage();
			
			ImageEffects.addBlackAndWhite(imagePanel.getBufferedImage(),thresholdBlackAndWhite);
		    refreshImagePanel(); 
					
		}		
		else if(item.equalsIgnoreCase("EdgeDetection")){			
//			CannyEdgeDetector ced=new CannyEdgeDetector();
//			ced.setSourceImage(imagePanel.getBufferedImage());
//			imagePanel.saveOriginalImage();
//			ced.process();			
//			imagePanel.setBufferedImage(ced.getEdgesImage());
//			refreshImagePanel();
			displayEdgeDetectionFrame();
		}
		else if(item.equalsIgnoreCase("Histogram")){
			displayHistogram();			
		}
		else if(item.equalsIgnoreCase("Color Correction")){
			displayColorCorrectionFrame();			
		}
		else if(item.equalsIgnoreCase("Brightness And Contrast Correction")){
			displayBrightnessAndConstrastCorrectionFrame();			
		}
		else if(item.equalsIgnoreCase("Undo")){
			if(imagePanel!=null){
				if (imagePanel.getOriginalBufferedImage()!=null){
					imagePanel.setBufferedImage(imagePanel.getOriginalBufferedImage());
				}
				refreshImagePanel();
			}
			
		}else if(item.equalsIgnoreCase("Best Fit")){
			if(imagePanel!=null){
				calculateAndSetImageBestFitTransformRatio();
				refreshImagePanel();				
			}			
		}
		else if(item.equalsIgnoreCase("Actual Size")){
			if(imagePanel!=null){
				imagePanel.setTrasnformRatio(1);
				refreshImagePanel();				
			}			
		}
		else if(item.equalsIgnoreCase("Crop")){
			if(imagePanel!=null){
				imagePanel.doCropImage();
				refreshImagePanel();
			}			
		}
		else if(item.equalsIgnoreCase("Preference")){
			displayPreferenceWindow();
		}		
		else if(item.equalsIgnoreCase("Save Preference")){
			if (preferenceFrame!=null){
				preferenceFrame.setVisible(false);
				preferenceFrame.dispose();
				preferenceFrame=null;
			}
			System.out.println("Saving Preference...");
			
			try{
				int tmp=Integer.parseInt(inputBW.getText());
				
				if (tmp > 255)
					tmp=255;
				
				thresholdBlackAndWhite=tmp; 
			}catch(java.lang.NumberFormatException ex1){
				System.out.println(ex1.getMessage());
				thresholdBlackAndWhite=125;
			}
		}
		else if(item.equalsIgnoreCase("blurimage")){
			displayBlurImageFrame();
		}
		else if(item.equals("Reset Special Effect Selcection")){
			imagePanel.setSpecialActionRequested(ImageEditPanel.NORMAL);
		}
		else if(item.equals("Selective Focus")){
			imagePanel.setSpecialActionRequested(ImageEditPanel.SELECTIVE_FOCUS);
//			imagePanel.setTrasnformRatio(1);
//			refreshImagePanel();
		}
		else if(item.equals("Selective Blur")){
			imagePanel.setSpecialActionRequested(ImageEditPanel.SELECTIVE_BLUR);
//			imagePanel.setTrasnformRatio(1);
//			refreshImagePanel();
		}
		else if(item.equals("Selective Gray")){
			imagePanel.setSpecialActionRequested(ImageEditPanel.SELECTIVE_GRAY);
//			imagePanel.setTrasnformRatio(1);
//			refreshImagePanel();
		}
		else{
			if(this.imagePanel!=null){
				refreshImagePanel();
			}			
		}		
	}

	private void displayBlurImageFrame() {
		imagePanel.saveOriginalImage();
		imagePanel.setBufferedImage(ImageEffects.addBlurUsingConvolveOp(imagePanel.getBufferedImage()));
		refreshImagePanel();

	}
	
	

	private void saveImageFile() throws IOException {
		if(imagePanel==null)
			return;
		ShidhFileFilter filterImageFile=new ShidhFileFilter(ImageIO.getWriterFormatNames());		
		
		final JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView().getDefaultDirectory());
		fc.addChoosableFileFilter(filterImageFile) ;
		int returnVal = fc.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File newFile=fc.getSelectedFile();			
			if(filterImageFile.accept(newFile)){
				String format=filterImageFile.getExtension(newFile);
				ImageIO.write(imagePanel.getBufferedImage(), format, newFile);
			}else{
				
				JOptionPane.showMessageDialog(null, "File Format not supported....",  "File format error", JOptionPane.ERROR_MESSAGE);
				System.out.println("File Format not supported...");
			}
							
		}			
		mainFrame.setVisible(true);
		if (imagePanel!=null){
			this.imagePanel.setVisible(true);
			refreshImagePanel();
		}
		
	}

	private void displayBrightnessAndConstrastCorrectionFrame() {
		if(imagePanel==null || imagePanel.getBufferedImage() ==null)
			return;
		
		if(frameBrightnessAndContrastCorrection!=null){
			frameBrightnessAndContrastCorrection.setVisible(false);
			frameBrightnessAndContrastCorrection.dispose();
			frameBrightnessAndContrastCorrection=null;
		}
		
		frameBrightnessAndContrastCorrection= new FrameBrightnessAndContrastCorrection(imagePanel);
		frameBrightnessAndContrastCorrection.setVisible(true);
		
	}

	private void displayColorCorrectionFrame() {
		if(imagePanel==null || imagePanel.getBufferedImage() ==null)
			return;
		
		if(frameColorCorrection!=null){
			frameColorCorrection.setVisible(false);
			frameColorCorrection.dispose();
			frameColorCorrection=null;
		}
		
		frameColorCorrection= new FrameColorCorrection(imagePanel);
		frameColorCorrection.setVisible(true);
	}
	
	private void displaySepiaEffectFrame() {
		if(imagePanel==null || imagePanel.getBufferedImage() ==null)
			return;
		
		if(frameSepiaImage!=null){
			frameSepiaImage.setVisible(false);
			frameSepiaImage.dispose();
			frameSepiaImage=null;
		}
		
		frameSepiaImage= new FrameSepiaImage(imagePanel);
		frameSepiaImage.setVisible(true);
	}
	
	private void displayEdgeDetectionFrame() {
		if(imagePanel==null || imagePanel.getBufferedImage() ==null)
			return;
		
		if(frameEdgeDetection!=null){
			frameEdgeDetection.setVisible(false);
			frameEdgeDetection.dispose();
			frameEdgeDetection=null;
		}
		
		frameEdgeDetection= new FrameEdgeDetectionPanel(imagePanel);
		frameEdgeDetection.setVisible(true);
	}

	private void openImageFile() {
		ShidhFileFilter filterImageFile=new ShidhFileFilter(ImageIO.getReaderFormatNames());
		
		final JFileChooser fc = new JFileChooser(userOpenDirectory);
		fc.addChoosableFileFilter(filterImageFile) ;
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File newFile=fc.getSelectedFile();			
			if(filterImageFile.accept(newFile)){				
				openImageFile(fc.getSelectedFile());				
			}else{
				JOptionPane.showMessageDialog(null, "File Format not supported....",  "File format error", JOptionPane.ERROR_MESSAGE);
				System.out.println("File Format not supported...");
			}
		}	
		
		
		/*mainFrame.setVisible(true);
		if (imagePanel!=null){
			this.imagePanel.setVisible(true);
			refreshImagePanel();
		}*/
	}
	private void openImageFile(File f) {
		long freeMemBeforeImageLoad=Runtime.getRuntime().freeMemory();
		currentFile=f;
		addImageEditPanel(currentFile.getAbsolutePath());
		userOpenDirectory=currentFile.getParentFile();
				
		mainFrame.setVisible(true);
		if (imagePanel!=null){
			this.imagePanel.setVisible(true);
			refreshImagePanel();
		}
		long freeMemAfterImageLoad=Runtime.getRuntime().freeMemory();
		System.out.println("Image Loaded. Im-memory size="+(freeMemBeforeImageLoad-freeMemAfterImageLoad)/1000+"KB");
		
	}

	private void displayHistogram() {
		
		if(imagePanel==null || imagePanel.getBufferedImage() ==null)
			return;
		
		if(frameHistogram!=null){
			frameHistogram.setVisible(false);
			frameHistogram.dispose();
			frameHistogram=null;
		}
		frameHistogram= new JFrame("Histogram");
		frameHistogram.setSize(new Dimension(300,400));
				
		int[] histogram= new int[256];
		
		calculateHistogram(histogram);
		PanelHistogram ph= new PanelHistogram(histogram);
		
		frameHistogram.add(ph);
		
		frameHistogram.setVisible(true);
	}

	private void calculateHistogram(int[] histogram) {
		int imageWidth=imagePanel.getBufferedImage().getWidth();
		int imageHeight=imagePanel.getBufferedImage().getHeight();
		
		double total_pixels=imageWidth*imageHeight;
				
		int[] pixel= new int[4];		
		for (int i=0; i<imageWidth; i++)
			for (int j=0;j<imageHeight; j++)
			{
				pixel=imagePanel.getBufferedImage().getRaster().getPixel(i, j, new int[4]);
				
				double gray=(0.2*pixel[1])+(0.7*pixel[2])+(0.07*pixel[3]);
				int grayInt=new Double(gray).intValue();
				histogram[grayInt]++;				
			}

		for (int i=0; i<histogram.length; i++){
			double percentageIntensity=histogram[i]*1000/total_pixels;
			//System.out.println("sbins["+i+"]:"+percentageIntensity[i]);
			histogram[i]=(int)percentageIntensity;
		}
		
		
	}

	private void displayPreferenceWindow() {
		if(preferenceFrame!=null){
			preferenceFrame.setVisible(false);
			preferenceFrame.dispose();
			preferenceFrame=null;
		}
		
			
		preferenceFrame	=	new JFrame ("Preference");
		
		preferenceFrame.setSize(200, 100);
		preferenceFrame.setLayout(new GridLayout(0,1));
		
		JPanel inputPanel=new JPanel();
		inputPanel.setLayout(new GridLayout(0,2));
		inputPanel.setSize(preferenceFrame.getWidth()-10, preferenceFrame.getHeight()-50);
		
		JPanel commandPanel=new JPanel();
		commandPanel.setLayout(new GridLayout(1,1));
		commandPanel.setSize(preferenceFrame.getWidth()-10, 50);
		
		JLabel label;
		
		
		label = new JLabel ("Black and Whilte Threshold");
		inputPanel.add(label);
		
		inputBW=new JFormattedTextField(NumberFormat.getInstance());
		inputBW.setText(new Integer(thresholdBlackAndWhite).toString());
		inputPanel.add(inputBW);			
		
		JButton buttonSavePreference= new JButton("Save Preference");
		buttonSavePreference.setPreferredSize(new Dimension(30,20));
		
		buttonSavePreference.addActionListener(this);
		commandPanel.add(buttonSavePreference);
		
		preferenceFrame.add(inputPanel);
		preferenceFrame.add(commandPanel);
		
		preferenceFrame.setVisible(true);
	}

	private void calculateAndSetImageBestFitTransformRatio() {
		if(pane==null)
			return;
		
		
		int paneWidth= pane.getWidth();
		int paneHeight= pane.getHeight();
		
		int imageWidth=imagePanel.getBufferedImage().getWidth();
		int imageHeight=imagePanel.getBufferedImage().getHeight();
		
		double ratioPanelToImageWidth	= 	(double)paneWidth/imageWidth;
		double ratioPanelToImageHeight	=	(double)paneHeight/imageHeight;
		
		double trasnformRatio = ratioPanelToImageWidth < ratioPanelToImageHeight ? ratioPanelToImageWidth
				: ratioPanelToImageHeight;	
		
		imagePanel.setTrasnformRatio(trasnformRatio);
	}

	private void refreshImagePanel() {
		//this.imagePanel.
		this.imagePanel.revalidate();
		this.imagePanel.repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		//System.out.println("System Key Pressed :"+e.getKeyChar()+":"+e.getKeyCode()+":"+e.getKeyLocation());
		imagePanel.setKeyPressedEvent(e);
		if(e.getKeyCode()==37){
			//left arrow
			//move to previous image
			if (userOpenDirectory==null){
				return;
			}else{
				ShidhFileFilter filterImageFile=new ShidhFileFilter(ImageIO.getReaderFormatNames());
				File[] currentFiles=userOpenDirectory.listFiles();
			}
			
			
		}else if(e.getKeyCode()==39){
			//right arrow
			//move to next image
		}else if(e.getKeyCode()==32){
			//space key
			//toggle between full screen and best fit
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		System.out.println("System Key Released :"+e.getKeyChar()+":"+e.getKeyCode()+":"+e.getKeyLocation());		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		System.out.println("System Key Typed :"+e.getKeyChar()+":"+e.getKeyCode()+":"+e.getKeyLocation());		
	}

	

}
