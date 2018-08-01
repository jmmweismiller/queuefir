package com.launchcode.queuefir.models;


import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import javax.persistence.*;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter
@Data
@Entity @Table(name = "RECIPES")
public class Recipe {

        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "RECIPE_NAME")
        private String recipeName;

        @Column(name = "BODY")
        private String body;

        @Column(name = "AUTHOR")
        private String author;

        @Column(name = "AUTHOR_ID")
        private Long authorId;

        @Column(name = "PUBLISH_DATE")
        private LocalDate publishDate;

        public Recipe(String recipeName, String body, String author, Long authorId, LocalDate publishDate) {
            this.recipeName = recipeName;
            this.body = body;
            this.author = author;
            this.authorId = authorId;
            this.publishDate = publishDate;
        }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", recipeName='" + recipeName + '\'' +
                ", body='" + body + '\'' +
                ", author='" + author + '\'' +
                ", publishDate=" + publishDate +
                '}';
    }
}
