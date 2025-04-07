package model;

public class User {
    private int id;
    private String email;
    private String role;
    private String pseudo;

    public User (int id, String email, String pseudo, String role) {
        this.id = id;
        this.email = email;
        this.pseudo = pseudo;
        this.role = role;

    }




    // Getters et setters

    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getPseudo() { return pseudo; }
    public String getRole() { return role; }
}
