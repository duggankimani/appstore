package co.ke.workpoint.store;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import co.ke.workpoint.store.helpers.ProcessHelper;
import co.ke.workpoint.store.model.ProcessDef;

public class FavoriteProcesses {

	private String categoryRefId;
	
	public FavoriteProcesses(String categoryRefId) {
		this.categoryRefId = categoryRefId;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ProcessDef> getAll() {
		
		return ProcessHelper.getFavoriteProcessesByCategoryId(categoryRefId);
	}

}
