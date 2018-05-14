package com.juankpapi.bookstore.rest;

import com.juankpapi.bookstore.model.Book;
import com.juankpapi.bookstore.repository.BookRepository;


import javax.inject.Inject;
import javax.validation.constraints.Min;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;


@Path("/books")                      //REST Endpoint (http:www.bookstore.com/books)
//@ApplicationScoped
public class BookEndPoint {
    // ======================================
    // =          Injection Points          =
    // ======================================
    @Inject
    private BookRepository bookRepository;

    // ======================================
    // =          Business methods          =
    // ======================================
    @GET                                    //HTTP METHOD
    @Produces(APPLICATION_JSON)             //<Produces> assure method returns a JSON representation of the list of books
    public Response getBooks() {            //<Response> class allows some control over the HTTP response returned from the endpoint.
        List<Book> books = bookRepository.findAll();

        if (books.size() == 0)
            return Response.noContent().build();
            //return Response.status(Response.Status.NO_CONTENT).build();     //another way

        return Response.ok(books).build();  //JAX-RS will pass the list of books entity into a JSON string and send it back into the response.
    }


    @GET
    @Path("/count")
    @Produces(TEXT_PLAIN)
    public Response countBooks() {
        Long nbOfBooks = bookRepository.countAll();

        if (nbOfBooks == 0)
            return Response.noContent().build();

        return Response.ok(nbOfBooks).build();
    }


    @GET
    @Path("/{id : \\d+}")
    @Produces(APPLICATION_JSON)
    public Response getBook(@PathParam("id") @Min(1) Long id) {         //Method not called if <id> is not type Long (i.e. String)
        Book book = bookRepository.find(id);

        if (book == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.ok(book).build();
    }


    @DELETE
    @Path("/{id : \\d+}")
    public Response deleteBook(@PathParam("id") @Min(1) Long id) {
        bookRepository.delete(id);
        return Response.noContent().build();
    }


    @POST
    @Consumes(APPLICATION_JSON)                                                 //Consumes a JSON representation of a Book
    public Response createBook(Book book, @Context UriInfo uriInfo) {           //<@Context> is used to inject instances related to the context of HTTP requests, in this case UriInfo. (See JAX-RS API below)
        book = bookRepository.create(book);
        URI createdURI = uriInfo.getBaseUriBuilder().path(book.getId().toString()).build();     // book's URI = original path + "/book_id". Then returns it
        return Response.created(createdURI).build();
    }

}


/*
// ======================================
// =    EXPOSE REST API USIG JAX-RS     =
// ======================================
REST (REPRESENTATIONAL STATE TRANSFER)
	- Architecture style
	- Heavily relies on HTTP -- cost of HTTP request and responses
	- JAX-RS
		+ Annotations
		+ URI to get a Resource
		+ Resource is return in JSON format
		+ HTTP status code -- in case request fail for instance.
		+ HTTP has an Uniform Interface --- CRUD
			> Create  --  POST - return a new URI
	  		> Read	  --  GET
	  		> Update  --  PUT existing URI
	  		> Delete  --  DELETE
	  	+ Instances related to the context of HTTP requests injected using <@context> (Inject them as instance field or as a method parameter):
	  	    > SecurityContext – Security context instance for the current HTTP request
	  	    > Request – Used for setting precondition request processing
	  	    > Application, Configuration, and Providers -> Provide access to the JAX-RS application, configuration, and providers instances
	  	    > ResourceContext – Resource context class instances
	  	    > ServletConfig – The ServletConfig instance instance
	  	    > ServletContext – The ServletContext instance
	  	    > HttpServletRequest – The HttpServletRequest instance for the current request
	  	    > HttpServletResponse – The HttpServletResponse instance for the current request
	  	    > HttpHeaders – Maintains the HTTP header keys and values
	  	    > UriInfo – Query parameters and path variables from the URI called

 */
