package com.baeldung.cxf.jaxrs.implementation;

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
import javax.ws.rs.core.UriInfo;

import io.opentracing.Span;
import io.opentracing.util.GlobalTracer;

@Path("meshd")
@Produces("application/json")
public class SubjectRepository {

	@GET
	@Path("subjects/getDepth2")
	public String getDepth2(@Context HttpHeaders headers, @QueryParam("count") int studentCount) {
		final Span span = GlobalTracer.get().activeSpan();

		System.out.println("TraceId: " + span.context().toTraceId());
		System.out.println("SpanId: " + span.context().toSpanId());

		return "Hi I am returning from Subjects";
	}

}
