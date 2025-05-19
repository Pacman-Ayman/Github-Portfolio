package Vue;


import Controleur.Controleur;
import Modele.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;



public class Game extends Application {


	private static Modele modele = new Modele(); // creation du modele du jeu
    private static Mode mode = new Mode(Menu.selected_bombe, Menu.selected_pouvoir, Menu.selected_portail, Menu.multijoueur, modele.map); // recuperation des modes choisis dans le menu
    private boolean fin = false; // permet de savoir si le jeu est fini
    
    private static final int TILE_SIZE = 30; // taille d'une cellule de la grille
    private final int WIDTH = modele.map.getWidth(); // nombre de cellules dans la grille (largeur)
    private final int HEIGHT = modele.map.getHeight(); // nombre de cellules dans la grille (hauteur)
    private long lastUpdateTime = System.currentTimeMillis(); // heure de la derniere mise a jour de l'interface graphique
    private final long UPDATE_INTERVAL = 190; // delai avant reactualisation fantome (permet de regler la vitesse des fantomes)
    
    private Stage endStage = new Stage(); // stage affiche en fin de partie

    private boolean showDetectionRadius = false; // pour dessiner les cercles des fantomes (voir fonctionnement ia fantome)
    
    // chargement des ressources necessaires au jeu
    public Image pacman1Image = new Image(getClass().getResourceAsStream("Ressources/pacman1droite.gif"));
    public Image pacman2Image = new Image(getClass().getResourceAsStream("Ressources/pacman2gauche.gif"));
    public ImageView pacman1View = new ImageView(pacman1Image);
    private Image fantomeImage = new Image(getClass().getResourceAsStream("Ressources/fantome.png"));
    private Image portailImage = new Image(getClass().getResourceAsStream("Ressources/portail.png"));
    private Image bombePoseeImage = new Image(getClass().getResourceAsStream("Ressources/bombeposee.png"));
    private Image bombeRamasserImage = new Image(getClass().getResourceAsStream("Ressources/bomberamasser.png"));
    private Image pouvoirImage = new Image(getClass().getResourceAsStream("Ressources/pouvoir.png"));
    
    @Override
    public void start(Stage primaryStage) { // methode appelee lors du lancement de l'application
        
        VBox root = new VBox(40); // vertical box qui contient les elements graphiques du jeu
        VBox score1 = new VBox(10); // contient le score du joueur 1
        VBox score2 = new VBox(10); // contient le score du joueur 2
        VBox annonce =new VBox(10); // permet d'afficher le pouvoir utilise si le mode pouvoir est active
        HBox info = new HBox(200); // horizontal box qui contient les scores et l'annonce
        
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: BLACK");
        
        Canvas canvas = new Canvas(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);	// canvas pour dessiner le jeu
        
        Image titre = new Image(getClass().getResourceAsStream("Ressources/PacmanLogo.png")); // chargement du titre
        ImageView titreView = new ImageView(titre);
        titreView.setFitWidth(500);
        titreView.setFitHeight(150);
        
        Label scorej1 = new Label();						// affichage du score du joueur 1
        scorej1.setTextFill(Color.GHOSTWHITE);
        scorej1.setFont(new Font("Serif", 20));
        Label lifej1 = new Label();							// affichage du nombre de vie du joueur 1
        lifej1.setTextFill(Color.GHOSTWHITE);
        lifej1.setFont(new Font("Serif", 20));
        Label bombej1 = new Label();						// affichage du nombre de bombe du joueur 1
        bombej1.setTextFill(Color.GHOSTWHITE);
        bombej1.setFont(new Font("Serif", 20));
        Label pseudoj1 = new Label();						// affichage du pseudo du joueur 1
        pseudoj1.setTextFill(Color.GHOSTWHITE);
        pseudoj1.setFont(new Font("Serif", 20));
        
        score1.getChildren().addAll(pseudoj1, scorej1, lifej1); // ajout des labels dans score1
        if(Menu.selected_bombe) {
        	score1.getChildren().add(bombej1);
        }
        
        Label scorej2 = new Label();						// affichage du score du joueur 2
        scorej2.setTextFill(Color.GHOSTWHITE);
        scorej2.setFont(new Font("Serif", 20));
        Label lifej2 = new Label();							// affichage du nombre de vie du joueur 2
        lifej2.setTextFill(Color.GHOSTWHITE);				
        lifej2.setFont(new Font("Serif", 20));
        Label bombej2 = new Label();						// affichage du nombre de bombe du joueur 2
        bombej2.setTextFill(Color.GHOSTWHITE);				
        bombej2.setFont(new Font("Serif", 20));
        Label pseudoj2 = new Label();						// affichage du pseudo du joueur 2
        pseudoj2.setTextFill(Color.GHOSTWHITE);
        pseudoj2.setFont(new Font("Serif", 20));
        
        Label annonce1 = new Label();						// affichage des pouvoirs du joueur 1
        annonce1.setTextFill(Color.GHOSTWHITE);
        annonce1.setFont(new Font("Serif", 20));
        annonce1.setVisible(true);
        
        Label annonce2 = new Label();						// affichage des pouvoirs du joueur 2
        annonce2.setTextFill(Color.GHOSTWHITE);
        annonce2.setFont(new Font("Serif", 20));
        annonce2.setVisible(true);
        
        annonce.getChildren().addAll(annonce1, annonce2);	// ajout des labels dans annonce
        
        info.setAlignment(Pos.CENTER);
        info.getChildren().addAll(score1, annonce);			// ajout des box dans info
        
        if(Menu.multijoueur == true) {
            score2.getChildren().addAll(pseudoj2, scorej2, lifej2);
            if (Menu.selected_bombe) {
            	score2.getChildren().add(bombej2);
            }
            info.getChildren().add(score2);					// ajout de score2 si le mode multijoueur est active
        }
        
        root.getChildren().addAll(titreView, info, canvas);	//ajout des box et du canvas sur la toile
        
        Scene scene = new Scene(root, 1400, 900);			//création d'une scène montrant la toile
        
        // pour afficher les cercles
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {			// cliquer sur espace pour afficher les cercles
                showDetectionRadius = !showDetectionRadius;  
            }
            // chargement des bons sprites en fonction des déplacements et du modele
            if (event.getCode() == KeyCode.UP) {			
            	if(modele.map.pacmans.get(0).life == 0) {
                	pacman1Image = new Image(getClass().getResourceAsStream("Ressources/morthaut.gif"));
            	} else {
            		if(modele.map.pacmans.get(0).pouvoir != null) {
            			pacman1Image = new Image(getClass().getResourceAsStream("Ressources/spacman1haut.gif"));
            		} else {
            			pacman1Image = new Image(getClass().getResourceAsStream("Ressources/pacman1haut.gif"));
            		}
            	}
            } if (event.getCode() == KeyCode.DOWN) {
            	if(modele.map.pacmans.get(0).life == 0) {
                	pacman1Image = new Image(getClass().getResourceAsStream("Ressources/mortbas.gif"));
            	} else {
            		if(modele.map.pacmans.get(0).pouvoir != null) {
            			pacman1Image = new Image(getClass().getResourceAsStream("Ressources/spacman1bas.gif"));
            		} else {
            			pacman1Image = new Image(getClass().getResourceAsStream("Ressources/pacman1bas.gif"));
            		}
            	}
            } if (event.getCode() == KeyCode.LEFT) {
            	if(modele.map.pacmans.get(0).life == 0) {
                	pacman1Image = new Image(getClass().getResourceAsStream("Ressources/mortgauche.gif"));
            	} else {
            		if(modele.map.pacmans.get(0).pouvoir != null) {
            			pacman1Image = new Image(getClass().getResourceAsStream("Ressources/spacman1gauche.gif"));
            		} else {
            			pacman1Image = new Image(getClass().getResourceAsStream("Ressources/pacman1gauche.gif"));
            		}
            	}
            } if (event.getCode() == KeyCode.RIGHT) {
            	if(modele.map.pacmans.get(0).life == 0) {
                	pacman1Image = new Image(getClass().getResourceAsStream("Ressources/mortdroite.gif"));
            	} else {
            		if(modele.map.pacmans.get(0).pouvoir != null) {
            			pacman1Image = new Image(getClass().getResourceAsStream("Ressources/spacman1droite.gif"));
            		} else {
            			pacman1Image = new Image(getClass().getResourceAsStream("Ressources/pacman1droite.gif"));
            		}
            	}
            } if (event.getCode() == KeyCode.Z) {
            	if(Menu.multijoueur == true) {
                	if(modele.map.pacmans.get(1).life == 0) {
                		pacman2Image = new Image(getClass().getResourceAsStream("Ressources/morthaut.gif"));
                	} else {
                		if(modele.map.pacmans.get(1).pouvoir != null) {
                			pacman2Image = new Image(getClass().getResourceAsStream("Ressources/spacman2haut.gif"));
                		} else {
                			pacman2Image = new Image(getClass().getResourceAsStream("Ressources/pacman2haut.gif"));
                		}
                	}
            	}
            } if (event.getCode() == KeyCode.S) {
            	if(Menu.multijoueur == true) {
                	if(modele.map.pacmans.get(1).life == 0) {
                		pacman2Image = new Image(getClass().getResourceAsStream("Ressources/mortbas.gif"));
                	} else {
                		if(modele.map.pacmans.get(1).pouvoir != null) {
                			pacman2Image = new Image(getClass().getResourceAsStream("Ressources/spacman2bas.gif"));
                		} else {
                			pacman2Image = new Image(getClass().getResourceAsStream("Ressources/pacman2bas.gif"));
                		}
                	}
            	}
            } if (event.getCode() == KeyCode.Q) {
            	if(Menu.multijoueur == true) {
                	if(modele.map.pacmans.get(1).life == 0) {
                		pacman2Image = new Image(getClass().getResourceAsStream("Ressources/mortgauche.gif"));
                	} else {
                		if(modele.map.pacmans.get(1).pouvoir != null) {
                			pacman2Image = new Image(getClass().getResourceAsStream("Ressources/spacman2gauche.gif"));
                		} else {
                			pacman2Image = new Image(getClass().getResourceAsStream("Ressources/pacman2gauche.gif"));
                		}
                	}
            	}
            } if (event.getCode() == KeyCode.D) {
            	if(Menu.multijoueur == true) {
                	if(modele.map.pacmans.get(1).life == 0) {
                		pacman2Image = new Image(getClass().getResourceAsStream("Ressources/mortdroite.gif"));
                	} else {
                		if(modele.map.pacmans.get(1).pouvoir != null) {
                			pacman2Image = new Image(getClass().getResourceAsStream("Ressources/spacman2droite.gif"));
                		} else {
                			pacman2Image = new Image(getClass().getResourceAsStream("Ressources/pacman2droite.gif"));
                		}
                	}
            	}
            }
        });
        
        // lancement des modes en fonction des choix effectues dans le menu
        mode.modePortail();
        mode.modeBomberman();
        mode.modePouvoir();
        mode.modeMultijoueur();
        
        Controleur controleur = new Controleur(modele, scene);	// creation d'un controleur pour les joueurs 
        GraphicsContext gc = canvas.getGraphicsContext2D();		// variable contenant le contexte graphique du canvas
        
        
        new AnimationTimer() {   			// permet l'actualisation du jeu
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {	// gere les mises a jour de l'affichage avec l'actualisation du jeu

                if (now - lastUpdate >= 1_000_000) {		// actualisation toutes les 10ms
                	update();								// methode update a completer par les autres membres en cas de besoin
                    draw(gc);								// permet de dessiner le canvas
                    long currentTime = System.currentTimeMillis();
                    
                    // mise a jour des labels
                    updateScore(scorej1,0);
                    updateLife(lifej1,0);
                    updateBombe(bombej1,0);
                    updatePseudo(pseudoj1,0);
                    updateAnnonce(annonce1, 0);
                    
                    if (Menu.multijoueur == true) {
                    	updateScore(scorej2,1);
                        updateLife(lifej2,1);
                        updateBombe(bombej2,1);
                        updatePseudo(pseudoj2,1);
                        updateAnnonce(annonce2,1);
                    }
                    
                    // gere la transformation en fantome en cas de mort
                    for(int k=0; k<modele.map.pacmans.size();k++){
                        modele.map.transformationEnFantome(modele.map.pacmans.get(k));
                    }
                    
                    // mise à jour de chaque fantôme
                    if (currentTime - lastUpdateTime > UPDATE_INTERVAL) {
                    	for(int k=0; k<4; k++) {
                            modele.map.fantomes.get(k).moveRandomly(modele.map);
                    	}    
                        lastUpdateTime = currentTime;
                    }

                    // creation des pauses entre les reinitialisations
                    if (modele.map.pause) {
                        try {
                            // Met le thread en pause pour 1 seconde
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        modele.map.pause = false; // fin de la pause
                    }
                    
                    // gestion de la fin du jeu
                    if(modele.isEnd() && fin == false){
                        fin = true;
                        
                        //chargement des gif finaux
                        pacman1Image = new Image(getClass().getResourceAsStream("Ressources/pacman1droite.gif"));
                        ImageView pacman1View = new ImageView(pacman1Image);
                        pacman1View.setFitWidth(100);
                        pacman1View.setFitHeight(100);
                        
                        pacman2Image = new Image(getClass().getResourceAsStream("Ressources/pacman2droite.gif"));
                        ImageView pacman2View = new ImageView(pacman2Image);
                        pacman2View.setFitWidth(100);
                        pacman2View.setFitHeight(100);
                        
                        primaryStage.close(); // ferme la fenetre de jeu
                        
                        VBox endScreenRoot = new VBox(50); // vertical box qui contient les elements graphiques de l'ecran de fin
                        VBox res = new VBox(30);		   // vertical box qui affiche les informations du gagnant
                        HBox info = new HBox(50);		   // horizontal box qui affiche les infos des 2 joueurs
                        VBox score1 = new VBox(10);		   // vertical box qui contient les infos du joueur 1
                        VBox score2 = new VBox(10);        // vertical box qui contient les infos du joueur 2
                        HBox allButton = new HBox(30);     // horizontal box qui contient les boutons
                        
                        // changement graphique
                        endScreenRoot.setPadding(new Insets(20));
                        endScreenRoot.setAlignment(Pos.CENTER);
                        endScreenRoot.setStyle("-fx-background-color: BLACK");
                        
                        info.setAlignment(Pos.CENTER);
                        
                        allButton.setAlignment(Pos.CENTER);
                        
                        res.setAlignment(Pos.CENTER);
                        
                        Label scorej1 = new Label("Score : " + modele.map.pacmans.get(0).points); // label recuperant le score final du joueur 1
                        scorej1.setTextFill(Color.GHOSTWHITE);
                        scorej1.setFont(new Font("Serif", 20));
                        Label pseudoj1 = new Label(Menu.pseudos.get(0));						// label recuperant le pseudo du joueur 1
                        pseudoj1.setTextFill(Color.GHOSTWHITE);
                        pseudoj1.setFont(new Font("Serif", 20));
                        
                        score1.getChildren().addAll(pseudoj1, scorej1);
                        
                        info.getChildren().add(score1);
                        
                        Label resultat = new Label("Bien joué " + Menu.pseudos.get(0) + " !"); // label recuperant le pseudo du gagnant
                        resultat.setTextFill(Color.GHOSTWHITE);
                        resultat.setFont(new Font("Serif", 20));
                        
                        // creation des labels relatifs aux informations du joueur 2 si le mode multijoueur est active
                        if (Menu.multijoueur == true) {
                        	Label scorej2 = new Label("Score : " + modele.map.pacmans.get(1).points);
                        	scorej2.setTextFill(Color.GHOSTWHITE);
                        	scorej2.setFont(new Font("Serif", 20));
                        	Label pseudoj2 = new Label(Menu.pseudos.get(1));
                        	pseudoj2.setTextFill(Color.GHOSTWHITE);
                        	pseudoj2.setFont(new Font("Serif", 20));
                        	score2.getChildren().addAll(pseudoj2, scorej2);
                        	if (modele.map.pacmans.get(1).points > modele.map.pacmans.get(0).points) {
                        		resultat.setText("Bien joué " + Menu.pseudos.get(1) + " !");
                        		res.getChildren().addAll(pacman2View, resultat);
                        	} else if(modele.map.pacmans.get(1).points == modele.map.pacmans.get(0).points) {
                        		resultat.setText("Egalité !");
                        	} else {
                        		res.getChildren().addAll(pacman1View, resultat);
                        	}
                        	info.getChildren().add(score2);
                        }

                        Button replayButton = new Button("Rejouer"); // bouton permettant de rejouer
                        replayButton.setFont(new Font("Serif", 20));
                        replayButton.setOnAction(event -> { // relance le jeu avec les memes parametres
                        	modele = new Modele();
                        	mode = new Mode(Menu.selected_bombe, Menu.selected_pouvoir, Menu.selected_portail, Menu.multijoueur, modele.map);
                        	fin = false;
                        	Controleur controleur = new Controleur(modele,scene);
                        	mode.modePortail();
                        	mode.modePouvoir();
                            mode.modeBomberman();
                            mode.modeMultijoueur();
                        	primaryStage.setScene(scene);
                        	primaryStage.setTitle("Pac-Man");
                            primaryStage.setResizable(true);
                            primaryStage.show();
                            endStage.hide();
                        });
                        
                        Button menuButton = new Button("Retour au menu"); // bouton permettant de retourner dans le menu, NE PERMET PAS DE RELANCER DEPUIS LE MENU
                        menuButton.setFont(new Font("Serif", 20));
                        menuButton.setOnAction(event -> {
                        	Platform.exit();
                        	Menu.clipBgSound.stop();
                        	Menu.main(null);                      	
                        });
                        
                        Button quitButton = new Button("Quitter"); // bouton permettant d'arreter le programme
                        quitButton.setFont(new Font("Serif", 20));
                        quitButton.setOnAction(event -> {  // quitte l'application
                        	endStage.close();
                        	System.exit(0);
                        });
                        
                        allButton.getChildren().addAll(replayButton, menuButton, quitButton); // ajout des boutons dans la hbox
                        
                        endScreenRoot.getChildren().addAll(titreView, res, info, allButton); // ajout de tous les elements necessaires a l'ecran de fin
                        
                        // creation de la scene et affichage du stage
                        Scene end = new Scene(endScreenRoot, 1500, 1000);
                        endStage.setScene(end);
                        endStage.setTitle("Game Over !");
                        endStage.setResizable(true);
                        endStage.show();
                    }

                    lastUpdate = now;
                }

            }
        }.start();
        
        //ouverture d'une fenetre affichant la scene
        primaryStage.setScene(scene);
        primaryStage.setTitle("Pac-Man");
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    private void update() {
        // Mise à jour de l'interface, a completer si besoin
        for (int k=0; k<modele.map.pacmans.size(); k++) {
            modele.map.collisionFantome(modele.map.pacmans.get(k), modele.board); // permet de corriger des bugs de collision
        }
    }
    
    // methode dessinant le canvas
    private void draw(GraphicsContext gc) {
    	
        // Efface le canvas
        gc.clearRect(0, 0, WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);

        // Fond d'ecran du canvas
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);

        //dessiner les cercles
        if (showDetectionRadius) {
            gc.setStroke(Color.RED);
            gc.setLineWidth(2);
            for (Fantome fantome : modele.map.fantomes) {
                int radiusPixels = fantome.DETECTION_RADIUS * TILE_SIZE;
                int centerX = fantome.position.getX() * TILE_SIZE + TILE_SIZE / 2;
                int centerY = fantome.position.getY() * TILE_SIZE + TILE_SIZE / 2;
                gc.strokeOval(centerX - radiusPixels / 2, centerY - radiusPixels / 2, radiusPixels, radiusPixels);
            }
        }

        // Dessine un mur
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (modele.map.board[i][j] == '%') {
                    gc.setFill(Color.PURPLE);
                    gc.fillRect(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

     // Dessine un portail
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (modele.map.board[i][j] == '/') {
                    gc.drawImage(portailImage, i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        // Dessine un pouvoir
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (modele.map.board[i][j] == '#') {
                    gc.drawImage(pouvoirImage, i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        // Dessine une pacgomme
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (modele.map.board[i][j] == '.') {
                    gc.setFill(Color.YELLOW);
                    gc.fillOval(i * TILE_SIZE + TILE_SIZE/3 , j * TILE_SIZE + TILE_SIZE/3, TILE_SIZE/3, TILE_SIZE/3);
                }
            }
        }

        // Dessine fantome
        for (int i = 0; i < 4; i++) {
            gc.drawImage(fantomeImage, modele.map.fantomes.get(i).position.getX() * TILE_SIZE, modele.map.fantomes.get(i).position.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        };


        // Dessine PacMan1
        gc.drawImage(pacman1Image, modele.map.pacmans.get(0).position.X * TILE_SIZE, modele.map.pacmans.get(0).position.Y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // Dessine PacMan2
        if(Menu.multijoueur == true) {
        	gc.drawImage(pacman2Image, modele.map.pacmans.get(1).position.X * TILE_SIZE, modele.map.pacmans.get(1).position.Y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        }

        // Dessine une bombe posée
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (modele.map.board[i][j] == '@') {
                        gc.drawImage(bombePoseeImage, i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    }
                }
            }

        // Dessine une bombe explosée
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (modele.map.board[i][j] == '$') {
                    gc.setFill(Color.RED);
                    gc.fillRect(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        // Dessine une bombe à récupérer
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (modele.map.board[i][j] == '€') {
                    gc.drawImage(bombeRamasserImage, i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
    }

    private void updateScore(Label label, int i) {		//mise a jour étiquette du score
        label.setText("Score = " + modele.map.pacmans.get(i).points);
    }

    private void updateLife(Label label, int i) {		//mise a jour etiquette nombre de vies
        label.setText("Vie(s) = " + modele.map.pacmans.get(i).life);
    }

    private void updateBombe(Label label, int i) {		//mise a jour etiquette nombre de bombes
        label.setText("Bombes = " + modele.map.pacmans.get(i).nbrBombes);
    }
    
    private void updatePseudo(Label label, int i) {		//mise a jour etiquette du pseudo
        label.setText(Menu.pseudos.get(i));
    }
    
    public static void updateAnnonce(Label label, int i) {  // mise a jour etiquette annoncant les pouvoirs en cours
    	if(modele.map.pacmans.get(i).pouvoir == null) {
    		label.setVisible(false);
    	} else {
    		label.setText("Pouvoir activé : " + Menu.pseudos.get(i) + " utilise " + modele.map.pacmans.get(i).pouvoir);
    		label.setVisible(true);
    	}
    }
    
    public void setMode(boolean pouvoir, boolean portail, boolean bomberman, boolean multijoueur){  // setter de mode
        mode = new Mode(bomberman,pouvoir,portail,multijoueur, modele.map);
    }

}