package shidhartha.saikia;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.w3c.dom.events.MouseEvent;

import shidhartha.saikia.image.ImageEffects;

public class ImageEditPanel extends JPanel implements MouseListener, MouseMotionListener {
	//private static ImageEditPanel instance=null;
	
	public static final short NORMAL=0;
	public static final short SELECTIVE_BLUR=1;
	
	//to keep track of key pressed
	KeyEvent keyPressedEvent;
	
    public KeyEvent getKeyPressedEvent() {
		return keyPressedEvent;
	}

	public void setKeyPressedEvent(KeyEvent keyPressedEvent) {
		this.keyPressedEvent = keyPressedEvent;
	}

	int specialActionRequested=0; //0= nothing, 1=selective blur
	
	public int getSpecialActionRequested() {
		return specialActionRequested;
	}

	public void setSpecialActionRequested(int specialActionRequested) {
		this.specialActionRequested = specialActionRequested;
	}

	private BufferedImage imageOriginal;
	private BufferedImage img;
	private BufferedImage tmpImg;
	
	boolean isMouseDragging=false;
	boolean temporaryRectangle=true;
	
	Point pointDraggingStart;
	Point pointDraggingEnd;
	
	Point currentMousePoint;
	
	boolean isCropAreaSelected=false;
	int cropStartX=-1;
	int cropStartY=-1;
	int cropWidth=-1;
	int cropHeight=-1;
	
	Rectangle selectiveBlurRectangle;
	 
	private double trasnformRatio	=	1;
	
	private static final long serialVersionUID = 1L;
	
			
	public ImageEditPanel(String fileName) throws IOException{
		saveOriginalImage();		
		img=ImageIO.read(new File(fileName));		
		super.setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));		
		printImageProperties();	
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		//this.addKeyListener(this);
	}
	
	public ImageEditPanel(BufferedImage img1){
		saveOriginalImage();		
		img=img1;		
		super.setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));		
		printImageProperties();			
	}

	public void saveOriginalImage() {
		imageOriginal=copyImage(img);
		/*if (img!=null){
			imageOriginal=null;
			//System.gc();		
			imageOriginal = new BufferedImage(img.getWidth(),img.getHeight(),img.getType());			
			imageOriginal.setData(img.getData());
		}*/
	}
	
	public BufferedImage copyImage(BufferedImage imgSource) {	
		BufferedImage imgDest=null;
		System.out.println("Total/Free Memory: (MB) "+Runtime.getRuntime().totalMemory()/1000000+"/"+Runtime.getRuntime().freeMemory()/1000000);
		if (imgSource!=null){
			//imgDest=null;
			//System.gc();		
			imgDest = new BufferedImage(imgSource.getWidth(),imgSource.getHeight(),imgSource.getType());			
			imgDest.setData(imgSource.getData());
		}
		return imgDest;
	}

	private void printImageProperties() {
		String[] s1=img.getPropertyNames();
		if(s1!=null)
		for (String s : s1){
			if(s!=null)
				System.out.println(s+" : "+img.getProperty(s));			
		}
	}
	
	public void paint(Graphics g) {
		
		Graphics2D g2D= (Graphics2D)g;
		if (trasnformRatio!=1){			
			AffineTransform transform= new AffineTransform();			
			transform.scale(trasnformRatio, trasnformRatio);			
			g2D.setTransform(transform);			
			g2D.drawImage( img, 0, 0, null);			
		}else{
			g.drawImage( img, 0, 0, null);
		}
		
		if(isMouseDragging && getSpecialActionRequested()==NORMAL){
			drawCropRectangle(g2D);
		}
		else if(getSpecialActionRequested()==SELECTIVE_BLUR){
			drawSelectiveBlurBoundary(g2D,50);
		}
		
	}

	private void drawSelectiveBlurBoundary(Graphics2D g2D, int boundarySizeParam) {	 	
		
		selectiveBlurRectangle=new Rectangle(
				(int)currentMousePoint.getX()-boundarySizeParam,
				(int)currentMousePoint.getY()-boundarySizeParam, 
				2*boundarySizeParam, 
				2*boundarySizeParam);
		
		Color originalColor=g2D.getColor();
		g2D.setColor(Color.yellow);
		g2D.drawRect(selectiveBlurRectangle.x,selectiveBlurRectangle.y,selectiveBlurRectangle.width,selectiveBlurRectangle.height);
		g2D.setColor(originalColor);
		
	}

	private void drawCropRectangle(Graphics2D g2D) {
		int x1=(int)pointDraggingStart.getX();
		int y1=(int)pointDraggingStart.getY();
		int x2=(int)pointDraggingEnd.getX();
		int y2=(int)pointDraggingEnd.getY();
		
		cropStartX=(x1<x2)? x1 : x2;
		cropStartY=(y1<y2)? y1 : y2;
		
		int xEnd=(x1>=x2)? x1 : x2;
		int yEnd=(y1>=y2)? y1 : y2;		
		
		//apply transformation ration as the image might have been re-scaled
		cropStartX=(int) (cropStartX/trasnformRatio);
		cropStartY=(int) (cropStartY/trasnformRatio);
		xEnd=(int) (xEnd/trasnformRatio);
		yEnd=(int) (yEnd/trasnformRatio);
		
		cropWidth= xEnd-cropStartX;
		cropHeight= yEnd-cropStartY;	
		//System.out.println("Drawing Rect: xStart="+xStart+" yStart="+yStart+" w="+w+" h="+h);
		if(!temporaryRectangle){
			isMouseDragging=false;
			pointDraggingEnd=null;
			pointDraggingStart=null;
			doHighLightArea(cropStartX,cropStartY,cropWidth,cropHeight);
		}
		Color originalColor=g2D.getColor();
		g2D.setColor(Color.white);
		g2D.drawRect(cropStartX, cropStartY, cropWidth,cropHeight);
		g2D.setColor(originalColor);
	}
	
	private void doHighLightArea(int x, int y,int w, int h) {
		//hightlight the area by reducing light on the other area
		tmpImg=copyImage(img);		
		ImageEffects.doAdjustBrightnessAndContrastAroundRectangle(img,img, x, y, w, h, -70, -50);
		
		repaint();
	}
	
	private void drawOriginalImage(){
		cropStartX=-1;
		cropStartY=-1;
		cropHeight=-1;
		cropWidth=-1;
		if(tmpImg!=null){
			//System.out.println("tmpImg is not null");
			img=copyImage(tmpImg);
			repaint();
			tmpImg=null;
		}
		
	}

	public BufferedImage getBufferedImage(){
		return img;
	}
	
	public BufferedImage getOriginalBufferedImage(){
		return imageOriginal;
	}
	
	public void setBufferedImage(BufferedImage bImg){
		saveOriginalImage();
		img=bImg;
				
	}
	
	/*public double getTrasnformRatio() {
		return trasnformRatio;
	}*/

	public void setTrasnformRatio(double trasnformRatio) {
		this.trasnformRatio = trasnformRatio;
	}

	@Override
	public void mouseDragged(java.awt.event.MouseEvent e) {
		//System.out.println("Mouse dragged:"+e.getX()+":"+e.getY());
		isMouseDragging=true;
		if(getSpecialActionRequested()==NORMAL){
			temporaryRectangle=true;
			if(pointDraggingStart==null){
				pointDraggingStart=e.getPoint();
			}else{
				pointDraggingEnd=e.getPoint();
				repaint();
				isCropAreaSelected=true;
			}
		}
		
	}
	@Override
	public void mouseMoved(java.awt.event.MouseEvent e) {	
		currentMousePoint=e.getPoint();
		if(getSpecialActionRequested()==SELECTIVE_BLUR){
			
			//System.out.println("Mouse Moved:"+currentMousePoint.getX()+":"+currentMousePoint.getY());
			repaint();
			
		}
	}
	@Override
	public void mouseClicked(java.awt.event.MouseEvent e) {
		/*System.out.println("Mouse Clicked:"+e.getX()+":"+e.getY());*/
		drawOriginalImage();
		//repaint();
	}
	@Override
	public void mouseEntered(java.awt.event.MouseEvent e) {/*System.out.println("Mouse entered:"+e.getX()+":"+e.getY());*/}
	@Override
	public void mouseExited(java.awt.event.MouseEvent e) {/*System.out.println("Mouse Exited:"+e.getX()+":"+e.getY());*/}
	@Override
	public void mousePressed(java.awt.event.MouseEvent e) {
		//System.out.println("Mouse Pressed:"+e.getX()+":"+e.getY());
		//pointDraggingStart=null;
		//isMouseDragging=true;
		pointDraggingStart=e.getPoint();
		
		//if key pressed is char b and selective blur is selected then do selective blur
		if(getKeyPressedEvent()!=null && 
				getKeyPressedEvent().getKeyChar()=='b' &&
				getSpecialActionRequested()==SELECTIVE_BLUR){
			
			doSelectiveBlurAndUpdateImage();
		}
		
	}

	private void doSelectiveBlurAndUpdateImage() {
		System.out.println("doing selective blur for the rectangle:"+selectiveBlurRectangle.getX()+":"+selectiveBlurRectangle.getY()+":"+selectiveBlurRectangle.getWidth()+":"+selectiveBlurRectangle.getHeight());
		BufferedImage selectiveBlurTempImage= img.getSubimage(
				(int)selectiveBlurRectangle.getX(),
				(int)selectiveBlurRectangle.getY(),
				(int)selectiveBlurRectangle.getWidth(),
				(int)selectiveBlurRectangle.getHeight());
		
		BufferedImage resultImage=ImageEffects.addBlurUsingConvolveOp(selectiveBlurTempImage);
		
		//set the modified data in the original image
		saveOriginalImage();
		img.getRaster().setRect((int)selectiveBlurRectangle.getX(), (int)selectiveBlurRectangle.getY(), resultImage.getData());
		repaint();
	}
	@Override
	public void mouseReleased(java.awt.event.MouseEvent e) {
		//System.out.println("Mouse Release:"+e.getX()+":"+e.getY());
		//pointDraggingEnd=null;
		pointDraggingEnd=e.getPoint();
		
		if(getSpecialActionRequested()==NORMAL){
			if(isMouseDragging){
				//draw rectangle
				temporaryRectangle=false;
				//System.out.println("Draw Rect");			
				repaint();
				isCropAreaSelected=true;
				//pointDraggingEnd=null;
				//pointDraggingStart=null;
				//isMouseDragging=false;
			}
			drawOriginalImage();
		}		
		
		
	}

	public void doCropImage() {
		if(isCropAreaSelected){
			if(cropHeight<=0 || cropWidth<=0)
				return;
			
			saveOriginalImage();
			BufferedImage imgTmp=new BufferedImage(cropWidth, cropHeight, img.getType());
			WritableRaster wr=img.getRaster().createWritableChild(cropStartX, cropStartY, cropWidth, cropHeight, 0,0,null);
			
			imgTmp.setData(wr);
			img=imgTmp;
			
			
			//imgTmp.setData(img.getData(new Rectangle(cropStartX, cropStartY, cropWidth, cropHeight)));
			//img=imgTmp; 
		    
		}
		
	}

	

}
