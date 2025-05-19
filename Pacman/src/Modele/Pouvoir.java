package Modele;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
public class Pouvoir {
    Pacman pacman; // pour associer le pouvoir à un pacman
    String pouvoir; // nom du pouvoir

    public Pouvoir(Pacman pacman) {
        //liste des pouvoirs possibles
        String[] pouvoirs = new String[]{"changementVitesse","invulnérable","malédiction"};

        //choix alétoire du pouvoir
        Random random = new Random();
        int i = random.nextInt(pouvoirs.length);
        this.pouvoir=pouvoirs[i];
        this.pacman = pacman;
    }

    public Pacman getPacman() {
        return pacman;
    }

    //on applique le pouvoir au pacman
    public void givePower(){
    	if (pacman.pouvoir != null) { //si on a déjà un pouvoir, prendre un deuxième pouvoir efface ce qu'on a déjà
    		pacman.vulnerable = true;
    		pacman.speed = 190;
    		pacman.maudit = false;
    		pacman.pouvoir = null;
    	} else { //si on a pas de pouvoir
    		if (pouvoir.equals("invulnérable")){ //le cas où a eu le pouvoir invulnérable
                pacman.vulnerable=false;
                pacman.pouvoir = "Invulnérabilité !";
                //pour réactiver la vulnérabilité après 10 secondes
                PauseTransition attente1 = new PauseTransition(Duration.seconds(10));
                attente1.setOnFinished(event -> {
                	pacman.vulnerable = true;
                	pacman.pouvoir = null;
                });
                attente1.play();
            } else if (pouvoir.equals("changementVitesse")) { // le cas où a eu le pouvoir changement Vitesse
                Random random = new Random();
                int randomNumber = random.nextInt(2); // génère 0 ou 1
                int result = (randomNumber == 0) ? -1 : 1; // convertit 0 en -1 et 1 en 1 ( si on a 1, on augmente la vitesse et si on a -1 on diminue la vitesse )
                pacman.addSpeed(result*100);
                pacman.pouvoir = "Changement de vitesse !";
                // pour remettre la vitesse de base après 10 secondes
                PauseTransition attente2 = new PauseTransition(Duration.seconds(10));
                attente2.setOnFinished(event -> {
                	pacman.speed = 190;
                	pacman.pouvoir = null;
                });
                attente2.play();
            }
            else if (pouvoir.equals("malédiction")){ //le cas où on a eu le malus malédiction
                pacman.maudit = true;
                pacman.pouvoir = "Malédiction (sur lui-même !)";
                // pour enlever la malédiction après 10 secondes
                PauseTransition attente3 = new PauseTransition(Duration.seconds(10));
                attente3.setOnFinished(event -> {
                	pacman.maudit = false;
                	pacman.pouvoir = null;
                });
                attente3.play();
            }
    }
    }
}