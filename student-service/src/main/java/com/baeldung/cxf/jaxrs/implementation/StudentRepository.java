package com.baeldung.cxf.jaxrs.implementation;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.http.client.utils.URIBuilder;

import io.opentracing.Span;
import io.opentracing.util.GlobalTracer;

@Path("meshd")
@Produces("application/json")
public class StudentRepository {
  private String BASE_URL = System.getenv("subject.service.url");
  private String URL = BASE_URL != null ? BASE_URL + "/meshd/subjects?Ssource=aaa&Ttrial=bbb" :
      "http://34.220.106.159:8080/meshd/subjects?Ssource=aaa&Ttrial=bbb";
//  private Logger LOGGER = LoggerFactory.getLogger(CourseRepository.class);


  private Map<Integer, Student> students = new HashMap<>();

  {
    Student student1 = new Student();
    Student student2 = new Student();
    student1.setId(1);
    student1.setName("Student A");
    student2.setId(2);
    student2.setName("Student B");
    students.put(1, student1);
    students.put(2, student2);

  }

  @GET
  @Path("students/{studentId}")
  public Student getStudent(@PathParam("studentId") int studentId) {
    Student student = findById(studentId);
    if (student == null) {
      throw new NotFoundException();
    } else {
      return student;
    }
  }

  @POST
  @Path("students")
  public Response createStudent(Student student) {
    students.put(student.getId(), student);
    return Response.ok(student).build();
  }

  @DELETE
  @Path("students/{studentId}")
  public Response deleteStudent (@PathParam("studentId") int studentId) {
    Student student = findById(studentId);
    if (student == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    students.remove(studentId);
    return Response.ok().build();
  }

  private Student findById(int id) {
    for (Map.Entry<Integer, Student> student : students.entrySet()) {
      if (student.getKey() == id) {
        return student.getValue();
      }
    }
    return null;
  }

  @GET
  @Path("students/dummyStudentList")
  public List<Student> dummyStudentList(@Context HttpHeaders headers, @QueryParam("count") int studentCount) {
    List<Student> studentList = new ArrayList<>();
    Student student = new Student();
    student.setId(studentCount);
    student.setName("Dummy Student");
    for (int i=0; i<studentCount; i++) {
      studentList.add(student);
    }
    return studentList;
  }

  @GET
  @Path("students/getDepth2")
  public Response getDepth2(@Context HttpHeaders headers, @QueryParam("count") int studentCount)
      throws URISyntaxException {
//    LOGGER.info("Received called to course/getDepth2");
    final Span span = GlobalTracer.get().activeSpan();

    System.out.println("TraceId: " + span.context().toTraceId());
    System.out.println("SpanId: " + span.context().toSpanId());

    URIBuilder uriBuilder = new URIBuilder(URL);
    uriBuilder.setPath(uriBuilder.getPath() + "/getDepth2");
    uriBuilder.addParameter("count", String.valueOf(studentCount));
    WebClient subjectWebClient = WebClient.create(uriBuilder.build().toString())
        .accept(javax.ws.rs.core.MediaType.APPLICATION_JSON).type(
            javax.ws.rs.core.MediaType.APPLICATION_JSON);

    HTTPConduit http = WebClient.getConfig(subjectWebClient).getHttpConduit();
    HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
    httpClientPolicy.setConnectionTimeout(0);
    http.setClient(httpClientPolicy);

    Response response = subjectWebClient.get();
//    LOGGER.info(
//        "Recieved response from student/getDepth2. \nStatus" + response.getStatus()
//            + "\nResponse: " + response.toString());

    final Span span2 = GlobalTracer.get().activeSpan();

    System.out.println("TraceId after call: " + span2.context().toTraceId());
    System.out.println("SpanId after call: " + span2.context().toSpanId());

    return response;
  }

}
