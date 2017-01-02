package co.ke.workpoint.store;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import co.ke.workpoint.store.helpers.ProcessHelper;
import co.ke.workpoint.store.model.ProcessDef;
import co.ke.workpoint.store.model.Status;

@Path("processes")
public class ProcessResource {

	Logger log = Logger.getLogger(ProcessResource.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ProcessDef> getAll() {

		DBExecute<List<ProcessDef>> exec = new DBExecute<List<ProcessDef>>() {
			@Override
			protected String getQueryString() {

				return "select id, refid, name, description, iconstyle, "
						+ "backgroundcolor, processicon, status, category from processdef";
			}

			@Override
			protected List<ProcessDef> processResults(PreparedStatement pStmt,
					boolean hasResults) throws SQLException {
				ResultSet rs = getResultSet();

				List<ProcessDef> list = new ArrayList<ProcessDef>();
				while (rs.next()) {
					ProcessDef def = new ProcessDef();
					def.setId(rs.getInt(1));
					def.setRefId(rs.getString(2));
					def.setName(rs.getString(3));
					def.setDescription(rs.getString(4));
					def.setIconStyle(rs.getString(5));
					def.setBackgroundColor(rs.getString(6));
					def.setProcessIcon(rs.getString(6));
					Status status = Status.values()[rs.getInt(7)];
					def.setStatus(status);
					def.setCategory(rs.getString(8));
					list.add(def);
				}
				return list;
			}

			@Override
			protected void setParameters() throws SQLException {

			}
		};

		return exec.executeDbCall();
	}

	@GET
	@Path("/{processref}")
	@Produces(MediaType.APPLICATION_JSON)
	public ProcessDef getProcessById(
			@PathParam("processref") final String processRef) {

		DBExecute<ProcessDef> exec = new DBExecute<ProcessDef>() {
			@Override
			protected String getQueryString() {

				return "select id, refid, name, description, iconstyle, "
						+ "backgroundcolor, processicon, status, category from processdef where refid=?";
			}

			@Override
			protected ProcessDef processResults(PreparedStatement pStmt,
					boolean hasResults) throws SQLException {
				ResultSet rs = getResultSet();

				ProcessDef def = null;
				if (rs.next()) {
					def = new ProcessDef();
					def.setId(rs.getInt(1));
					def.setRefId(rs.getString(2));
					def.setName(rs.getString(3));
					def.setDescription(rs.getString(4));
					def.setIconStyle(rs.getString(5));
					def.setBackgroundColor(rs.getString(6));
					def.setProcessIcon(rs.getString(6));
					Status status = Status.values()[rs.getInt(7)];
					def.setStatus(status);
					def.setCategory(rs.getString(8));
				}
				return def;
			}

			@Override
			protected void setParameters() throws SQLException {
				setString(1, processRef);
			}
		};

		return exec.executeDbCall();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ProcessDef save(final ProcessDef processDef) {
		return ProcessHelper.save(processDef);
	}

}