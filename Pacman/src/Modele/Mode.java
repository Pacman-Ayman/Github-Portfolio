package Modele;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Mode {
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(); // pour gérer une tache qui s'exécute à une certaine fréquence
    private ScheduledExecutorService executorService2 = Executors.newSingleThreadScheduledExecutor(); // pour gérer une tache qui s'exécute à une certaine fréquence
    private ScheduledExecutorService executorService3 = Executors.newSingleThreadScheduledExecutor(); // pour gérer une tache qui s'exécute à une certaine fréquence
    Map map;
    boolean bomberman ; //boolean pour savoir si le mode bomberman a été activé
    boolean pouvoir; //boolean pour savoir si le mode pouvoir a été activé
    boolean portail; //boolean pour savoir si le mode portail a été activé
    boolean multijoueur; //boolean pour savoir si le multijoueur a été activé

    public Mode(boolean bomberman, boolean pouvoir, boolean portail, boolean multijoueur, Map map){
        this.map = map;
        this.bomberman= bomberman;
        this.pouvoir=pouvoir;
        this.portail=portail;
        this.multijoueur = multijoueur;
    }

    //ajout d'un "pouvoir" dans la map + retourne sa position
    public Position ajoutPouvoir(){
        Random random = new Random();
        int X = random.nextInt(map.getWidth());
        int Y = random.nextInt(map.getHeight());
        Position positionPouvoir = new Position(X, Y); // on  a créé une position aléatoire
        while (map.getElement(positionPouvoir) != ' ' && map.getElement(positionPouvoir) != '.') { //on verifie que c'est une bien une positon où on peut mettre le pouvoir ( ne pas remplacer un mur ou un pacman par ex )
            int X1 = random.nextInt(map.getWidth());
            int Y1 = random.nextInt(map.getHeight());
            positionPouvoir = new Position(X1, Y1);
        }
        map.setElement(positionPouvoir, '#'); //on modifie la map
        return positionPouvoir;
    }

    //gestion du fonctionnnement du mode pouvoir ( appartition des pouvoir sur la map )
    public void modePouvoir(){
        executorService3.scheduleAtFixedRate(() -> {
            // si le mode pouvoir a été activé et que la map ne contient pas de pouvoir, on en pose un
            if(pouvoir && !map.havePouvoir()) {
                this.ajoutPouvoir();
            }
        }, 5,  7, TimeUnit.SECONDS); //répétiton de cette tache
    }


    //mode multijoueur : on enlève le pacman en trop car initialement on place, le joueur2 dans notre board
    public void modeMultijoueur(){
        if(!multijoueur){
            map.setElement(map.findPacmanJoueur2(),'.');
            map.pacmans.remove(1);
        }
    }

    //ajout d'un portail dans la map + retourne sa position
    public Position ajoutPortail(){
        Random random = new Random();
        int X = random.nextInt(map.getWidth());
        int Y = random.nextInt(map.getHeight());
        Position positionPortail = new Position(X, Y); // on  a créé une position aléatoire
        while (map.getElement(positionPortail) != ' ' && map.getElement(positionPortail) != '.') { //on verifie que c'est une bien une positon où on peut mettre le pouvoir ( ne pas remplacer un mur ou un pacman par ex )
            int X1 = random.nextInt(map.getWidth());
            int Y1 = random.nextInt(map.getHeight());
            positionPortail = new Position(X1, Y1);
        }
        map.setElement(positionPortail, '/'); //on actualise la map
        return positionPortail;
    }

    //gestion des portails ( ajout et disparition des portails dans la map )
    public void modePortail() {
        if (portail) {
            ArrayList<Position> positionsPortails = new ArrayList<>(); // on va garder en mémoire les positions des portails
            //le compteur va nous servir à savoir quel portail enlever ( en faisant modulo 4 )
            final int[] compteur = {0}; // on doit utiliser un tableau pour pouvoir l'utiliser dans un lambda expression (suggestion/correction de intelliJ)

            // Planification de la tâche pour ajouter/supprimer un portail toutes les 3 secondes
            executorService.scheduleAtFixedRate(() -> {
                int indice = compteur[0] % 4;
                //gestion des 4 premiers portails
                if(positionsPortails.size() != 4){
                    positionsPortails.add(this.ajoutPortail());
                }
                else { //dés que notre tableau à 4 position de portails, on enlève et ajoute un portail à chauqe fois sur la map
                    map.setElement(positionsPortails.get(indice), '.');
                    positionsPortails.set(indice, this.ajoutPortail());
                }
                compteur[0] += 1;

            }, 3, 3, TimeUnit.SECONDS);
        }
    }

    //ajoute une bombe récupérable dans la map
     public Position ajoutBombeArecup(){
         Random random = new Random();
         int X = random.nextInt(map.getWidth());
         int Y = random.nextInt(map.getHeight());
         Position positionBombe = new Position(X, Y); // position aléatoire de la bomebe
         while (map.getElement(positionBombe) != ' ' && map.getElement(positionBombe) != '.') { //on vérifie que c'est une postion possible
             int X1 = random.nextInt(map.getWidth());
             int Y1 = random.nextInt(map.getHeight());
             positionBombe = new Position(X1, Y1);
         }
         map.setElement(positionBombe, '€'); // on actualise la map
         return positionBombe;
     }

     //"gestion" du mode bomberman ( apparition des bombes ) (pour l'explosion et la disparition des bombes, c'est gérer dans la classe pacman car c'est le pacman qui fait exploser la bombe)
    public void modeBomberman() {
        if (bomberman) {
            // on ajoute les bombes initiale au pacmans
            for(int k=0; k<map.pacmans.size(); k++){
                map.pacmans.get(k).nbrBombes= 3;
            }
            // Planification de la tâche pour ajouter une bombe à récupérer toutes les 10 secondes
            executorService2.scheduleAtFixedRate(() -> {
                Position positionBombe = this.ajoutBombeArecup();
                map.setElement(positionBombe, '€');
            }, 5,  10, TimeUnit.SECONDS);
        }
    }

}
