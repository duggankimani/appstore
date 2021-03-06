package co.ke.workpoint.store.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import co.ke.workpoint.store.ProcessResource;
import co.ke.workpoint.store.dao.DBExecute;
import co.ke.workpoint.store.model.Attachment;
import co.ke.workpoint.store.model.Category;
import co.ke.workpoint.store.model.ProcessDef;
import co.ke.workpoint.store.model.Status;

public class ProcessHelper {

	static final Logger log = Logger.getLogger(ProcessHelper.class);
	static final String OUTPUT_FOLDER = "output";

	static FileAttribute<Set<PosixFilePermission>> getPosixPermissions() {
		Set<PosixFilePermission> perms = new HashSet<>();
		// add permission as rw-r--r-- 644
		perms.add(PosixFilePermission.OWNER_WRITE);
		perms.add(PosixFilePermission.OWNER_READ);
		perms.add(PosixFilePermission.OWNER_EXECUTE);
		perms.add(PosixFilePermission.GROUP_READ);
		perms.add(PosixFilePermission.GROUP_WRITE);
		perms.add(PosixFilePermission.GROUP_EXECUTE);
		perms.add(PosixFilePermission.OTHERS_READ);
		perms.add(PosixFilePermission.OTHERS_EXECUTE);
		FileAttribute<Set<PosixFilePermission>> fileAttributes = PosixFilePermissions
				.asFileAttribute(perms);

		return fileAttributes;
	}

	static FileAttribute<?> getPermissions() {
		try {
			Path tempFolder = Paths.get(System.getProperty("java.io.tmpdir"));
			FileStore fs = Files.getFileStore(tempFolder);
			if (!fs.supportsFileAttributeView(PosixFileAttributeView.class)) {
				return null;
			}
		} catch (Exception e) {

		}

		return getPosixPermissions();
	}

	public static ProcessDef importProcessAsStream(String name, long size,
			InputStream is) throws IOException {
		log.info("Importing process from inputstream {name:" + name + ", size:"
				+ (size / 1024) + "kb}");

		FileAttribute<?> fileAttributes = getPermissions();

		Path zipFilePath = null;

		if (fileAttributes != null) {
			zipFilePath = Files.createTempFile(name, null, fileAttributes);
		} else {
			zipFilePath = Files.createTempFile(name, null);
		}

		writeFile(is, zipFilePath);

		// Save
		ProcessDef process = importProcessAsZip(zipFilePath.toString());

		// Generate Process Root Path Folders
		Path rootPath = getProcessFilesRootPath(process.getRefId());
		if (fileAttributes != null) {
			Files.createDirectories(rootPath, fileAttributes);
		} else {
			Files.createDirectories(rootPath);
		}

		// Process Zip Path
		Path processZipPath = rootPath.resolve(name);

		// move
		if (Files.exists(processZipPath, LinkOption.NOFOLLOW_LINKS)) {
			Files.delete(processZipPath);
		}
		Files.move(zipFilePath, processZipPath);

		return process;
	}

	public static ProcessDef importProcessAsZip(String zipFileName) {

		ProcessDef process = null;

		log.info("Importing zip file " + zipFileName);
		// create output directory is not exists

		// FileAttribute<Set<PosixFilePermission>> fileAttributes =
		// getPosixPermissions();

		FileAttribute<?> fileAttributes = getPermissions();

		ZipFile zipFile = null;
		Path root = null;
		try {
			zipFile = new ZipFile(zipFileName);

			// fileAttributes);
			if (fileAttributes != null) {
				root = Files.createTempDirectory(OUTPUT_FOLDER, fileAttributes);
			} else {
				root = Files.createTempDirectory(OUTPUT_FOLDER);
			}

			log.debug("Creating temporary working directory " + root.toString());

			// File folder = path.toFile();
			// folder.deleteOnExit();

			for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e
					.hasMoreElements();) {
				ZipEntry zipEntry = e.nextElement();

				String fileName = zipEntry.getName();
				// File newFile = File.createTempFile(OUTPUT_FOLDER +
				// File.separator
				// + fileName,"", folder);
				// newFile.deleteOnExit();

				// Might be process.json
				// or
				// triggers/triggers.json
				Path newFilePath = root.resolve(fileName);

				// File newFile = newFilePath.toFile();
				log.debug("file unzip : " + newFilePath.toString());
				// log.debug("file unzip : " + newFile.getAbsoluteFile());

				// create all non exists folders
				// else you will hit FileNotFoundException for compressed folder
				if (newFilePath.getParent() != null) {
					if (fileAttributes != null) {
						Files.createDirectories(newFilePath.getParent(),
								fileAttributes);
					} else {
						Files.createDirectories(newFilePath.getParent());
					}
				}

				writeFile(zipFile.getInputStream(zipEntry), newFilePath);
			}

			process = importProcess(root);
		} catch (FileSystemException fse) {
			log.fatal("importProcessAsZip threw FileSystemException msg: "
					+ fse.getMessage() + ": reason " + fse.getReason()
					+ ": otherFile " + fse.getOtherFile());
			throw new RuntimeException(fse);
		} catch (IOException ioe) {
			log.fatal("importProcessAsZip threw IOException: "
					+ ioe.getMessage());
			throw new RuntimeException(ioe);
		} finally {
			try {
				if (zipFile != null) {
					zipFile.close();
				}
			} catch (IOException e) {
				log.fatal("importProcessAsZip unable to close ZipInputStream: "
						+ e.getMessage());
				e.printStackTrace();
			}
		}

		try {
			log.debug("Cleaning up working directory " + root.toString());
			FileUtils.deleteDirectory(root.toFile());
		} catch (IOException e) {
			log.warn("Unable to clean working directory " + root.toString()
					+ ", cause: " + e.getMessage());
		}

		return process;
	}

	private static void writeFile(InputStream inputStream, Path newFilePath)
			throws IOException {
		OpenOption[] options = new OpenOption[] { StandardOpenOption.WRITE,
				StandardOpenOption.CREATE };

		// File Exists
		if (Files.exists(newFilePath, LinkOption.NOFOLLOW_LINKS)) {
			Files.delete(newFilePath);
		}

		log.debug("Writing file " + newFilePath);
		// try with resources statement will automatically close the
		// stream
		OutputStream fos = Files.newOutputStream(newFilePath, options);
		final byte[] buffer = new byte[1024];
		int len;
		while ((len = inputStream.read(buffer)) > 0) {
			fos.write(buffer, 0, len);
		}
		fos.close();
		inputStream.close();
	}

	/**
	 * Import Process
	 * 
	 * @param rootPath
	 */
	private static ProcessDef importProcess(Path rootPath) {

		ProcessDef processDef = null;
		Path processPath = rootPath.resolve("process.json");
		try {

			byte[] processBytes = Files.readAllBytes(processPath);
			String processJsonStr = new String(processBytes);
			JSONObject processJson = new JSONObject(processJsonStr);

			log.info("Importing process ID: "
					+ processJson.getString(ProcessDef.ID) + ", Name:"
					+ processJson.getString(ProcessDef.NAME));
			processDef = new ProcessDef();
			processDef.setRefId(processJson.getString(ProcessDef.ID));
			processDef.setName(processJson.getString(ProcessDef.NAME));
			processDef
					.setProcessId(processJson.optString(ProcessDef.PROCESSID));
			processDef.setBackgroundColor(processJson
					.optString(ProcessDef.BACKGROUNDCOLOR));
			processDef
					.setIconStyle(processJson.optString(ProcessDef.ICONSTYLE));
			processDef.setFileName(processJson.optString(ProcessDef.FILENAME));
			processDef.setDescription(processJson
					.optString(ProcessDef.DESCRIPTION));
			processDef
					.setImageName(processJson.optString(ProcessDef.IMAGENAME));

			String category = processJson.optString(ProcessDef.CATEGORY);
			Category cat = getCategoryByName(category);
			if (cat == null) {
				cat = new Category();
				cat.setName(category);
				cat.setRefId(IDUtils.generateId());
				cat = createCategory(cat);
			}

			processDef.setCategory(category);
			processDef = create(processDef);

			// Process BPMN2 & SVG
			// JSONArray processAttachments = processJson
			// .getJSONArray("attachments");
			// for (int i = 0; i < processAttachments.length(); i++) {
			//
			// // Attachments
			// JSONObject att = processAttachments.getJSONObject(i);
			// LocalAttachment attachment = new LocalAttachment();
			// if (att.getString(Attachment.ID) != null) {
			// attachment = DB.getAttachmentDao()
			// .findByRefId(att.getString(Attachment.ID),
			// LocalAttachment.class);
			// }
			// if (attachment == null) {
			// attachment = new LocalAttachment();
			// }
			//
			// attachment.setRefId(att.getString(Attachment.ID));
			// attachment.setName(att.getString(Attachment.NAME));
			// if (att.optString(Attachment.TYPE) != null) {
			// attachment.setType(AttachmentType.valueOf(att
			// .getString(Attachment.TYPE)));
			// }
			// attachment.setSize(att.optLong(Attachment.SIZE));
			// attachment
			// .setContentType(att.getString(Attachment.CONTENTTYPE));
			// attachment.setPath(att.optString(Attachment.PATH));
			// if (attachment.getName().endsWith("bpmn2")
			// || attachment.getName().endsWith("BPMN2")) {
			// attachment.setProcessDef(processDefModel);
			// } else {
			// attachment.setProcessDefImage(processDefModel);
			// }
			//
			// String attachmentName = attachment.getName();
			// attachmentName = format(attachmentName);
			// log.info("importing attachment " + attachmentName);
			// attachment.setAttachment(Files.readAllBytes(rootPath
			// .resolve(attachmentName)));
			//
			// DB.getAttachmentDao().save(attachment);
			// // Process SVG Image not included
			// }

			// importTriggers(rootPath, processDefModel);
			// importOutputs(rootPath, processDefModel);
			// importForms(rootPath, processDefModel);
			// importTaskSteps(processJson, processDefModel);
			// importTaskNotifications(processJson, processDefModel);

		} catch (IOException e) {
			log.fatal("Unable to read bytes from " + processPath.toString());
			throw new RuntimeException(e);
		} catch (JSONException jsonEx) {
			log.fatal("Unable to read json from " + processPath.toString());
			throw new RuntimeException(jsonEx);
		}

		return processDef;
	}

	public static ProcessDef save(final ProcessDef processDef) {

		if (processDef.getRefId() != null && processDef.getRefId().isEmpty()) {
			processDef.setRefId(null);
		}

		ProcessDef saved = null;
		log.debug("Saving processDef " + processDef);
		if (processDef.getRefId() != null) {
			Integer id = checkExists(processDef.getRefId());
			if (id != null) {
				saved = update(id, processDef);
			} else {
				saved = create(processDef);
			}
		} else {
			saved = create(processDef);
		}

		return saved;
	}

	private static ProcessDef create(final ProcessDef processDef) {
		if (processDef.getRefId() != null) {
			Integer id = checkExists(processDef.getRefId());
			if (id != null) {
				return update(id, processDef);
			}
		}

		DBExecute<ProcessDef> processSave = new DBExecute<ProcessDef>() {
			@Override
			protected String getQueryString() {

				return "insert into processdef(refid, name, description,iconstyle,backgroundcolor,status,category) "
						+ "values(?,?,?,?,?,?,?)";
			}

			@Override
			protected ProcessDef processResults(PreparedStatement pStmt,
					boolean hasResults) throws SQLException {
				return processDef;
			}

			@Override
			protected void setParameters() throws SQLException {
				if (processDef.getRefId() == null) {
					processDef.setRefId(IDUtils.generateId());
				}
				setString(1, processDef.getRefId());
				setString(2, processDef.getName());
				setString(3, processDef.getDescription());
				setString(4, processDef.getIconStyle());
				setString(5, processDef.getBackgroundColor());
				setInt(6,
						processDef.getStatus() == null ? Status.AVAILABLE
								.ordinal() : Status.UPCOMING.ordinal());
				setString(7, processDef.getCategory());

			}
		};

		ProcessDef ret = processSave.executeDbCall();
		log.info("Created new process {refid:" + ret.getRefId() + ", name="
				+ ret.getName() + "}");
		return ret;
	}

	private static ProcessDef update(final Integer id,
			final ProcessDef processDef) {
		DBExecute<ProcessDef> processSave = new DBExecute<ProcessDef>() {
			@Override
			protected String getQueryString() {

				return "update processdef set refid=?, name=?, description=?,"
						+ "iconstyle=?,backgroundcolor=?,status=?,"
						+ "category=? where id=?";
			}

			@Override
			protected ProcessDef processResults(PreparedStatement pStmt,
					boolean hasResults) throws SQLException {
				processDef.setId(id);
				return processDef;
			}

			@Override
			protected void setParameters() throws SQLException {
				if (processDef.getRefId() == null) {
					processDef.setRefId(IDUtils.generateId());
				}
				setString(1, processDef.getRefId());
				setString(2, processDef.getName());
				setString(3, processDef.getDescription());
				setString(4, processDef.getIconStyle());
				setString(5, processDef.getBackgroundColor());
				setInt(6,
						processDef.getStatus() == null ? Status.AVAILABLE
								.ordinal() : processDef.getStatus().ordinal());
				setString(7, processDef.getCategory());
				setInt(8, id);
			}
		};

		return processSave.executeDbCall();
	}

	private static Integer checkExists(final String refId) {

		DBExecute<Integer> idExec = new DBExecute<Integer>() {
			@Override
			protected String getQueryString() {

				return "select id from processdef where refid=?";
			}

			@Override
			protected Integer processResults(PreparedStatement pStmt,
					boolean hasResults) throws SQLException {
				ResultSet rs = getResultSet();
				Integer id = null;
				if (rs.next()) {
					id = rs.getInt(1);
				}
				return id;
			}

			@Override
			protected void setParameters() throws SQLException {
				setString(1, refId);
			}
		};

		return idExec.executeDbCall();
	}

	private static Category createCategory(final Category cat) {

		DBExecute<Category> exec = new DBExecute<Category>() {
			@Override
			protected String getQueryString() {

				return "insert into category(refid,name) values (?,?)";
			}

			@Override
			protected Category processResults(PreparedStatement pStmt,
					boolean hasResults) throws SQLException {
				return cat;
			}

			@Override
			protected void setParameters() throws SQLException {
				setString(1, cat.getRefId() == null ? IDUtils.generateId()
						: cat.getRefId());
				setString(2, cat.getName());
			}
		};

		return exec.executeDbCall();
	}

	private static Category getCategoryByName(final String categoryName) {

		DBExecute<Category> exec = new DBExecute<Category>() {
			@Override
			protected String getQueryString() {
				return "select id, refid, name from category where name=?";
			}

			@Override
			protected Category processResults(PreparedStatement pStmt,
					boolean hasResults) throws SQLException {

				ResultSet rs = getResultSet();
				Category cat = null;
				if (rs.next()) {
					cat = new Category();
					cat.setId(rs.getInt(1));
					cat.setRefId(rs.getString(2));
					cat.setName(rs.getString(3));
				}
				return cat;
			}

			@Override
			protected void setParameters() throws SQLException {
				setString(1, categoryName);
			}
		};

		return exec.executeDbCall();
	}

	public static List<Category> getCategories() {

		DBExecute<List<Category>> exec = new DBExecute<List<Category>>() {
			@Override
			protected String getQueryString() {
				return "select id, refid, name from category";
			}

			@Override
			protected List<Category> processResults(PreparedStatement pStmt,
					boolean hasResults) throws SQLException {

				ResultSet rs = getResultSet();

				List<Category> categories = new ArrayList<Category>();
				while (rs.next()) {
					Category cat = new Category();
					cat.setId(rs.getInt(1));
					cat.setRefId(rs.getString(2));
					cat.setName(rs.getString(3));
					categories.add(cat);
				}

				return categories;
			}

			@Override
			protected void setParameters() throws SQLException {
			}
		};

		return exec.executeDbCall();
	}

	public static Category getCategoryByRefId(final String refId) {

		DBExecute<Category> exec = new DBExecute<Category>() {
			@Override
			protected String getQueryString() {
				return "select id, refid, name from category where refId=?";
			}

			@Override
			protected Category processResults(PreparedStatement pStmt,
					boolean hasResults) throws SQLException {

				ResultSet rs = getResultSet();
				Category cat = null;
				if (rs.next()) {
					cat = new Category();
					cat.setId(rs.getInt(1));
					cat.setRefId(rs.getString(2));
					cat.setName(rs.getString(3));
				}
				return cat;
			}

			@Override
			protected void setParameters() throws SQLException {
				setString(1, refId);
			}
		};

		return exec.executeDbCall();
	}

	public static List<ProcessDef> getProcessesByCategoryId(
			final String categoryRefId, final String searchPhrase) {
		DBExecute<List<ProcessDef>> exec = new DBExecute<List<ProcessDef>>() {
			@Override
			protected String getQueryString() {
				
				if (categoryRefId == null
						|| categoryRefId.equals(ProcessResource.ALL)) {
					return "select id, refid, name, description, iconstyle, "
							+ "backgroundcolor, processicon, status, category from processdef "
							+(searchPhrase==null? "" : "where lower(name) like ?");
				} else {
					return "select p.id, p.refid, p.name, p.description, p.iconstyle, "
							+ "p.backgroundcolor, p.processicon, p.status, p.category "
							+ "from processdef p where p.category=(select name from category where refid=?) "
							+ (searchPhrase==null? "" : "and lower(name) like ?");
				}

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
					def.setProcessIcon(rs.getString(7));
					Status status = Status.values()[rs.getInt(8)];
					def.setStatus(status);
					def.setCategory(rs.getString(9));
					list.add(def);
				}
				return list;
			}

			@Override
			protected void setParameters() throws SQLException {
				int i=1;
				if (categoryRefId != null
						&& !categoryRefId.equals(ProcessResource.ALL)) {
					setString(i++, categoryRefId);
				}
				
				if(searchPhrase!=null){
					setString(i++, "%"+searchPhrase.toLowerCase()+"%");
				}
			}
		};

		return exec.executeDbCall();
	}

	public static List<ProcessDef> getFavoriteProcessesByCategoryId(
			final String categoryRefId) {
		DBExecute<List<ProcessDef>> exec = new DBExecute<List<ProcessDef>>() {
			@Override
			protected String getQueryString() {
				if (categoryRefId == null
						|| categoryRefId.equals(ProcessResource.ALL)) {
					return "select id, refid, name, description, iconstyle, "
							+ "backgroundcolor, processicon, status, category from processdef limit 5";
				} else {
					return "select p.id, p.refid, p.name, p.description, p.iconstyle, "
							+ "p.backgroundcolor, p.processicon, p.status, p.category "
							+ "from processdef p where p.category=(select name from category where refid=? limit 5)";
				}

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
					def.setProcessIcon(rs.getString(7));
					Status status = Status.values()[rs.getInt(8)];
					def.setStatus(status);
					def.setCategory(rs.getString(9));
					list.add(def);
				}
				return list;
			}

			@Override
			protected void setParameters() throws SQLException {
				if (categoryRefId != null
						&& !categoryRefId.equals(ProcessResource.ALL)) {
					setString(1, categoryRefId);
				}
			}
		};

		return exec.executeDbCall();
	}

	public static void saveFile(String processRefId, InputStream inputStream,
			String fileName) {
		log.debug("Writing files for processRefId '" + processRefId + "'");
		Path rootPath = getProcessFilesRootPath(processRefId);
		FileAttribute<?> attributes = getPermissions();

		try {

			if (attributes != null) {
				rootPath = Files.createDirectories(rootPath, attributes);
			} else {
				rootPath = Files.createDirectories(rootPath);
			}

			log.debug("Resolve path - rootPath = " + rootPath + ", FileName = "
					+ fileName);
			Path filePath = rootPath.resolve(fileName);
			log.debug("File path - " + filePath.toString());
			if (attributes != null) {
				Files.createDirectories(filePath.getParent(), attributes);
			} else {
				Files.createDirectories(filePath.getParent());
			}

			writeFile(inputStream, filePath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private static Path getProcessFilesRootPath(String processRefId) {
		// Root file
		String dir = ApplicationSettings.getInstance().getProperty(
				"app.files.dir");
		Path rootPath = Paths.get(dir, processRefId);

		return rootPath;
	}

	public static ProcessDef getProcessByRefId(final String processRefId) {
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
					def.setProcessIcon(rs.getString(7));
					Status status = Status.values()[rs.getInt(8)];
					def.setStatus(status);
					def.setCategory(rs.getString(9));
				}
				return def;
			}

			@Override
			protected void setParameters() throws SQLException {
				setString(1, processRefId);
			}
		};

		return exec.executeDbCall();

	}

	public static List<Attachment> getAttachments(String processRefId) {
		List<Attachment> files = new ArrayList<Attachment>();
		Path rootPath = getProcessFilesRootPath(processRefId);

		if (!Files.exists(rootPath)) {
			return files;
		}

		loadFiles(processRefId, rootPath, files);

		return files;
	}

	private static void loadFiles(String processRefId, Path basePath,
			List<Attachment> files) {
		Path rootPath = getProcessFilesRootPath(processRefId);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(basePath)) {
			for (Path path : stream) {
				if (Files.isDirectory(path)) {
					loadFiles(processRefId, path, files);
				} else {
					Attachment a = new Attachment();
					a.setName(path.getFileName().toString());
					if (!Files.isSameFile(rootPath, basePath)) {
						a.setPath(rootPath.relativize(path).toString());
					}else{
						a.setPath("root/"+rootPath.relativize(path).toString());
					}
					Date lastModified = new Date(Files
							.getLastModifiedTime(path).toMillis());
					
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
					a.setLastModified(format.format(lastModified));
					files.add(a);
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadFile(String processRefId,
			HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
		Path rootPath = getProcessFilesRootPath(processRefId);
		String requestPath = httpRequest.getRequestURI();
		requestPath = URLDecoder.decode(requestPath, "UTF-8").replace("/root/", "/./");

		int beginIdx = requestPath.indexOf(processRefId);
		String path = requestPath.substring(beginIdx+processRefId.length()+1);
		log.debug("Extracted path "+path);
		
		Path filePath = rootPath.resolve(path);
		log.debug("Final file path = '"+filePath.toString()+"'");
		
		OutputStream os = httpResponse.getOutputStream();
		Files.copy(filePath, os);
		os.close();
	}

	public static void deleteFile(String processRefId,
			HttpServletRequest httpRequest) throws IOException{
		Path rootPath = getProcessFilesRootPath(processRefId);
		String requestPath = httpRequest.getRequestURI();
		requestPath = URLDecoder.decode(requestPath, "UTF-8").replace("/root/", "/./");

		int beginIdx = requestPath.indexOf(processRefId);
		String path = requestPath.substring(beginIdx+processRefId.length()+1);
		log.debug("Extracted delete path "+path);
		
		Path filePath = rootPath.resolve(path);
		log.debug("Delete path = '"+filePath.toString()+"'");

		Files.delete(filePath);
	}
}
