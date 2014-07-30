package dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.ColumnHeader;
import model.FilterType;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class TableDaoImpl{
	@PersistenceContext
	private EntityManager em;
	private String defaultColumnArrayStr = "51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69";
	@Transactional
	public List<List<ColumnHeader>> showTableHeader(List<Integer>columnIdList)
	{
		String columnArrayStr = convertToColumnArrayStr(columnIdList);
		
		String sql = "select a.column_id,a.column_name,a.column_group,b.cnt from tbl_prod_column a left join (select column_id,count(*) as cnt from tbl_prod_column group by column_group) b on a.column_id=b.column_id where a.column_id in ("+columnArrayStr+")";
//		String sql = "select a.column_id,a.column_name,a.column_group,b.cnt from tbl_prod_column a left join (select column_id,count(*) as cnt from tbl_prod_column group by column_group) b on a.column_id=b.column_id where a.column_id like '%'";
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
				columnHeader.initData(field,(String) obj[1],2,1,true,true);
				firstColumnList.add(columnHeader);
				
			}
			else
			{
				
				if(obj[3]!=null&&obj[3]!="")
				{
					String field = obj[0].toString();
					ColumnHeader fcolumnHeader = new ColumnHeader();
					fcolumnHeader.initData(null,(String) obj[2],1, 6,true,true);
					firstColumnList.add(fcolumnHeader);
					ColumnHeader scolumnHeader = new ColumnHeader();
					scolumnHeader.initData(field,(String)obj[1],1,1,true,true);
					secondColumnList.add(scolumnHeader);
				}
				else
				{
					String field = obj[0].toString();
					ColumnHeader columnHeader = new ColumnHeader();
					columnHeader.initData(field,(String)obj[1],1,1,true,true);
					secondColumnList.add(columnHeader);
				}
			}
		}
		data.add(firstColumnList);
		data.add(secondColumnList);
		return data;
	}
	
	@Transactional
	public JSONObject showTableData(List<Integer>columnIdList)
	{
		String columnArrayStr = convertToColumnArrayStr(columnIdList);
		
		//String sql = "SELECT a.row_id,b.column_id,a.data_value FROM tbl_prod_column b left join tbl_prod_data a on b.column_id = a.column_id where b.column_id like '%' order by a.row_id,b.column_id ";
		String sql = "SELECT a.row_id,b.column_id,a.data_value FROM tbl_prod_column b left join tbl_prod_data a on b.column_id = a.column_id where b.column_id in("+columnArrayStr+") order by a.row_id,b.column_id ";
		
		return getTableData(sql);
	}
	private JSONObject getTableData(String sql)
	{	
		Query query = em.createNativeQuery(sql);
		List list = query.getResultList();
		JSONObject tableData = new JSONObject();
		int row_id = 0;
		int pref_row_id = -1;
		String field = "";
		String value = "";
		JSONObject row = null;
		JSONArray rowsList = new JSONArray();
		int rowCnt = 0;
		for(int i = 0;i<list.size();i++)
		{
			Object[]obj = (Object[])list.get(i);
			row_id = (Integer) obj[0];
			field = obj[1].toString();
			value = obj[2].toString();
			if(row_id!=pref_row_id)
			{
				if(row!=null)
				{
					rowsList.put(row);
					rowCnt++;
					row = new JSONObject();
					row.put(field, value);
				}
				else
				{
					row = new JSONObject();
					row.put(field, value);
				}
				pref_row_id = row_id;
				
			}
			else
			{
				if(row!=null)
				{
					row.put(field, value);
				}
			}
			
		}
		tableData.put("rows", rowsList);
		tableData.put("count", rowCnt);
		return tableData;
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
		String sql = "SELECT column_serial FROM tbl_user_column t where user_name='"+username+"'";
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
	public JSONObject getTableDataByNumber(List<Integer>columnIdList,String searchKey){
		String columnArrayStr = convertToColumnArrayStr(columnIdList);
		String sql = "SELECT a.row_id,b.column_id,a.data_value FROM tbl_prod_column b left join tbl_prod_data a on b.column_id = a.column_id where b.column_id in("+columnArrayStr+") and row_id in (select distinct(row_id) from tbl_prod_data where column_id in("+defaultColumnArrayStr+") and data_value_ignore like '%"+searchKey+"%') order by a.row_id,b.column_id";
		return getTableData(sql);
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
	public void setFormula(int columnId,int categoryId,String formula){
		String sql = "update tbl_formula set formula='"+formula+"' where column_id="+columnId+" and category_id="+categoryId;
		System.out.println(sql);
		Query query = em.createNativeQuery(sql);
		query.executeUpdate();
	}
	
	@Transactional
	public JSONObject getTableDataByFilter(List<Integer>columnIdList,List<FilterType>filterList)
	{
		String columnArrayStr = convertToColumnArrayStr(columnIdList);
		String sql = "SELECT a.row_id,b.column_id,a.data_value FROM tbl_prod_column b left join tbl_prod_data a on b.column_id = a.column_id where b.column_id in("+columnArrayStr+") and row_id in (select distinct(row_id) from tbl_prod_data where"; 
		
		if(filterList.size()>0)
		{
			if(filterList.get(0).getType().equals("text"))
			{
				sql = sql + " column_id="+filterList.get(0).getColumnId()+" and data_value_ignore like '%"+filterList.get(0).getValue()+"%'";
			}
			else if(filterList.get(0).getType().equals("number"))
			{
				if(filterList.get(0).getOperation().equals("equal"))
				{
					sql = sql + " column_id="+filterList.get(0).getColumnId()+" and data_value_ignore = '"+filterList.get(0).getValue()+"'";
				}
				else if(filterList.get(0).getOperation().equals("smaller"))
				{
					sql = sql + " column_id="+filterList.get(0).getColumnId()+" and data_value_ignore < '"+filterList.get(0).getValue()+"'";
				}
				else if(filterList.get(0).getOperation().equals("larger"))
				{
					sql = sql + " column_id="+filterList.get(0).getColumnId()+" and data_value_ignore > '"+filterList.get(0).getValue()+"'";
				}
			}
			
		}
		for(int i = 1;i<filterList.size();i++)
		{
			if(filterList.get(i).getType().equals("text"))
			{
				sql = sql + " and column_id="+filterList.get(i).getColumnId()+" and data_value_ignore like '%"+filterList.get(0).getValue()+"%'";
			}
			else if(filterList.get(i).getType().equals("number"))
			{
				if(filterList.get(i).getOperation().equals("equal"))
				{
					sql = sql + " and column_id="+filterList.get(i).getColumnId()+" and data_value_ignore = '"+filterList.get(0).getValue()+"'";
				}
				else if(filterList.get(i).getOperation().equals("smaller"))
				{
					sql = sql + " and column_id="+filterList.get(i).getColumnId()+" and data_value_ignore < '"+filterList.get(0).getValue()+"'";
				}
				else if(filterList.get(i).getOperation().equals("larger"))
				{
					sql = sql + " and column_id="+filterList.get(i).getColumnId()+" and data_value_ignore > '"+filterList.get(0).getValue()+"'";
				}
			}
			
		}
		sql = sql+" ) order by a.row_id,b.column_id";
		System.out.println(sql);
		return getTableData(sql);
	}
	
	
}
