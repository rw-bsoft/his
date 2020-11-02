$package("phis.application.cic.script")

$import("phis.script.EditorList")

phis.application.cic.script.ClinicDiagnosisSubEntryList = function(cfg) {
	cfg.disablePagingTbr = true;
	// cfg.showButtonOnTop = true;
	cfg.autoLoadData = false;
	cfg.selectOnFocus = true;
	cfg.showRowNumber = true;
	cfg.height = 180;
	phis.application.cic.script.ClinicDiagnosisSubEntryList.superclass.constructor
			.apply(this, [cfg])
}
var _grid = null;
var _ctx = null;
function addClick() {
	_ctx.doCreate();
	_grid.getView().refresh();// 刷新行号
}
function downClick() {
	dsN = _grid.getStore();
	var index = _grid.getSelectionModel().getSelectedCell()[0];
	var record = dsN.getAt(index);
	//var index = dsN.indexOf(record);
	if (index < dsN.getCount() - 1) {
		dsN.removeAt(index);
		dsN.insert(index + 1, record);
		_grid.getView().refresh();// 刷新行号
		_grid.getSelectionModel().selectRow(index + 1);
	}
}
function upClick() {
	dsN = _grid.getStore();
	var index = _grid.getSelectionModel().getSelectedCell()[0];
	var record = dsN.getAt(index);
	if (index > 0) {
		dsN.removeAt(index);
		dsN.insert(index - 1, record);
		_grid.getView().refresh();
		_grid.getSelectionModel().selectRow(index - 1);
	}
}
function delClick() {
	var index = _grid.getSelectionModel().getSelectedCell()[0];
	dsN = _grid.getStore();
	dsN.removeAt(index);
	_grid.getView().refresh();
}
Ext.extend(phis.application.cic.script.ClinicDiagnosisSubEntryList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.cic.script.ClinicDiagnosisSubEntryList.superclass.initPanel
						.call(this, sc)
				_ctx = this;
				_grid = grid;
				return grid
			},
			onRenderer : function(v, params, data, row, col, store) {
				var retStr = "";
				var count = store.getCount();
				if (row == 0) {
					if (count == 1) {
						retStr = "<img class='x-grid-cell-img' src='images/add.gif' title='新增子诊断' onClick='addClick()'/>";
					} else {
						retStr = "<img class='x-grid-cell-img' src='images/down.png' onClick='downClick()' title='下移'/>";
					}
				} else {
					if (row == count - 1) {
						retStr = "<img class='x-grid-cell-img' src='images/up.png' onClick='upClick()' title='上移'/><img class='x-grid-cell-img' src='images/add.gif' onClick='addClick()'/>";
					} else {
						retStr = "<img class='x-grid-cell-img' src='images/up.png' onClick='upClick()' title='上移'/><img class='x-grid-cell-img' title='新增子诊断' src='images/down.png' onClick='downClick()' title='下移'/>";
					}
				}
				retStr = "<img class='x-grid-cell-img' src='images/del.png' onClick='delClick()' title='删除'/>"+retStr;
				return retStr;
			}
		});