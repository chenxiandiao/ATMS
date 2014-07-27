package dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import model.ColumnHeader;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class TableDaoImpl{
	@PersistenceContext
	private EntityManager em;
	
	@Transactional
	public List<List<ColumnHeader>> showTableHeader()
	{
		String sql = "select a.column_id,a.column_name,a.column_group,b.cnt from tbl_prod_column a left join (select column_id,count(*) as cnt from tbl_prod_column group by column_group) b on a.column_id=b.column_id where a.column_id like '%'";
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
				String field ="field"+obj[0];
				firstColumnList.add(new ColumnHeader(field,(String) obj[1],2,1,true));
			}
			else
			{
				
				if(obj[3]!=null&&obj[3]!="")
				{
					String field ="field"+obj[0];
					firstColumnList.add(new ColumnHeader(field,(String) obj[2],1, 6,true));
					secondColumnList.add(new ColumnHeader(field,(String)obj[1],1,1,true));
				}
				else
				{
					String field ="field"+obj[0];
					secondColumnList.add(new ColumnHeader(field,(String)obj[1],1,1,true));
				}
			}
		}
		data.add(firstColumnList);
		data.add(secondColumnList);
		return data;
	}
	
	@Transactional
	public JSONObject showTableData()
	{
		JSONObject tableData = new JSONObject();
		String sql = "SELECT a.row_id,b.column_id,a.data_value FROM tbl_prod_column b left join tbl_prod_data a on b.column_id = a.column_id where b.column_id like '%' order by a.row_id,b.column_id ";
		Query query = em.createNativeQuery(sql);
		List list = query.getResultList();
		int row_id = 0;
		int pref_row_id = -1;
		String field = "field";
		String value = "";
		JSONObject row = null;
		JSONArray rowsList = new JSONArray();
		int rowCnt = 0;
		for(int i = 0;i<list.size();i++)
		{
			Object[]obj = (Object[])list.get(i);
			row_id = (Integer) obj[0];
			field = "field" + obj[1];
			value = (String) obj[2];
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
	
}
