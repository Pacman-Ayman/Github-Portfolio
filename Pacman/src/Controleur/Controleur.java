package Controleur;

import Modele.*;
import Modele.Pacman;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
public class Controleur {
    public Modele modele; //le controleur applique des changements sur un modele
    public Pacman pacman1; // on récupère le joueur1
    public Pacman pacman2; //on récupère le joueur2 s'il y en a un
    public Scene scene; //on récupère la scène de l'interface graphique
    KeyCode memoire; //on récupère la touche qui a été appuyé dans une variable
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(); // pour gérer une tache qui s'exécute à une certaine fréquence

    // pour joueur 1 : on enregistre le mouvement en cours à l'aide de boolean
    boolean haut = false;
    boolean bas = false;
    boolean droite = false;
    boolean gauche = false;
    private ScheduledFuture<?> pacmanTask; // il va nous servir pour éviter de créer plusieurs tache qui se répète et donc d'accumuluer des directions : on a seulement besoin d'une tache qui fait déplacer le pacman

    // pour joueur 2 : on enregistre le mouvement en cours à l'aide de boolean
    boolean haut2 = false;
    boolean bas2 = false;
    boolean droite2 = false;
    boolean gauche2 = false;
    private ScheduledFuture<?> pacmanTask2; // il va nous servir pour éviter de créer plusieurs tache qui se répète et donc d'accumuluer des directions : on a seulement besoin d'une tache qui fait déplacer le pacman

    //constructeur :
    public Controleur(Modele modele, Scene scene) {
        this.modele = modele;
        this.pacman1= modele.map.pacmans.get(0);
        if (modele.map.pacmans.size()==2) {
            this.pacman2 = modele.map.pacmans.get(1);
        }
        this.scene = scene;
        setSceneEventHandler();
    }

    //méthode qui applique les modifications nécessaires lors de l'appuie d'une touche
    private void setSceneEventHandler() {
                scene.setOnKeyReleased(event -> {
                    // commande  du joueur 1
                    //on vérifie que lorsque le joueur appuie sur une touche, le mouvement est bien possible afin d'évtier des bugs de déplacement
                    if((event.getCode() == KeyCode.UP && pacman1.movePossible(modele.map)[0] != null) || (event.getCode() == KeyCode.DOWN && pacman1.movePossible(modele.map)[1] != null) || (event.getCode()==KeyCode.LEFT && pacman1.movePossible(modele.map)[2] != null) || (event.getCode() == KeyCode.RIGHT && pacman1.movePossible(modele.map)[3] != null) || event.getCode() == KeyCode.M ) {
                        //on verifie que le joueur a fait une demande de changement de direction possible (évite touts problèmes liés au spam d'une direction)
                        if (memoire != event.getCode() || memoire == KeyCode.M) {
                            //on enregistre dans la memoire la dernière touche active
                            memoire = event.getCode();
                            //on regarde le déplacment que doit effectuer le pacman1
                            if (memoire == KeyCode.UP && pacman1.movePossible(modele.map)[0] != null) { //HAUT
                                haut = true;
                                bas = false;
                                droite = false;
                                gauche = false;
                            } else if (memoire == KeyCode.DOWN && pacman1.movePossible(modele.map)[1] != null) { //BAS
                                haut = false;
                                bas = true;
                                droite = false;
                                gauche = false;
                            } else if (memoire == KeyCode.LEFT && pacman1.movePossible(modele.map)[2] != null) { //GAUCHE
                                haut = false;
                                bas = false;
                                droite = false;
                                gauche = true;
                            } else if (memoire == KeyCode.RIGHT && pacman1.movePossible(modele.map)[3] != null) { //DROITE
                                haut = false;
                                bas = false;
                                droite = true;
                                gauche = false;
                            }
                            if (pacmanTask != null) { //évite d'accumuler des taches : seul une tache non nulle doit être active
                                pacmanTask.cancel(false);
                            }

                            //répétition du déplacment à une certaine fréquence (vitesse du pacman)
                            pacmanTask = executorService.scheduleAtFixedRate(() -> {
                                //si le jeu n'a pas été mis en pause
                                if (!modele.map.pause) {
                                    if (haut && !bas && !gauche && !droite) {
                                        modele.map.deplacementPacman(pacman1.movePossible(modele.map)[0], modele.board, pacman1);
                                    } else if (!haut && bas && !gauche && !droite) {
                                        modele.map.deplacementPacman(pacman1.movePossible(modele.map)[1], modele.board, pacman1);
                                    } else if (!haut && !bas && gauche && !droite) {
                                        modele.map.deplacementPacman(pacman1.movePossible(modele.map)[2], modele.board, pacman1);
                                    } else if (!haut && !bas && !gauche && droite) {
                                        modele.map.deplacementPacman(pacman1.movePossible(modele.map)[3], modele.board, pacman1);
                                    }
                                } else { //si le jeu a été en pause = pacman mort, donc est initialement immobile
                                    memoire = null;
                                    haut = false;
                                    bas = false;
                                    gauche = false;
                                    droite = false;
                                    modele.map.deplacementPacman(null, modele.board, pacman1);
                                }
                            }, 0, pacman1.speed, TimeUnit.MILLISECONDS);

                            //pose la bombe "derrière" le pacman
                            // dans ce controleur si le pacman est immobile entre deux mur il ne peut pas poser de bombe sur le coté : il est obligé d'être en mouvement pour poser une bombe
                            if (memoire == KeyCode.M && pacman1.nbrBombes != 0) { //pose la bombe dans le sens opposé de la direction si possible
                                if (haut && !bas && !gauche && !droite && pacman1.positionsPossible[1] != null) {
                                    pacman1.poseBombe(pacman1.positionsPossible[1], modele.map);
                                } else if (!haut && bas && !gauche && !droite && pacman1.positionsPossible[0] != null) {
                                    pacman1.poseBombe(pacman1.positionsPossible[0], modele.map);
                                } else if (!haut && !bas && gauche && !droite && pacman1.positionsPossible[3] != null) {
                                    pacman1.poseBombe(pacman1.positionsPossible[3], modele.map);
                                } else if (!haut && !bas && !gauche && droite && pacman1.positionsPossible[2] != null) {
                                    pacman1.poseBombe(pacman1.positionsPossible[2], modele.map);
                                }
                            }

                        }
                        //commande joueur 2
                        //on vérifie que lorsque le joueur appuie sur une touche, le mouvement est bien possible afin d'évtier des bugs de déplacement
                    } else if ( event.getCode()==KeyCode.Z || event.getCode()==KeyCode.Q || event.getCode()==KeyCode.S || event.getCode()==KeyCode.D || event.getCode() == KeyCode.R ) {
                        //on verifie que le joueur a fait une demande de changement de direction possible (évite touts problèmes liés au spam d'une direction)
                        if (memoire != event.getCode() || memoire == KeyCode.R) {
                            //on enregistre dans la memoire la dernière touche active
                            memoire = event.getCode();
                            //on regarde le déplacment que doit effectuer le pacman2
                            if (memoire == KeyCode.Z && pacman2.movePossible(modele.map)[0] != null) { //HAUT
                                haut2 = true;
                                bas2 = false;
                                droite2 = false;
                                gauche2 = false;
                            } else if (memoire == KeyCode.S && pacman2.movePossible(modele.map)[1] != null) { //BAS
                                haut2 = false;
                                bas2 = true;
                                droite2 = false;
                                gauche2 = false;
                            } else if (memoire == KeyCode.Q && pacman2.movePossible(modele.map)[2] != null) { //GAUCHE
                                haut2 = false;
                                bas2 = false;
                                droite2 = false;
                                gauche2 = true;
                            } else if (memoire == KeyCode.D && pacman2.movePossible(modele.map)[3] != null) { //DROITE
                                haut2 = false;
                                bas2 = false;
                                droite2 = true;
                                gauche2 = false;
                            }

                            if (pacmanTask2 != null) { //évite d'accumuler des taches : seul une tache non nulle doit être active
                                pacmanTask2.cancel(false);
                            }

                            if (modele.map.pacmans.size() == 2) { // déplacement possible si on est en mode multijoueur
                                //répétition du déplacment à une certaine fréquence (vitesse du pacman)
                                pacmanTask2 = executorService.scheduleAtFixedRate(() -> {
                                    //si le jeu n'a pas été mis en pause
                                    if (!modele.map.pause) {
                                        if (haut2 && !bas2 && !gauche2 && !droite2) {
                                            modele.map.deplacementPacman(pacman2.movePossible(modele.map)[0], modele.board, pacman2);
                                        } else if (!haut2 && bas2 && !gauche2 && !droite2) {
                                            modele.map.deplacementPacman(pacman2.movePossible(modele.map)[1], modele.board, pacman2);
                                        } else if (!haut2 && !bas2 && gauche2 && !droite2) {
                                            modele.map.deplacementPacman(pacman2.movePossible(modele.map)[2], modele.board, pacman2);
                                        } else if (!haut2 && !bas2 && !gauche2 && droite2) {
                                            modele.map.deplacementPacman(pacman2.movePossible(modele.map)[3], modele.board, pacman2);
                                        }
                                    } else { //si le jeu a été en pause = pacman mort, donc est initialement immobile
                                        memoire = null;
                                        haut2 = false;
                                        bas2 = false;
                                        gauche2 = false;
                                        droite2 = false;
                                        System.out.println("Le jeu est en pause. Arrêt des mouvements de Pac-Man.");
                                        modele.map.deplacementPacman(null, modele.board, pacman2);
                                    }
                                }, 0, pacman2.speed, TimeUnit.MILLISECONDS);
                            }

                            //pose la bombe "derrière" le pacman
                            // dans ce controleur si le pacman est immobile entre deux mur il ne peut pas poser de bombe sur le coté : donc à modifier si besoin/possible
                            if (memoire == KeyCode.R && pacman2.nbrBombes != 0) {
                                if (haut2 && !bas2 && !gauche2 && !droite2 && pacman2.positionsPossible[1] != null) { //pose la bombe dans le sens opposé de la direction si possible
                                    pacman2.poseBombe(pacman2.positionsPossible[1], modele.map);
                                } else if (!haut2 && bas2 && !gauche2 && !droite2 && pacman2.positionsPossible[0] != null) {
                                    pacman2.poseBombe(pacman2.positionsPossible[0], modele.map);

                                } else if (!haut2 && !bas2 && gauche2 && !droite2 && pacman2.positionsPossible[3] != null) {
                                    pacman2.poseBombe(pacman2.positionsPossible[3], modele.map);

                                } else if (!haut2 && !bas2 && !gauche2 && droite2 && pacman2.positionsPossible[2] != null) {
                                    pacman2.poseBombe(pacman2.positionsPossible[2], modele.map);
                                }
                            }
                        }
                    }
        });
    }
}