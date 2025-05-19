package Modele;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Fantome extends Personnage {
    private Position lastPosition = null; // Utiliser pour eviter les probleme d'impasse
    public int DETECTION_RADIUS = 5; //Rayon de detection
    public Fantome(int X, int Y) {
        super(X, Y);
    }
    public int distanceToPacman(Position pacmanPos) {// Calcule la distance entre le Pacman et le fantome
        return Math.abs(this.position.getX() - pacmanPos.getX()) + Math.abs(this.position.getY() - pacmanPos.getY());
    }
    private Position moveToPacman(Position pacmanPos, List<Position> Movesfontome) {
    	// Oriente le fantome vers le Pacman en se basant sur la distance
        Position closestMove = null;
        int minDistance = Integer.MAX_VALUE;

        for (Position move : Movesfontome) {// Choisi le move qui permet de minimiser la distance
            int distance = Math.abs(move.getX() - pacmanPos.getX()) + Math.abs(move.getY() - pacmanPos.getY());
            if (distance < minDistance) {// Choisi la distance minimal
                closestMove = move;
                minDistance = distance;
            }
        }

        if (closestMove != null) {// Si il y a une distance minimal
            return closestMove;
        } else {
            return this.position;
        }
    }
    public boolean fantomeBehind(Position pacmanPos, Position vecteurDrirecteur) {
        // Vérifiez si Pacman est derrière le fantome en projetant le vecteur directeur sur le vecteur du fantôme à Pacman
        Position FantomeToPacman = new Position(pacmanPos.getX() - this.position.getX(), pacmanPos.getY() - this.position.getY());
        int ProduitScalaire = vecteurDrirecteur.getX() * FantomeToPacman.getX() + vecteurDrirecteur.getY() * FantomeToPacman.getY();// produit scalaire(Projection)
        return ProduitScalaire < 0;  //Si le produit scalire est negative alors il est en arriere
    }
    // Mehode random La plus avancé
    public void moveRandomly(Map map) {
        Position[] possibleMoves = movePossible(map);
        List<Position> Movesfontome = new ArrayList<>();
        Position pacmanPos = null;
        
        //1er cas : les deux pacman ne sont pas maudit 
        //on choisit le pacman le plus proche des deux si on joue en multijoueur
        if(map.pacmans.size()==2) {
            //1er cas : les deux pacmans ne sont pas maudit 
            //on choisit le pacman le plus proche des deux
            if(!map.pacmans.get(0).maudit && !map.pacmans.get(1).maudit) {
                if (distanceToPacman(map.findPacmanJoueur1()) > distanceToPacman(map.findPacmanJoueur2())) {
                    pacmanPos = map.findPacmanJoueur2();
                    DETECTION_RADIUS = 5; //Rayon de detection
                } else {
                    pacmanPos = map.findPacmanJoueur1();
                    DETECTION_RADIUS = 5; //Rayon de detection
                }

            
            } else if (map.pacmans.get(0).maudit && !map.pacmans.get(1).maudit){//2ème cas : le joueur 1 est maudit : donc il est préféré par les fantomes et on augmenter les "cercles" des fantomes
                pacmanPos = map.findPacmanJoueur1();
                DETECTION_RADIUS = 10; //Rayon de detection
                
            } else if (!map.pacmans.get(0).maudit && map.pacmans.get(1).maudit){//3ème cas : le joueur 2 est maudit : donc il est préféré par les fantomes et on augmenter les "cercles" des fantomes
                pacmanPos = map.findPacmanJoueur2();
                DETECTION_RADIUS = 10; //Rayon de detection

            }
        } else { //cas où on joue en solo
            pacmanPos = map.findPacmanJoueur1();
            //s'il est maudit (pour le mode pouvoir)
            if (map.pacmans.get(0).maudit){
                DETECTION_RADIUS = 10; //Rayon de detection
            } else {
                DETECTION_RADIUS = 5; //Rayon de detection
            }
        }

        Position vecteurdurecteur = null;
        if (possibleMoves.length == 0) {
            return; // Aucun mouvement possible, sortie anticipée
        }

        // Récupère les mouvements possibles et identifie le mouvement inverse
        for (Position move : possibleMoves) {
            if (move != null && !move.equals(lastPosition)) {
                Movesfontome.add(move);
                // Calcule la direction de chaque mouvement possible par rapport à la position actuelle
                if (vecteurdurecteur == null) { // Initialise vecteurdurecteur avec le premier mouvement valide
                    vecteurdurecteur = new Position(move.getX() - this.position.getX(), move.getY() - this.position.getY());
                }
            }
        }

        if (Movesfontome.isEmpty()) {
            if (lastPosition != null) {
                this.setPosition(lastPosition); // Si il y a pas autre position retourne à ta position d'avant
                return;
            } else {
                return; // If no last position available, no movement can be made (stuck at a corner or enclosed space)
            }
        }

        Position PositionChoisi = this.position; // Par défaut reste en place

        // Si Pacman est détecté à proximité
        if (distanceToPacman(pacmanPos) <= DETECTION_RADIUS) {
            // Si Pacman est derrière, inclure la direction inverse dans les choix possibles
            if (lastPosition != null && fantomeBehind(pacmanPos, vecteurdurecteur)) {
                Movesfontome.add(lastPosition);
            }

            // Choisir la position la plus proche de Pacman
            PositionChoisi = moveToPacman(pacmanPos, Movesfontome);
        } else if (!Movesfontome.isEmpty()) {
            // Choisir un mouvement aléatoire parmi les options valides
            Random rand = new Random();
            PositionChoisi = Movesfontome.get(rand.nextInt(Movesfontome.size()));
        }

        lastPosition = this.position;  // Mettre à jour la dernière position
        this.setPosition(PositionChoisi);  // Déplacer le fantôme

    }

}