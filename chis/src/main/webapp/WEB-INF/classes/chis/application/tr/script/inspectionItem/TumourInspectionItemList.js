$package("chis.application.tr.script.inspectionItem");

$import("chis.script.BizSimpleListView");

chis.application.tr.script.inspectionItem.TumourInspectionItemList = function(
		cfg) {
	chis.application.tr.script.inspectionItem.TumourInspectionItemList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.tr.script.inspectionItem.TumourInspectionItemList,
		chis.script.BizSimpleListView, {
			doCreateTII : function() {
				this.showFormModule(null);
			},
			onDblClick : function() {
				this.doModify();
			},
			doModify : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				var itemId = r.get("itemId");
				this.showFormModule(itemId);
			},
			showFormModule : function(itemId) {
				var refModule = this.refChisForm;
				var domain = "chis";
				if (this.mainApp.phisActive) {
					refModule = this.refPhisForm;
					domain = "phis";
				}
				var module = this.createSimpleModule("TII_" + domain + "_Fomr",
						refModule);
				module.initDataId = itemId;
				module.on("beforeSave", this.onModuleBeforeSave, this);
				module.on("save", this.afterSave, this);
				this.showWin(module);
				 module.loadData();
			},
			onModuleBeforeSave : function(entryName, op, saveData) {
				var vr = true;
				if (op == "create") {
					var definiteItemName = saveData.definiteItemName;
					var itemType = saveData.itemType;
					var len = this.store.getCount();
					for (var i = 0; i < len; i++) {
						var r = this.store.getAt(i);
						var curDIN = r.get("definiteItemName");
						var curIT = r.get("itemType");
						if (curDIN == definiteItemName && curIT == itemType) {
							vr = false;
							Ext.Msg.alert("提示", "该项目已经存在在该项目类别中！");
							break;
						}
					}
				}else{
					var definiteItemName = saveData.definiteItemName;
					var itemType = saveData.itemType;
					var itemId = saveData.itemId;
					var len = this.store.getCount();
					for (var i = 0; i < len; i++) {
						var r = this.store.getAt(i);
						var curDIN = r.get("definiteItemName");
						var curIT = r.get("itemType");
						var curItemId = r.get("itemId");
						if (curDIN == definiteItemName && curIT == itemType && curItemId != itemId) {
							vr = false;
							Ext.Msg.alert("提示", "该项目已经存在在该项目类别中！");
							break;
						}
					}
				}
				return vr;
			},
			afterSave : function(entryName, op, json, data){
				this.refresh();
			}
		});