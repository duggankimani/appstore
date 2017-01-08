package co.ke.workpoint.store;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import co.ke.workpoint.store.helpers.ProcessHelper;
import co.ke.workpoint.store.model.ProcessDef;

public class ProcessResource {

	public static final String ALL = "all";
	Logger log = Logger.getLogger(ProcessResource.class);
	private String categoryRefId;
	
	public ProcessResource(String categoryId) {
		this.categoryRefId = categoryId;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ProcessDef> getAll(@QueryParam("q") String searchPhrase) {

		return ProcessHelper.getProcessesByCategoryId(categoryRefId, searchPhrase);
	}

	@GET
	@Path("/{processref}")
	@Produces(MediaType.APPLICATION_JSON)
	public ProcessDef getProcessById(
			@PathParam("processref") final String processRefId) {
		ProcessDef def = ProcessHelper.getProcessByRefId(processRefId);
		
		def.setAttachments(ProcessHelper.getAttachments(processRefId));
		
		return def;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ProcessDef save(final ProcessDef processDef) {
		
		 ProcessDef def = ProcessHelper.save(processDef);
		 def.setAttachments(ProcessHelper.getAttachments(def.getRefId()));
		 
		 return def;
	}

}
