package Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Modele.Map;
import Modele.Personnage;
import Modele.Position;

class PersonnageTestUnitaire {
	
	private Personnage personnage;
	@BeforeEach
	public void setUp() {
	    personnage = new Personnage(2, 1);  // Correspond à la position de 'P' dans la carte
	}

    @Test
    public void testGetPosition() {
        Position position = personnage.getPosition();
        assertEquals(2, position.getX());
        assertEquals(1, position.getY());
    }

    @Test
    public void testSetPosition() {
        Position newPosition = new Position(1, 1);
        personnage.setPosition(newPosition);
        assertEquals(newPosition, personnage.getPosition());
    }
    

}





