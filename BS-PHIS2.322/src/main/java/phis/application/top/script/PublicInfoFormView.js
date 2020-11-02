$package("com.bsoft.phis.pub")

$import("com.bsoft.phis.TableForm")

com.bsoft.phis.pub.PublicInfoFormView = function(cfg) {
	cfg.width = 1800;
	// cfg.autoHeight = false
	// cfg.showButtonOnTop = true
	// cfg.autoFieldWidth = false
	// cfg.fldDefaultWidth = 180
	// cfg.actions = [{
	// id : "new",
	// name : "新建"
	// }, {
	// id : "save",
	// name : "保存"
	// }, {
	// id : "cancel",
	// name : "取消",
	// iconCls : "common_cancel"
	// }]
	com.bsoft.phis.pub.superclass.constructor.apply(this, [cfg])
}

Ext.extend(com.bsoft.phis.pub.PublicInfoFormView, com.bsoft.phis.TableForm, {})