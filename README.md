# 🟡 Pac-Man Multijoueur 

## 📘 Description

Ce projet équipe est une adaptation multijoueur du célèbre jeu **Pac-Man**. Il offre une expérience enrichie grâce à des **modes de jeu innovants**, une **IA améliorée** et un **mode 2 joueurs** avec la possibilité de continuer la partie en tant que fantôme.

---

## 🎮 Fonctionnalités principales

- 🧑‍🤝‍🧑 **Mode Solo ou 2 joueurs** (sur le même clavier)
- 🌀 **Modes de jeu cumulables** :
  - **Mode Portail** : téléportation aléatoire via des portails mobiles
  - **Mode Pouvoir** : bonus/malus comme invincibilité, malédiction, vitesse
  - **Mode Bomberman** : pose de bombes pour éliminer l’adversaire
- 👻 **Fantômes intelligents** avec IA :
  - Déplacement **aléatoire** ou **ciblé** selon la distance à Pac-Man
- 🔄 **Rôle inversé** : un joueur éliminé devient fantôme
- 🏆 **Système de scores** sauvegardé avec JDBC

---

## 📸 Aperçu visuel

| Mode                              | Animation                                |
|-----------------------------------|-------------------------------------------|
| Déplacement aléatoire des fantômes | <img src="https://github.com/user-attachments/assets/52d2c579-4cfa-4c49-90bb-db0e1ddac9ee" width="200"/> |
| Déplacement ciblé des fantômes     | <img src="https://github.com/user-attachments/assets/cbaf827a-1356-4b35-b072-2900fbbd5093" width="200"/>     |
| Mode Bomberman                     | <img src="https://github.com/user-attachments/assets/7640b0fd-9a56-478f-aa89-29aa151a5e09" width="200"/>          |
| Mode Portail                       | <img src="https://github.com/user-attachments/assets/365b62f0-f36d-47e7-a72b-49f07bd499ee" width="200"/>                 |
| Mode Pouvoir                       | <img src="https://github.com/user-attachments/assets/d774f5af-bb32-4d7b-bb9c-18cee5c665b1" width="200"/>                 |
| Gameplay complet                   | <img src="https://github.com/user-attachments/assets/02990cba-e276-4c80-9634-6c565428a6c6" width="200"/>  

---

## 🏗️ Architecture du projet

Le jeu suit une architecture **Modèle-Vue-Contrôleur (MVC)** :


- **Modèle** : logique du jeu (joueurs, fantômes, collisions, portails…)
- **Vue** : interface graphique (JavaFX + Swing)
- **Contrôleur** : gestion des entrées clavier
- **Utile** : ressources (sprites, musique)

---

## 🛠️ Technologies utilisées

- 💻 **Langage** : Java
- 🖼️ **Interface** : JavaFX + Swing
- 🗂️ **Base de données** : JDBC
- 🛠️ **IDE** : Eclipse
- 🔁 **Versioning** : Git (GitLab)

---

## ✅ Tests réalisés

- ✔️ **Tests unitaires** : IA des fantômes, collisions, mouvements
- ✔️ **Tests d’intégration** : enchaînement des comportements (portails, bombes, pouvoirs)

---

## 📅 Organisation du projet

- 📌 **Réunions fréquentes**
- 🗓️ **Diagramme de Gantt** pour la planification
- 👥 **Répartition claire des tâches** par module
- 🔁 **Mise à jour régulière** du plan de charge
