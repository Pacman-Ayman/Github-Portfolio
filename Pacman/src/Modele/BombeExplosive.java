package Modele;

import java.util.ArrayList;
import java.util.Random;

public class BombeExplosive {
    public Pacman pacmanAppartient; //on garde en mémoire à qui appartient la bombe pour pouvoir faire perdre des vies et gagner/perdre des points
    public ArrayList<Position> positions; // une position s'il n a pas encore explosé et 5 positions max s'il a explosé
    public int damage = 100; // points gagné si on touche son adversaire et points perdu si on s'auto touche
    public int delaiAvantExplosion = 3000; //millisecondes delai avant explosion
    public int delaiExplosion = 5000; //millisecondes delai explosion

    public BombeExplosive(int X, int Y ) {
        Position position = new Position(X,Y);
        positions = new ArrayList<Position>();
        positions.add(position);
    }

    public void explose(Map map){ // fait explosé la bombe en lui "rajoutant" des positions

        //ajout des positions selon la règle de la croix s'il n y a pas de mur ou pacman ou fantome ou portail ou pouvoir
        Position positionHaut = new Position(positions.get(0).getX(), positions.get(0).getY()+1);
        Position positionBas  = new Position(positions.get(0).getX(), positions.get(0).getY()-1);
        Position positionGauche = new Position(positions.get(0).getX()-1, positions.get(0).getY());
        Position positionDroite = new Position(positions.get(0).getX()+1, positions.get(0).getY());

        if(map.getElement(positionHaut) ==' ' || map.getElement(positionHaut)=='.'){ //ajout des positions selon la règle de la croix s'il n y a pas de mur ou pacman ou fantome ou portail ou pouvoir
            positions.add(positionHaut);
        }
        if(map.getElement(positionBas) ==' ' || map.getElement(positionBas)=='.'){ //ajout des positions selon la règle de la croix s'il n y a pas de mur ou pacman ou fantome ou portail ou pouvoir
            positions.add(positionBas);
        }
        if(map.getElement(positionDroite) ==' ' || map.getElement(positionDroite)=='.'){ //ajout des positions selon la règle de la croix s'il n y a pas de mur ou pacman ou fantome ou portail ou pouvoir
            positions.add(positionDroite);
        }
        if(map.getElement(positionGauche) ==' ' || map.getElement(positionGauche)=='.'){ //ajout des positions selon la règle de la croix s'il n y a pas de mur ou pacman ou fantome ou portail ou pouvoir
            positions.add(positionGauche);
        }
        for(int k=0;k<4;k++){
            map.setElement(positions.get(k),'$'); //on actualise la map
        }

    }

    // verifie s'il y a une colision avec un pacman
    public void toucheBombe(Pacman pacman){
        if(pacman==this.pacmanAppartient){ //si le pacman touché est le propriétaire de la bombe
            pacman.changePoints(-damage); //on perd des points
            pacman.PerteVie(); // on perd une vie
        } else { // si le pacman touché n'est pas le propriétaire de la bombe
            pacmanAppartient.changePoints(damage); //on augmente les points du propriétaire
            pacman.PerteVie();
        }
    }

    //fait disparaitre la bombe de la map
    public void dispariton(Map map){
        for(int k=0; k<positions.size(); k++){
            map.setElement(positions.get(k), '.');
        }
    }

}
