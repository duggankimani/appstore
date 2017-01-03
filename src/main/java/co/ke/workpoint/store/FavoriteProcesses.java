package co.ke.workpoint.store;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import co.ke.workpoint.store.dao.DBExecute;
import co.ke.workpoint.store.model.ProcessDef;
import co.ke.workpoint.store.model.Status;

@Path("favprocesses")
public class FavoriteProcesses {

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

}
