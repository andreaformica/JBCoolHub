package atlas.cool.web;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import atlas.cool.rest.utils.WarResources;

public class FileDownloadController {

    private StreamedContent file;  
    
    private static String webpath =  WarResources.externalwebdir;
    
    public FileDownloadController(String filename) {          
        InputStream stream;
		try {
			stream = new FileInputStream(webpath+"/"+filename);
	        file = new DefaultStreamedContent(stream, "text/plain", "downloaded_"+filename);  
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
    }  
  
    public StreamedContent getFile() {  
        return file;  
    }    
}
