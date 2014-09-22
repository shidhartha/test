package shidhartha.saikia;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import shidhartha.saikia.image.ImageEffects;

public class ImageEditPanel extends JPanel implements MouseListener,
		MouseMotionListener {
	// private static ImageEditPanel instance=null;

	public static final short NORMAL = 0;

	public static final short SELECTIVE_ACTIONS_STARTING_POINT = 101;
	public static final short SELECTIVE_FOCUS = 101;
	public static final short SELECTIVE_BLUR = 102;
	public static final short SELECTIVE_GRAY = 103;
	public static final short SELECTIVE_ACTIONS_END_POINT = 200;

	// to keep track of key pressed
	KeyEvent keyPressedEvent;

	int specialActionRequested = 0; // 0= nothing, 1=selective blur

	private BufferedImage imageOriginal;
	private BufferedImage imgMain;

	private BufferedImage tmpImg;

	boolean isMouseDragging = false;
	boolean temporaryRectangle = true;

	Point pointDraggingStart;
	Point pointDraggingEnd;

	// Point currentMousePoint;
	MouseEvent currentMouseEvent;

	boolean isCropAreaSelected = false;
	int cropStartX = -1;
	int cropStartY = -1;
	int cropWidth = -1;
	int cropHeight = -1;

	Rectangle selectiveActionRectangle;

	private double trasnformRatio = 1;

	private static final long serialVersionUID = 1L;

	private int selectiveActionBoundaryParameterXAxis = 50;
	private int selectiveActionBoundaryParameterYAxis = 50;

	public ImageEditPanel(String fileName) throws IOException {
		saveOriginalImage();
		setImgMain(ImageIO.read(new File(fileName)));
		super.setPreferredSize(new Dimension(getImgMain().getWidth(),
				getImgMain().getHeight()));
		printImageProperties();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		// this.addKeyListener(this);
	}

	public ImageEditPanel(BufferedImage img1) {
		saveOriginalImage();
		setImgMain(img1);
		super.setPreferredSize(new Dimension(getImgMain().getWidth(),
				getImgMain().getHeight()));
		printImageProperties();
	}

	public void saveOriginalImage() {
		imageOriginal = copyImage(getImgMain());
		/*
		 * if (img!=null){ imageOriginal=null; //System.gc(); imageOriginal =
		 * new BufferedImage(img.getWidth(),img.getHeight(),img.getType());
		 * imageOriginal.setData(img.getData()); }
		 */
	}

	public BufferedImage copyImage(BufferedImage imgSource) {
		BufferedImage imgDest = null;
		System.out.println("Total/Free Memory: (MB) "
				+ Runtime.getRuntime().totalMemory() / 1000000 + "/"
				+ Runtime.getRuntime().freeMemory() / 1000000);
		if (imgSource != null) {
			// imgDest=null;
			// System.gc();
			imgDest = new BufferedImage(imgSource.getWidth(),
					imgSource.getHeight(), imgSource.getType());
			imgDest.setData(imgSource.getData());
		}
		return imgDest;
	}

	private void printImageProperties() {
		String[] s1 = getImgMain().getPropertyNames();
		if (s1 != null)
			for (String s : s1) {
				if (s != null)
					System.out.println(s + " : " + getImgMain().getProperty(s));
			}
	}

	public void paint(Graphics g) {

		Graphics2D g2D = (Graphics2D) g;
		// if (trasnformRatio != 1) {
		AffineTransform transform = new AffineTransform();
		transform.scale(trasnformRatio, trasnformRatio);
		g2D.setTransform(transform);
		g2D.drawImage(getImgMain(), 0, 0, null);
		// } else {
		// g.drawImage(getImgMain(), 0, 0, null);
		// }

		if (isMouseDragging && getSpecialActionRequested() == NORMAL) {
			drawCropRectangle(g2D);
		} else if (getSpecialActionRequested() >= SELECTIVE_ACTIONS_STARTING_POINT
				&& getSpecialActionRequested() < SELECTIVE_ACTIONS_END_POINT) {
			drawSelectiveActionBoundary(g2D,
					selectiveActionBoundaryParameterXAxis,
					selectiveActionBoundaryParameterYAxis);
		}
		
		g2D.dispose();

	}

	private void drawSelectiveActionBoundary(Graphics2D g2D,
			int boundarySizeParamX,int boundarySizeParamY) {
		// System.out.println("trasnformRatio:"+trasnformRatio);
		selectiveActionRectangle = new Rectangle(
				(int)(((int) currentMouseEvent.getPoint().getX() - boundarySizeParamX)/trasnformRatio), 
				(int)(((int) currentMouseEvent.getPoint().getY() - boundarySizeParamY)/trasnformRatio), 
				(int)((2 * boundarySizeParamX)/trasnformRatio),
				(int)((2 * boundarySizeParamY)/trasnformRatio));

		Color originalColor = g2D.getColor();
		g2D.setColor(Color.yellow);
		g2D.drawRect(selectiveActionRectangle.x, selectiveActionRectangle.y,
				selectiveActionRectangle.width, selectiveActionRectangle.height);
		g2D.setColor(originalColor);

	}

	private void drawCropRectangle(Graphics2D g2D) {
		int x1 = (int) pointDraggingStart.getX();
		int y1 = (int) pointDraggingStart.getY();
		int x2 = (int) pointDraggingEnd.getX();
		int y2 = (int) pointDraggingEnd.getY();

		cropStartX = (x1 < x2) ? x1 : x2;
		cropStartY = (y1 < y2) ? y1 : y2;

		int xEnd = (x1 >= x2) ? x1 : x2;
		int yEnd = (y1 >= y2) ? y1 : y2;

		// apply transformation ration as the image might have been re-scaled
		cropStartX = (int) (cropStartX / trasnformRatio);
		cropStartY = (int) (cropStartY / trasnformRatio);
		xEnd = (int) (xEnd / trasnformRatio);
		yEnd = (int) (yEnd / trasnformRatio);

		cropWidth = xEnd - cropStartX;
		cropHeight = yEnd - cropStartY;
		// System.out.println("Drawing Rect: xStart="+xStart+" yStart="+yStart+" w="+w+" h="+h);
		if (!temporaryRectangle) {
			isMouseDragging = false;
			pointDraggingEnd = null;
			pointDraggingStart = null;
			doHighLightArea(cropStartX, cropStartY, cropWidth, cropHeight);
		}
		Color originalColor = g2D.getColor();
		g2D.setColor(Color.white);
		g2D.drawRect(cropStartX, cropStartY, cropWidth, cropHeight);
		g2D.setColor(originalColor);
	}

	private void doHighLightArea(int x, int y, int w, int h) {
		// hightlight the area by reducing light on the other area
		tmpImg = copyImage(getImgMain());
		ImageEffects.doAdjustBrightnessAndContrastAroundRectangle(getImgMain(),
				getImgMain(), x, y, w, h, -70, -50);

		repaint();
	}

	private void drawOriginalImage() {
		cropStartX = -1;
		cropStartY = -1;
		cropHeight = -1;
		cropWidth = -1;
		if (tmpImg != null) {
			// System.out.println("tmpImg is not null");
			setImgMain(tmpImg);
			// repaint();
			tmpImg = null;
		}

	}

	public BufferedImage getBufferedImage() {
		return getImgMain();
	}

	public BufferedImage getOriginalBufferedImage() {
		return imageOriginal;
	}

	public void setBufferedImage(BufferedImage bImg) {
		saveOriginalImage();
		setImgMain(bImg);

	}

	/*
	 * public double getTrasnformRatio() { return trasnformRatio; }
	 */

	public void setTrasnformRatio(double trasnformRatio) {
		this.trasnformRatio = trasnformRatio;
	}

	@Override
	public void mouseDragged(java.awt.event.MouseEvent e) {
		// System.out.println("Mouse dragged:"+e.getX()+":"+e.getY());
		isMouseDragging = true;
		if (getSpecialActionRequested() == NORMAL) {
			temporaryRectangle = true;
			if (pointDraggingStart == null) {
				pointDraggingStart = e.getPoint();
			} else {
				pointDraggingEnd = e.getPoint();
				repaint();
				isCropAreaSelected = true;
			}
		}

		currentMouseEvent = e;
	}

	@Override
	public void mouseMoved(java.awt.event.MouseEvent e) {
		// currentMousePoint=e.getPoint();
		currentMouseEvent = e;
		
		if (getKeyPressedEvent() != null) {
			if (getKeyPressedEvent().getKeyChar() == 'f'
					&& getSpecialActionRequested() == SELECTIVE_FOCUS) {
				doSelectiveFocusAndUpdateImage();
			} else if (getKeyPressedEvent().getKeyChar() == 'b'
					&& getSpecialActionRequested() == SELECTIVE_BLUR) {
				doSelectiveBlurAndUpdateImage();
			}
			if (getKeyPressedEvent().getKeyChar() == 'g'
					&& getSpecialActionRequested() == SELECTIVE_GRAY) {
				doSelectiveGRAYAndUpdateImage();
			}

		}

		repaint();
	}

	@Override
	public void mouseClicked(java.awt.event.MouseEvent e) {
		/* System.out.println("Mouse Clicked:"+e.getX()+":"+e.getY()); */
		drawOriginalImage();
		// repaint();
		currentMouseEvent = e;
	}

	@Override
	public void mouseEntered(java.awt.event.MouseEvent e) {
		currentMouseEvent = e;
	}

	@Override
	public void mouseExited(java.awt.event.MouseEvent e) {
		currentMouseEvent = e;
	}

	@Override
	public void mousePressed(java.awt.event.MouseEvent e) {
		// System.out.println("Mouse Pressed:"+e.getX()+":"+e.getY());
		// pointDraggingStart=null;
		// isMouseDragging=true;
		pointDraggingStart = e.getPoint();

		if (getKeyPressedEvent() != null) {
			if (getKeyPressedEvent().getKeyChar() == 'f'
					&& getSpecialActionRequested() == SELECTIVE_FOCUS) {
				doSelectiveFocusAndUpdateImage();
			} else if (getKeyPressedEvent().getKeyChar() == 'b'
					&& getSpecialActionRequested() == SELECTIVE_BLUR) {
				doSelectiveBlurAndUpdateImage();
			}
			if (getKeyPressedEvent().getKeyChar() == 'g'
					&& getSpecialActionRequested() == SELECTIVE_GRAY) {
				doSelectiveGRAYAndUpdateImage();
			}

		}
		currentMouseEvent = e;

	}

	@Override
	public void mouseReleased(java.awt.event.MouseEvent e) {
		// System.out.println("Mouse Release:"+e.getX()+":"+e.getY());
		// pointDraggingEnd=null;
		pointDraggingEnd = e.getPoint();

		if (getSpecialActionRequested() == NORMAL) {
			if (isMouseDragging) {
				// draw rectangle
				temporaryRectangle = false;
				// System.out.println("Draw Rect");
				repaint();
				isCropAreaSelected = true;
				// pointDraggingEnd=null;
				// pointDraggingStart=null;
				// isMouseDragging=false;
			}
			drawOriginalImage();
		}

		currentMouseEvent = e;
	}

	private void doSelectiveFocusAndUpdateImage() {
		try {
			saveOriginalImage();
			// BufferedImage selectiveActionTempImage = getTempBufferedImage();
			Raster copyData = getImgMain().getData(selectiveActionRectangle);
			BufferedImage resultImage = ImageEffects
					.addBlurUsingConvolveOp(getImgMain());

			// set the saved image to modified image

			resultImage.getRaster().setRect(copyData);

			setImgMain(resultImage);
		} catch (Exception e) {
			System.out.println(e.getMessage());

		}
	}

	private void doSelectiveBlurAndUpdateImage() {
		try {
			BufferedImage selectiveActionTempImage = getTempBufferedImage();
			BufferedImage resultImage = ImageEffects
					.addBlurUsingConvolveOp(selectiveActionTempImage);

			// set the modified data in the original image
			saveOriginalImage();
			getImgMain().getRaster().setRect(
					(int) selectiveActionRectangle.getX(),
					(int) selectiveActionRectangle.getY(),
					resultImage.getData());
			repaint();
		} catch (Exception e) {
			System.out.println(e.getMessage());

		}
	}

	private BufferedImage getTempBufferedImage() {
		// System.out.println("doing selective action for the rectangle:"+selectiveActionRectangle.getX()+":"+selectiveActionRectangle.getY()+":"+selectiveActionRectangle.getWidth()+":"+selectiveActionRectangle.getHeight());
		BufferedImage selectiveActionTempImage = getImgMain().getSubimage(
				(int) selectiveActionRectangle.getX(),
				(int) selectiveActionRectangle.getY(),
				(int) selectiveActionRectangle.getWidth(),
				(int) selectiveActionRectangle.getHeight());
		return selectiveActionTempImage;
	}

	private void doSelectiveGRAYAndUpdateImage() {
		try {
			BufferedImage selectiveActionTempImage = getTempBufferedImage();
			BufferedImage resultImage = ImageEffects
					.addGrayScale(selectiveActionTempImage);

			// set the modified data in the original image
			saveOriginalImage();
			getImgMain().getRaster().setRect(
					(int) selectiveActionRectangle.getX(),
					(int) selectiveActionRectangle.getY(),
					resultImage.getData());
			repaint();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public void doCropImage() {
		if (isCropAreaSelected) {
			if (cropHeight <= 0 || cropWidth <= 0)
				return;

			saveOriginalImage();
			BufferedImage imgTmp = new BufferedImage(cropWidth, cropHeight,
					getImgMain().getType());
			WritableRaster wr = getImgMain().getRaster().createWritableChild(
					cropStartX, cropStartY, cropWidth, cropHeight, 0, 0, null);

			imgTmp.setData(wr);
			setImgMain(imgTmp);

			// imgTmp.setData(img.getData(new Rectangle(cropStartX, cropStartY,
			// cropWidth, cropHeight)));
			// img=imgTmp;

		}

	}

	public KeyEvent getKeyPressedEvent() {
		return keyPressedEvent;
	}

	public void setKeyPressedEvent(KeyEvent keyPressedEvent) {
		this.keyPressedEvent = keyPressedEvent;

		switch (this.keyPressedEvent.getKeyCode()) {

		case 37:// user press up arrow
			// and selective blur is enabled then it will increase the blur
			// rectangle size
			if (getSpecialActionRequested() >= SELECTIVE_ACTIONS_STARTING_POINT
					&& getSpecialActionRequested() < SELECTIVE_ACTIONS_END_POINT) {
				setSelectiveActionBoundaryParameterXAxis(getSelectiveActionBoundaryParameterXAxis() - 1);
			}
			break;
		case 38:// user press up arrow
			// and selective blur is enabled then it will increase the blur
			// rectangle size
			if (getSpecialActionRequested() >= SELECTIVE_ACTIONS_STARTING_POINT
					&& getSpecialActionRequested() < SELECTIVE_ACTIONS_END_POINT) {
				setSelectiveActionBoundaryParameterYAxis(getSelectiveActionBoundaryParameterYAxis() + 1);
			}
			break;
		case 39:// user press up arrow
			// and selective blur is enabled then it will increase the blur
			// rectangle size
			if (getSpecialActionRequested() >= SELECTIVE_ACTIONS_STARTING_POINT
					&& getSpecialActionRequested() < SELECTIVE_ACTIONS_END_POINT) {
				setSelectiveActionBoundaryParameterXAxis(getSelectiveActionBoundaryParameterXAxis() + 1);
			}
			break;
		case 40:// user press downarrow arrow
			// and selective blur is enabled then it will decrease the blur
			// rectangle size
			if (getSpecialActionRequested() >= SELECTIVE_ACTIONS_STARTING_POINT
					&& getSpecialActionRequested() < SELECTIVE_ACTIONS_END_POINT) {
				setSelectiveActionBoundaryParameterYAxis(getSelectiveActionBoundaryParameterYAxis() - 1);
			}
			break;

		default:
			// System.out.println("default");

		}
	}

	public int getSpecialActionRequested() {
		return specialActionRequested;
	}

	public void setSpecialActionRequested(int specialActionRequested) {
		this.specialActionRequested = specialActionRequested;
	}

	private BufferedImage getImgMain() {
		return imgMain;
	}

	private void setImgMain(BufferedImage imgMain1) {
		this.imgMain = imgMain1;
		repaint();
	}

	public int getSelectiveActionBoundaryParameterXAxis() {
		return selectiveActionBoundaryParameterXAxis;
	}

	public void setSelectiveActionBoundaryParameterXAxis(
			int selectiveBlurBoundaryParameterX) {
		this.selectiveActionBoundaryParameterXAxis = selectiveBlurBoundaryParameterX;
//		System.out.println("selectiveActionBoundaryParameterXAxis"
//				+ selectiveActionBoundaryParameterXAxis);
		repaint();
	}
	
	public int getSelectiveActionBoundaryParameterYAxis() {
		return selectiveActionBoundaryParameterYAxis;
	}

	public void setSelectiveActionBoundaryParameterYAxis(
			int selectiveBlurBoundaryParameterY) {
		this.selectiveActionBoundaryParameterYAxis = selectiveBlurBoundaryParameterY;
//		System.out.println("selectiveActionBoundaryParameterYAxis"
//				+ selectiveActionBoundaryParameterYAxis);
		repaint();
	}

}
