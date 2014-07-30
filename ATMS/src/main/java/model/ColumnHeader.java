package model;


public class ColumnHeader{

	/**
	 * 
	 */
	private String field;
	private String title;
	private int rowspan;
	private int colspan;
	private boolean sortable;
	private boolean showTag;
    private String type;
	/*public ColumnHeader(String field, String title, int rowspan,
			int colspan, boolean sortable,boolean showTag) {
		super();
		this.field = field;
		this.title = title;
//		this.rowspan = rowspan;
//		this.colspan = colspan;
//		this.sortable = sortable;
//		this.showTag = showTag;
	}*/
	
	public void initData(String field, String title, int rowspan,
			int colspan, boolean sortable,boolean showTag) {
		this.field = field;
		this.title = title;
		this.rowspan = rowspan;
		this.colspan = colspan;
		this.sortable = sortable;
		this.showTag = showTag;
	}
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getRowspan() {
		return rowspan;
	}
	public void setRowspan(int rowspan) {
		this.rowspan = rowspan;
	}
	public int getColspan() {
		return colspan;
	}
	public void setColspan(int colspan) {
		this.colspan = colspan;
	}
	public boolean isSortable() {
		return sortable;
	}
	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}
	public boolean isShowTag() {
		return showTag;
	}
	public void setShowTag(boolean showTag) {
		this.showTag = showTag;
	}

	@Override
	public String toString() {
		return "ColumnHeader [field=" + field + ", title=" + title
				+ ", rowspan=" + rowspan + ", colspan=" + colspan
				+ ", sortable=" + sortable + ", showTag=" + showTag + "]";
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	

	
	
	
	
}
