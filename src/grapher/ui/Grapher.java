package grapher.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.BasicStroke;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.Vector;

import static java.lang.Math.*;

import grapher.fc.*;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class Grapher extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener{
	static final int MARGIN = 40;
	static final int STEP = 5;

	private State state = State.IDLE;
	private Point p;
	private Point pc;

	
	static final BasicStroke dash = new BasicStroke(1, BasicStroke.CAP_ROUND,
	                                                   BasicStroke.JOIN_ROUND,
	                                                   1.f,
	                                                   new float[] { 4.f, 4.f },
	                                                   0.f);
	                                                   
	protected int W = 400;
	protected int H = 300;
	
	protected double xmin, xmax;
	protected double ymin, ymax;

	protected DefaultListModel<Function> functions;
	public Function a_mettre_en_gras=null;
	
	public Grapher() {
		xmin = -PI/2.; xmax = 3*PI/2;
		ymin = -1.5;   ymax = 1.5;
		
		functions = new DefaultListModel<Function>();
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		

	}
	public void setGras (Function f){
		a_mettre_en_gras=f;
	}
	
	public void add(String expression) {
		add(FunctionFactory.createFunction(expression));
	}
	
	public void add(Function function) {
		functions.addElement(function);
		repaint();
	}
		
	public Dimension getPreferredSize() { return new Dimension(W, H); }
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		W = getWidth();
		H = getHeight();

		
		
		Graphics2D g2 = (Graphics2D)g;

		// background
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, W, H);
		
		g2.setColor(Color.BLACK);

		// box
		g2.translate(MARGIN, MARGIN);
		W -= 2*MARGIN;
		H -= 2*MARGIN;
		if(W < 0 || H < 0) { 
			return; 
		}
		
		g2.drawRect(0, 0, W, H);
		
				
		
		g2.drawString("x", W, H+10);
		g2.drawString("y", -10, 0);
		
	
		// plot
		g2.clipRect(0, 0, W, H);
		g2.translate(-MARGIN, -MARGIN);

		// x values
		final int N = W/STEP + 1;
		final double dx = dx(STEP);
		double xs[] = new double[N];
		int    Xs[] = new int[N];
		for(int i = 0; i < N; i++) {
			double x = xmin + i*dx;
			xs[i] = x;
			Xs[i] = X(x);
		}
		
		for(int j = 0; j< functions.size(); j++) {
			// y values
			//setstroke
			Stroke s = g2.getStroke();
			if(a_mettre_en_gras==functions.get(j)) {
				g2.setStroke(new BasicStroke(3));
			}
			int Ys[] = new int[N];
			for(int i = 0; i < N; i++) {
				Ys[i] = Y(functions.get(j).y(xs[i]));
			}
			
			g2.drawPolyline(Xs, Ys, N);
			
			g2.setStroke(s);
		}

		g2.setClip(null);

		// axes
		drawXTick(g2, 0);
		drawYTick(g2, 0);
		
		double xstep = unit((xmax-xmin)/10);
		double ystep = unit((ymax-ymin)/10);

		g2.setStroke(dash);
		for(double x = xstep; x < xmax; x += xstep)  { drawXTick(g2, x); }
		for(double x = -xstep; x > xmin; x -= xstep) { drawXTick(g2, x); }
		for(double y = ystep; y < ymax; y += ystep)  { drawYTick(g2, y); }
		for(double y = -ystep; y > ymin; y -= ystep) { drawYTick(g2, y); }
		
		
		if(state==State.D_DRAGGING || state == State.D_PRESSED) 
		{
			//dessinons le rectangle
			Rectangle R = new Rectangle(p);
			R.add(pc);
			g2.draw(R);
		}

	}
	
	protected double dx(int dX) { return  (double)((xmax-xmin)*dX/W); }
	protected double dy(int dY) { return -(double)((ymax-ymin)*dY/H); }

	protected double x(int X) { return xmin+dx(X-MARGIN); }
	protected double y(int Y) { return ymin+dy((Y-MARGIN)-H); }
	
	protected int X(double x) { 
		int Xs = (int)round((x-xmin)/(xmax-xmin)*W);
		return Xs + MARGIN; 
	}
	protected int Y(double y) { 
		int Ys = (int)round((y-ymin)/(ymax-ymin)*H);
		return (H - Ys) + MARGIN;
	}
		
	protected void drawXTick(Graphics2D g2, double x) {
		if(x > xmin && x < xmax) {
			final int X0 = X(x);
			g2.drawLine(X0, MARGIN, X0, H+MARGIN);
			g2.drawString((new Double(x)).toString(), X0, H+MARGIN+15);
		}
	}
	
	protected void drawYTick(Graphics2D g2, double y) {
		if(y > ymin && y < ymax) {
			final int Y0 = Y(y);
			g2.drawLine(0+MARGIN, Y0, W+MARGIN, Y0);
			g2.drawString((new Double(y)).toString(), 5, Y0);
		}
	}
	
	protected static double unit(double w) {
		double scale = pow(10, floor(log10(w)));
		w /= scale;
		if(w < 2)      { w = 2; } 
		else if(w < 5) { w = 5; }
		else           { w = 10; }
		return w * scale;
	}
	

	protected void translate(int dX, int dY) {
		double dx = dx(dX);
		double dy = dy(dY);
		xmin -= dx; xmax -= dx;
		ymin -= dy; ymax -= dy;
		repaint();	
	}
	
	protected void zoom(Point center, int dz) {
		double x = x(center.x);
		double y = y(center.y);
		double ds = exp(dz*.01);
		xmin = x + (xmin-x)/ds; xmax = x + (xmax-x)/ds;
		ymin = y + (ymin-y)/ds; ymax = y + (ymax-y)/ds;
		repaint();	
	}
	
	protected void zoom(Point p0, Point p1) {
		double x0 = x(p0.x);
		double y0 = y(p0.y);
		double x1 = x(p1.x);
		double y1 = y(p1.y);
		xmin = min(x0, x1); xmax = max(x0, x1);
		ymin = min(y0, y1); ymax = max(y0, y1);
		repaint();	
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		
		pc = arg0.getPoint();
		switch(state)
		{

		case G_PRESSED : 
			state = State.G_DRAGGING;
		case G_DRAGGING :
			this.translate(pc.x - p.x, pc.y - p.y);
			p=pc;
			this.setCursor(new Cursor(Cursor.HAND_CURSOR));
			break;
		case D_PRESSED :
			state = State.D_DRAGGING;
		case D_DRAGGING :
			break;
		default :
			break;
		}
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

		switch(state)
		{
			case IDLE : p = arg0.getPoint();
				if(arg0.getButton()==MouseEvent.BUTTON1) 
				{
					state = State.G_PRESSED;
				}
				else if(arg0.getButton()==MouseEvent.BUTTON3) 
				{
					state = State.D_PRESSED;
				}
				break;
			default : break;
		}

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		switch(state)
		{
			case G_PRESSED :
				if(arg0.getButton() == MouseEvent.BUTTON1){
					this.zoom(p, 5);
					state = State.IDLE;
				}
				break;
			case G_DRAGGING :
				if(arg0.getButton() == MouseEvent.BUTTON1){
					state = State.IDLE;
					this.setCursor(Cursor.getDefaultCursor());
				}
				else state = State.G_DRAGGING;
				break;
			case D_PRESSED : 
				if(arg0.getButton() == MouseEvent.BUTTON3)
				{
					this.zoom(p, -5);
					state = State.IDLE;					
				}
				break;
			case D_DRAGGING :
				if(arg0.getButton() == MouseEvent.BUTTON3)
				{
					this.zoom(p, arg0.getPoint());
					state = State.IDLE;
				}
				else state = State.D_DRAGGING;
				break;
			default : break;
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		switch(state)
		{
			case IDLE : 
				if(arg0.getWheelRotation()>0)
				{
					this.zoom(arg0.getLocationOnScreen(),5);
				}
				else if(arg0.getWheelRotation()<0)
					this.zoom(arg0.getLocationOnScreen(),-5);
				break;
			default :
				break;
		}
	}
}
