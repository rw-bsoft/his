$package("chis.application.sch.script");

$import("chis.script.BizSimpleListView");

chis.application.sch.script.SchistospmaManageListView = function(cfg) {
	this.schema = cfg.entryName;
	chis.application.sch.script.SchistospmaManageListView.superclass.constructor.apply(this,
			[cfg]);
	this.removeServiceId = "chis.schistospmaManageService";
	this.removeAction = "removeSchistospmaManage";
}

Ext.extend(chis.application.sch.script.SchistospmaManageListView, chis.script.BizSimpleListView, {
			doCreateDoc : function() {
				var schistospmaManage = this.midiModules["schistospmaManage"];
				if (!schistospmaManage) {
					var cfg = this.loadModuleCfg(this.refModule);
					cfg["mainApp"]=this.mainApp; //网格地址改造的时候，加进去的
					$import(cfg.script);
					schistospmaManage = eval("new " + cfg.script + "(cfg)")
					this.midiModules["schistospmaManage"] = schistospmaManage;
					
					
				}
				schistospmaManage.op="create";
				schistospmaManage.on("save", this.onSave, this);
				schistospmaManage.on("close", this.active, this);
				var win = schistospmaManage.getWin();
				
				win.show();
			},
			onDblClick : function() {
				this.doModify();
			},
			doModify : function() {
				var schistospmaManage = this.midiModules["schistospmaManage"];
				if (!schistospmaManage) {
					var cfg = this.loadModuleCfg(this.refModule);
					cfg["mainApp"]=this.mainApp; //网格地址改造的时候，加进去的
					$import(cfg.script);
					schistospmaManage = eval("new " + cfg.script + "(cfg)")
					this.midiModules["schistospmaManage"] = schistospmaManage;
				}
				var r = this.getSelectedRecord();
				var formData = {};
				if (r) {
					formData = this.castListDataToForm(r.data, this.schema);
				}
				schistospmaManage.op="update";
				schistospmaManage.on("save", this.onSave, this);
				schistospmaManage.on("close", this.active, this);
				schistospmaManage.formData=formData;
				var win = schistospmaManage.getWin();
				win.show();
			}
		})