$package("chis.application.sch.script");

$import("chis.script.BizSimpleListView", "chis.application.sch.script.SnailKillInfomationFormView");

chis.application.sch.script.SnailKillInfomationListView = function(cfg) {
	this.initCnd = ["eq", ["$", "a.snailBaseInfoId"],
			["s", this.snailBaseInfoId]];
	cfg.showButtonOnTop = true;
	cfg.buttonIndex = 3
//	this.schema = cfg.entryName;
	chis.application.sch.script.SnailKillInfomationListView.superclass.constructor.apply(this,
			[cfg]);
	this.removeServiceId = "chis.snailBaseInfoService";
	this.removeAction = "removeSnailKillInfo";
};

Ext.extend(chis.application.sch.script.SnailKillInfomationListView, chis.script.BizSimpleListView,
		{
			resetCnd : function() {
				if(this.cndFldCombox){
					this.cndFldCombox.setValue("");
				}
				if(this.cndField){
					this.cndField.setValue("");
				}
			},

			onSave : function(entryName, op, json, rec) {
				if (json.body) {
					this.snailBaseInfoId = json.body.snailBaseInfoId
				}
				this.requestData.cnd = this.initCnd = ["eq",
						["$", "snailBaseInfoId"], ["s", this.snailBaseInfoId]];
				this.refresh()
			},
			doCreateInfo : function() {
				var snailKillInfo = this.getSnailKillInfo("create");
			},
			onDblClick : function(grid, index, e) {
				this.doModify();
			},
			doModify : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				var snailKillInfo = this.getSnailKillInfo("update")
				var formData = {};
				if (r) {
					formData = this.castListDataToForm(r.data, this.schema);
				}
				snailKillInfo.initFormData(formData);

			},
			getSnailKillInfo : function(op) {
				var snailKillInfo = this.midiModules["snailKillInfo"];
				if (!snailKillInfo) {
					var cfg = this.loadModuleCfg("chis.application.sch.SCH/SCH/X0601");
					cfg.autoLoadData = false;
					cfg.autoLoadSchema = true;
					cfg.op = op;
					cfg.snailBaseInfoId = this.snailBaseInfoId;
					snailKillInfo = eval("new " + cfg.script + "(cfg)");
					this.midiModules["snailKillInfo"] = snailKillInfo;
				} else {
					snailKillInfo.op = op;
					snailKillInfo.snailBaseInfoId = this.snailBaseInfoId;
				}
				snailKillInfo.on("save", this.onSave, this);
				snailKillInfo.on("close", this.active, this);
				var win = snailKillInfo.getWin();
				win.setTitle(snailKillInfo.name);
				win.show();
				return snailKillInfo;
			}
		});
