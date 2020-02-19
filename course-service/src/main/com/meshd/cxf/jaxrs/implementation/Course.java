package com.meshd.cxf.jaxrs.implementation;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;


@XmlRootElement(name = "Course")
public class Course {
    private int id;
    private String name;
    private List<Integer> studentIds = new ArrayList<>();
    private String URL = "http://localhost:8081/meshd/students/";
    private Client client = ClientBuilder.newBuilder().newClient();
    private WebTarget studentWebTarget = client.target(URL);
    private ObjectMapper objectMapper = new ObjectMapper();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getStudents() {
        return studentIds;
    }

    public void setStudents(List<Integer> studentIds) {
        this.studentIds = studentIds;
    }

    @GET
    @Path("{studentId}")
    public Student getStudent(@PathParam("studentId") int studentId) throws Exception {
        return findById(studentId);
    }

    @POST
    public Response createStudent(Student student) throws Exception     {
        for (Integer id : studentIds) {
            if (id == student.getId()) {
                return Response.status(Response.Status.CONFLICT).build();
            }
        }

        Invocation.Builder builder = studentWebTarget.request(MediaType.APPLICATION_JSON);
        builder.post(Entity.entity(objectMapper.writeValueAsString(student), MediaType.APPLICATION_JSON));
        Response response = builder.get();

        int code = response.getStatus();
        if (code >= 200 && code <= 299) {
            return Response.ok(student).build();
        } else {
            throw new IllegalArgumentException(
                "HTTP error response returned by Transformer service " + code);
        }
    }

    @DELETE
    @Path("{studentId}")
    public Response deleteStudent(@PathParam("studentId") int studentId) throws Exception {
        Student student = findById(studentId);
        if (student == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        WebTarget studentGetWebTarget = studentWebTarget.path(String.valueOf(id));
        Invocation.Builder builder = studentGetWebTarget.request();
        Response response = builder.delete();


        int code = response.getStatus();
        return Response.status(code).build();
    }

    private Student findById(int id) throws Exception {


        WebTarget studentGetWebTarget = studentWebTarget.path(String.valueOf(id));
        Invocation.Builder builder = studentGetWebTarget.request();
        Response response = builder.get();


        int code = response.getStatus();
        if (code >= 200 && code <= 299) {
            String studentString = response.readEntity(String.class);
            Student student = objectMapper.readValue(studentString, Student.class);
            return student;
        } else {
            throw new IllegalArgumentException(
                "HTTP error response returned by Transformer service " + code);
        }
    }

    @Override
    public int hashCode() {
        return id + name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Course) && (id == ((Course) obj).getId()) && (name.equals(((Course) obj).getName()));
    }
}