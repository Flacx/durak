import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//Controller Klasse, wird nacher die Hauptklasse
public class Durak implements ActionListener {
	Game g;

	public void main(String[] args) {
		new Durak();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.equals("GAME_START"))
		{
			g = new Game(0); //Wenn GUI fertig ist, kommt bei 0 ein Getter hin
		}
	
	}
}
