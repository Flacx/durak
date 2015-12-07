import java.awt.event.ActionEvent;
//Controller Klasse, wird nacher die Hauptklasse
public class Durak implements ActionListener {
	Game g;

	public void main(String[] args) {
		new Durak();
	}
	
	public void ActionPerformed(ActionEvent ae) {
		if(ae.equals("GAME_START"))
		{
			g = new Game(0); //Wenn GUI fertig ist, kommt bei 0 ein Getter hin
		}
		}
	}
}
