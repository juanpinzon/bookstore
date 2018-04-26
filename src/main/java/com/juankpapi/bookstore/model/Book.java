package com.juankpapi.bookstore.model;

import javax.persistence.*;
import java.util.Date;
//Metadata is extra information that will be used for JPA to map this object <Book> to a database

//Object is an Entity
@Entity
public class Book {

    //Primary key
    @Id @GeneratedValue (strategy = GenerationType.AUTO)
    private Long id;

    //Use Column(length = <val>) to change the length of an atribute
    @Column(length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    //Use Column(name = <val>) to change the name of an atribute in the DB table
    @Column(name = "unit_cost")
    private Float unitCost;

    private String isbn;

    //Use Temporal to specify a Date
    @Column(name = "publication_date")
    @Temporal(TemporalType.DATE)
    private Date publicationDate;

    private Integer nbOfPages;

    private String imageURL;

    private Language language;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(Float unitCost) {
        this.unitCost = unitCost;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Integer getNbOfPages() {
        return nbOfPages;
    }

    public void setNbOfPages(Integer nbOfPages) {
        this.nbOfPages = nbOfPages;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", unitCost=" + unitCost +
                ", isbn='" + isbn + '\'' +
                ", publicationDate=" + publicationDate +
                ", nbOfPages=" + nbOfPages +
                ", imageURL='" + imageURL + '\'' +
                ", language=" + language +
                '}';
    }
}
