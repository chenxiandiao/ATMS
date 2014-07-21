package controller;

import java.util.ArrayList;
import java.util.List;

import model.ColumnHeader;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/TableController")
public class TableController {
	
	@RequestMapping(value="/showTableColumn")
	@ResponseBody
	public List<List<ColumnHeader>> showTableColumn(){
		System.out.println("showTableColumn");
		List<List<ColumnHeader>> data = new ArrayList<List<ColumnHeader>>();
		List<ColumnHeader>firstColumnList = new ArrayList<ColumnHeader>();
		firstColumnList.add(new ColumnHeader("itemid","item ID",2,1,true));
		firstColumnList.add(new ColumnHeader("Item Details","Item Details",1,4,true));
		
		List<ColumnHeader>secondColumnList = new ArrayList<ColumnHeader>();
		secondColumnList.add(new ColumnHeader("listprice","'List Price",1,1,true));
		secondColumnList.add(new ColumnHeader("unitcost","Unit Cost",1,1,true));
		secondColumnList.add(new ColumnHeader("attr1","Attribute",1,1,true));
		secondColumnList.add(new ColumnHeader("status","status",1,1,true));
		
		data.add(firstColumnList);
		data.add(secondColumnList);
		return data;
	}
}
