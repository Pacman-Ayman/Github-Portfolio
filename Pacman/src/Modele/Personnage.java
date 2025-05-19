package Modele;

public class Personnage { // soit fantome ou pacman
    public Position position; // position de notre personnage
    public Position[] positionsPossible; // déplacement possible Haut,Bas,Gauche,Droite

    public Personnage(int X, int Y) {
        position = new Position(X, Y);
        positionsPossible = new Position[4];
    }

    public Position getPosition() {
        return position;
    }
    public void setPosition(Position position) {
        this.position = position;
    }
    public String toString() {
        return "(" + position.getX() + ";" + position.getY() + ")";
    }

    //colision avec le mur + move possible
    public Position[] colisionMur(Map map) {
        Position Haut = new Position(this.position.getX(), this.position.getY() - 1); //création de la postion haut
        Position Bas = new Position(this.position.getX(), this.position.getY() + 1); //création de la position bas
        Position Droite = new Position(this.position.getX() + 1, this.position.getY()); //création de la position droite
        Position Gauche = new Position(this.position.getX() - 1, this.position.getY()); //création de la position gauche
        Position[] Direction = {Haut, Bas, Gauche, Droite};
        for (int k = 0; k < Direction.length; k++) {
            if (map.getElement(Direction[k]) == '%') { //si la direction k est un mur, on dit que le mouvement n'est pas possible
                this.positionsPossible[k] = null;
            } else {
                this.positionsPossible[k] = Direction[k];
            }
        }
        return this.positionsPossible;
    }

    // return les mouvements possibles
    public Position[] movePossible(Map map) {
        return colisionMur(map);
    }

} 
