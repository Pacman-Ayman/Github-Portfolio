package Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Modele.Fantome;
import Modele.Map;
import Modele.Position;

class TestIntegrationFantome {
	private Fantome fantome;
	private Map map;
	@BeforeEach
	void setUp(){
		String board="%%%%%\n%P  %\n%   %\n%  F%\n%%%%%";// La map est carré et chacun des fantomes et pacman est dans un coté4
		map=new Map(board);
		fantome=map.fantomes.get(0);// On a un seul fantome
	}

	@Test
	void testMoveRandomly() {// Le fantome ne doit pas rester fixe puisqu'il est dans un carré ou il y a pas d'obstacle alors il doit se deplacer
		Position Position0=fantome.getPosition();
		fantome.moveRandomly(map);
		Position newPos=fantome.getPosition();
		assertNotEquals(Position0,newPos,"Le fantome ne doit pas rester fixe");
	}

}
