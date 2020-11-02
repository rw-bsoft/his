/**
 * 儿童3-6岁以内体格检查整体页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.checkup.threeToSix")
$import("chis.application.cdh.script.checkup.ChildrenCheckupModuleUtil", "chis.script.util.helper.Helper")
chis.application.cdh.script.checkup.threeToSix.ChildrenCheckupThreeToSixModule = function(cfg) {
	this.checkupType = "3"
	chis.application.cdh.script.checkup.threeToSix.ChildrenCheckupThreeToSixModule.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(chis.application.cdh.script.checkup.threeToSix.ChildrenCheckupThreeToSixModule,
		chis.application.cdh.script.checkup.ChildrenCheckupModuleUtil, {

			onLoadModule : function(moduleId, module) {
				chis.application.cdh.script.checkup.threeToSix.ChildrenCheckupThreeToSixModule.superclass.onLoadModule
						.call(this, moduleId, module);
				module.checkupType = this.checkupType;
			},

			loadData : function() {
				var exc = {
					"businessType" : "6",
					"checkupType" : this.checkupType
				};
				Ext.apply(this.exContext.args, exc);
				chis.application.cdh.script.checkup.threeToSix.ChildrenCheckupThreeToSixModule.superclass.loadData
						.call(this);
			},
			groupActionsByTabType : function() {
				var tabAction = [];
				var otherAction = [];
				for (var i = 0; i < this.actions.length; i++) {
					var action = this.actions[i];
					var type = action.type;
					if (type == "tab") {
						if (action.id == "ChildrenCheckupThreeToSixForm"
								&& this.mainApp.exContext.childrenCheckupType == "paper") {
							action.ref = "chis.application.cdh.CDH/CDH/H99-1-1";
						}
						tabAction.push(action);
					} else {
						otherAction.push(action);
					}
				}
				return {
					"tab" : tabAction,
					"other" : otherAction[0]
				}
			}
		})