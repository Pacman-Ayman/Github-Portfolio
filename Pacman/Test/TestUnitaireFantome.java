package Test;
import Modele.Fantome;
import Modele.Position;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestUnitaireFantome {
	private Fantome fantome;

	@BeforeEach
	void setUp()  {
		fantome=new Fantome(1,1);
	}

	@Test
	 public void testDistanceToPacman() {
		Position pacmanPos=new Position(4,5);
		int distance = fantome.distanceToPacman(pacmanPos);//(4-1)+(5-1)=7
		assertEquals(7,distance,"Erreur La distance doit etre 7");
	}
	public void testFontomeBehind() {
		Position pacmanPos=new Position(2,2);// Le vecteur du pacman au fontome dans le rayon
		Position vecteurDirecteur=new Position(1,0);// Vecteur du mouvement du fantome
		boolean pasDerriere=fantome.fantomeBehind(pacmanPos, vecteurDirecteur);// Dans ce cas le pacman est devant le fantome
		assertFalse(pasDerriere,"Le Pacman n'est pas derriere le fantome");
		
	}

}
