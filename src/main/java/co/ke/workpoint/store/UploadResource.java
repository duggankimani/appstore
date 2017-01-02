package co.ke.workpoint.store;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import co.ke.workpoint.store.helpers.ProcessHelper;
import co.ke.workpoint.store.model.ProcessDef;

@Path("upload")
public class UploadResource {

	@POST
	@Path("/process")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public ProcessDef uploadFile(
			@FormDataParam("process_zip") InputStream uploadedInputStream,
			@FormDataParam("process_zip") FormDataContentDisposition fileDetail) throws IOException {

		// save it
		ProcessDef process = ProcessHelper.importProcessAsStream(fileDetail.getFileName(),fileDetail.getSize(), uploadedInputStream);

		return process;

	}
}
