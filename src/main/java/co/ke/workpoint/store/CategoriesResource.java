package co.ke.workpoint.store;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import co.ke.workpoint.store.helpers.ProcessHelper;
import co.ke.workpoint.store.model.Category;

@Path("categories")
public class CategoriesResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Category> getCategories(){
		return ProcessHelper.getCategories();
	}
	
	@GET
	@Path("/{refid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Category getCategory(@PathParam("refid") String refId){
		return ProcessHelper.getCategoryByRefId(refId);
	}
	
	@Path("/{refid}/processes")
	public ProcessResource getProcessResource(@PathParam("refid") String categoryRef){
		return new ProcessResource(categoryRef);
	}
	
	@Path("/{refid}/favprocesses")
	public FavoriteProcesses getFavoriteProcessResource(@PathParam("refid") String categoryRef){
		return new FavoriteProcesses(categoryRef);
	}
	
	
	
}
