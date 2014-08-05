var atms;
(function ($) {
	var _atms = {};
	var _dg = {};
	var loading = false;
	var editingRow;
	var loadColumn = function () {
		
		$.ajax({
			//url : '../json/columns1.json',
			url : '/ATMS/TableController/showTableColumn',
			dataType : 'json',
			success : function (data) {
				populateColumn(data);
				populateFilter(data);
			}
		});
	}
	var populateColumn = function (data) {
		var columns = [],
			column0 = [],
			column1 = [];
		
		for (var index in data[0]) {
			var properties = {};
			if(data[0][index].type == 'text'){
				properties.editor = {
					type : 'text'
				}
			}
			if(data[0][index].type == 'number'){
				properties.editor = {
					type : 'numberbox'
				}
			}
			var a = $.extend({}, {
					field : data[0][index].field,
					title : data[0][index].title,
					rowspan : data[0][index].rowspan || 1,
					colspan : data[0][index].colspan || 1
				}, properties);
			
			column0.push(a);
		}
		for (var index in data[1]) {
			var properties = {};
			if(data[1][index].type == 'text'){
				properties.editor = {
					type : 'text'
				}
			}
			if(data[1][index].type == 'number'){
				properties.editor = {
					type : 'numberbox'
				}
			}
			var a = $.extend({}, {
					field : data[1][index].field,
					title : data[1][index].title,
					rowspan : data[1][index].rowspan || 1,
					colspan : data[1][index].colspan || 1
				}, properties);
			column1.push(a);
		}
		columns.push(column0);
		columns.push(column1);
		_dg.datagrid({
			columns : columns
		});
	}
	var populateFilter = function (data) {
		var mergeColumns = data[0].concat(data[1]),
		filter = [];
		for (var index in mergeColumns) {
			if (mergeColumns[index].type == 'number') {
				var a = $.extend({}, {
						options : {
							precision : 1
						},
						op : ['equal', 'notequal', 'less', 'lessorequal', 'greater', 'greaterorequal']
					}, {
						field : mergeColumns[index].field,
						type : 'numberbox'
					});
				filter.push(a);
			}
			if (mergeColumns[index].type == 'text') {
				var a = $.extend({}, {
						op : ['contains']
					}, {
						field : mergeColumns[index].field,
						type : 'text'
					});
				filter.push(a);
			}
		}
		_dg.datagrid('enableFilter', filter);
	}
	var loadData = function () {}
	var checkConsistent = function (data) {
		var count = 0;
		for (var field in data.rows[0]) {
			count++;
		}
		if (_dg.datagrid('getColumnFields').length === count) {
			console.info('column and data are consistent.');
		}else{
			console.error('column and data are not consistent');
		}
	}
	var refresh = function () {}
	var updateActions = function(index, row){
			_dg.datagrid('updateRow',{
				index: index,
				row: row
			});
		}
	var editrow = function (index) {
		_dg.datagrid('beginEdit', index);
	}
	var deleterow = function (index) {
		$.messager.confirm('Confirm', 'Are you sure?', function (r) {
			if (r) {
				_dg.datagrid('deleteRow', index);
			}
		});
	}
	var saverow = function (index) {
		_dg.datagrid('endEdit', index);
	}
	var cancelrow = function (index) {
		_dg.datagrid('cancelEdit', index);
	}

	_atms.initConfig = function (dom) {
		_dg = dom;
		_dg.datagrid({
			method : 'get',
			nowrap : true,
			loadMsg : 'Processing, please wait ...',
			title : 'ATMS',
			//width : '500',
			striped : true,
			rownumbers : true,
			fitColumns : true,
			singleSelect : false,
			ctrlSelect : true,
			pagination : true,
			pageNumber : 1,
			pageSize : 20,
			pageList : [10, 20, 30, 40, 50],
			pagePosition : 'bottom',
			//url : '../json/data1.json',
			url : '/ATMS/TableController/showTableData',
			remoteFilter : true,
			onBeforeLoad : function (param) {
				if (!loading) {
					loading = true;
					loadColumn();
				}
			},
			onLoadSuccess : function (data) {
				if (loading) {
					loading = false;
					checkConsistent(data);
				}
			},
			onLoadError : function () {
				if (loading) {
					loading = false;
				}
			},
			onBeforeEdit:function(index,row){
				console.info('before edit'+index);
				row.editing = true;
				updateActions(index, row);
			},
			onAfterEdit:function(index,row){
				console.info('after edit'+index);
				row.editing = false;
				updateActions(index, row);
			},
			onCancelEdit:function(index,row){
				console.info('cancel edit'+index);
				row.editing = false;
				updateActions(index, row);
			},
			onDblClickRow : function(index,row){
				console.info('edit'+index);
				editingRow = index;
				editrow(index);
			},
			onClickRow : function(index, row){
				if(index != editingRow){
					saverow(editingRow);
				}
			}
			
			/*loading : function () {
				alert('loading');
			},
			loaded : function () {
				alert('loaded');
			}*/
		});
	}
	_atms.reload = function (param) {
		_dg.datagrid('reload', param);
	}
	_atms.load = function (param) {
		_dg.datagrid('load', param);
	}

	atms = _atms;
})(jQuery);

$(function () {
	var dom = $('#atms_dg');
	atms.initConfig(dom);
	atms.load();
});
