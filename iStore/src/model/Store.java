package model;

public class Store {
        private int id;          // Identifiant unique
        private String nom;      // Nom du magasin

        // Constructeur
        public Store(int id, String nom) {
            this.id = id;
            this.nom = nom;
        }

        // Getters et setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }

        @Override
        public String toString() {
            return "Store{" +
                    "id=" + id +
                    ", nom='" + nom + '\'' +
                    '}';
        }
    }



