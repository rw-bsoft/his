/**
 * 产时信息整体模块
 * 
 * @author :Liyt
 */
$package("chis.application.mhc.script.delivery")
$import("chis.script.BizTabModule")
chis.application.mhc.script.delivery.DeliveryRecord2Module = function(cfg) {
	this.activateId = 0
	chis.application.mhc.script.delivery.DeliveryRecord2Module.superclass.constructor.apply(
			this, [cfg])
	this.on("loadModule", this.loadModule, this)
}
Ext.extend(chis.application.mhc.script.delivery.DeliveryRecord2Module, chis.script.BizTabModule, {

	loadModule : function(moduleName, module) {
		if (moduleName == this.actions[0].id) {
			module.on("save", this.onSave, this);
			module.on("checkTab", this.checkOtherTabEnable, this);
		}
	},

	loadData : function() {
		this.changeSubItemDisabled(true, this.actions[0].id);
		this.activeModule(0);
	},

	checkOtherTabEnable : function(body) {
		var numberofply = body.numberofply;
		if (numberofply && numberofply != '') {
			this.setTabItemDisabled(1, false);
		} else {
			this.setTabItemDisabled(1, true);
		}
	},

	onSave : function(entryName, op, json, body) {
		this.checkOtherTabEnable(body);
		this.fireEvent("save", entryName, op, json, body);
	}
});