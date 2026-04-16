package fr.bookhub.bo;


import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table
public class Categories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, length = 10)
    private int Id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    public Categories() {
    }

    public Categories(int id, String name) {
        Id = id;
        this.name = name;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Categories that = (Categories) o;
        return Id == that.Id && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id, name);
    }

    @Override
    public String toString() {
        return "Categories{" +
                "Id=" + Id +
                ", name='" + name + '\'' +
                '}';
    }
}
