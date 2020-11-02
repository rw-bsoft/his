$package("chis.application.sch.script");

$import("chis.script.BizSimpleListView", "chis.application.sch.script.SnailFindInfomationFormView");

chis.application.sch.script.SnailFindInfomationListView = function(cfg) {
	cfg.showButtonOnTop = true;
	cfg.buttonIndex = 3
	this.schema = cfg.entryName;
	chis.application.sch.script.SnailFindInfomationListView.superclass.constructor.apply(this,
			[cfg]);
	this.removeServiceId = "chis.snailBaseInfoService";
	this.removeAction = "removeSnailFindInfo";

};

Ext.extend(chis.application.sch.script.SnailFindInfomationListView, chis.script.BizSimpleListView,
		{

			onSave : function(entryName, op, json, rec) {
				if (json.body) {
					this.snailBaseInfoId = json.body.snailBaseInfoId
				}
				this.requestData.cnd=this.initCnd= ["eq", ["$", "snailBaseInfoId"],
						["s", this.snailBaseInfoId]];
				this.refresh()
			},
			resetCnd : function() {
				if(this.cndFldCombox){
					this.cndFldCombox.setValue("");
				}
				if(this.cndField){
					this.cndField.setValue("");
				}
			},
			doCreateInfo : function() {
				var snailFindInfo = this.getSnailFindInfo("create");
			},
			onDblClick : function(grid, index, e) {
				this.doModify();
			},
			doModify : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				var snailFindInfo = this.getSnailFindInfo("update");
				var formData = {};
				if (r) {
					formData = this.castListDataToForm(r.data, this.schema);
				}
				snailFindInfo.initFormData(formData);
			},
			getSnailFindInfo : function(op) {
				var snailFindInfo = this.midiModules["snailFindInfo"];
				if (!snailFindInfo) {
					var cfg = this.loadModuleCfg("chis.application.sch.SCH/SCH/X0501");
					cfg.op = op;
					cfg.snailBaseInfoId = this.snailBaseInfoId;
					cfg.autoLoadData=false;
					snailFindInfo = eval("new " + cfg.script + "(cfg)");
					this.midiModules["snailFindInfo"] = snailFindInfo;
				} else {
					snailFindInfo.op = op;
					snailFindInfo.snailBaseInfoId = this.snailBaseInfoId;
				}
				snailFindInfo.on("save", this.onSave, this);
				snailFindInfo.on("close", this.active, this);
				var win = snailFindInfo.getWin();
				win.setTitle(snailFindInfo.name);
				win.show();
				return snailFindInfo;
			}
		});
