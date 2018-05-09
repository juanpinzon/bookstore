package com.juankpapi.bookstore.model;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
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
    @NotNull        //Validation Anotation
    @Size(min = 1, max =200)
    private String title;

    @Column(length = 1000)
    @Size(min = 1, max = 10000)
    private String description;

    //Use Column(name = <val>) to change the name of an atribute in the DB table
    @Column(name = "unit_cost")
    @Min(1)
    private Float unitCost;

    @Column(length = 50)
    @NotNull
    @Size(min = 1, max = 50)
    private String isbn;

    //Use Temporal to specify a Date
    @Column(name = "publication_date")
    @Temporal(TemporalType.DATE)
    @Past
    private Date publicationDate;

    @Column(name = "nb_of_pages")
    private Integer nbOfPages;

    @Column(name = "image_url")
    private String imageURL;

    @Enumerated
    private Language language;

    // ======================================
    // =            Constructors            =
    // ======================================
    public Book() {
    }

    public Book(String isbn, String title, Float unitCost, Integer nbOfPages, Language language, Date publicationDate, String imageURL, String description) {
        this.isbn = isbn;
        this.title = title;
        this.unitCost = unitCost;
        this.nbOfPages = nbOfPages;
        this.language = language;
        this.publicationDate = publicationDate;
        this.imageURL = imageURL;
        this.description = description;
    }

    // ======================================
    // =        Getters and Setters         =
    // ======================================
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


    // ======================================
    // =   Methods hash, equals, toString   =
    // ======================================

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


/*
// ======================================
// =     VALIDATION EXPLANATION          =
// ======================================
- Retrieve valid data is crucial - ensure data is correct
- Constrain our model (business rule) to the valid data
- If data is invalid, send feedback so it can be corrected.

* BEA  N VALIDATION
    - Use in all Java EE components
    - Defines a set of contrains
    - Using annotations
    - Validates the set of constrains
    - Built-in common constrains and APIs to create our own
    NOTE: Constrains can be added not only to class attribute, but also to constructor and method parameters, as well as return value
          Then in our app we add Constratins notations to Book class and BookRepository class

* Built-in constrains
    + Boolean
        - @AssertFalse
        - @AssertTrue
    + Size
        - @Size
        - @Digits
        - @Max, @Min
    + Date
        - @Future
        - @Past
    + Object
        - @Null
        - @NotNull
    + Regular Expression
        @Pattern, ie. ^[0-9]+abc$



 */