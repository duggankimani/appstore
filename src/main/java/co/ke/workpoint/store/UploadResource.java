package co.ke.workpoint.store;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import co.ke.workpoint.store.helpers.ProcessHelper;
import co.ke.workpoint.store.helpers.ServerConstants;
import co.ke.workpoint.store.model.ProcessDef;

@Path("upload")
public class UploadResource {

	@POST
	@Path("/process")
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

	private boolean isNullOrEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}
}
