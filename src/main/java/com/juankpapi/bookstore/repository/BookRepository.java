package com.juankpapi.bookstore.repository;

import com.juankpapi.bookstore.model.Book;
import com.juankpapi.bookstore.util.NumberGenerator;
import com.juankpapi.bookstore.util.TextUtil;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.SUPPORTS;

// We do CRUD operations by using the Entity Manager API from JPA
// JPA will map automatically to a database, using JDBC. Then JDBC will generate and execute SQL statements


// SUPPORTS:
//           - If the client is running within a transaction and invokes the enterprise bean's method, the method executes within the client's transaction.
//           - If the client is not associated with a transaction, the container does not start a new transaction before running the method.
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
    // =          Business methods          =ß
    // ======================================

    //**** Read Methods --> DB doesn't change ****
    public Book find(@NotNull Long id) {
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
    // =            INJECTION              =
    // ======================================
    // To create a Book we need to create a random isbn and sanitize the title to avoid double spaces for example.
    // In order to do that we need to external services done by two external Beans: NumberGenerator and TextUtil
    @Inject
    private NumberGenerator generator;

    @Inject
    private TextUtil textUtil;


    //@Transactional start a JTA transaction before the method is invoke and commits all rollbacks after method is executed
    //REQUIRED:
    //          - If the client is running within a transaction and invokes the enterprise bean's method, the method executes within the client's transaction.
    //          - If the client is not associated with a transaction, the container starts a new transaction before running the method.
    @Transactional(REQUIRED)
    public Book create(@NotNull Book book) {
        //Call Injected Dependencies
        if(book.getIsbn() != null)
            book.setIsbn(generator.generateNumber());
        book.setTitle(textUtil.sanitize(book.getTitle()));
        em.persist(book);
        return book;
    }

    @Transactional(REQUIRED)
    public void delete(@NotNull Long id) {
        em.remove(em.getReference(Book.class, id));
    }

}

/*
// ======================================
// =            TRANSACTIONS            =
// ======================================
    - Transactions are used to ensure the data is kept in a consistent state
    - DB transaction properties: ACID (Atomicity, Consistency, Isolation, Durability)
    - Atomicity: Indivisible and irreducible series of database operations such that either all occur, or nothing occurs. Thus, it prevents updates to the database occurring only partially,
    - We use JTA API to start, commit and rollback transactions
    - Here is what the specification says about each of the six transaction attributes:
        + Required - If the client is running within a transaction and invokes the enterprise bean's method, the method executes within the client's transaction. If the client is not associated with a transaction, the container starts a new transaction before running the method. Most container-managed transactions use Required.
        + RequiresNew - If the client is running within a transaction and invokes the enterprise bean's method, the container suspends the client's transaction, starts a new transaction, delegates the call to the method, and finally resumes the client's transaction after the method completes. If the client is not associated with a transaction, the container starts a new transaction before running the method.
        + Mandatory - If the client is running within a transaction and invokes the enterprise bean's method, the method executes within the client's transaction. If the client is not associated with a transaction, the container throws the TransactionRequiredException. Use the Mandatory attribute if the enterprise bean's method must use the transaction of the client.
        + NotSupported - If the client is running within a transaction and invokes the enterprise bean's method, the container suspends the client's transaction before invoking the method. After the method has completed, the container resumes the client's transaction. If the client is not associated with a transaction, the container does not start a new transaction before running the method. Use the NotSupported attribute for methods that don't need transactions. Because transactions involve overhead, this attribute may improve performance.
        + Supports - If the client is running within a transaction and invokes the enterprise bean's method, the method executes within the client's transaction. If the client is not associated with a transaction, the container does not start a new transaction before running the method. Because the transactional behavior of the method may vary, you should use the Supports attribute with caution.
        + Never - If the client is running within a transaction and invokes the enterprise bean's method, the container throws a RemoteException. If the client is not associated with a transaction, the container does not start a new transaction before running the method.
 */


/*
// ======================================
// =        BEAN EXPLANATION            =
// ======================================
- A Bean is an object that is managed by Java EE contaoner
- In Java EE most objects are Beans, except for JPA Entities (Persitance)
- Extra services given by the container to a Bean
	+ services such as lifecycle management, interception or injection
- In terms of code, with very few exceptions, every Java class with a default counstructor is a Bean in Java EE
	+ It can have any annotation and can define any method
	+ Does not implement and interface
	+ Does not extend a specific class


// ======================================
// =      INJECTION EXPLANATION         =
// ======================================
- Service given by the container
- Beans depend on other beans
- Beans don't create their own dependencies (objECts). They delegate this task to the container
- Inversion of control: External container choose the dependency instead of the Bean
- CDI (Context and Dependency Injection)
	+ Java EE specification taking care of Dependency Injection (DI)
	+ Loose coupling, strong typing
	+ Container injects bean's references into other beans
*/
