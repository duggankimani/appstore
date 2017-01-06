package co.ke.workpoint.store;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import co.ke.workpoint.store.helpers.ProcessHelper;
import co.ke.workpoint.store.helpers.ServerConstants;
import co.ke.workpoint.store.model.ProcessDef;

@Path("files")
public class FilesResource {

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public ProcessDef uploadFile(
			@FormDataParam("refId") String processRefId,
			@FormDataParam("process_zip") InputStream uploadedInputStream,
			@FormDataParam("process_zip") FormDataContentDisposition fileDetail,
			@FormDataParam("processIcon") InputStream processIcon,
			@FormDataParam("processIcon") FormDataContentDisposition processIconDetail,
			@FormDataParam("process_images") List<FormDataBodyPart> screenshots)
			throws IOException {
		
		return upload(processRefId, uploadedInputStream, fileDetail, processIcon, processIconDetail, screenshots);
	}
	
	@POST
	@Path("/{processRefId}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public ProcessDef uploadFilesForProcess(
			@PathParam("refId") String processRefId,
			@FormDataParam("process_zip") InputStream uploadedInputStream,
			@FormDataParam("process_zip") FormDataContentDisposition fileDetail,
			@FormDataParam("processIcon") InputStream processIcon,
			@FormDataParam("processIcon") FormDataContentDisposition processIconDetail,
			@FormDataParam("process_images") List<FormDataBodyPart> screenshots)
			throws IOException {
		return upload(processRefId, uploadedInputStream, fileDetail, processIcon, processIconDetail, screenshots);
	}

	private ProcessDef upload(String processRefId,
			InputStream uploadedInputStream,
			FormDataContentDisposition fileDetail, InputStream processIcon,
			FormDataContentDisposition processIconDetail,
			List<FormDataBodyPart> screenshots) throws IOException {
		
		ProcessDef process = null;
		if (!isNullOrEmpty(fileDetail.getFileName())) {
			// save process
			process = ProcessHelper.importProcessAsStream(
					fileDetail.getFileName(), fileDetail.getSize(),
					uploadedInputStream);
		}
		
		//Check refid exists
		if (process == null && isNullOrEmpty(processRefId)) {
			return null;
		}

		if (isNullOrEmpty(processRefId)) {
			processRefId = process.getRefId();
		}

		// screenshots
		for (FormDataBodyPart screenshot : screenshots) {
			BodyPartEntity bodyPartEntity = (BodyPartEntity) screenshot
					.getEntity();
			String fileName = screenshot.getContentDisposition().getFileName();

			if (!isNullOrEmpty(fileName)) {
				ProcessHelper.saveFile(processRefId,
						bodyPartEntity.getInputStream(),
						ServerConstants.PROCESSSCREENSHOTS + "/" + fileName);
			}

		}

		// Process Icon
		if (!isNullOrEmpty(processIconDetail.getFileName())) {
			ProcessHelper.saveFile(
					processRefId,
					processIcon,
					ServerConstants.PROCESSICONS + "/"
							+ processIconDetail.getFileName());
		}

		if(process==null){
			process = ProcessHelper.getProcessByRefId(processRefId);
		}
		
		process.setAttachments(ProcessHelper.getAttachments(processRefId));
		
		return process;
	}
	
	@GET
	@Path("/{processRefId}/{folder}/{file}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFile(@PathParam("processRefId") String processRefId,
			@Context HttpServletRequest httpRequest, 
			@Context HttpServletResponse httpResponse) throws IOException{
		ProcessHelper.loadFile(processRefId,httpRequest, httpResponse);
		
		return Response.ok().build();
	}
	
	@DELETE
	@Path("/{processRefId}")
	public void getFile(@PathParam("processRefId") String processRefId,@Context HttpServletRequest httpRequest){
		ProcessHelper.deleteFile(processRefId,httpRequest);
	}

	private boolean isNullOrEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}
}
