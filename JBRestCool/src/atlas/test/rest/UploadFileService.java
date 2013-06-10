package atlas.test.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

/**
 * @author formica
 *
 */
@Path("/file")
public class UploadFileService {

	private final String UPLOADED_FILE_PATH = "/tmp/";

	/**
	 * @param input
	 * @return
	 */
	@POST
	@Path("/upload")
	@Consumes("multipart/form-data")
	public Response uploadFile(final MultipartFormDataInput input) {

		String fileName = "";

		final Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		final List<InputPart> inputParts = uploadForm.get("uploadedFile");

		for (final InputPart inputPart : inputParts) {

			try {

				final MultivaluedMap<String, String> header = inputPart.getHeaders();
				fileName = getFileName(header);

				// convert the uploaded file to inputstream
				final InputStream inputStream = inputPart
						.getBody(InputStream.class, null);

				final byte[] bytes = IOUtils.toByteArray(inputStream);

				// constructs upload file path
				fileName = UPLOADED_FILE_PATH + fileName;

				writeFile(bytes, fileName);

				System.out.println("Done");

			} catch (final IOException e) {
				e.printStackTrace();
			}

		}

		return Response.status(200)
				.entity("uploadFile is called, Uploaded file name : " + fileName).build();

	}

	/**
	 * header sample { Content-Type=[image/png], Content-Disposition=[form-data;
	 * name="file"; filename="filename.extension"] }
	 **/
	// get uploaded filename, is there a easy way in RESTEasy?
	/**
	 * @param header
	 * @return
	 */
	private String getFileName(final MultivaluedMap<String, String> header) {

		final String[] contentDisposition = header.getFirst("Content-Disposition").split(
				";");

		for (final String filename : contentDisposition) {
			if (filename.trim().startsWith("filename")) {

				final String[] name = filename.split("=");

				final String finalFileName = name[1].trim().replaceAll("\"", "");
				return finalFileName;
			}
		}
		return "unknown";
	}

	/**
	 * @param content
	 * @param filename
	 * @throws IOException
	 */
	private void writeFile(final byte[] content, final String filename)
			throws IOException {

		final File file = new File(filename);

		if (!file.exists()) {
			if (!file.createNewFile()) {
				throw new IOException("Cannot create new file");
			}
		}

		final FileOutputStream fop = new FileOutputStream(file);

		fop.write(content);
		fop.flush();
		fop.close();

	}
}