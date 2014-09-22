package shidhartha.saikia.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class ImageEffects {
	
	
	public static void addNegative(BufferedImage image){	
		
		int height=image.getHeight();
		int width=image.getWidth();
		
		for (int h=0; h<height; h++){
			for (int w=0 ; w<width; w++){
				
				int pixel=image.getRGB(w, h);
				int [] argb=getArgbFromPixelInt(pixel);
				
				argb[1]=255-argb[1];//red
				argb[2]=255-argb[2];//green
				argb[3]=255-argb[3];//blue
				
				int newPixel=getPixelIntFromArgb(argb[0], argb[1], argb[2], argb[3]);
				image.setRGB(w, h, newPixel);				
			}
		}		
		
	}
	
	public static void doAdjustRGB(BufferedImage sourceImage ,BufferedImage destImage , int deltaRed, int deltaGreen, int deltaBlue){	
		
		int height=sourceImage.getHeight();
		int width=sourceImage.getWidth();
		
		for (int h=0; h<height; h++){
			for (int w=0 ; w<width; w++){
				
				int pixel=sourceImage.getRGB(w, h);
				int [] argb=getArgbFromPixelInt(pixel);
				
				argb[1]=argb[1]+deltaRed;//red
				argb[2]=argb[2]+deltaGreen;//green
				argb[3]=argb[3]+deltaBlue;//blue
				
				for (int i=0;i<argb.length;i++){
					if(argb[i]>255)
						argb[i]=255;
					else if (argb[i]<0)
						argb[i]=0;
				}
				
				int newPixel=getPixelIntFromArgb(argb[0], argb[1], argb[2], argb[3]);
				destImage.setRGB(w, h, newPixel);				
			}
		}		
	}
	
	public static void doAdjustHSB(BufferedImage sourceImage ,BufferedImage destImage , int deltaHue, int deltaSaturation, int deltaBrightness){	
		
		int height=sourceImage.getHeight();
		int width=sourceImage.getWidth();
		
		for (int h=0; h<height; h++){
			for (int w=0 ; w<width; w++){
				
				int pixel=sourceImage.getRGB(w, h);
				int [] argb=getArgbFromPixelInt(pixel);
				
				float[] hsb = Color.RGBtoHSB(argb[1], argb[2], argb[3], null);
				
				hsb[0]=hsb[0]+deltaHue;//hue
				hsb[1]=hsb[1]+deltaSaturation;//saturation
				hsb[2]=hsb[2]+deltaBrightness;//brightness
				
				for (int i=0;i<hsb.length;i++){
					if(hsb[i]>255)
						hsb[i]=255;
					else if (hsb[i]<0)
						hsb[i]=0;
				}
				
				int rgbNewPixel = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
				destImage.setRGB(w, h, rgbNewPixel);
				//argb=getArgbFromPixelInt(rgbNewPixel);
				
				//int newPixel=getPixelIntFromArgb(argb[0], argb[1], argb[2], argb[3]);
				//destImage.setRGB(w, h, newPixel);				
			}
		}		
	}

	private static int getPixelIntFromArgb(int alpha, int red, int green, int blue) {
		return (alpha << 24 | red << 16 | green << 8 | blue);
	}

	private static int[] getArgbFromPixelInt(int pixel) {
		int[] argb= new int[] {
				(pixel >> 24) & 0xff, //alpha
			    (pixel >> 16) & 0xff, //red
			    (pixel >>  8) & 0xff, //green
			    (pixel      ) & 0xff  //blue
			};
		return argb;
	}
	
	public static BufferedImage addGrayScale(BufferedImage image){
		int height=image.getHeight();
		int width=image.getWidth();
		
		//for returning a new image
		BufferedImage result=new BufferedImage(width,height,image.getType());
	
		for (int h=0; h<height; h++){
			for (int w=0 ; w<width; w++){
				
				int newPixel = getGrayPixel(image, h, w);
				image.setRGB(w, h, newPixel);				
				
			}
		}	
		result.setData(image.getData());
		return result;
	}
	
	public static BufferedImage addSepiaEffect(BufferedImage image, int depth){
		int height=image.getHeight();
		int width=image.getWidth();
		
		//for returning a new image
		BufferedImage result=new BufferedImage(width,height,image.getType());
		
		//addGrayScale(image);	
		for (int h=0; h<height; h++){
			for (int w=0 ; w<width; w++){
				
				int newPixel = getGrayPixel(image, h, w);
				
				//now do sepia
				int sepiaPixel = getSepiaPixel(depth, newPixel);
				result.setRGB(w, h, sepiaPixel);				
				
			}
		}	
		//result.setData(image.getData());
		return result;
	}

	private static int getSepiaPixel(int depth, int newPixel) {
		int [] argb=getArgbFromPixelInt(newPixel);	
		int rr=(int)(argb[1]+(depth*2));
		int gg=(int)(argb[2]+depth);
		int bb=(int)(argb[3]-depth);
		
		argb[1]=(rr<=(depth*2-1))? argb[1]:rr;//red
		argb[2]=(gg<=(depth-1))? argb[2]:gg;//green
		//argb[3]=bb<0?0:(bb>255?255:bb);//blue
		
		
		int sepiaPixel=getPixelIntFromArgb(argb[0], rr, gg, 0);
		return sepiaPixel;
	}

	private static int getGrayPixel(BufferedImage image, int h, int w) {
		int pixel=image.getRGB(w, h);
		int [] argbTemp=getArgbFromPixelInt(pixel);	
		
		//convert to grayscal
		argbTemp[1]=(int)(0.21*argbTemp[1]);//red
		argbTemp[2]=(int)(0.71*argbTemp[2]);//green
		argbTemp[3]=(int)(0.07*argbTemp[3]);//blue				
		int gray=argbTemp[1]+argbTemp[2]+argbTemp[3];				
		int newPixel=getPixelIntFromArgb(argbTemp[0], gray, gray, gray);
		return newPixel;
	}
	public static void addBlackAndWhite(BufferedImage image,int threshold){
		int height=image.getHeight();
		int width=image.getWidth();
		
	
		for (int h=0; h<height; h++){
			for (int w=0 ; w<width; w++){
				
				int pixel=image.getRGB(w, h);
				int [] argb=getArgbFromPixelInt(pixel);
				
				argb[1]=(int)(0.21*argb[1]);//red
				argb[2]=(int)(0.71*argb[2]);//green
				argb[3]=(int)(0.07*argb[3]);//blue
				
				int gray=argb[1]+argb[2]+argb[3];
				
				//int threshold=150;
				
				if (gray < threshold)
					gray=0;
				else
					gray=255;
				
				int newPixel=getPixelIntFromArgb(argb[0], gray, gray, gray);
				image.setRGB(w, h, newPixel);
								
			}
		}
	}
public static void doAdjustRGBandHSB(BufferedImage sourceImage ,BufferedImage destImage , int deltaRed, int deltaGreen, int deltaBlue, int deltaHue, int deltaSaturation, int deltaBrightness){	
		
		int height=sourceImage.getHeight();
		int width=sourceImage.getWidth();
		
		for (int h=0; h<height; h++){
			for (int w=0 ; w<width; w++){
				
				int pixel=sourceImage.getRGB(w, h);
				int [] argb=getArgbFromPixelInt(pixel);
				
				argb[1]=argb[1]+deltaRed;//red
				argb[2]=argb[2]+deltaGreen;//green
				argb[3]=argb[3]+deltaBlue;//blue
				
				for (int i=0;i<argb.length;i++){
					if(argb[i]>255)
						argb[i]=255;
					else if (argb[i]<0)
						argb[i]=0;
				}
				
				//int newPixel=getPixelIntFromArgb(argb[0], argb[1], argb[2], argb[3]);
								
				//int pixel=sourceImage.getRGB(w, h);
				//int [] argb=getArgbFromPixelInt(pixel);
				
				float[] hsb = Color.RGBtoHSB(argb[1], argb[2], argb[3], null);
				
				//hsb[0]=(int)((double)hsb[0]*(double)(1+(deltaHue*0.01)));//hue
				//hsb[1]=(int)((double)hsb[1]*(double)(1+(deltaSaturation*0.01)));//saturation
				//hsb[2]=(int)((double)hsb[2]*(double)(1+(deltaBrightness*0.01)));//brightness
			
				hsb[0]=hsb[0]+deltaHue;//hue
				hsb[1]=hsb[1]+deltaSaturation;//saturation
				hsb[2]=hsb[2]+deltaBrightness;//brightness
				
				/*for (int i=0;i<hsb.length;i++){
					if(hsb[i]>255)
						hsb[i]=255;
					else if (hsb[i]<0)
						hsb[i]=0;
				}*/
				
				//hue range 0-359
				if(hsb[0]<0)
					hsb[0]=0;
				else if(hsb[0]>255)
					hsb[0]=359;
				
				//saturation range 0-100
				if(hsb[1]<0)
					hsb[1]=0;
				else if(hsb[1]>255)
					hsb[1]=255;
				//brightness range 0-100
				if(hsb[2]<0)
					hsb[2]=0;
				else if(hsb[2]>255)
					hsb[2]=255;
				
				
				int rgbNewPixel = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
				destImage.setRGB(w, h, rgbNewPixel);
				//argb=getArgbFromPixelInt(rgbNewPixel);
				
				//int newPixel=getPixelIntFromArgb(argb[0], argb[1], argb[2], argb[3]);
				//destImage.setRGB(w, h, newPixel);				
			}
		}		
	}

	public static void doAdjustBrightnessAndContrast(	BufferedImage sourceImage,	BufferedImage destImage, int brightnessDelta,	int contrastDelta) {
		
		int height=sourceImage.getHeight();
		int width=sourceImage.getWidth();
		
		for (int h=0; h<height; h++){
			for (int w=0 ; w<width; w++){
				
				int pixel=sourceImage.getRGB(w, h);
				int [] argb=getArgbFromPixelInt(pixel);
				
				
				//brightness adjustment
				
				argb[1]=argb[1]+brightnessDelta;//red
				argb[2]=argb[2]+brightnessDelta;//green
				argb[3]=argb[3]+brightnessDelta;//blue
				
				//contrast adjustment
				argb[1]=(int)((double)argb[1]* (double)(1+contrastDelta*0.01));//red
				argb[2]=(int)((double)argb[2]* (double)(1+contrastDelta*0.01));//green
				argb[3]=(int)((double)argb[3]* (double)(1+contrastDelta*0.01));//blue
				
				
				for (int i=0;i<argb.length;i++){
					if(argb[i]>255)
						argb[i]=255;
					else if (argb[i]<0)
						argb[i]=0;
				}
				
				
				
				
				
				int newPixel=getPixelIntFromArgb(argb[0], argb[1], argb[2], argb[3]);
				destImage.setRGB(w, h, newPixel);				
			}
		}		
	}
	
	public static void doAdjustBrightnessAndContrastAroundRectangle(BufferedImage sourceImage,	BufferedImage destImage, int x, int y, int width, int height, int brightnessDelta,	int contrastDelta) {
		
		int heightOriginal=sourceImage.getHeight();
		int widthOriginal=sourceImage.getWidth();
		
		for (int h=0; h<heightOriginal; h++){
			for (int w=0 ; w<widthOriginal; w++){
				
				if(h>=y && h<=(y+height)){
					if(w>=x && w<=(x+width)){
						continue;
					}
					
				}
					
				
				int pixel=sourceImage.getRGB(w, h);
				int [] argb=getArgbFromPixelInt(pixel);
				
				
				//brightness adjustment
				
				argb[1]=argb[1]+brightnessDelta;//red
				argb[2]=argb[2]+brightnessDelta;//green
				argb[3]=argb[3]+brightnessDelta;//blue
				
				//contrast adjustment
				argb[1]=(int)((double)argb[1]* (double)(1+contrastDelta*0.01));//red
				argb[2]=(int)((double)argb[2]* (double)(1+contrastDelta*0.01));//green
				argb[3]=(int)((double)argb[3]* (double)(1+contrastDelta*0.01));//blue
				
				
				for (int i=0;i<argb.length;i++){
					if(argb[i]>255)
						argb[i]=255;
					else if (argb[i]<0)
						argb[i]=0;
				}
				
				
				
				
				
				int newPixel=getPixelIntFromArgb(argb[0], argb[1], argb[2], argb[3]);
				destImage.setRGB(w, h, newPixel);				
			}
		}	
		
	}
	
	public static BufferedImage addBlurUsingConvolveOp(BufferedImage sourceImg){
		BufferedImage resultImg= new BufferedImage(sourceImg.getWidth(),sourceImg.getHeight(),sourceImg.getType());
		float data[] = { 
//				//3x3 matrix    		
//	    		0.0625f, 0.125f, 0.0625f, 
//	    		0.125f, 0.25f, 0.125f,
//	    		0.0625f, 0.125f, 0.0625f 
//	    		
//	    		//7x7 matrix
	    		0.00000067f,	0.00002292f,	0.00019117f,	0.00038771f,	0.00019117f,	0.00002292f,	0.00000067f,
	    		0.00002292f,	0.00078634f,	0.00655965f,	0.01330373f,	0.00655965f,	0.00078633f,	0.00002292f,
	    		0.00019117f,	0.00655965f,	0.05472157f,	0.11098164f,	0.05472157f,	0.00655965f,	0.00019117f,
	    		0.00038771f,	0.01330373f,	0.11098164f,	0.22508352f,	0.11098164f,	0.01330373f,	0.00038771f,
	    		0.00019117f,	0.00655965f,	0.05472157f,	0.11098164f,	0.05472157f,	0.00655965f,	0.00019117f,
	    		0.00002292f,	0.00078633f,	0.00655965f,	0.01330373f,	0.00655965f,	0.00078633f,	0.00002292f,
	    		0.00000067f,	0.00002292f,	0.00019117f,	0.00038771f,	0.00019117f,	0.00002292f,	0.00000067f

	    		};
	    Kernel kernel = new Kernel(7, 7, data);
	    ConvolveOp convolve = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP,
	        null);
	    convolve.filter(sourceImg, resultImg);
	    return resultImg;
	    
	}
	
	public static BufferedImage addBlur(BufferedImage sourceImg,	int radius){
		
		
		int height=sourceImg.getHeight();
		int width=sourceImg.getWidth();	
		System.out.println("height="+height+" ,width="+width);
		
		BufferedImage destImg=new BufferedImage(width, height, sourceImg.getType());
		
        doHorizontalBlur(sourceImg, radius, height, width, destImg);
        doVerticalBlur(sourceImg, radius, height, width, destImg);
        return destImg;
    }

	private static void doVerticalBlur(BufferedImage sourceImg, int radius,
			int height, int width, BufferedImage destImg) {
		for (int x = 0; x < width; ++x) {
        	//System.out.println("hi:"+y);
            int total = 0;
            
            // Process entire window for first pixel
            for (int ky = 0; ky <= radius; ++ky){
            	//System.out.println("kx="+kx+" y="+y);            
                total += sourceImg.getRGB(x, ky);
            }
            	destImg.setRGB(x, 0, total / (radius + 1)); 
            
            // Subsequent pixels just update window total
            for (int y = radius+1; y < height; ++y) {
                // Subtract pixel leaving window
            	//System.out.println("(x - radius - 1="+(x - radius - 1)+" y="+y);
            	
                total -= sourceImg.getRGB(x, y - radius - 1);
                
                // Add pixel entering window
                try{
                	total += sourceImg.getRGB(x, y+ radius);
                	destImg.setRGB(x, y, total / (radius * 2 + 1)); 
                }catch(Exception e){
                	
                }

                
            }
        }
	}

	private static void doHorizontalBlur(BufferedImage sourceImg, int radius,
			int height, int width, BufferedImage destImg) {
		for (int y = 0; y < height; ++y) {
        	//System.out.println("hi:"+y);
            int total = 0;
            
            // Process entire window for first pixel
            for (int kx = 0; kx <= radius; ++kx){
            	//System.out.println("kx="+kx+" y="+y);            
                total += sourceImg.getRGB(kx, y);
            }
            	destImg.setRGB(0, y, total / (radius + 1)); 
            
            // Subsequent pixels just update window total
            for (int x = radius+1; x < width; ++x) {
                // Subtract pixel leaving window
            	//System.out.println("(x - radius - 1="+(x - radius - 1)+" y="+y);
            	
                total -= sourceImg.getRGB(x - radius - 1, y);
                
                // Add pixel entering window
                try{
                	total += sourceImg.getRGB(x + radius, y);
                	destImg.setRGB(x, y, total / (radius * 2 + 1)); 
                }catch(Exception e){
                	
                }                
            }
        }
	}

}
