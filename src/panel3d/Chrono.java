package panel3d;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Will be called at each blitter page */
public class Chrono implements ActionListener {

	GameCanvas gc;
	// constructor that receives the GameCanvas that we will repaint every 60 milliseconds
	Chrono(GameCanvas gc) {
		this.gc = gc;
	}
	// calls the method to repaint the anim everytime I am called
	public void actionPerformed(ActionEvent e) {
		gc.myRepaint();
	}

}

