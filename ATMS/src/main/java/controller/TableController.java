package controller;

import java.util.ArrayList;
import java.util.List;

import model.ColumnHeader;
import model.FilterType;
import model.Tes;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import dao.TableDaoImpl;

@Controller
@RequestMapping(value="/TableController")
public class TableController {
	
	@Autowired
	private TableDaoImpl tableDaoImpl;
	private List<Integer> columnIdList;
	
	@RequestMapping(value="login")
	private String login(String username,String password){
//		System.out.println(cnt++);
//		return "/WEB-INF/jsp/index.jsp";
		if(tableDaoImpl.login(username, password))
		{
			columnIdList = tableDaoImpl.getColumnIdList(username);
			return "index";
		}
		else
		{
			return "error";
		}
	}
	@RequestMapping(value="/showTableColumn")
	@ResponseBody
	public List<List<ColumnHeader>> showTableColumn(){
		System.out.println("showTableColumn");
		List<List<ColumnHeader>> data = new ArrayList<List<ColumnHeader>>();
		data = tableDaoImpl.showTableHeader(columnIdList);
		return data;
	}
	
	@RequestMapping(value="/showTableData")
	@ResponseBody
	public String showTableData(){
		System.out.println("showTableData");
		JSONObject data = tableDaoImpl.showTableData(columnIdList);
		return data.toString();
	}
	
	@RequestMapping(value="/setShowColumn")
	@ResponseBody
	public String index(@RequestBody List<ColumnHeader> columnHeaderList){
//		System.out.println(columnHeaderList);
		System.out.println(columnHeaderList.size());
		columnIdList = new ArrayList<Integer>();
		for(int i=0;i<columnHeaderList.size();i++)
		{
			columnIdList.add(Integer.parseInt(columnHeaderList.get(i).getField()));
		}
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
	public String setFormula(int columnId,int categoryId,String formula)
	{
		JSONObject data = new JSONObject();
		tableDaoImpl.setFormula(columnId, categoryId, formula);
		data.put("msg", "success");
		return data.toString();
	}
	
	@RequestMapping(value="/searchByFilter")
	@ResponseBody
	public String showTableDataOfSearchByFilter(@RequestBody List<FilterType>filterTypeList)
	{
		JSONObject data = tableDaoImpl.getTableDataByFilter(columnIdList,filterTypeList);
		return data.toString();
	}
	
}