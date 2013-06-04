package catsdogs.sim;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

public class BoardPanel extends JPanel {
	final int           _CWIDTH                         = 600;
    final int           _CHEIGHT                        = 600;
	final int	    _MARGIN			    = 30;
        double              _ratio;
        final Font          _CVIEW_FONT                     = new Font("Courier", Font.BOLD, 35);
        final Font          _CAMOEBA_FONT                   = new Font("Courier", Font.BOLD, (max(Board.X, Board.Y) >= 50) ? (max(Board.X, Board.Y) >= 60) ? 8 : 9 : 10);
        final int           _CHOFFSET                       = _CAMOEBA_FONT.getSize() / 3;
        final int           _CVOFFSET                       = _CAMOEBA_FONT.getSize();
        final Color         _CBLACK                         = new Color(0.0f, 0.0f, 0.0f);
        final Color         _CBACKGROUND_COLOR              = new Color(1.0f, 1.0f, 1.0f);
        final int           _COUTLINE_THICKNESS             = 2;
        
    	public static Point2D MouseCoords;

        private GameEngine engine;
        
        int max(int a, int b)
        {
    	    return (a > b)?a:b;
        }

        
        //********************************************
        //*
        //* Constructor
        //*
        //********************************************            
        public BoardPanel(GameEngine engine) {// throws Exception {
            super();                                        
            setLayout(new BorderLayout());
            setBackground(_CBACKGROUND_COLOR);
            setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
                                                         BorderFactory.createLoweredBevelBorder()));
            setPreferredSize(new Dimension(_CWIDTH, _CHEIGHT));
            setMinimumSize(new Dimension(_CWIDTH, _CHEIGHT));
            setFont(_CVIEW_FONT);
            
            this.engine = engine;
            

        }       
        
        
        //********************************************
        //*
        //* paint() Override
        //*
        //********************************************            
    public void paintComponent(Graphics g)
	{
    	int X = Board.X;
    	int Y = Board.Y;


    	try {
    		super.paintComponent(g);

    		int width = getWidth() - _MARGIN;
    		int height = getHeight() - _MARGIN;
    		_ratio = (width < height ? (double) width / (double) max(Board.X, Board.Y) : (double) height / (double) max(Board.X, Board.Y));
    		g.setFont(_CAMOEBA_FONT);

			g.setColor(Color.BLACK);
    		g.drawLine(_MARGIN, _MARGIN,_MARGIN, _MARGIN + (int) (Y * _ratio));
    		g.drawLine(_MARGIN, _MARGIN, _MARGIN + (int) (X * _ratio), _MARGIN);

    		for (int x=0;x < X;x++)
    		{
    			g.drawLine(_MARGIN + (int) ((x+1) * _ratio), _MARGIN,_MARGIN + (int) ((x+1) * _ratio), _MARGIN + (int) (Y * _ratio));
    			g.drawString(Integer.toString(x), _MARGIN + (int) (x * _ratio) + _CHOFFSET, _CVOFFSET);
    			for (int y=0;y < Y;y++)
    			{
    				if (x == 0)
    				{
    					g.drawLine(_MARGIN, _MARGIN + (int) ((y+1) * _ratio), _MARGIN + (int) (X * _ratio), _MARGIN + (int) ((y+1) * _ratio));
    					g.drawString(Integer.toString(y), _CHOFFSET, _MARGIN + (int) (y * _ratio) + _CVOFFSET);
    				}
    				
					//g.setColor(Color.WHITE);
					//g.fillRect(_MARGIN + (int) (x * _ratio), _MARGIN + (int) (y * _ratio), (int) _ratio, (int) _ratio);
    				//g.fillRect(_MARGIN + (int) (x * _ratio), _MARGIN + (int) (y * _ratio), (int) _ratio, (int) _ratio);

    			}
    		}
    		
    		Board board = engine.getBoard();
    		
    		if (board == null) {
    			return;
    		}
    		
    		int[][] objects = board.objects;
    		
    		for (int x = 0; x < X; x++) {
    			for (int y = 0; y < Y; y++) {
    				
    				int obj = objects[x][y];
    				if (obj != Board.EMPTY) {
    					
    					if (obj == Board.CAT) {
    						//g.setColor(Color.RED);
    						//g.fillRect(_MARGIN + (int) (x * _ratio), _MARGIN + (int) (y * _ratio), (int) _ratio, (int) _ratio);
    						ImageIcon icon = new ImageIcon("cat.gif");
    						Image img = icon.getImage();
    						g.drawImage(img, _MARGIN + (int) (x * _ratio)+1, _MARGIN + (int) (y * _ratio)+1, (int) _ratio-2, (int) _ratio-2, null);
    						
    						
    						
    						//g.drawImage(image.getImage(), _MARGIN + (int) (x * _ratio), _MARGIN + (int) (y * _ratio), null);
    						
    					}
    					else if (obj == Board.DOG) {
    						ImageIcon icon = new ImageIcon("dog.jpg");
    						Image img = icon.getImage();
    						g.drawImage(img, _MARGIN + (int) (x * _ratio)+1, _MARGIN + (int) (y * _ratio)+1, (int) _ratio-2, (int) _ratio-2, null);

    					}
    				}
    			}
    		}
    		
    		

    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	} // End - paintComponent



	public void setEngine(GameEngine eng) {
		engine = eng;
	}

	public void recalculateDimensions() {
		// TODO Auto-generated method stub
		
	}
}

