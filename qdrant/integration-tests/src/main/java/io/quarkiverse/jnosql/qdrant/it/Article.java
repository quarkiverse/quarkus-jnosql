package io.quarkiverse.jnosql.qdrant.it;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

import org.eclipse.jnosql.mapping.vector.DenseVector;

@Entity
public class Article {

    @Id
    private String id;

    @Column
    private String title;

    @Column
    private String category;

    @Column
    private DenseVector embedding;

    public Article() {
    }

    public Article(String id, String title, String category, DenseVector embedding) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.embedding = embedding;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public DenseVector getEmbedding() {
        return embedding;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setEmbedding(DenseVector embedding) {
        this.embedding = embedding;
    }
}
