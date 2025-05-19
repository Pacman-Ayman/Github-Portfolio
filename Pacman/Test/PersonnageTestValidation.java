package Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Modele.Map;
import Modele.Personnage;
import Modele.Position;

class PersonnageTestValidation {
	
	private Personnage personnage;
	private Map map;

	@BeforeEach
	public void setUp() {
	    personnage = new Personnage(2, 1);  // Correspond à la position de 'P' dans la carte
	    String board = "%%%%\n% P%\n%  %\n%%%%";
	    map = new Map(board);
	    /* %%%%\n
	     * % P%\n
	     * %  %\n
	     * %%%%
	     */
	}

    
    @Test
	public void testColisionMur() {
	    // La carte est déjà configurée correctement par le constructeur

	    // Appeler la méthode à tester
	    Position[] result = personnage.colisionMur(map);

	    // Vérifier que les positions possibles sont correctes
	    assertNull(result[0]); // Haut bloqué par un mur
	    assertNotNull(result[1]); // Bas accessible
	    assertNotNull(result[2]); // Gauche accessible
	    assertNull(result[3]); // Droite bloqué par un mur
	}

}





