package com.juankpapi.bookstore.repository;

import com.juankpapi.bookstore.model.Book;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.SUPPORTS;

// We do CRUD operations by using the Entity Manager API from JPA
// JPA will map automatically to a database, using JDBC. Then JDBC will generate and execute SQL statements


// SUPPORTS --> This strategy means: if a "read" db_method is invoked ...
// - outside a transaction, then a new transaction will not be created.
// - inside another transaction, then we want this "read" only db_method to be executed within the same transaction to allow DB isolation
// We can put the injection here on the class --> Use for all the methods on the class interacting with the DB, except those that specify a new Transactional Method (here see create or delete)
// or we can put on each one of the methods we need
@Transactional(SUPPORTS)
public class BookRepository {
    // ======================================
    // =          Injection Points          =
    // ======================================
    @PersistenceContext(unitName = "bookStorePU")
    private EntityManager em;

    // ======================================
    // =          Business methods          =
    // ======================================

    //**** Read Methods --> DB doesn't change ****
    public Book find(Long id) {
        return em.find(Book.class, id);
    }

    public List<Book> findAll() {
        //This is 'JPQL' language wich instead of dealing with tables, rows and columns, it manages entities
        TypedQuery<Book> query = em.createQuery("SELECT b FROM Book b ORDER BY b.title DESC", Book.class);
        return query.getResultList();
    }

    public Long countAll() {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(b) FROM Book b", Long.class);
        return query.getSingleResult();
    }


    // ======================================
    // =           Transactions             =
    // ======================================
    // Transactions are used to ensure the data is kept in a consistent state
    // DB transaction properties: ACID (Atomicity, Consistency, Isolation, Durability)
    // Atomicity: Indivisible and irreducible series of database operations such that either all occur, or nothing occurs. Thus, it prevents updates to the database occurring only partially,
    // We use JTA API to start, commit and rollback transactions


    //@Transactional start a JTA transaction before the method is invoke and commits all rollbacks after method is executed
    //REQUIRED --> This strategy means the container always propagate the transaction through the code.
    @Transactional(REQUIRED)
    public Book create(Book book) {
        em.persist(book);
        return book;
    }

    @Transactional(REQUIRED)
    public void delete(Long id) {
        em.remove(em.getReference(Book.class, id));
    }

}
