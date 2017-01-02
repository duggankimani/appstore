package co.ke.workpoint.store;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import co.ke.workpoint.store.dao.DBExecute;
import co.ke.workpoint.store.helpers.IDUtils;
import co.ke.workpoint.store.model.ProcessDef;

@Path("processes")
public class ProcessResource {

	Logger log = Logger.getLogger(ProcessResource.class);
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getIt() {
        return "Got it!";
    }
	
	@GET
	@Path("/{processref}")
	@Produces(MediaType.APPLICATION_JSON)
	public ProcessDef getProcessById(@PathParam("processref") String processRef){
		ProcessDef process = new ProcessDef();
		process.setRefId("Z3KVjUt31OVrmcJc");
		process.setName("Leave Application");
		process.setProcessId("com.wira.leaveapplication");
		return process;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ProcessDef save(final ProcessDef processDef){
		
		System.err.println("Saving processDef "+processDef);
		log.debug("Saving processDef "+processDef);
		DBExecute<ProcessDef> processSave = new DBExecute<ProcessDef>() {
			@Override
			protected String getQueryString() {
				
				return "insert into processdef(refid, name, processid) values(?,?,?)";
			}
			
			@Override
			protected ProcessDef processResults(PreparedStatement pStmt,
					boolean hasResults) throws SQLException {
				
				return processDef;
			}
			
			@Override
			protected void setParameters() throws SQLException {
				if(processDef.getRefId()==null){
					processDef.setRefId(IDUtils.generateId());
				}
				setString(1, processDef.getRefId());
				setString(2, processDef.getName());
				setString(3, processDef.getProcessId());
			}
		};
		
		return processSave.executeDbCall();
	}
}
