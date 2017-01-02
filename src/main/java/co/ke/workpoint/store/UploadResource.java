package co.ke.workpoint.store;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import co.ke.workpoint.store.helpers.ProcessHelper;

@Path("upload")
public class UploadResource {

	@POST
	@Path("/process")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) throws IOException {

		// save it
		ProcessHelper.importProcessAsStream(fileDetail.getFileName(),fileDetail.getSize(), uploadedInputStream);

		String output = "File successfully uploaded.";

		return Response.status(200).entity(output).build();

	}
}
