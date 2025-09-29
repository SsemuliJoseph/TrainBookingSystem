/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author ssemu
 */
package models;

/**
 * Simple User model used by login flows and dashboards.
 */
public class User {
    private final int userId;
    private final String username;
    private final String roleName;

    public User(int userId, String username, String roleName) {
        this.userId = userId;
        this.username = username;
        this.roleName = roleName;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRoleName() { return roleName; }
}
