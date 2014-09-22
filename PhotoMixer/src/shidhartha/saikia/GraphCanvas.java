package shidhartha.saikia;

/*
 * The contents of this file are subject to the Sapient Public License
 * Version 1.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://carbon.sf.net/License.html.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is The Carbon Component Framework.
 *
 * The Initial Developer of the Original Code is Sapient Corporation
 *
 * Copyright (C) 2003 Sapient Corporation. All Rights Reserved.
 */



import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;



/** <p>This class provides a consistent space within to graph real
 * numbers. It provides features such as auto-centering and
 * real-time scaling. A user of this graph provides data by creating
 * one or more tracks and then adding real points to those. Calling
 * translate periodically allows you to create a scrolling graph as
 * well.</p>
 *
 * <p>This graph will also maintain tick marks that resize and
 * can be stuck to the sides of the screen so they are always
 * visible even if the origin is off-screen.</p>
 *
 * Copyright 2001 Sapient
 * @author Greg Hinkle
 * @version $Revision: 1.4 $ ($Author: dvoet $ / $Date: 2003/05/05 21:21:26 $)
 */
public class GraphCanvas extends JPanel {

    /** A list of  Track's that are a part of this graph */
    private Map tracks = new HashMap(11);

    /** The current graph bounds that are visible */
    protected Rectangle2D graphBounds;

    /** The portion of the entire height that should be researved as
     * a border, above and below the highest and lowest track points */
    private static final double BORDER_PERCENT = 0.1d;

    /** The background color for this graph */
    protected Color backgroundColor = new Color(204,204,204);

    protected static NumberFormat labelFormat = null;
    protected static NumberFormat bigNumberLabelFormat = null;

    /**
     * Instantiates a graph canvas
     */
    public GraphCanvas() {
        super();

        setBackground(Color.blue);

        this.graphBounds = new Rectangle2D.Double(-5,0,150,2);


        this.labelFormat = NumberFormat.getNumberInstance();
        this.labelFormat.setMaximumFractionDigits(2);

        this.bigNumberLabelFormat = NumberFormat.getNumberInstance();
        this.bigNumberLabelFormat.setMaximumFractionDigits(0);


        System.out.println("GraphCanvas::<init> - New GraphCanvas created.");
    }

    /**
     * <p>Sets the background color of this graph
     *
     * @param color the Color to set the background to
     */
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    /** Gets the bounds of the graphing space that are currently showing
     * on the screen.
     * @return Rectangle2D The bounds of the currently visible graph
     */
    public Rectangle2D getGraphBounds() {
        return this.graphBounds;
    }

    /**
     * Sets the bounds that this graph is displaying
     *
     * @param rect the Rectangle2D of the desired graph points
     */
    public void setGraphBounds(Rectangle2D rect) {
        this.graphBounds = rect;
    }


    public AffineTransform getTransform() {

        AffineTransform affineT =
        new AffineTransform(1d,0d,0d,-1d,0d,super.getParent().getHeight());



        // scale to current scale
        affineT.concatenate(
        AffineTransform.getScaleInstance(
        this.getBounds().getWidth() / this.graphBounds.getWidth(),
        this.getBounds().getHeight() / this.graphBounds.getHeight()));

        // translate to the current origin
        affineT.concatenate(
        AffineTransform.getTranslateInstance(
        -this.graphBounds.getX(),
        -this.graphBounds.getY()));

        return affineT;
    }



    // CLEAR ALL CURVES FROM PLOT
    public void clear() {

    }

    public void addTrack(String trackName) {
        this.tracks.put(trackName, new Track(trackName));
    }
    public void addTrack(String trackName,Color color) {
        this.tracks.put(trackName, new Track(trackName,color));
    }


    // ADD CURVE TO STORAGE (DOESN'T GRAPH UNTIL REPAINT()).
    public void addPoint(String track, Point2D point) {
        ((Track)this.tracks.get(track)).addPoint(point);
    }

    public Track getTrack(String trackName) {
        return (Track) this.tracks.get(trackName);
    }




    public void clearAll() {
        this.getGraphics().clearRect(
        (int)getBounds().getX(),
        (int)getBounds().getY(),
        (int)getBounds().getWidth(),
        (int)getBounds().getHeight());
    }

    public void paint(Graphics gg) {
        Graphics2D g = (Graphics2D) gg;
        g.setBackground(this.backgroundColor);
        // What is the current graph to panel transform
        AffineTransform newTrans = getTransform();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);


        // Erase this entire graph so that we can redraw it
        g.clearRect(
            (int)getBounds().getX(),
            (int)getBounds().getY(),
            (int)getBounds().getWidth(),
            (int)getBounds().getHeight());


        // This draws the tick marks and the tick values
        drawLines(g,newTrans);

        // This drawes the axeses
        drawAxes(g,newTrans);

        // This draws each of tracks in this graph
        drawTracks(g,newTrans);

        // This draws the keys for each graph
        drawKey(g,newTrans);
    }

    /**
     * <p>Draw the key to the tracks by calling thier toString</p>
     *
     * @param graphics2D the Graphics2D to draw to
     * @param transform the Affine Transform to use to determine how to draw
     */
    protected void drawKey(Graphics2D g, AffineTransform transform) {

        int start = 20;

        Iterator trackIterator = this.tracks.values().iterator();
        while(trackIterator.hasNext()) {

            Track track = (Track) trackIterator.next();

            String info = track.toString();
            // Will draw the key in the same color as it's line
            g.setColor(track.getColor());
            g.drawString(info,50, start+=25);
        }
    }

    protected void drawTracks(Graphics2D g, AffineTransform transform) {

        // Store original transform to restore it later
        // until I figure out how to track differences only
        AffineTransform origTrans = g.getTransform();


        // Transform for local drawing
        g.transform(transform);

        g.setColor(Color.orange);

        // Using a small stroke will minimize the width to a single output
        // level pixel up to a reasonable scaling size
        g.setStroke(new BasicStroke(0.001f));

        // Draw the tracks
        Iterator trackIterator = this.tracks.values().iterator();
        while (trackIterator.hasNext()) {
            Track track = (Track) trackIterator.next();
            g.setColor(track.getColor());
            GeneralPath path = track.getPath();

            g.draw(path);
        }

        // Reset transformation
        g.setTransform(origTrans);
    }

    /**
     * This draws the axes
     */
    protected void drawAxes(Graphics2D g,AffineTransform transform) {
        g.setColor(Color.white);

        Point2D origin = transform.transform(new Point2D.Double(0,0),null);

        // If you want to have rubber banding axes (always visible)
        Rectangle2D axesRect = new Rectangle2D.Double(5,5,this.bounds().getWidth()-10,this.bounds().getHeight());
        origin = floorPoint(origin,axesRect);

        Line2D x = new Line2D.Double(
        getBounds().getMinX(), origin.getY(),
        getBounds().getMaxX(), origin.getY());

        Line2D y = new Line2D.Double(
        origin.getX(), getBounds().getMinY(),
        origin.getX(), getBounds().getMaxY());

        g.draw(x);
        g.draw(y);
    }


    /**
     * <p>This finds the closest point on a rectangle's edge to a point outside
     * the rectangle or if that point is within the rectangle it is returned.
     * </p>
     *
     * @param point The point to rectangularly floor
     * @param rect The rectangle to floor within
     */
    public static Point2D floorPoint(Point2D point, Rectangle2D rect) {
        double x = point.getX();
        double y = point.getY();

        if (x < rect.getMinX())
            x = rect.getMinX();
        if (x > rect.getMaxX())
            x = rect.getMaxX();
        if (y < rect.getMinY())
            y = rect.getMinY();
        if (y > rect.getMaxY())
            y = rect.getMaxY();

        return new Point2D.Double(x,y);
    }


    /**
     * <p>This draws the tick marks in the graph
     *
     */
    protected void drawLines(Graphics2D g, AffineTransform transform) {

        g.setColor(Color.white);
        int REAL_TICK_SPACE = 40;
        int REAL_TICK_HEIGHT = 10;

        double graphTickSpaceX = (REAL_TICK_SPACE / transform.getScaleX());
        double graphTickSpaceY = (REAL_TICK_SPACE / Math.abs(transform.getScaleY()));


        Point2D origin = transform.transform(new Point2D.Float(0,0),null);

        // If you want to have rubber banding axes (always visible)
        Rectangle2D axesRect = new Rectangle2D.Double(5,5,this.bounds().getWidth()-10,this.bounds().getHeight());
        Point2D falseOrigin = floorPoint(origin,axesRect);

        double firstX = this.graphBounds.getMinX();

        Point2D pt = new Point2D.Float();
        for (double x = firstX; x <= (this.graphBounds.getMaxX()+graphTickSpaceX); x += graphTickSpaceX) {
            double tx = (Math.floor(x/graphTickSpaceX)) * graphTickSpaceX;
            pt.setLocation(tx,0);
            transform.transform(pt,pt);
            g.drawLine((int)pt.getX(),(int)falseOrigin.getY() - 5 ,(int)pt.getX(),(int)falseOrigin.getY() + 5);

            String label;
            if (tx > 10)
                label = this.bigNumberLabelFormat.format(tx);
            else
                label = this.labelFormat.format(tx);

            g.drawString(label,
                (float)pt.getX(),(float)falseOrigin.getY()-9);
        }


        double firstY = this.graphBounds.getMinY();
        for (double y = firstY; y <= (this.graphBounds.getMaxY()+graphTickSpaceY); y += graphTickSpaceY) {
            double ty = (Math.floor(y/graphTickSpaceY)) * graphTickSpaceY;
            pt.setLocation(0,ty);
            transform.transform(pt,pt);
            g.drawLine((int)falseOrigin.getX() - 5,(int)pt.getY() ,(int)falseOrigin.getX() + 5,(int)pt.getY());

            String label;
            if (ty > 10)
                label = this.bigNumberLabelFormat.format(ty);
            else
                label = this.labelFormat.format(ty);

            g.drawString(label,
                (float)falseOrigin.getX()+7,(float)pt.getY());

        }
    }

    public static class Track {
        protected String name;
        protected Color color = Color.black;  //Default to black
        protected GeneralPath path = new GeneralPath();
        protected boolean started = false;
        protected NumberFormat keyFormat;

        public Track(String name) {
            super();
            this.name = name;

            this.keyFormat = NumberFormat.getNumberInstance();
            this.keyFormat.setMaximumFractionDigits(2);
        }

        public Track(String name, Color color) {
            this(name);
            this.color = color;
        }

        public void setPath(GeneralPath path) {
            this.path = path;
        }

        public GeneralPath getPath() {
            return this.path;
        }

        public void addPoint(Point2D point) {
            if (path.getCurrentPoint() == null) {
                this.path.moveTo((float)point.getX(),(float)point.getY());
                this.started = true;
            } else {
                this.path.lineTo((float)point.getX(),(float)point.getY());
            }

        }

        public Color getColor() {
            return this.color;
        }
        public void setColor(Color color) {
            this.color = color;
        }

        public String toString() {
            String value = null;
            if (this.path.getCurrentPoint() != null) {
                double val = this.path.getCurrentPoint().getY();

                //NumberFormat nf = NumberFormat.getNumberInstance();
                value = this.keyFormat.format(val);
            }
            return this.name + ": " + value;
        }
    }


    /**
     * <p>Bounds the graph to the limits of the tracks verticaly providing a
     * useful scaling. A more intelligent implementation could have minimum
     * bounds to limit the bouncyness to startup.</p>
     */
    public void verticalBound() {
        Rectangle2D rect = null;
        Rectangle2D orig = getGraphBounds();

        Iterator trackIterator = this.tracks.values().iterator();
        while(trackIterator.hasNext()) {
            Track track = (Track) trackIterator.next();

            GeneralPath path = track.getPath();

            if (rect == null)
                rect = path.getBounds2D();
            else
                Rectangle.union(rect,path.getBounds2D(),rect);
        }
        Rectangle.union(rect,new Rectangle2D.Double(orig.getX(),0,1,1),rect);

        double border = rect.getHeight() * BORDER_PERCENT;

        setGraphBounds(new Rectangle2D.Double(
            orig.getMinX(),
            rect.getMinY()-border,
            orig.getWidth(),
            rect.getHeight()+(2d*border)));
    }

    public void clipOld() {
        Rectangle2D rect = getGraphBounds();

        //Rectangle2D orig = AffineTransform.getScaleInstance(1.5,1.5).createTransformedShape(getGraphBounds()).getBounds();

        Iterator trackIterator = this.tracks.values().iterator();
        double[] cs = new double[6];
        while(trackIterator.hasNext()) {
            Track track = (Track) trackIterator.next();

            GeneralPath path = track.getPath();

            GeneralPath newPath = new GeneralPath();

            PathIterator pIter = path.getPathIterator(new AffineTransform());
            while (!pIter.isDone()) {

                pIter.currentSegment(cs);

                //Point2D pt = new Point2D.Double(cs[0],cs[1]);
                if (cs[0] > rect.getMinX()) {
                    if (newPath.getCurrentPoint() == null)
                        newPath.moveTo((float)cs[0],(float)cs[1]);
                    else
                        newPath.lineTo((float)cs[0],(float)cs[1]);
                }

                /*
                System.out.println("Current Segment: " +
                cs[0] + ", " +
                cs[1] + ", " +
                cs[2] + ", " +
                cs[3] + ", " +
                cs[4] + ", " +
                cs[5]);
                 **/
                pIter.next();

            }
            track.setPath(newPath);
        }

    }

    /**
     * <p>Translates the main graph rect by x and y, horizontally and vertically
     * respectively.</p>
     */
    public void translate(double x, double y) {



        Rectangle2D rect = getGraphBounds();
        setGraphBounds(
        new Rectangle2D.Double(rect.getMinX()+x,
        rect.getMinY()+y,rect.getWidth(),rect.getHeight()));
    }

    public static void main(String[] args) throws Exception {

        GraphCanvas gc = new GraphCanvas();
        gc.show();

        JFrame frame = new JFrame("Memory Graph");
        frame.getContentPane().add(gc);
        frame.setSize(600,200);

        // TODO: Add window exit listener

        frame.show();
        gc.repaint();
        gc.paint((Graphics2D)gc.getGraphics());



        long start = System.currentTimeMillis();

        gc.addTrack("test", Color.cyan);
        gc.addTrack("test2", Color.blue);
        gc.addTrack("test3", Color.red);
        gc.addTrack("test4", Color.yellow);
        gc.addTrack("test5", Color.green);
        gc.addTrack("test6", Color.orange);
        gc.addTrack("test7", Color.pink);



        int i=0;
        while (true) {
            i++;

            Point2D pt = new Point2D.Float(i,((float)Math.cos(i/20f) + (float)Math.sin(i/40f)) * 3f);
            gc.addPoint("test",pt);

            Point2D pt2 = new Point2D.Float(i,(float)Math.cos(i/25.0f)*10f);
            gc.addPoint("test2",pt2);

            Point2D pt3 = new Point2D.Float(i,Math.min((float)Math.cos(Math.sin(i/4f))*13f - (float)Math.cos(i/80f)*20f,400f));
            gc.addPoint("test3",pt3);

            Point2D pt4 = new Point2D.Float(i,
                (float) Math.sin(.31*i)*2f +
                ((float)2f*(float)Math.cos(.07f*i))*8f);
            gc.addPoint("test4",pt4);

            Point2D pt5 = new Point2D.Float(i,
                (float) Math.cos(.66*i)*1f +
                ((float)2f*(float)Math.cos(.07f*i))*3f);
            gc.addPoint("test5",pt5);

            Point2D pt6 = new Point2D.Float(i,
                (float) Math.sin(.31*i)*2f +
                ((float)2f*(float)Math.cos(.07f*Math.tan(i)))*5f);
            gc.addPoint("test6",pt6);

            Point2D pt7 = new Point2D.Float(i,
                (float) Math.sin(i)*2f +
                ((float)2f*(float)Math.sin(.25f*i))*0.5f);
            gc.addPoint("test7",pt7);


            if (i > 150)
                gc.translate(1,0);

            gc.verticalBound();

            //if(i%100 == 0) {
                gc.clipOld();
            //}
            gc.repaint();

            Thread.sleep(10);
            if (i % 100 == 0) {
                System.out.println("Framewrate: " +
                    (100d / ((System.currentTimeMillis()-start)/1000d)));
                start = System.currentTimeMillis();
            }
        }

    }
}
