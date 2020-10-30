package org.report.xml.support;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ApachePOIExcelRead {

	private static String FILE_NAME = "C:\\data\\";
	private static ApachePOIExcelRead instance = null;
	private static Sheet sheet;
	private static XSSFWorkbook workbook;
	private static Map<Object, Map<Enum<ADDRESS>, Object>> identMap;
	private static Map<Enum<ADDRESS>, Object> addressMap;
	private static ADDRESS type;

	private enum ADDRESS {
		HouseNumber, Place, Name, Street, PostalCode, Annotation, LSVIdentAddresses, LSVIdent, CScountryCd, CountryName, IsoCountryCd, Country, PostBox;

	}

	protected ApachePOIExcelRead() {

		workbook = new XSSFWorkbook();
		sheet = workbook.createSheet("F2B Report");

	}

	public static ApachePOIExcelRead getInstance() {
		if (instance == null) {
			instance = new ApachePOIExcelRead();
		}
		return instance;
	}

	public List<SixModel> transposeReport(Sheet sheet) {
		Map<Object, Map<Enum<ADDRESS>, Object>> transforemedMap = transformTable(sheet);
		List<SixModel> listOfSix = convertToSix(transforemedMap);

		return listOfSix;
	}

	private List<SixModel> convertToSix(Map<Object, Map<Enum<ADDRESS>, Object>> transforemedMaps) {
		Set<Object> keys = transforemedMaps.keySet();
		List<SixModel> listSix = new ArrayList<SixModel>();

		for (Object key : keys) {
			Map<Enum<ADDRESS>, Object> addressMap = transforemedMaps.get(key);
			listSix.add(populateSixModel(addressMap, key));
		}
		return listSix;

	}

	private SixModel populateSixModel(Map<Enum<ADDRESS>, Object> addressMap, Object lsvIndentAddresses) {
		Set<Enum<ADDRESS>> keys = addressMap.keySet();
		SixModel model = new SixModel();
		String houseNumber = null, place = null, name = null, street = null, postalCode = null, annotation = null,
				countryName = null, postBox = null, csCountryCd = null, isoCountryCd = null;

		for (Object key : keys) {

			type = ADDRESS.valueOf(key.toString());

			switch (type) {
			case HouseNumber:
				houseNumber = addressMap.get(type) != null ? addressMap.get(type).toString() : null;
				break;
			case Place:
				place = addressMap.get(type) != null ? addressMap.get(type).toString() : null;
				break;
			case Name:
				name = addressMap.get(type) != null ? addressMap.get(type).toString() : null;
				break;
			case Street:
				street = addressMap.get(type) != null ? addressMap.get(type).toString() : null;
				break;
			case PostalCode:
				postalCode = addressMap.get(type) != null ? addressMap.get(type).toString() : null;
				break;
			case Annotation:
				annotation = addressMap.get(type) != null ? addressMap.get(type).toString() : null;
				break;
			case CountryName:
				countryName = addressMap.get(type) != null ? addressMap.get(type).toString() : null;
				break;
			case PostBox:
				postBox = addressMap.get(type) != null ? addressMap.get(type).toString() : null;
				break;
			case CScountryCd:
				csCountryCd = addressMap.get(type) != null ? addressMap.get(type).toString() : null;
				break;
			case IsoCountryCd:
				isoCountryCd = addressMap.get(type) != null ? addressMap.get(type).toString() : null;
				break;
			case Country:
				countryName = addressMap.get(type) != null ? addressMap.get(type).toString() : null;
				break;
			default:
				break;

			}

		}

		model.setAnnotation(StringUtils.isBlank(annotation) ? null : annotation.trim());
		model.setCountry(StringUtils.isBlank(countryName) ? null : countryName.trim());
		model.setCsCountryCd(StringUtils.isBlank(csCountryCd) ? null : csCountryCd.trim());
		model.setIsoCountryCd(StringUtils.isBlank(isoCountryCd) ? null : isoCountryCd.trim());
		model.setLsvIdentAddresses(lsvIndentAddresses.toString());
		model.setName(StringUtils.isBlank(name) ? null : name.trim());
		model.setPlace(StringUtils.isBlank(place) ? null : place.trim());
		model.setPostalCode(StringUtils.isBlank(postalCode) ? null : postalCode.trim());
		model.setPostBox(StringUtils.isBlank(postBox) ? null : postBox.trim());
		String streetTemp = StringUtils.isBlank(street) ? null : street.trim();
		String houseNumberTemp = StringUtils.isBlank(houseNumber) ? null : houseNumber.trim();
		model.setStreet(streetTemp + " " + houseNumberTemp);

		return model;
	}

	public Sheet readExcel(String xlsName) throws InvalidFormatException {

		try {

			Workbook workbook = WorkbookFactory.create(new File(FILE_NAME + xlsName));

			sheet = workbook.getSheetAt(0);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sheet;
	}

	private static Map<Object, Map<Enum<ADDRESS>, Object>> transformTable(Sheet sheet) {

		for (Row row : sheet) {

			String ident = row.getCell(1).toString();

			if ((identMap == null)) {

				identMap = new HashMap<Object, Map<Enum<ADDRESS>, Object>>();
				addressMap = new HashMap<Enum<ADDRESS>, Object>();
				type = ADDRESS.valueOf(row.getCell(2).toString());
				String address = row.getCell(3).toString();
				setAddressMap(type, addressMap, address);
				identMap.put(ident, addressMap);

			} else if ((identMap.containsKey(ident))) {

				Map<Enum<ADDRESS>, Object> addressMap = identMap.get(ident);
				type = ADDRESS.valueOf(row.getCell(2).toString());
				String address = row.getCell(3).toString();
				setAddressMap(type, addressMap, address);
				identMap.put(ident, addressMap);
			} else if (!(identMap.containsKey(ident))) {

				addressMap = new HashMap<Enum<ADDRESS>, Object>();
				type = ADDRESS.valueOf(row.getCell(2).toString());
				String address = row.getCell(3).toString();
				setAddressMap(type, addressMap, address);
				identMap.put(ident, addressMap);
			}

		}

		return identMap;
	}

	private static void setAddressMap(ADDRESS type, Map<Enum<ADDRESS>, Object> addressMap2, String address) {

		switch (type) {
		case HouseNumber:
			addressMap2.put(type, address);
			break;
		case Place:
			addressMap2.put(type, address);
			break;
		case Name:
			addressMap2.put(type, address);
			break;
		case Street:
			addressMap2.put(type, address);
			break;
		case PostalCode:
			addressMap2.put(type, address);
			break;
		case Annotation:
			addressMap2.put(type, address);
			break;
		case CountryName:
			addressMap2.put(type, address);
			break;
		case PostBox:
			addressMap2.put(type, address);
			break;
		case CScountryCd:
			addressMap2.put(type, address);
			break;
		case IsoCountryCd:
			addressMap2.put(type, address);
			break;
		case Country:
			addressMap2.put(ADDRESS.CountryName, address);
			break;
		default:
			break;

		}

	}

}
