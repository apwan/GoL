import javax.swing.*;

import com.sun.javafx.scene.control.skin.CellSkinBase;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.File;
import java.lang.Integer;
public class GoL extends JFrame {
	public static void main(String args[]){
		System.out.println("Welcome to Java Game_of_Life! Enjoy it!");
		GoL gol = new GoL();
	}
	
	public GoL(){
		super();
		
		setTitle("Game of Life");
		setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new FlowLayout());
		panel = new GoLPanel();
		add(panel);
		setVisible(true);
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		JButton jbt = new JButton(new ImageIcon(toolkit.getImage("play.png")
				.getScaledInstance(40, 40, 0)));
		jbt.setPreferredSize(new Dimension(40,40));
		jbt.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				panel.updateAlive();
				panel.updateCell();
				//System.out.println("update");
			}
		});
		add(jbt);
		
		
		
		JTextField jtf = new JTextField("200");
		jtf.setPreferredSize(new Dimension(50,20));
		add(jtf);
		
		JButton jbtAuto = new JButton();
		jbtAuto.setText("Start");
		jbtAuto.setPreferredSize(new Dimension(40, 40));
		jbtAuto.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(jbtAuto.getText()=="Start"){
					int tm = 0;
					try{
						tm = Integer.parseInt(jtf.getText());
					}catch(Exception ex){
						System.out.println(ex.toString());
						tm = 100;
					}
					if(tm<100) tm = 100;
					jtf.setText(""+tm);
					jbtAuto.setText("Stop");
					
					timer.schedule(new TimerTask(){
						@Override
						public void run(){
							panel.updateAlive();
							panel.updateCell();
						}
					}, 0, tm);
				}else{
					jbtAuto.setText("Start");
					timer.cancel();
					timer = new java.util.Timer();
				}
			}
		});
		add(jbtAuto);
		
		
		
		
	}
	GoLPanel panel;
	java.util.Timer timer = new java.util.Timer();
	public static int rows = 80;
	public static int cols = 80;
	public static int sidelen = 10;
	public static GoLCell[][]grids;
	public static Vector<Integer>updateGrids = new Vector<Integer>();
	public static class GoLPanel extends JPanel {
	static int[] gunx = {5,6,5,6,5,6,7,4,8,3,9,3,9,6,4,8,5,6,7,6,3,4,5,3,4,5, 2,6,1,2,6,7,3,4,3,4};
	static int[] guny = {1,1,2,2,11,11,11,12,12,13,13,14,14,15,16,16,17,17,17,18,21,21,21,22,22,22, 23,23,25,25,25,25,35,35,36,36};
		public GoLPanel(){
			super();
			setPreferredSize(new Dimension(cols*sidelen, rows*sidelen));
			setOpaque(false);
			setLayout(null);
			grids = new GoLCell[rows][cols];
			for(int k=rows*cols-1; k>=0; --k){
				add(grids[k/cols][k%cols] = new GoLCell(k));
			}
			addGun(10, 10, true);
			repaint();
		}
		public void addGun(int x, int y, boolean hori){
			if(hori){
				for(int i=0;i<36;++i){
					grids[x+gunx[i]][y+guny[i]].setAlive(true);
				}
			}
			else {
				for(int i=0;i<36;++i){
					grids[x+guny[i]][y+gunx[i]].setAlive(true);
				}
			}
			updateCell();
		}
		public void updateCell(){
			if(updateGrids == null) return;
			while(! updateGrids.isEmpty()){
				int k = updateGrids.elementAt(updateGrids.size()-1).intValue();
				int i = k/cols; int j = k%cols;
				if(i<1 || i>rows-2 || j<1 || j>cols-2){
					updateGrids.removeElementAt(updateGrids.size()-1);
					continue;
				}
				if(grids[i][j].alive){
					++grids[i-1][j-1].neighbor;
					++grids[i-1][j].neighbor;
					++grids[i-1][j+1].neighbor;
					++grids[i][j-1].neighbor;
					++grids[i][j+1].neighbor;
					++grids[i+1][j-1].neighbor;
					++grids[i+1][j].neighbor;
					++grids[i+1][j+1].neighbor;
				}else{
					--grids[i-1][j-1].neighbor;
					--grids[i-1][j].neighbor;
					--grids[i-1][j+1].neighbor;
					--grids[i][j-1].neighbor;
					--grids[i][j+1].neighbor;
					--grids[i+1][j-1].neighbor;
					--grids[i+1][j].neighbor;
					--grids[i+1][j+1].neighbor;
				}
				updateGrids.removeElementAt(updateGrids.size()-1);
			}
		}
		public void updateAlive(){
			GoLCell gc;
			for(int i=1;i<rows-1;++i){
				for(int j=1;j<cols-1;++j){
					gc = grids[i][j];
					if(gc.alive && (gc.neighbor<2 || gc.neighbor>3)){
						//System.out.println("become dead!"+gc.neighbor);
						gc.setAlive(false);
						
					}else if(!gc.alive && gc.neighbor == 3){
						//System.out.println("become alive!"+gc.neighbor);
						gc.setAlive(true);
					}
				}
			}
		}
		@Override
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			
		}
	}
	public static class GoLCell extends JComponent{
		
		public static Color aliveColor = new Color(0, 200, 0);
		public boolean alive = false;
		public int neighbor = 0;
		public int id;
		public GoLCell(int k){
			id = k;
			setBounds(0, 0, sidelen, sidelen);
			setLocation((id%cols)*sidelen, (id/cols)*sidelen);
			
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e){
					
				}
				@Override
				public void mouseReleased(MouseEvent e){
					setAlive(!alive);
					((GoLPanel)getParent()).updateCell();
				}
			});
		}
		public void setAlive(boolean f){
			if(alive != f) 
				updateGrids.addElement(new Integer(id));
			alive = f;
			repaint();
		}
		@Override
		public void paint(Graphics g){
			super.paint(g);
			g.drawRect(0, 0, sidelen, sidelen);
			
			if(alive){
				g.setColor(aliveColor);
				g.fillRect(1, 1, sidelen-2, sidelen-2);
			}
		}
	}
}