/**
 * 个人基本信息列表
 * 
 * @author tianj
 */
$package("chis.application.dr.script");

$import("chis.script.BizSimpleListView");

chis.application.dr.script.DRMPIBaseSelect = function(cfg) {
	cfg.actions = [{
				id : "select",
				name : "选择",
				iconCls : "common_select"
			}, {
				id : "close",
				name : "取消",
				iconCls : "common_cancel"
			}];
	cfg.showButtonOnTop = true;
	cfg.disablePagingTbr = true;
	chis.application.dr.script.DRMPIBaseSelect.superclass.constructor.apply(this, [cfg]);
	this.on("hide", this.closeWin, this);
}

Ext.extend(chis.application.dr.script.DRMPIBaseSelect, chis.script.BizSimpleListView,{
	setRecords : function(records) {
		this.store.removeAll();
		this.store.add(records);
	},
	closeWin : function() {
		this.getWin().hide()
	},
	doClose : function() {
		this.fireEvent("hide", this);
	},
	doSelect : function() {
		var record = this.getSelectedRecord();
		if (!record){
			return;
		}
		this.fireEvent("onSelect", record);
		this.getWin().hide();
	},
	onDblClick : function(grid, index, e) {
		this.doSelect();
	}
});
