package shidhartha.saikia.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageInfo implements Cloneable{
	BufferedImage image;
	short [][] argb;
	public int width;
	public int height;
		
	public ImageInfo(String fileName) throws IOException{
		File input = new File(fileName);
		image = ImageIO.read(input);
		width=image.getWidth();
		height=image.getHeight();
		
				
		//calculateARGB();
		
	}
	
	public ImageInfo(BufferedImage img) throws IOException{
		image = img;
		width=image.getWidth();
		height=image.getHeight();
		
		//calculateARGB();
		
	}
	public ImageInfo(int width1, int height1) {
		
		this.width=width1;
		this.height=height1;
		this.image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		
		//intializeARGB();
			 
		
	}
	
	public void intializeARGB() {
		argb = new short [height*width][4];
	}
	
	public void calculateARGB() {
		intializeARGB();
		
		 for(int i = 0; i < height; i++){
	            for(int j = 0; j < width; j++){
	            	argb[i*width+j]=getPixelData(image, j, i);
	            }
		 }
	}
	
	
	public BufferedImage getBufferedImage(){
		return image;
	}
		
	public short[][] getARGB(){
		return argb;
	}
	public void setARGB(short [][] ARGB){
		this.argb=ARGB;
	}
	private static short[] getPixelData(BufferedImage img, int x, int y) {
		int pixel = img.getRGB(x, y);

		int argb[] = new int[] {
			(pixel >> 24) & 0xff, //alpha
 		    (pixel >> 16) & 0xff, //red
		    (pixel >>  8) & 0xff, //green
		    (pixel      ) & 0xff  //blue
		};

		
		short[] s_argb=new short[argb.length];
		
		for (int i=0;i<s_argb.length;i++){
			s_argb[i]=(short)argb[i];
		}
		return s_argb;
	}
	public void setPixelData(int x, int y, int alpha, int red, int green, int blue){
		
		int pixel=alpha << 24 | red << 16 | green << 8 | blue;
		image.setRGB(x, y, pixel);
			
	}
	public void setPixelData(int x, int y, int luminocity){
		
		//int pixel=alpha << 24 | red << 16 | green << 8 | blue;
		image.setRGB(x, y, luminocity);
			
	}
	
	

}
