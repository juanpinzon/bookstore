package com.juankpapi.bookstore.repository;

import com.juankpapi.bookstore.model.Book;
import com.juankpapi.bookstore.model.Language;
import com.juankpapi.bookstore.util.NumberGenerator;
import com.juankpapi.bookstore.util.TextUtil;
import com.juankpapi.bookstore.util.IsbnGenerator;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Date;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class BookRepositoryTest {

    // ======================================
    // =             Attributes             =
    // ======================================
    private static Long bookId;

    // ======================================
    // =          Injection Points          =
    // ======================================
    @Inject
    private BookRepository bookRepository;

    // ======================================
    // =             Deployment             =
    // ======================================
    //- ShrinkWrap creates deployable archives: Here a jar
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                //Files you need to add to the package file so everything is ready on the container to run the tests.
                .addClass(Book.class)
                .addClass(Language.class)
                .addClass(BookRepository.class)
                .addClass(IsbnGenerator.class)
                .addClass(NumberGenerator.class)
                .addClass(TextUtil.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsManifestResource("META-INF/test-persistence.xml", "persistence.xml");
    }


    // ======================================
    // =            Test methods            =
    // ======================================
    @Test
    @InSequence(1)
    public void basicTest() {
        // Count all
        assertEquals(Long.valueOf(0), bookRepository.countAll());
        // Find all
        assertEquals(0, bookRepository.findAll().size());

        //Create a book
        Book book = new Book("isbn", "a   title", 12F, 123, Language.ENGLISH, new Date(), "imageURL", "description");
        book = bookRepository.create(book);
        Long bookId = book.getId();

        //Check created book Id
        assertNotNull(bookId);

        //Find created book
        Book bookFound = bookRepository.find(bookId);
        //Check the found Book
        assertEquals("a title", bookFound.getTitle());

        // Count all --> Shoud be 1 Book on DB
        assertEquals(Long.valueOf(1), bookRepository.countAll());
        // Find all
        assertEquals(1, bookRepository.findAll().size());

        //Delete
        bookRepository.delete(bookId);

        // Count all --> Shoud be 0 Books on DB
        assertEquals(Long.valueOf(0), bookRepository.countAll());
        // Find all
        assertEquals(0, bookRepository.findAll().size());
    }

    @Test
    @InSequence(2)
    public void shouldBeDeployed() {
        assertNotNull(bookRepository);
    }

    @Test
    @InSequence(3)
    public void shouldGetNoBook() {
        // Count all
        assertEquals(Long.valueOf(0), bookRepository.countAll());
        // Find all
        assertEquals(0, bookRepository.findAll().size());
    }

    @Test
    @InSequence(4)
    public void shouldCreateABook() {
        // Creates a book
        Book book = new Book("isbn", "title", 12F, 123, Language.ENGLISH, new Date(), "imageURL", "description");
        book = bookRepository.create(book);
        // Checks the created book
        assertNotNull(book);
        assertNotNull(book.getId());
        bookId = book.getId();
    }

    @Test
    @InSequence(5)
    public void shouldFindTheCreatedBook() {
        // Finds the book
        Book bookFound = bookRepository.find(bookId);
        // Checks the found book
        assertNotNull(bookFound.getId());
        assertEquals("title", bookFound.getTitle());
    }

    @Test
    @InSequence(6)
    public void shouldGetOneBook() {
        // Count all
        assertEquals(Long.valueOf(1), bookRepository.countAll());
        // Find all
        assertEquals(1, bookRepository.findAll().size());
    }

    @Test
    @InSequence(7)
    public void shouldDeleteTheCreatedBook() {
        // Deletes the book
        bookRepository.delete(bookId);
        // Checks the deleted book
        Book bookDeleted = bookRepository.find(bookId);
        assertNull(bookDeleted);
    }

    @Test
    @InSequence(8)
    public void shouldGetNoMoreBook() {
        // Count all
        assertEquals(Long.valueOf(0), bookRepository.countAll());
        // Find all
        assertEquals(0, bookRepository.findAll().size());
    }


    // ======================================
    // =          VALIDATION TEST           =
    // ======================================

    @Test(expected = Exception.class)           // Like <throws Exception>, but handle automatically
    @InSequence(10)
    public void createInvalidBook() {
        //Create an invalid book with null title
        Book book = new Book("isbn", null, 12F, 123, Language.ENGLISH, new Date(), "imageURL", "description");
        book = bookRepository.create(book);
    }

    @Test(expected = Exception.class)
    @InSequence(11)
    public void findWithInvalidId() {
        bookRepository.find(null);
    }


    @Test(expected = Exception.class)  //expect is used when you know the method is going to fail due an Exception
    @InSequence(12)
    public void shouldFailCreatingANullBook() {
        bookRepository.create(null);
    }

    @Test(expected = Exception.class)
    @InSequence(13)
    public void shouldFailCreatingABookWithNullTitle() {
        bookRepository.create(new Book("isbn", null, 12F, 123, Language.ENGLISH, new Date(), "imageURL", "description"));
    }

    @Test(expected = Exception.class)
    @InSequence(14)
    public void shouldFailCreatingABookWithLowUnitCostTitle() {
        bookRepository.create(new Book("isbn", "title", 0F, 123, Language.ENGLISH, new Date(), "imageURL", "description"));
    }

    @Test(expected = Exception.class)
    @InSequence(15)
    public void shouldFailCreatingABookWithNullISBN() {
        bookRepository.create(new Book(null, "title", 12F, 123, Language.ENGLISH, new Date(), "imageURL", "description"));
    }

    @Test(expected = Exception.class)
    @InSequence(16)
    public void shouldFailInvokingFindByIdWithNull() {
        bookRepository.find(null);
    }

    @Test
    @InSequence(17)
    public void shouldNotFindUnknownId() {
        assertNull(bookRepository.find(99999L));
    }

    @Test(expected = Exception.class)
    @InSequence(18)
    public void shouldFailInvokingDeleteByIdWithNull() {
        bookRepository.delete(null);
    }

    @Test(expected = Exception.class)
    @InSequence(19)
    public void shouldNotDeleteUnknownId() {
        bookRepository.delete(99999L);
    }

}
/*
// ======================================
// =       TESTING EXPLANATION          =
// ======================================
* Unit Test - JUnit
	- Single Functionality
	- In Isolation
	- Fast, Simple an easy-to-run
	- Mock things outside the code
	- No dependencies on outside systems

* Integration Test - Arquillian
	- Several pieces work together
	- Cover whole app
	- Require external resources
	- No Mock
	- Demonstrates the system works

Before Integration Test:
- Use ShrinkWrap to package the business code
	- ShrinkWrap creates deployable archives: jar, war or zip files
	- Add external dependencies if needed
- Then Arquillian deploy it to WildFly


Integration Test: Rely on Arquillian runtime
- Access the container services
- Package the business code
- Test class in the package
- Deploy it to WildFly
- Execute tests inside the container

1. RUN WildFly
2. In another Terminal RUN $ mvn test
	- CONFIGURE AUTOMATION IN IntellJ to Run Arquillian just with "play button"
		1. Right Click name pf Test Class, then Choose "Run BookRepositoryTest"
		2. Click "Edit Arquillian Container Configuration"
		3. Choose Manual
		4. Add Dependency "org.arquillian.universe.arquillian-test"
		5. Click OK, Click OK
		6. Create "resources" directory with files
		    - arquillian.xml
		    - META-INF/test-persistence.xml
 */