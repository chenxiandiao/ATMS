package model;

public class ExcelImportTabelCell {

	private int row_id;
	private int column_id;
	private String data_value;
	private int formula_type;
	private String data_value_ignore;
	public int getRow_id() {
		return row_id;
	}
	public void setRow_id(int row_id) {
		this.row_id = row_id;
	}
	public int getColumn_id() {
		return column_id;
	}
	public void setColumn_id(int column_id) {
		this.column_id = column_id;
	}
	public String getData_value() {
		return data_value;
	}
	public void setData_value(String data_value) {
		this.data_value = data_value;
	}
	public int getFormula_type() {
		return formula_type;
	}
	public void setFormula_type(int formula_type) {
		this.formula_type = formula_type;
	}
	public String getData_value_ignore() {
		return data_value_ignore;
	}
	public void setData_value_ignore(String data_value_ignore) {
		this.data_value_ignore = data_value_ignore;
	}
	
}
