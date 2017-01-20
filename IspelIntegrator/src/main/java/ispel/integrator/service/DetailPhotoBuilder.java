package ispel.integrator.service;

import ispel.integrator.dao.Dao;

import java.util.List;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DetailPhotoBuilder {

	private static final Namespace ns = Namespace.getNamespace("iris", "http://IRISWebServices.sk/"); 
	
	private static final Logger log = Logger.getLogger(DetailPhotoBuilder.class);
	
	private static final String FILE_PREFIX = "KO_";
	
	private static final int BUFFER = 1024;
	
	@Autowired
	private Dao dao;
	
	private String imagesDirectoryPrefix;
	private XPathFactory xpathFactory;
	
	@PostConstruct
	public void init() {
		imagesDirectoryPrefix = dao.getImagesDirectory();
		xpathFactory = XPathFactory.instance();
	}

	public List<String> build(Element root, long carId) {
		List<String> photoList = new ArrayList<String>();
		XPathExpression<Element> xp = (XPathExpression<Element>) xpathFactory.compile("//iris:zipFoto", Filters.element(ns), null, ns);
		Element e = xp.evaluateFirst(root);
		if (e == null) {
			log.info("No photo in response.");
		} else {
			processPhotoData(e, carId, photoList);
		}
		return photoList;
	}
	
	private void processPhotoData(Element photo, long carId, List<String> photoList) {
		byte[] decodedContent = new Base64().decode(photo.getText());
		ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(decodedContent));
		try {
			ZipEntry zipEntry = null;
		    while ((zipEntry = zipStream.getNextEntry()) != null) {
//		    	String fileName = zipEntry.getName();
//		    	System.out.println("KO_" + fileName);
		    	int count;
		        byte data[] = new byte[BUFFER];
		        // Write the files to the disk
		        File file = getPhotoFile(carId,zipEntry.getName());
		        if (!file.exists()) {
		        	photoList.add(file.getName());
		        }
		        FileOutputStream fos = new FileOutputStream(file);
		        BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
		        while ((count = zipStream.read(data, 0, BUFFER)) != -1) {
		          dest.write(data, 0, count);
		        }
		        dest.flush();
		        dest.close();
		        zipStream.closeEntry();
		    }
		} catch(Exception e) {
			log.error(e);
		} finally {
			if (zipStream != null) {
				try { zipStream.close(); } catch(Exception ex) {}
			}
		}
	}
	
	private File getPhotoFile(long carId, String name) throws IOException {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(imagesDirectoryPrefix)
		.append(System.getProperty("file.separator"))
		.append(String.format("%08d", carId));
		File dir = new File(sBuilder.toString());
		FileUtils.forceMkdir(dir);
		File file = new File(dir, FILE_PREFIX + name);
		return file;
	}
	
}
