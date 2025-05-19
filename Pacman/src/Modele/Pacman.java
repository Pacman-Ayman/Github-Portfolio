package Modele;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Pacman extends Personnage {
    public int points; //compteur de points
    public int life; // nombre de vie
    public int speed = 200; //vitesse

    public boolean vulnerable = true; //s'il peut etre attaqué par des fantomes (pour le mode pouvoir )
    // pour le mode pouvoir :
    public boolean maudit = false; //s'il est préféré par les fantomes + le trouvent plus rapidement (pour le mode pouvoir)
    public int nbrBombes = 0;
    public char lettre; //permet de savoir si c'est le joueur 1,2 ou un fantome
    public char lettreFuturPosition; //permet de récuperer la lettre qu'il remplace lorsque qu'il se deplace (quand c'est un fantome)
    public ArrayList<BombeExplosive> bombes = new ArrayList<>(); //garde en mémoire les bombes qu'il a posé et qui ont explosé
    public String pouvoir; //on récupère le nom du pouvoir (s'il en a pris un )
    public int numero;

    public Pacman(int X, int Y, char lettre) {
        super(X, Y);
        this.life=3;
        this.lettre = lettre;
        if(lettre == 'P') {
        	this.numero = 0; //joueur1
        } else {
        	this.numero = 1; //joueur2
        }
    }
    public int getLife(){
        return life;
    }
    public void PerteVie(){
        this.life -= 1;
    }
    public void changePoints(int points){
        this.points += points;
    }
    public void ajoutBombe(){
        nbrBombes ++;
    }
    public void addSpeed(int add ){
        speed+=add;
    } //modification de la vitesse en ajoutant un nombre ( qui peut être négaitf également )

    //fait poser une bombe sur la map par le pacman et gère son explosion et sa disparition
    public void poseBombe(Position position, Map map){
        if (nbrBombes != 0) { // on vérfie qu'il a bien le droit de poser une bombe
            nbrBombes--; // on décrémente le compteur

            map.setElement(position, '@'); //on actualise la map ( on pose une bombe mais pas explosé encore )
            BombeExplosive bombeRamasse = new BombeExplosive(position.getX(), position.getY()); //on crée notre bombe

            // Faire exploser la bombe après un certain temps
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    bombeRamasse.pacmanAppartient = Pacman.this; // affecte à la propriété pacmanAppartient de l'objet bombeRamasse une référence à l'instance actuelle de la classe Pacman
                    bombes.add(bombeRamasse); // Ajouter la bombe à la liste de bombes
                    bombeRamasse.explose(map); // on fait explosé la bombe
                    timer.cancel();
                }
            }, bombeRamasse.delaiAvantExplosion); // Délai avant explosion

            //disparition de la bombe après un certain temps
            Timer timer2 = new Timer();
            timer2.schedule(new TimerTask() {
                @Override
                public void run() {
                    bombeRamasse.dispariton(map); //on fait disparaitre la bombe
                    bombes.remove(bombeRamasse); // on enlève la bombe de notre liste
                    timer2.cancel();
                }
            }, bombeRamasse.delaiAvantExplosion + bombeRamasse.delaiExplosion); // Délai disparition
        }
    }

    //permet de savoir si la bombe qu'on a touché est la notre ou pas ( ie si c'est pas la notre, c'est que c'est à l'autre pacman )
    // et permet de savoir quels bombes à été touché
    public BombeExplosive trouveBombeTouche(Position position){
        for(int k=0; k<bombes.size(); k++){
            for(int j=0; j<bombes.get(k).positions.size(); j++){
                if(position.equals(bombes.get(k).positions.get(j))){
                    return bombes.get(k);
                }
            }
        }
        return null;
    }
}