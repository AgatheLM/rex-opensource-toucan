
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
- cf diapo
- discussions