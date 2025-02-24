package com.example.fujiapp.data;

public class User {

    private long id;
    private String name;
    private String firstName;
    private String sex;
    private String birthday;
    private String userName;
    private String password;
    private double weight;
    private double height;

    public User() {
    }

    public User(int id, String name, String firstName, String sex, String date, String userName, String password, double weight, double height) {
        this.id = id;
        this.name = name;
        this.firstName = firstName;
        this.sex = sex;
        this.birthday = date;
        this.userName = userName;
        this.password = password;
        this.weight = weight;
        this.height = height;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDate() {
        return birthday;
    }

    public void setDate(String date) {
        this.birthday = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", firstName='" + firstName + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday='" + birthday + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", weight='" + weight + '\'' +
                ", height='" + height + '\'' +
                '}';
    }
}
