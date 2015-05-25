package com.savoirtech.hecate.load.web;

import com.savoirtech.hecate.load.web.domain.Person;
import com.savoirtech.hecate.pojo.dao.PojoDao;
import com.savoirtech.hecate.pojo.dao.PojoDaoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/hello")
@Service("helloBean")
public class HelloResource {

    private PojoDao<String,Person> dao;

    @Autowired
    private PojoDaoFactory daoFactory;

    @PostConstruct
    public void init() {
        dao = daoFactory.createPojoDao(Person.class);
        Person p = new Person();
        p.setId("jcarman");
        p.setFirstName("Jim");
        p.setLastName("Carman");
        dao.save(p);
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayHello(@PathParam("id") final String id) {
        Person person = dao.findById(id);
        return person != null ? "Hello, " + person.getFirstName() + "!" : "Not Found!";
    }
}
