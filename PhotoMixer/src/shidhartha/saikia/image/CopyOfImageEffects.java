package shidhartha.saikia.image;

public class CopyOfImageEffects {
	
	
	public static ImageInfo addNegative(ImageInfo image){
		
		image.calculateARGB();
		
		short[][] argb=image.getARGB();
		
		for (int h=0; h<image.height; h++){
			for (int w=0 ; w<image.width; w++){
				image.setPixelData(w,h,argb[h*image.width+w][0],255-argb[h*image.width+w][1],255-argb[h*image.width+w][2],255-argb[h*image.width+w][3]);
			}
		}
		return image;
		
	}
	
	public static ImageInfo addGrayScale(ImageInfo image){
		
		image.calculateARGB();
		
		short[][] argb=image.getARGB();
		
		for (int h=0; h<image.height; h++){
			for (int w=0 ; w<image.width; w++){
				double gray=(0.21*argb[h*image.width+w][1]) + (0.71*argb[h*image.width+w][2]) + (0.07*argb[h*image.width+w][3]);
								
				image.setPixelData(w,h,argb[h*image.width+w][0],(int)gray,(int)gray,(int)gray);
			}
		}
		
		return image;
		
	}

}
