$package("chis.application.sch.script");

$import("chis.script.BizSimpleListView", "chis.application.sch.script.SnailBaseInfomationModule");

chis.application.sch.script.SnailBaseInfomationListView = function(cfg) {
//	this.schema = cfg.entryName;
	chis.application.sch.script.SnailBaseInfomationListView.superclass.constructor.apply(this,
			[cfg]);
	this.removeServiceId = "chis.snailBaseInfoService";
	this.removeAction = "removeSnailBaseInfo";
}

Ext.extend(chis.application.sch.script.SnailBaseInfomationListView, chis.script.BizSimpleListView,
		{
			doCreateInfo : function() {
				var snailModule = this.getSnailModule("create");
			},
			onDblClick : function(grid, index, e) {
				this.doModify();
			},
			doModify : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				var snailBaseInfoId = r.get("snailBaseInfoId");
				var snailModule = this
						.getSnailModule("update", snailBaseInfoId);
				snailModule.loadData(r.data);
			},
			getSnailModule : function(op, snailBaseInfoId) {
				var snailModule = this.midiModules["snailModule"];
				if (!snailModule) {
					var cfg = {
						schema : this.schema,
						snailBaseInfoId : snailBaseInfoId,
						op : op,
						autoLoadSchema:true,
						mainApp:this.mainApp
					};
					snailModule = new chis.application.sch.script.SnailBaseInfomationModule(cfg);
					this.midiModules["snailModule"] = snailModule;
				} else {
					snailModule.op = op;
					if (op == "update") {
						snailModule.snailBaseInfoId = snailBaseInfoId;
						snailModule.refresh();
					}
				}
				snailModule.on("save", this.onSave, this);
				snailModule.on("close", this.active, this);
				var win = snailModule.getWin();
				win.setTitle("查螺灭螺基本情况");
				win.setPosition(250, 100);
				win.show();
				return snailModule;
			}
		})