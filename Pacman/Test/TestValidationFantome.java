package Test;

import static org.junit.jupiter.api.Assertions.*;
import Modele.Pacman;
import Modele.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Modele.Fantome;
import Modele.Map;

class TestValidationFantome {
    private Fantome fantome;
    private Map map;
    private Pacman pacman;

    @BeforeEach
    void setUp(){
        String board = "%%%%%\n%P  %\n%   %\n% F %\n%%%%%";// on donne un board carré pour faire le test
        map = new Map(board);
        fantome = map.fantomes.get(0);// on utilise un seul fantome
        pacman = map.pacmans.get(0);// on utilise un seul pacman
    }

    @Test
    void testDuFantomeAuPacman(){
        Position pacmanPos = pacman.getPosition();// determine la position du Pacman
        int distanceInit = fantome.distanceToPacman(pacmanPos);// determine la distance initial entre les deux 

        for (int i = 0; i < 10; i++) { // On bouge le fantome
            fantome.moveRandomly(map);
        }

        int distanceFin = fantome.distanceToPacman(pacmanPos);// La distance final entre les deux
        assertTrue(distanceFin <= distanceInit, "Le fantome doit s'approcher du pacman.");// Verification
    }
}
