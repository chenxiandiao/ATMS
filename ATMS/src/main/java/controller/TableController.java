package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import model.ColumnHeader;
import model.ExcelImportTabelCell;
import model.FilterType;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import dao.TableDaoImpl;

@Controller
@RequestMapping(value="/TableController")
public class TableController {
	
	@Autowired
	private TableDaoImpl tableDaoImpl;
	
	private List<Integer> columnIdList;
	private String username;
	
	private String savePath = "E:\\3.xls";
	
	@RequestMapping(value="login")
	private String login(String username,String password,HttpServletRequest request){
		if(tableDaoImpl.login(username, password))
		{
			columnIdList = tableDaoImpl.getColumnIdList(username);
			System.out.println("用户可查看的列"+columnIdList.size());
			this.username = username;
			return "/html/atms.html";
		}
		else
		{
			return "error";
		}
//		return  "/html/atms.html";
	}
	@RequestMapping(value="/showTableColumn")
	@ResponseBody
	public String showTableColumn(){
		System.out.println("showTableColumn");
		JSONArray data = new JSONArray();
		data = tableDaoImpl.showTableHeader(this.username);
		return data.toString();
	}
	
	/*@RequestMapping(value="/showTableColumn")
	@ResponseBody
	public List<List<ColumnHeader>> showTableColumn(){
		System.out.println("showTableColumn");
		List<List<ColumnHeader>> data = new ArrayList<List<ColumnHeader>>();
		data = tableDaoImpl.showTableHeader(columnIdList);
		return data;
	}*/
	
	@RequestMapping(value="/showTableData")
	@ResponseBody
	public String showTableData(){
		System.out.println("showTableData");
		JSONObject data = tableDaoImpl.showTableData(columnIdList);
		return data.toString();
	}
	
	//传递不显示的列
	@RequestMapping(value="/setShowColumn")
	@ResponseBody
	public String setShowColumn(@RequestBody List<ColumnHeader> columnHeaderList){
		System.out.println(columnHeaderList.size());
		
		List<Integer> hideColumnIdList = new ArrayList<Integer>();
		for(int i=0;i<columnHeaderList.size();i++)
		{
			hideColumnIdList.add(Integer.parseInt(columnHeaderList.get(i).getField()));
		}
		
		tableDaoImpl.setShowColumn(username,hideColumnIdList);
		//重新获得显示列
		columnIdList = tableDaoImpl.getColumnIdList(username);
		System.out.println(columnIdList.size());
		JSONObject data = new JSONObject();
		data.put("msg", "success");
		return data.toString();
	}
	
	@RequestMapping(value="/getFilter")
	@ResponseBody
	public List<ColumnHeader> getFilter(){
//		JSONObject data = new JSONObject();
		List<ColumnHeader> data = tableDaoImpl.getFilter();
		return data;
	}
	
	@RequestMapping(value="/searchByNumber")
	@ResponseBody
	public String showTableDataOfSearchByNumber(String searchKey){
		JSONObject data = tableDaoImpl.getTableDataByNumber(columnIdList, searchKey);
		return data.toString();
	}
	
	@RequestMapping(value="/setFormula")
	@ResponseBody
	public String setFormula(int columnId,String categoryName,String formula)
	{
		JSONObject data = new JSONObject();
		tableDaoImpl.setFormula(columnId, categoryName, formula);
		data.put("msg", "success");
		return data.toString();
	}
	
	@RequestMapping(value="/searchByFilter")
	@ResponseBody
	public String showTableDataOfSearchByFilter(@RequestBody List<FilterType>filterTypeList)
	{
		System.out.println("searchByFilter");
		JSONObject data = tableDaoImpl.getTableDataByFilter(columnIdList,filterTypeList);
		return data.toString();
	}
	
	
	@RequestMapping(value="/importExcel")
	@ResponseBody
	public Map<String,Object> upload(HttpServletRequest httpServletRequest)
	{
		 Map<String,Object>backData = new HashMap<String, Object>();
		 try{
			String path = saveFile(httpServletRequest);
			int maxRowId = tableDaoImpl.getMaxRowId();
			List<ExcelImportTabelCell> cellList = storeDBByPOI(maxRowId,path);
			tableDaoImpl.insertCell(cellList);
			backData.put("msg", "success");
		  }catch (Exception e) {
			  backData.put("msg", "fail");
			  e.printStackTrace();
			} 
		  return backData; 
	} 
	
	
	
	private String saveFile(HttpServletRequest httpServletRequest) throws Exception{
		
		  String s = httpServletRequest.getParameter("file_name");
		  MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) httpServletRequest;
		  CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile("Filedata");	 
		  String fileName = null;
		  fileName = URLDecoder.decode(s, "UTF-8");
		  InputStream inputStream = file.getInputStream();
		  String savePath = "F:\\uploads\\"+ fileName;
		  File file1 = new File(savePath);
		  FileOutputStream fileOutputStream = new FileOutputStream(file1);
		  byte[] buf = new byte[1024];  
		  int ch = -1;
		  while ((ch = inputStream.read(buf)) != -1) { 
				  fileOutputStream.write(buf, 0, ch);          
			 }
		  fileOutputStream.flush();
		  if(fileOutputStream!=null){
				 fileOutputStream.close();
		 }
		 return savePath;
	}
	
	private List<ExcelImportTabelCell> storeDBByPOI(int maxRowId,String savePath){
		List<ExcelImportTabelCell> cellList = new ArrayList<ExcelImportTabelCell>();
		try {
			XSSFWorkbook xwb = new XSSFWorkbook(new FileInputStream(savePath));
			XSSFSheet sheet = xwb.getSheetAt(0); 
			XSSFRow row;  
			String value;
			System.out.println(sheet.getPhysicalNumberOfRows());
			for (int i = 2; i < sheet.getPhysicalNumberOfRows(); i++) {  
			    row = sheet.getRow(i);  
			    for (int j = row.getFirstCellNum(); j < row.getPhysicalNumberOfCells(); j++) {  
			        // 通过 row.getCell(j).toString() 获取单元格内容，  
			    	if(row.getCell(j)!=null)
			    	{
			    		value = getCellValue(row.getCell(j));  
			    	}
			    	else
			    	{
			    		value = "";
			    	}
			    	
			        ExcelImportTabelCell cell = new ExcelImportTabelCell();
					cell.setRow_id(maxRowId+i-1);
					cell.setColumn_id(j+1);
					cell.setData_value(value);
					cell.setFormula_type(0);
					cell.setData_value_ignore(value.replace("/[^a-zA-Z0-9]/", ""));
					cellList.add(cell);
			    }  
			    System.out.println(row.getCell(0));
			}
			System.out.println("cellList.size:"+cellList.size());
		
		}catch (IOException e) {
			e.printStackTrace();
		}
		return cellList;
	}
	
	private String getCellValue(XSSFCell cell) {
		         String value = null;
		         if (cell != null) {
		             switch (cell.getCellType()) {
		             case XSSFCell.CELL_TYPE_FORMULA:
		                 // cell.getCellFormula();
		                try {
		                     value = String.valueOf(cell.getNumericCellValue());
		                 } catch (IllegalStateException e) {
		                     value = String.valueOf(cell.getRichStringCellValue());
		                 }
		                 break;
		             case XSSFCell.CELL_TYPE_NUMERIC:
		                 value = String.valueOf(cell.getNumericCellValue());
		                 break;
		             case XSSFCell.CELL_TYPE_STRING:
		                 value = String.valueOf(cell.getRichStringCellValue());
		                 break;
		             case XSSFCell.CELL_TYPE_BLANK: // 空值     
		            	 value = " ";    
                         break;         
		             }
		         }
		  return value;
	}
}