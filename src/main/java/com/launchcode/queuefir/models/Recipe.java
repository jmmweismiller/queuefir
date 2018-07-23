package com.launchcode.queuefir.models;


import lombok.*;
import java.util.Date;
import javax.persistence.*;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter
@Data
@Entity @Table(name = "RECIPES")
public class Recipe {

        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "NAME")
        private String name;

        @Column(name = "BODY")
        private String body;

        @Column(name = "AUTHOR")
        private String author;

        @Column(name = "DATE")
        private Date date = new Date();

        public Recipe(String name, String body, String author, Date date) {
            this.name = name;
            this.body = body;
            this.author = author;
            this.date = date;
        }

    @Override
    public String toString() {
        return "Recipes{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", body='" + body + '\'' +
                ", author=" + author +
                ", date=" + date +
                '}';
    }
}
