
non-adhérence :
- composants libres (postgre/keycloak) vs composants propriétaires (Igesa/Spoc/InseeConfig...)
- gains : exécution en local/hors réseau Insee, facilité de développement, contraintes légales (loi Rép num 2016)


schéma 1 :
- création d'une interface pour les services pointant vers des composants propriétaires Insee, et d'une implémentation Mock
- properties vers BDD et Keycloak local

schéma 2 :
- création d'une API autonome (bdd H2 en mémoire, authentification BASIC avec les rôles et id/mdp en mémoire et mocks vers services externes) : contient 95% du code
- utilisation de cette API en tant que bibliothèque d'une API Insee qui redéfinit les services internes


démo :
- démarrage de l'appli autonome en ligne de commande (en quelques secondes) avec des données au démarrage
- démo import de données + visu

conclusion :
- il n'y a pas d'effets de bord avec des données qui peuvent changer à la base à laquelle on se connecte car le chargement de la base en mémoire se fait au démarrage de l'application avec les données que j'ai précisé dans le code source
- on peut développer n'importe ou, même si je n'ai pas de réseau, pas grave car pas besoin de me connecter au serveur Keycloak ou au serveur Postgre
- l'avantage du pc hors réseau et qu'on s'affranchit de tous les problèmes réseau avec Direct Access qu'il peut y avoir depuis le début du télétravail massif

discussion