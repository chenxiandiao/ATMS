package controller;

import java.util.ArrayList;
import java.util.List;

import model.ColumnHeader;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import dao.TableDaoImpl;

@Controller
@RequestMapping(value="/TableController")
public class TableController {
	
	@Autowired
	private TableDaoImpl tableDaoImpl;
	
	@RequestMapping(value="/showTableColumn")
	@ResponseBody
	public List<List<ColumnHeader>> showTableColumn(){
		System.out.println("showTableColumn");
		List<List<ColumnHeader>> data = new ArrayList<List<ColumnHeader>>();
		data = tableDaoImpl.showTableHeader();
		return data;
	}
	
	@RequestMapping(value="/showTableData")
	@ResponseBody
	public String showTableData(){
		System.out.println("showTableData");
		JSONObject data = tableDaoImpl.showTableData();
		return data.toString();
	}
}