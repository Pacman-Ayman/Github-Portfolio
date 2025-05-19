package Modele;
import java.util.ArrayList;
import java.util.Random;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class Map {
    public char[][] board; //on créer un double tableau de caractères à partir d'un string, qui sera notre map
    public ArrayList<Pacman> pacmans; //on va enregistrer les pacmans dans une liste. Si cette liste est égale à 2, alors on joue en multijoueur
    public ArrayList<Fantome> fantomes; //on va enregistrer les fantomes dnas une liste.
    public boolean pause = false; // va nous servir pour mettre en pause le jeu après une rénitialisation

    //librement inspiré du tp (adapté à notre problème)
    public Map(String board){
        fantomes = new ArrayList<Fantome>(); // crée nos fantomes
        pacmans = new ArrayList<Pacman>(); // crée nos pacmans
        board = board.strip(); // retire les retours à la ligne au début et à la fin du string
        String[] lines = board.split("\n"); // récupère les lignes de notre map
        this.board = new char[lines[0].strip().length()][lines.length]; // initialise la taille du tableau
        for (int i=0; i < lines.length; i++){
            String line=lines[i].strip();
            //on enregistre les caractères dans le double tableau board et on récupère les fantomes et pacmans
            for (int j=0; j < line.length(); j++){
                if (line.charAt(j) == 'P') { // on récupère le pacman1
                    Pacman pacman = new Pacman(j,i, 'P');
                    pacmans.add(pacman);
                    this.board[j][i] = 'P';
                }
                else if (line.charAt(j) == 'Q') { // on récupère le pacman2
                    Pacman pacman = new Pacman(j,i, 'Q');
                    pacmans.add(pacman);
                    this.board[j][i] = 'Q';
                }
                else if (line.charAt(j)=='F') { // on récupère les fantomes
                    Fantome fantome = new Fantome(j,i);
                    this.fantomes.add(fantome);
                    this.board[j][i] = 'F';
                } else {
                    this.board[j][i] = line.charAt(j);
                }
            }
        }
    }

    //récupère la longeur de la map
    public int getWidth() {
        return this.board.length;
    }

    //récupère la hauteur de la map
    public int getHeight() {
        return this.board[0].length;
    }

    //récupère l'élément se trouvant à une certaine position
    public char getElement(Position position){
        return this.board[position.getX()][position.getY()];
    }

    //on change un élément du tableau en prenant en paramètre la positon qu'on veut changer sur la map et la valeur
    public void setElement(Position position, char value){
        this.board[position.getX()][position.getY()] = value;
    }

    //return la position du pacman1
    public Position findPacmanJoueur1(){
        for (int k=0; k<board.length; k++){
            for(int j=0; j<board[0].length; j++){
                if(board[k][j] == 'P'){
                    Position position = new Position(k,j);
                    return position;
                }
            }
        }
        // probleme quand la position du pacman n'est pas trouver important pour IA
        return new Position(0, 0); // Renvoie une position par défaut, la dernière position valide connue
    }

    //return la position du pacman2
    public Position findPacmanJoueur2(){
        for (int k=0; k<board.length; k++){
            for(int j=0; j<board[0].length; j++){
                if(board[k][j] == 'Q'){
                    Position position = new Position(k,j);
                    return position;
                }
            }
        }
        // probleme quand la position du pacman n'est pas trouver important pour IA
        return new Position(0, 0); // Renvoie une position par défaut, la dernière position valide connue
    }

    //verifie l'existence ou non d'un pouvoir sur la map
    public boolean havePouvoir(){
        for (int k=0; k<board.length; k++){
            for(int j=0; j<board[0].length; j++){
                if(this.board[k][j]=='#'){
                    return true;
                }
            }
        }
        return false;
    }

    //renitialise la map sans passer par une nouvelle instance;
    public void reinitialiseMap(String board){
        fantomes = new ArrayList<Fantome>(); // crée nos fantomes
        board = board.strip(); // retire les retours à la ligne au début et à la fin du string
        String[] lines = board.split("\n"); // récupère les lignes de notre map
        this.board = new char[lines[0].strip().length()][lines.length]; // initialise la taille du tableau
        for (int i=0; i < lines.length; i++){
            String line=lines[i].strip();
            //on enregistre les caractères dans le double tableau board et on récupère les fantomes et pacmans
            for (int j=0; j < line.length(); j++){
                if (line.charAt(j) == 'P') { // on récupère le pacman1
                    this.pacmans.get(0).position = new Position(j,i);
                    this.board[j][i] = 'P';
                }
                else if (line.charAt(j) == 'Q' && pacmans.size()==2) { //on récupère le pacman2
                    this.pacmans.get(1).position = new Position(j,i);
                    this.board[j][i] = 'Q';
                }
                else if (line.charAt(j) == 'Q' && pacmans.size()!=2){ // on enlève le pacman2 si on joue pas en multijoueur
                    this.board[j][i] = '.';
                }
                else if (line.charAt(j)=='F') {
                    Fantome fantome = new Fantome(j,i); // on récupère les fantomes
                    this.fantomes.add(fantome);
                    this.board[j][i] = 'F';
                } else {
                    this.board[j][i] = line.charAt(j);
                }
            }
        }
        //on enlève les pouvoirs/malus des pacmans
        for(int k=0; k<pacmans.size();k++){
            pacmans.get(k).maudit=false;
            pacmans.get(k).vulnerable=true;
            pacmans.get(k).speed = 190;
            pacmans.get(k).pouvoir = null;
        }
    }

    //on cherche l'autre pacman (celui qui est différent du pacman en paramètre)
    public Pacman chercheAutrePacman(Pacman pacman){
        if(pacmans.size()==2) {
            if (pacman.equals(this.pacmans.get(0))) {
                return pacmans.get(1);
            } else {
                return pacmans.get(0);
            }
        }
        return null;
    }

    //changement de l'état de la map lors du déplacement du pacman
    // on prend en paramètre le pacman qui se déplace, la position futur du pacman et board va nous servir à rénitialiser la map
    public void deplacementPacman(Position positionFutur, String board, Pacman pacman) {
        if (pacman.lettre != 'F') { // si le pacman n'a pas été transformé en fantome
            this.setElement(pacman.position, ' ');
            if (this.getElement(positionFutur) == '.') { //récupération de la pacgomme
                pacman.changePoints(30); //augmentation des points
                pacman.setPosition(positionFutur); //actualise sa position
                this.setElement(pacman.position, pacman.lettre); //actualise la map

            } else if (this.getElement(positionFutur) == '#') { //récupération d'un pouvoir
                Pouvoir pouvoir = new Pouvoir(pacman); // crée notre pouvoir
                pouvoir.givePower(); // et on le donne au pacman
                pacman.setPosition(positionFutur); // actualise la positon du pacman
                this.setElement(pacman.position, pacman.lettre); // actualise la map

            } else if (this.getElement(positionFutur) == '/') { //prendre un portail
                //choix alétoire de la futur position
                Random random = new Random();
                int X = random.nextInt(this.getWidth());
                int Y = random.nextInt(this.getHeight());
                Position positionTeleportation = new Position(X, Y);
                //on vérifie qu'on va se téléporter dans un bon endroit de la map ( pas un mur, fantome ou autre portail )
                while (this.getElement(positionTeleportation) == '/' || this.getElement(positionTeleportation) == 'F' || this.getElement(positionTeleportation) == '%') {
                    int X1 = random.nextInt(this.getWidth());
                    int Y1 = random.nextInt(this.getHeight());
                    positionTeleportation = new Position(X1, Y1);
                }
                //téléportation du pacman
                pacman.setPosition(positionTeleportation);
                this.setElement(pacman.position, pacman.lettre);

            } else if (this.getElement(positionFutur) == '$') { //on arrive sur une bombe qui a explosé
                //verifie si la bombe lui appartient
                if (pacman.trouveBombeTouche(positionFutur) != null) {
                    pacman.trouveBombeTouche(positionFutur).toucheBombe(pacman);
                    pause = true; //on le mettra en false dans la classe game
                    //fait une pause
                    PauseTransition pause = new PauseTransition(Duration.millis(1500));
                    pause.setOnFinished(event -> {
                    	this.reinitialiseMap(board); //réinitialise la map car perdu
                    });
                    pause.play();
                } else { //si la bombe appartient à l'adversiare
                    Pacman pacmanAdverse = this.chercheAutrePacman(pacman); //on récupère l'autre pacman
                    pacmanAdverse.trouveBombeTouche(positionFutur).toucheBombe(pacman); //on applique les conséquences
                    pause = true; //on le met en false dans la classe game car c'est la classe game
                    //fait une pause
                    PauseTransition pause = new PauseTransition(Duration.millis(1500));
                    pause.setOnFinished(event -> {
                    	this.reinitialiseMap(board); //réinitialise la map car perdu
                    });
                    pause.play();
                }
            } else if (this.getElement(positionFutur) == '€') { // position futur = bombe à récupérer
                this.setElement(pacman.position, ' ');
                pacman.setPosition(positionFutur); //actualise la position du pacman
                pacman.ajoutBombe(); //on augmente le compteur de bombe
                this.setElement(positionFutur, pacman.lettre); //actualise la map

            } else if (this.getElement(positionFutur)=='@' || positionFutur==null) { //si on arrive sur une bombe qui a été posé mais pas explosé ( elle doit se comporter comme un mur, donc on ne fait rien )

            } else{ //c'est tout les autres cas
                pacman.setPosition(positionFutur); //actualise la position du pacman
                this.setElement(pacman.position, pacman.lettre); //actualise la map
            }
        } else { //si c'est un fantome
            if (this.getElement(positionFutur) != '%') { //on verifie seulement qu'on ne se dirige pas vers un mur
                this.setElement(pacman.position, pacman.lettreFuturPosition);
                pacman.lettreFuturPosition=this.getElement(positionFutur);
                pacman.setPosition(positionFutur);
            }

        }
    }

    //lorsqu'on a perdu, on devient un fantome
    public void transformationEnFantome(Pacman pacman){
        if(pacman.getLife()==0) {
            pacman.lettre = 'F'; //la lettre du pacman change et on devient un F
            this.setElement(pacman.position, 'F');
        }
    }

    // permet de gérer les collisions avec les fantomes et le pacman-fantome
    public void collisionFantome(Pacman pacman, String board){
        //s'il est vulnérable (peut être false si on a pris un pouvoir)
        if(pacman.vulnerable) {
            for (int k = 0; k < 4; k++) {
                if (pacman.position.equals(fantomes.get(k).position) && pacman.lettre != 'F') { //on verfie qu'on est pas un fantome et qu'on a la meme position q'un fantome
                    pacman.PerteVie();
                    pause = true; //on le met en false dans la classe game car c'est la classe game qui permet de mettre en pause le jeu
                    reinitialiseMap(board);
                }
            }
        }
        //pour le mode multijoueur
        if (pacmans.size()==2) {
            //cas où l'autre pacman est un fantome
            if ((chercheAutrePacman(pacman).lettre == 'F' && chercheAutrePacman(pacman).position.equals(pacman.position)) || (chercheAutrePacman(pacman).lettre == 'F' && pacman.position.equals(chercheAutrePacman(pacman)))  ) {
                pacman.PerteVie();
                chercheAutrePacman(pacman).changePoints(300); // on augmente les points du pacman-fantome
                pause = true; //on le met en false dans la classe game car c'est la classe game qui permet de mettre en pause le jeu
                reinitialiseMap(board);
            }
        }
    }


}