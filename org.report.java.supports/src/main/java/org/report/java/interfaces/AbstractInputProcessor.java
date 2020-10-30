package org.report.java.interfaces;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.xml.sax.SAXException;

public abstract class AbstractInputProcessor {

	protected LocalDateTime date = LocalDateTime.now();
	protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss");
	protected DateTimeFormatter formatterTaxReclaim = DateTimeFormatter.ofPattern("hh_mm_ss");


	@Autowired
	protected JdbcTemplate jdbcLPUTemplate;
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	@Autowired
	protected JdbcTemplate jdbcTemplateTaxReclaim;

	protected enum IDENT {
		HouseNumber, Place, Name, Street, PostalCode, Annotation, LSVIdentAddresses, LSVIdent, CScountryCd, CountryName, IsoCountryCd, Country, PostBox;

	}

	public abstract List<Map<String, Object>> connectToSource(String... sql)
			throws SAXException, IOException, ParserConfigurationException;

	protected List<Map<Object, String>> deserializeObject(String sql, String xml, String id) {

		List<Map<Object, String>> list = jdbcLPUTemplate.query(sql, (rs, rowNum) -> {
			String xml_data = null;
			String xml_id = null;
			Map<Object, String> resultMap = new HashMap<Object, String>();
			try {

				byte[] byteArr = rs.getBytes(xml);

				if (byteArr == null) {
					throw new RuntimeException("INPUT_ORDER_XML is not available!");

				}

				xml_data = new String(byteArr);
				xml_id = rs.getString(id);
				resultMap.put(xml_id, xml_data);

			} catch (Exception e) {

				e.printStackTrace();
			}
			return resultMap;

		});
		return list;

	}

	protected void saveToFile(Path newDirectoryPath, String xml, String id, String folder_id) throws IOException {
		Path path2 = Paths.get(newDirectoryPath + "/" + id);
		Path path = Paths.get(path2 + "/" + folder_id + ".xml");

		Files.createDirectories(path2);
		try (BufferedWriter writer = Files.newBufferedWriter(path))

		{

			writer.write(xml);
			writer.close();
		}

	}

}
