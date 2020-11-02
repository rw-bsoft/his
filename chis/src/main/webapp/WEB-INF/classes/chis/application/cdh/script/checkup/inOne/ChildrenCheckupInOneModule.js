/**
 * 儿童1岁以内体格检查整体页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.checkup.inOne")
$import("chis.application.cdh.script.checkup.ChildrenCheckupModuleUtil",
		"chis.script.util.helper.Helper")
chis.application.cdh.script.checkup.inOne.ChildrenCheckupInOneModule = function(
		cfg) {
	this.checkupType = "1"
	this.oneCase = "1";
	this.twoCase = "3";
	this.threeCase = "6";
	this.fourCase = "9";
	chis.application.cdh.script.checkup.inOne.ChildrenCheckupInOneModule.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(
		chis.application.cdh.script.checkup.inOne.ChildrenCheckupInOneModule,
		chis.application.cdh.script.checkup.ChildrenCheckupModuleUtil, {

			onLoadModule : function(moduleId, module) {
				chis.application.cdh.script.checkup.inOne.ChildrenCheckupInOneModule.superclass.onLoadModule
						.call(this, moduleId, module);
				module.checkupType = this.checkupType;
			},

			loadData : function() {
				var exc = {
					"businessType" : "6",
					"checkupType" : this.checkupType
				};
				Ext.apply(this.exContext.args, exc);
				chis.application.cdh.script.checkup.inOne.ChildrenCheckupInOneModule.superclass.loadData
						.call(this);
			},
			changeField : function() {
				if (this.mainApp.exContext.childrenCheckupType == "paper") {
					return;
				}
				var checkupStage = this.exContext.args[this.checkupType
						+ "_param"].checkupStage;
				var form = this.midiModules["ChildrenCheckupInOneForm"].form
						.getForm();
				var kyglbtz = form.findField("kyglbtz");
				var kyglbtzStore = kyglbtz.getStore();
				kyglbtzStore.removeAll();
				if (checkupStage == this.oneCase
						|| checkupStage == this.twoCase) {
					kyglbtzStore.add(new Ext.data.Record({
								key : "1",
								text : "无"
							}));
					kyglbtzStore.add(new Ext.data.Record({
								key : "2",
								text : "颅骨软化"
							}));
					kyglbtzStore.add(new Ext.data.Record({
								key : "3",
								text : "方颅"
							}));
					kyglbtzStore.add(new Ext.data.Record({
								key : "4",
								text : "枕秃"
							}));
				} else {
					kyglbtzStore.add(new Ext.data.Record({
								key : "1",
								text : "肋串珠"
							}));
					kyglbtzStore.add(new Ext.data.Record({
								key : "2",
								text : "肋外翻"
							}));
					kyglbtzStore.add(new Ext.data.Record({
								key : "3",
								text : "肋软骨沟"
							}));
					kyglbtzStore.add(new Ext.data.Record({
								key : "4",
								text : "鸡胸"
							}));
					kyglbtzStore.add(new Ext.data.Record({
								key : "5",
								text : "手镯征"
							}));
				}
			},
			groupActionsByTabType : function() {
				var tabAction = [];
				var otherAction = [];
				for (var i = 0; i < this.actions.length; i++) {
					var action = this.actions[i];
					var type = action.type;
					if (type == "tab") {
						if (action.id == "ChildrenCheckupInOneForm"
								&& this.mainApp.exContext.childrenCheckupType == "paper") {
							action.ref = "chis.application.cdh.CDH/CDH/H97-1-1";
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