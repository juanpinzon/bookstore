package com.juankpapi.bookstore.rest;

import com.juankpapi.bookstore.model.Book;
import com.juankpapi.bookstore.model.Language;
import com.juankpapi.bookstore.repository.BookRepository;
import com.juankpapi.bookstore.util.IsbnGenerator;
import com.juankpapi.bookstore.util.NumberGenerator;
import com.juankpapi.bookstore.util.TextUtil;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.util.Date;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.junit.Assert.*;

// When Testing Databse is erased from scratch
// ======================================
// =        BLACK BOX TESTING           =
// =   We only access external API      =
// ======================================

@RunWith(Arquillian.class)                  //Run with Arquillian
@RunAsClient                                //Inform Arquillian this test will run as Remote Client
public class BookEndpointTest {

    // ======================================
    // =             Attributes             =
    // ======================================
    private static String bookId;
    private Response response;


    // ======================================
    // =             Deployment             =
    // ======================================
    //We need to package the REST application that is going to be deploy on WildFly
    //However, we need to specify not to package the current test class.
    @Deployment(testable = false)                   //<testable = false> Specify not to include current test on package
    public static Archive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(Book.class)
                .addClass(Language.class)
                .addClass(BookRepository.class)
                .addClass(NumberGenerator.class)
                .addClass(IsbnGenerator.class)
                .addClass(TextUtil.class)
                .addClass(BookEndPoint.class)
                .addClass(JAXRSConfiguration.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml");
    }


    // ======================================
    // =            Test methods            =
    // = Use GET, POST, PUT, DELETE in the right URI to interact with the app's API and call the correct method you want
    // ======================================

    @Test
    @InSequence(2)
    public void shouldGetNoBook(@ArquillianResteasyResource("api/books") WebTarget webTarget) {
        // Count all --> should be Zero due DB is empty
        // Build a HTTP GET request that accepts "text/plain" response type. URI=/api/books/count --> Call API's method <countBooks()>
        // Only for "text/plain" you can leave it empty --> response = webTarget.path("count").request().get();
        response = webTarget.path("count").request("text/plain").get();
        assertEquals(NO_CONTENT.getStatusCode(), response.getStatus());

        // Find all --> Empty list
        // Build a HTTP GET request that accepts "APPLICATION_JSON" response type. URI=/api/books --> Call API's method <getBooks()>
        response = webTarget.request(APPLICATION_JSON).get();
        assertEquals(NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    @InSequence(3)
    public void shouldCreateABook(@ArquillianResteasyResource("api/books") WebTarget webTarget) {
        // Creates a book
        Book book = new Book("isbn", "a   title", 12F, 123, Language.ENGLISH, new Date(), "imageURL", "description");
        // Build a HTTP POST request that accepts "APPLICATION_JSON" response type. URI=/api/books --> Call API's method <createBook()>
        response = webTarget.request(APPLICATION_JSON).post(Entity.entity(book, APPLICATION_JSON));
        assertEquals(CREATED.getStatusCode(), response.getStatus());

        // Checks the created book and store its bookId for next method
        String location = response.getHeaderString("location");
        assertNotNull(location);
        bookId = location.substring(location.lastIndexOf("/") + 1);
    }


    @Test
    @InSequence(4)
    public void shouldFindTheCreatedBook(@ArquillianResteasyResource("api/books") WebTarget webTarget) {
        // Finds the book
        response = webTarget.path(bookId).request(APPLICATION_JSON).get();
        assertEquals(OK.getStatusCode(), response.getStatus());
        // Checks the found book
        Book bookFound = response.readEntity(Book.class);
        assertNotNull(bookFound.getId());
        assertTrue(bookFound.getIsbn().startsWith("13-84356-"));
        assertEquals("a title", bookFound.getTitle());
    }

    @Test
    @InSequence(5)
    public void shouldGetOneBook(@ArquillianResteasyResource("api/books") WebTarget webTarget) {
        // Count all
        response = webTarget.path("count").request().get();
        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(Long.valueOf(1), response.readEntity(Long.class));
        // Find all
        response = webTarget.request(APPLICATION_JSON).get();
        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(1, response.readEntity(List.class).size());
    }


    @Test
    @InSequence(6)
    public void shouldDeleteTheCreatedBook(@ArquillianResteasyResource("api/books") WebTarget webTarget) {
        // Deletes the book
        response = webTarget.path(bookId).request(APPLICATION_JSON).delete();
        assertEquals(NO_CONTENT.getStatusCode(), response.getStatus());
        // Checks the deleted book
        Response checkResponse = webTarget.path(bookId).request(APPLICATION_JSON).get();
        assertEquals(NOT_FOUND.getStatusCode(), checkResponse.getStatus());
    }

    @Test
    @InSequence(7)
    public void shouldGetNoMoreBook(@ArquillianResteasyResource("api/books") WebTarget webTarget) {
        // Count all
        response = webTarget.path("count").request().get();
        assertEquals(NO_CONTENT.getStatusCode(), response.getStatus());
        // Find all
        response = webTarget.request(APPLICATION_JSON).get();
        assertEquals(NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    @InSequence(10)
    public void shouldFailCreatingANullBook(@ArquillianResteasyResource("api/books") WebTarget webTarget) {
        response = webTarget.request(APPLICATION_JSON).post(null);
        assertEquals(UNSUPPORTED_MEDIA_TYPE.getStatusCode(), response.getStatus());
    }

    @Test
    @InSequence(11)
    public void shouldFailCreatingABookWithNullTitle(@ArquillianResteasyResource("api/books") WebTarget webTarget) {
        Book book = new Book("isbn", null, 12F, 123, Language.ENGLISH, new Date(), "imageURL", "description");
        response = webTarget.request(APPLICATION_JSON).post(Entity.entity(book, APPLICATION_JSON));
        assertEquals(INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    @InSequence(12)
    public void shouldFailCreatingABookWithLowUnitCost(@ArquillianResteasyResource("api/books") WebTarget webTarget) {
        Book book = new Book("isbn", "title", 0F, 123, Language.ENGLISH, new Date(), "imageURL", "description");
        response = webTarget.request(APPLICATION_JSON).post(Entity.entity(book, APPLICATION_JSON));
        assertEquals(INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test(expected = Exception.class)
    @InSequence(13)
    public void shouldFailInvokingFindByIdWithNull(@ArquillianResteasyResource("api/books") WebTarget webTarget) {
        response = webTarget.path(null).request(APPLICATION_JSON).get();
    }

    @Test
    @InSequence(14)
    public void shouldNotFindUnknownId(@ArquillianResteasyResource("api/books") WebTarget webTarget) {
        response = webTarget.path("999").request(APPLICATION_JSON).get();
        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test(expected = Exception.class)
    @InSequence(15)
    public void shouldFailInvokingDeleteByIdWithNull(@ArquillianResteasyResource("api/books") WebTarget webTarget) {
        response = webTarget.path(null).request(APPLICATION_JSON).delete();
    }

    @Test
    @InSequence(16)
    public void shouldFailInvokingFindByIdWithZero(@ArquillianResteasyResource("api/books") WebTarget webTarget) {
        response = webTarget.path("0").request(APPLICATION_JSON).get();
        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    @InSequence(17)
    public void shouldFailInvokingDeleteByIdWithZero(@ArquillianResteasyResource("api/books") WebTarget webTarget) {
        response = webTarget.path("0").request(APPLICATION_JSON).delete();
        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    @InSequence(18)
    public void shouldNotDeleteUnknownId(@ArquillianResteasyResource("api/books") WebTarget webTarget) {
        response = webTarget.path("999").request(APPLICATION_JSON).delete();
        assertEquals(INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }
}


/*
// ======================================
// =        TESTING REST API            =
// ======================================

- JAX-RS has a Client API which allows us to make HTTP requests

//CLIENT API
============================================================================
    Client client = ClientBuilder.newClient();											//Client interface manages and configures HTTP connections
    WebTarget target = client.target("http://www.bookstore.com/books/123");				//WebTarget represents the URI to work on
    Invocation invocation = target.request(MediaType.APPLICATION_JSON).buildGet();		//Simple HTTP GET asking for JSON representation
    Response response = invocation.invoke();											//Invke the remote REST service and get a response object
============================================================================
EQUIVALENT TO
============================================================================
    Response response = ClientBuilder.newClient()
                            .target("http://www.bookstore.com/books/123")
                            .request(MediaType.APPLICATION_JSON)
                            .get();			//GET, POST, PUT OR DELETE
============================================================================

Testing using some assertions
- assertTrue(response.getStatusInfo == Response.Status.OK);
- assertTrue(response.getLength() == 4);
- assertTrue(response.getDate() != null);
- assertTrue(response.getHeaderString("Content-type").equals("application/json"));

//**************  Most of the time we really want the entity sent from the RESTful web service within <response>
Book book = response.readEntity(Book.class);		//Read the JSON input stream and then JAX-RS will automatically unmarshall it into our book entity


Arquillian Client Test
- Different than Persistance test: we need to test it from outside the container
	+ We don't need to package the test classes inside an archive (we package only the app, not the tests)
	+ Test are not executed from inside the container, but outside through HTTP
*/