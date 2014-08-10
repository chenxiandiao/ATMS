package dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.ColumnHeader;
import model.ExcelImportTabelCell;
import model.FilterType;
import model.FormulaCell;
import model.TableData;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import util.Expression;

@Repository
public class TableDaoImpl{
	@PersistenceContext
	private EntityManager em;
	private int defaultColumnBegin = 51;
	private int defaultColumnEnd = 69;
	private String defaultColumnArrayStr = "51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69";
	
	@Transactional
	public JSONArray showTableHeader(String username)
	{
		JSONArray tableHeaderArray = new JSONArray();
		String sql = "select b.column_id,b.column_name,b.column_group,b.data_type,a.showflag from tbl_user_column a left join tbl_prod_column b on a.column_id=b.column_id where user_name='"+username+"'";
		System.out.println(sql);
		Query query = em.createNativeQuery(sql);
		List list = query.getResultList();
		String column_group = null;
		JSONObject secondParentObject = null;
		JSONArray childrenArray = null;
		
		for(int i = 0;i<list.size();i++)
		{
			Object[]obj = (Object[])list.get(i);
			if(obj[2]==null||obj[2]=="")
			{
				JSONObject firstHeaderObject = new JSONObject();
				firstHeaderObject.put("id",  "C"+obj[0].toString());
				firstHeaderObject.put("name",  obj[1].toString());
				firstHeaderObject.put("display",obj[4].toString().equals("1")?true:false);
				if(obj[3].equals("number")||obj[3].equals("money"))
				{
					firstHeaderObject.put("type", "number");
				}
				else
				{
					firstHeaderObject.put("type", "text");
				}
				tableHeaderArray.put(firstHeaderObject);
			}
			else
			{
				if(!obj[2].toString().equals(column_group))
				{
					System.out.println(obj[2]);
					if(secondParentObject!=null&&childrenArray!=null)
					{
						secondParentObject.put("children",childrenArray);
						tableHeaderArray.put(secondParentObject);
					}
					secondParentObject = new JSONObject();
					childrenArray = new JSONArray();
					secondParentObject.put("name", obj[2].toString());
					
					JSONObject childrenObject = new JSONObject();
					childrenObject.put("id","C"+obj[0].toString());
					childrenObject.put("name", obj[1].toString());
					if(obj[3].equals("number")||obj[3].equals("money"))
					{
						childrenObject.put("type", "number");
					}
					else
					{
						childrenObject.put("type", "text");
					}
					childrenObject.put("display",obj[4].toString().equals("1")?true:false);
					childrenArray.put(childrenObject);
					
					
					column_group =  obj[2].toString();
				}
				else
				{
					JSONObject childrenObject = new JSONObject();
					childrenObject.put("id","C"+obj[0].toString());
					childrenObject.put("name", obj[1].toString());
					if(obj[3].equals("number")||obj[3].equals("money"))
					{
						childrenObject.put("type", "number");
					}
					else
					{
						childrenObject.put("type", "text");
					}
					childrenObject.put("display",obj[4].toString().equals("1")?true:false);
					childrenArray.put(childrenObject);
				}
				
			}	
		}
		//最后一个分组
		if(secondParentObject!=null&&childrenArray!=null)
		{
			secondParentObject.put("children",childrenArray);
			tableHeaderArray.put(secondParentObject);
		}
		return tableHeaderArray;
	}
				
	
	/*@Transactional
	public List<List<ColumnHeader>> showTableHeader(List<Integer>columnIdList)
	{
		String columnArrayStr = convertToColumnArrayStr(columnIdList);
		
		String sql = "select a.column_id,a.column_name,a.column_group,a.data_type,b.cnt from tbl_prod_column a left join (select column_id,count(*) as cnt from tbl_prod_column where column_id in ("+columnArrayStr+") group by column_group) b on a.column_id=b.column_id where a.column_id in ("+columnArrayStr+")";
		System.out.println(sql);
		Query query = em.createNativeQuery(sql);
		List list = query.getResultList();
		List<List<ColumnHeader>> data = new ArrayList<List<ColumnHeader>>();
		List<ColumnHeader>firstColumnList = new ArrayList<ColumnHeader>();
		List<ColumnHeader>secondColumnList = new ArrayList<ColumnHeader>();
		for(int i = 0;i<list.size();i++)
		{
			Object[]obj = (Object[])list.get(i);
			if(obj[2]==null||obj[2]=="")
			{
				String field =  obj[0].toString();
				ColumnHeader columnHeader = new ColumnHeader();
				if(obj[3].equals("number")||obj[3].equals("money"))
				{
					columnHeader.initData(field,(String) obj[1],2,1,true,true,"number");
				}
				else
				{
					columnHeader.initData(field,(String) obj[1],2,1,true,true,"text");
				}
				
				firstColumnList.add(columnHeader);
				
			}
			else
			{
				
				if(obj[4]!=null&&obj[4]!="")
				{
					String field = obj[0].toString();
					ColumnHeader fcolumnHeader = new ColumnHeader();
					if(obj[3].equals("number")||obj[3].equals("money"))
					{
						fcolumnHeader.initData(null,(String) obj[2],1,Integer.parseInt(String.valueOf(obj[4])),true,true,"number");
					}
					else
					{
						fcolumnHeader.initData(null,(String) obj[2],1,Integer.parseInt(String.valueOf(obj[4])),true,true,"text");
					}
					
					firstColumnList.add(fcolumnHeader);
					ColumnHeader scolumnHeader = new ColumnHeader();
					if(obj[3].equals("number")||obj[3].equals("money"))
					{
						scolumnHeader.initData(field,(String) obj[1],1,1,true,true,"number");
					}
					else
					{
						scolumnHeader.initData(field,(String) obj[1],1,1,true,true,"text");
					}
					secondColumnList.add(scolumnHeader);
				}
				else
				{
					String field = obj[0].toString();
					ColumnHeader columnHeader = new ColumnHeader();
					if(obj[3].equals("number")||obj[3].equals("money"))
					{
						columnHeader.initData(field,(String) obj[1],1,1,true,true,"number");
					}
					else
					{
						columnHeader.initData(field,(String) obj[1],1,1,true,true,"text");
					}
					secondColumnList.add(columnHeader);
				}
			}
		}
		data.add(firstColumnList);
		data.add(secondColumnList);
		return data;
	}*/
	
	
	
	
	@Transactional
	public JSONObject showTableData(List<Integer>columnIdList,int page,int rows)
	{
		String columnArrayStr = convertToColumnArrayStr(columnIdList);
		
		//String sql = "SELECT a.row_id,b.column_id,a.data_value FROM tbl_prod_column b left join tbl_prod_data a on b.column_id = a.column_id where b.column_id like '%' order by a.row_id,b.column_id ";
		String sql = "SELECT a.row_id,b.column_id,a.data_value,a.formula_type FROM tbl_prod_column b left join tbl_prod_data a on b.column_id = a.column_id where b.column_id in("+columnArrayStr+") order by a.row_id,b.column_id ";
		System.out.println(sql);
		return getTableData(sql,null,page,rows);
	}
	
	private JSONObject getTableData(String sql,String searchKey,int page,int rows)
	{
		Query query = em.createNativeQuery(sql);
		List list = query.getResultList();
		JSONObject tableDataJsonObject = new JSONObject();
		int row_id = 0;
		int pref_row_id = -1;
		String field = "";
		String value = "";
		int formula_type = 0;
		JSONObject row = null;
		JSONArray rowsList = new JSONArray();
		int rowCnt = 0;
		List<TableData>tableDataList = new ArrayList<TableData>();
		for(int i = 0;i<list.size();i++)
		{
			Object[]obj = (Object[])list.get(i);
			if(obj[0]==null)
			{
				continue;
			}
			row_id = (Integer) obj[0];
			field = obj[1].toString();
			value = obj[2].toString();
			formula_type = (Integer) obj[3];
			if(row_id!=pref_row_id)
			{
				if(row!=null)
				{
					if(searchKey!=null)
					{
						String[]searchArray = searchKey.split(",");
						List<String> showSearchKeyList = new ArrayList<String>();
						boolean flag = false;
						for(int j=0;j<tableDataList.size();j++)
						{
							int column_id = Integer.parseInt(tableDataList.get(j).getField());
							if(column_id>=this.defaultColumnBegin&&column_id<=this.defaultColumnEnd)
							{
								for(int k=0;k<searchArray.length;k++)
								{
									if(tableDataList.get(j).getValue().indexOf(searchArray[k])!=-1)
									{
										if(!showSearchKeyList.contains(searchArray[k]))
										{
											showSearchKeyList.add(searchArray[k]);
										}
										flag = true;
									}
								}
							}
						}
						if(flag)
						{
							String showSearchKey = "";
							for(int t = 0;t<showSearchKeyList.size();t++)
							{
								showSearchKey = showSearchKey + showSearchKeyList.get(t) +" ";
							}
							row.put("C0", showSearchKey);
						}
					}
					
					rowsList.put(row);
					rowCnt++;
					row = new JSONObject();
					tableDataList = new ArrayList<TableData>();
					TableData  tableData = new TableData();
					tableData.setField(field);
					tableData.setValue(value);
					tableData.setFormula_type(formula_type);
					tableDataList.add(tableData);
					row.put("row_id", row_id);
					row.put("C"+field, value);
					
				}
				else
				{
					row = new JSONObject();
					row.put("C"+field, value);
					row.put("row_id", row_id);
					TableData  tableData = new TableData();
					tableData.setField(field);
					tableData.setValue(value);
					tableData.setFormula_type(formula_type);
					tableDataList.add(tableData);
				}
				pref_row_id = row_id;
				
			}
			else
			{
				if(row!=null)
				{
					row.put("C"+field, value);
					TableData  tableData = new TableData();
					tableData.setField(field);
					tableData.setValue(value);
					tableData.setFormula_type(formula_type);
					tableDataList.add(tableData);
				}
			}
			
		}
		//最后一组数据
		if(row!=null)
		{
			if(searchKey!=null)
			{
				String[]searchArray = searchKey.split(",");
				List<String> showSearchKeyList = new ArrayList<String>();
				boolean flag = false;
				for(int j=0;j<tableDataList.size();j++)
				{
					int column_id = Integer.parseInt(tableDataList.get(j).getField());
					if(column_id>=this.defaultColumnBegin&&column_id<=this.defaultColumnEnd)
					{
						for(int k=0;k<searchArray.length;k++)
						{
							if(tableDataList.get(j).getValue().indexOf(searchArray[k])!=-1)
							{
								if(!showSearchKeyList.contains(searchArray[k]))
								{
									showSearchKeyList.add(searchArray[k]);
								}
								flag = true;
							}
						}
					}
				}
				if(flag)
				{
					String showSearchKey = "";
					for(int t = 0;t<showSearchKeyList.size();t++)
					{
						showSearchKey = showSearchKey + showSearchKeyList.get(t) +" ";
					}
					row.put("C0", showSearchKey);
				}
			}
			
			rowsList.put(row);
			rowCnt++;
		}
		if(page==0&&page==0)
		{
			tableDataJsonObject.put("rows", rowsList);
		}
		else
		{
			JSONArray pageRowArray = new JSONArray();
			int count = rowsList.length() - (page-1)*rows;
			count = (count>rows?rows:count);
			for(int i = (page-1)*rows;i<(page-1)*rows+count;i++)
			{
				pageRowArray.put(rowsList.get(i));
			}
			tableDataJsonObject.put("rows", pageRowArray);
		}
		
		tableDataJsonObject.put("count", rowCnt);
		return tableDataJsonObject;
	}
	
	
	@Transactional
	public boolean login(String username,String password)
	{
		String sql = "select * from tbl_user where user_name='"+username+"' and user_pswd='"+password+"'";
		Query query = em.createNativeQuery(sql);
		List list = query.getResultList();
		if(list.size()>0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Transactional
	public List<Integer>getColumnIdList(String username){
		String sql = "SELECT column_id FROM tbl_user_column t where user_name='"+username+"' and showflag=1";
		Query query = em.createNativeQuery(sql);
		List list = query.getResultList();
		List<Integer>columnIdList = new ArrayList<Integer>();
		for(int i = 0;i<list.size();i++)
		{
			columnIdList.add((Integer) list.get(i));
		}
		return columnIdList;
	}
	
	@Transactional
	public List<ColumnHeader>getFilter(){
		String sql = "select column_id,data_type from tbl_prod_column";
		Query query = em.createNativeQuery(sql);
		List<ColumnHeader> columnHeaderList = new ArrayList<ColumnHeader>();
		List list = query.getResultList();
		for(int i = 0;i<list.size();i++)
		{
			ColumnHeader columnHeader = new ColumnHeader();
			Object[]obj = (Object[])list.get(i);
			columnHeader.setField(obj[0].toString());
			columnHeader.setType(obj[1].toString());
			columnHeaderList.add(columnHeader);
		}
		return columnHeaderList;
	}
	
	
	@Transactional
	public JSONObject getTableDataByNumber(List<Integer>columnIdList,String searchKey,int page,int rows){
		String columnArrayStr = convertToColumnArrayStr(columnIdList);
		String converToSearchKeyStr = converToSearchKeyStr(searchKey);
		String sql = "SELECT a.row_id,b.column_id,a.data_value,a.formula_type FROM tbl_prod_column b left join tbl_prod_data a on b.column_id = a.column_id where b.column_id in("+columnArrayStr+") and row_id in (select distinct(row_id) from tbl_prod_data where column_id in("+defaultColumnArrayStr+") and data_value_ignore Rlike '"+converToSearchKeyStr+"') order by a.row_id,b.column_id";
		System.out.println(sql);
		return getTableData(sql,searchKey,page,rows);
	}
	
	private String converToSearchKeyStr(String searchKey)
	{
		String searchKeyStr = "";
		String[] searchArray = searchKey.split(",");
		for(int i= 0;i<searchArray.length;i++)
		{
			searchKeyStr = searchKeyStr + searchArray[i] + "|";
		}
		if(searchKeyStr!="")
			searchKeyStr = searchKeyStr.substring(0, searchKeyStr.length()-1);
		return searchKeyStr;
	}
	
	private String convertToColumnArrayStr(List<Integer>columnIdList){
		String columnArrayStr = "";
		for(int i = 0;i<columnIdList.size();i++)
		{
			 columnArrayStr = columnArrayStr + columnIdList.get(i)+",";
		}
		if(columnArrayStr!="")
			columnArrayStr = columnArrayStr.substring(0, columnArrayStr.length()-1);
		return columnArrayStr;
	}
	
	
	@Transactional
	public void setFormula(int columnId,int categoryId,String categoryName,String formula){
		
		String upadateColumnSql = "update tbl_formula set formula='"+formula+"' where column_id="+columnId+" and category_id="+categoryId;
		System.out.println(upadateColumnSql);
		Query upadateColumnQuery = em.createNativeQuery(upadateColumnSql);
		upadateColumnQuery.executeUpdate();
		
		String updateDataSql = "update tbl_prod_data set data_value='"+formula+"', formula_type=1 where column_id="+columnId+" and row_id in(select a.row_id from (select row_id from tbl_prod_data where data_value='"+categoryName+"')a)";
		System.out.println(updateDataSql);
		Query  upadateDataQuery = em.createNativeQuery(updateDataSql);
		upadateDataQuery.executeUpdate();
		
		String selectSql = "select row_id,column_id,data_value,formula_type from tbl_prod_data where  row_id in(select a.row_id from (select row_id from tbl_prod_data where data_value='"+categoryName+"')a)";
		System.out.println(selectSql);
		Query selectQuery = em.createNativeQuery(selectSql);
		List list = selectQuery.getResultList();

		int row_id = 0;
		int pref_row_id = -1;
		String field = "";
		String value = "";
		int formula_type = 0;
		FormulaCell cell = null;
		List<FormulaCell>cellList = new ArrayList<FormulaCell>();
		List<TableData>tableDataList = new ArrayList<TableData>();
		for(int i = 0;i<list.size();i++)
		{
			Object[]obj = (Object[])list.get(i);
			if(obj[0]==null)
			{
				continue;
			}
			row_id = (Integer) obj[0];
			field = obj[1].toString();
			value = obj[2].toString();
			formula_type = (Integer) obj[3];
			if(row_id!=pref_row_id)
			{
				for(int j=0;j<tableDataList.size();j++)
				{
					if(tableDataList.get(j).getFormula_type()==1)
					{
						String postFix = Expression.toPostfix(tableDataList.get(j).getValue());
						System.out.println(postFix);
						String formulaValue = Expression.formulaValue(tableDataList,postFix);
						cell = new FormulaCell(pref_row_id,Integer.parseInt(tableDataList.get(j).getField()),formulaValue);
						cellList.add(cell);
					}
				}
			
					
					
				tableDataList = new ArrayList<TableData>();
				TableData  tableData = new TableData();
				tableData.setField(field);
				tableData.setValue(value);
				tableData.setFormula_type(formula_type);
				tableDataList.add(tableData);
				
				pref_row_id = row_id;
				
			}
			else
			{
				TableData  tableData = new TableData();
				tableData.setField(field);
				tableData.setValue(value);
				tableData.setFormula_type(formula_type);
				tableDataList.add(tableData);
			}
			
		}
	
		for(int j=0;j<tableDataList.size();j++)
		{
			if(tableDataList.get(j).getFormula_type()==1)
			{
				String postFix = Expression.toPostfix(tableDataList.get(j).getValue());
				String formulaValue = Expression.formulaValue(tableDataList,postFix);
				cell = new FormulaCell(row_id,Integer.parseInt(tableDataList.get(j).getField()),formulaValue);
				cellList.add(cell);	
			}
		}
		
		for(int i = 0;i<cellList.size();i++)
		{
			String updateFormulaData = "update tbl_prod_data set data_value='"+cellList.get(i).getValue() +"' where row_id="+cellList.get(i).getRow_id()+" and column_id="+cellList.get(i).getColumn_id();
			System.out.println(updateFormulaData);
			Query updateFormulaDataQuery  = em.createNativeQuery(updateFormulaData);
			updateFormulaDataQuery.executeUpdate();
		}
		
		
	}
	
	@Transactional
	public void setFilterType(List<FilterType>filterList)
	{
		String filterColumnId = "";
		for(int i=0;i<filterList.size();i++)
		{
			filterColumnId = filterColumnId + filterList.get(i).getField() + ",";
		}
		if(filterColumnId!="")
		{
			filterColumnId = filterColumnId.substring(0, filterColumnId.length()-1);
		}
		String sql = "select data_type from tbl_prod_column where column_id in ("+filterColumnId+")";
		Query query = em.createNativeQuery(sql);
		List list = query.getResultList();
		for(int i=0;i<list.size();i++)
		{
			String dataType = list.get(i).toString();
			if(dataType.equals("number")||dataType.equals("money"))
			{
				filterList.get(i).setType("number");
			}
			else
			{
				filterList.get(i).setType("text");
			}
			
		}
	}
	
	
	@Transactional
	public JSONObject getTableDataByFilter(List<Integer>columnIdList,List<FilterType>filterList,int pageIndex,int pageCount)
	{
		setFilterType(filterList);
		
		String columnArrayStr = convertToColumnArrayStr(columnIdList);
		String sql = "SELECT a.row_id,b.column_id,a.data_value,a.formula_type FROM tbl_prod_column b left join tbl_prod_data a on b.column_id = a.column_id where b.column_id in("+columnArrayStr+") and row_id in"; 
		String rowIdSpace = "";
		if(filterList.size()>0)
		{
			if(filterList.get(0).getType().equals("text"))
			{
				rowIdSpace = "(select row_id from tbl_prod_data where column_id="+filterList.get(0).getField()+" and data_value like '%"+filterList.get(0).getValue()+"%')";
			}
			else if(filterList.get(0).getType().equals("number"))
			{
				if(filterList.get(0).getOp().equals("equal"))
				{
					rowIdSpace = "(select row_id from tbl_prod_data where column_id="+filterList.get(0).getField()+" and data_value= '"+filterList.get(0).getValue()+"')";
				    
				}
				else if(filterList.get(0).getOp().equals("less"))
				{
					rowIdSpace = "(select row_id from tbl_prod_data where column_id="+filterList.get(0).getField()+" and data_value< '"+filterList.get(0).getValue()+"')";
				}
				else if(filterList.get(0).getOp().equals("greater"))
				{
					rowIdSpace = "(select row_id from tbl_prod_data where column_id="+filterList.get(0).getField()+" and data_value> '"+filterList.get(0).getValue()+"')";
				}
			}
			
		}
		for(int i = 1;i<filterList.size();i++)
		{
			if(filterList.get(i).getType().equals("text"))
			{
				rowIdSpace = "(select row_id from tbl_prod_data where column_id="+filterList.get(i).getField()+" and data_value like '%"+filterList.get(i).getValue()+"%' and row_id in "+rowIdSpace+")";
			}
			else if(filterList.get(i).getType().equals("number"))
			{
				if(filterList.get(i).getOp().equals("equal"))
				{
					rowIdSpace = "(select row_id from tbl_prod_data where column_id="+filterList.get(i).getField()+" and data_value= '"+filterList.get(i).getValue()+"' and row_id in "+rowIdSpace+")";
				}
				else if(filterList.get(i).getOp().equals("less"))
				{
					rowIdSpace = "(select row_id from tbl_prod_data where column_id="+filterList.get(i).getField()+" and data_value< '"+filterList.get(i).getValue()+"' and row_id in "+rowIdSpace+")";
				}
				else if(filterList.get(i).getOp().equals("greater"))
				{
					rowIdSpace = "(select row_id from tbl_prod_data where column_id="+filterList.get(i).getField()+" and data_value> '"+filterList.get(i).getValue()+"' and row_id in "+rowIdSpace+")";
				}
			}
			
		}
		sql =  sql+rowIdSpace+" order by a.row_id,b.column_id";
		System.out.println(sql);
		return getTableData(sql,null,pageIndex,pageCount);
	}
	
	//动态添加列待实现
	@Transactional
	public void insertColumn(String field,String columnName)
	{
		String sql = "update tbl_prod_column  set column_serial= column_serial+1 where column_serial>(select a.column_serial from (SELECT column_serial FROM breaker.tbl_prod_column where column_id="+field+")a)";
		Query query = em.createNativeQuery(sql);
		query.executeUpdate();
		
		String selectSql = "SELECT column_serial FROM breaker.tbl_prod_column where column_id="+field;
		Query selectQuery = em.createNativeQuery(selectSql);
		List<Integer>column_serialList =  selectQuery.getResultList();
		int column_serail = column_serialList.get(0) + 1;
//		String insertSql = "insert into tbl_prod_column(column_name,column_group,)" 
	}

	@Transactional
	public void insertCell(List<ExcelImportTabelCell> cellList) {
		if(cellList.size()!=0)
		{
			String sql = "insert tbl_prod_data(row_id,column_id,data_value,formula_type,data_value_ignore) values";
			for(int i = 0;i<cellList.size();i++)
			{
				sql = sql + "("+cellList.get(i).getRow_id()+","+cellList.get(i).getColumn_id()+",'"+cellList.get(i).getData_value()+"',0,"+"'"+cellList.get(i).getData_value_ignore()+"'),";
			}
			sql = sql.substring(0,sql.length()-1);
			System.out.println(sql);
			Query query = em.createNativeQuery(sql);
			query.executeUpdate();
		}
		
		
	}

	@Transactional
	public int getMaxRowId() {
		String sql = "select Max(row_id) as row_id from tbl_prod_data";
		Query query = em.createNativeQuery(sql);
		List list = query.getResultList();
		System.out.println("maxRowId:"+list.size());
		if(list.get(0)==null)
		{
			return 0;
		}
		else
		{
			return (Integer) list.get(0);
		}
		
	}
	
	@Transactional
	public void setShowColumn(String username, List<Integer> columnIdList) {
		String sql = "update tbl_user_column set showflag=0 where user_name='"+username+"'";
		if(columnIdList.size()>0)
		{
			sql = sql + " and (";
		}
		for(int i = 0;i<columnIdList.size();i++)
		{
			sql = sql +" column_id=" + columnIdList.get(i) + " or";
		}
		sql = sql.substring(0, sql.length()-2);
		sql = sql + ")";
		System.out.println(sql);
		Query query = em.createNativeQuery(sql);
		query.executeUpdate();
	}

	@Transactional
	public void editRows(JSONArray rowsArray) {
		
		for(int i = 0;i<rowsArray.length();i++)
		{
			JSONObject obj = rowsArray.getJSONObject(i);
			String row_id = obj.getString("row_id");
			Iterator it = obj.keys();  
			it.next();
	            while (it.hasNext()) {  
	                String key = (String) it.next();  
	                String value = obj.getString(key);  
	                key = key.substring(1);
	                String updateSql = "update tbl_prod_data set data_value='"+value+"' where column_id="+key+" and row_id="+row_id; 	
	                Query updateQuery = em.createNativeQuery(updateSql);
	                updateQuery.executeUpdate();
	            }  
		}
		
		
	}
	
}
