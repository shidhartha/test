package shidhartha.saikia;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
//import javax.media.jai.JAI;
//import javax.media.jai.PlanarImage;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Histogram extends JPanel {

int[] bins = new int[256];
Histogram(int[] pbins) {
bins = pbins;
repaint();
}

@Override
protected void paintComponent(Graphics g) {
//g.drawLine();

for (int i = 0; i < 256; i++) {

 //System.out.println("bin[" + i + "]===" + bins[i]);
 g.drawLine(0 + i, 300, 0 + i, 300 - (bins[i]*3));
 //g.drawLine(200 + i, 200, 200 + i, 200-(bins[i])/1500);

 //  System.out.println("bin["+i+"]==="+bins[i]);
}

}


public static void main(String[] args) throws IOException {
JFrame frame = new JFrame();
frame.setSize(500, 500);
int[] pbins = new int[256];
int[] sbins = new int[256];
//PlanarImage image = JAI.create("fileload", "image12.tiff");
BufferedImage bi = ImageIO.read(new File("C:\\Users\\ssaikia\\Pictures\\Camera Foto\\2012-07-07\\DSC00441.jpg"));//image.getAsBufferedImage();
//BufferedImage bi = ImageIO.read(new File("C:\\Users\\ssaikia\\Pictures\\flower3.jpg"));//image.getAsBufferedImage();
//System.out.println("tipe is          " + bi.getType());
int[] pixel = new int[4];

int k = 0;
Color c = new Color(k);
Double d = 0.0;
Double d1;
for (int x = 0; x < bi.getWidth(); x++) {
 for (int y = 0; y < bi.getHeight(); y++) {
     pixel = bi.getRaster().getPixel(x, y, new int[4]);
     d=(0.2*pixel[1])+(0.7*pixel[2])+(0.07*pixel[3]);
     
     k=new Double(d).intValue();///256);
     
     //System.out.println("d="+d+" k="+k);

     sbins[k]++;
 }
 
 

}
double total_pixels=bi.getWidth()*bi.getHeight();
double max=0;
double[] percentageIntensity=new double[256];
for (int i=0;i<256;i++){
	
	percentageIntensity[i]=sbins[i]*1000/total_pixels;
	System.out.println("sbins["+i+"]:"+percentageIntensity[i]);
	sbins[i]=(int)percentageIntensity[i];
}
System.out.println("total pixel:"+total_pixels);
System.out.println("max:"+max);
//System.out.println("div factor:"+divFactor);
//System.out.println("total pixel:hist:"+total_pixels+"("+bi.getWidth()*bi.getHeight()+")");

System.out.println("completed" + d + "--" + k);
JTabbedPane jtp=new JTabbedPane();
ImageIcon im= new ImageIcon(bi);
//jtp.add("New image", new JLabel((im)));
 jtp.addTab("Histogram",new Histogram(sbins));
 frame.add(jtp);
frame.setVisible(true);
frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
}
}
