package dbmodel;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
@Entity
@Table(name="tbl_prod_column")
public class TableColumn {

	@Id
	@Column(name="column_id")
	private int column_id;
	
	@Column(name="column_name")
	private String column_name;
	
	@Column(name="column_group")
	private String column_group;
	
	@Column(name="data_type")
	private String data_type;
	
	@Column(name="column_serial")
	private int column_serial;

	public int getColumn_id() {
		return column_id;
	}

	public void setColumn_id(int column_id) {
		this.column_id = column_id;
	}

	public String getColumn_name() {
		return column_name;
	}

	public void setColumn_name(String column_name) {
		this.column_name = column_name;
	}

	public String getColumn_group() {
		return column_group;
	}

	public void setColumn_group(String column_group) {
		this.column_group = column_group;
	}

	public String getData_type() {
		return data_type;
	}

	public void setData_type(String data_type) {
		this.data_type = data_type;
	}

	public int getColumn_serial() {
		return column_serial;
	}

	public void setColumn_serial(int column_serial) {
		this.column_serial = column_serial;
	}

	
	
	
}
