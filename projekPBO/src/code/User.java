/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package code;
import java.sql.Timestamp;

/**
 *
 * @author DHAIFAN
 */

public class User {
    private int idUser;
    private String username;
    private String password;
    private String role;
    private Timestamp createdAt;

    // Constructor Kosong
    public User() {}

    // Constructor Full
    public User(int idUser, String username, String password, String role, Timestamp createdAt) {
        this.idUser = idUser;
        this.username = username;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Getter & Setter
    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}