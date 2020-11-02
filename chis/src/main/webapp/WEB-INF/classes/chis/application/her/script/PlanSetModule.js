$package("chis.application.her.script")

$import("chis.script.BizCombinedModule2")

chis.application.her.script.PlanSetModule = function(cfg) {
	cfg.layOutRegion = "north";
	cfg.moduleWidth = Ext.getBody().getWidth()-100;
	cfg.height = Ext.getBody().getHeight()*0.8;
	cfg.width = 980;
	cfg.itemWidth = 900; // ** 第一个Item的宽度
	cfg.itemHeight = 220;
	cfg.itemCollapsible = false;
	cfg.autoLoadData = false;
	cfg.frame = true;
	chis.application.her.script.PlanSetModule.superclass.constructor.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this);
	this.on("loadData", this.onLoadData, this);
}

Ext.extend(chis.application.her.script.PlanSetModule, chis.script.BizCombinedModule2, {
			initPanel : function() {
				var panel = chis.application.her.script.PlanSetModule.superclass.initPanel
						.call(this);
				this.panel = panel;
				this.form = this.midiModules[this.actions[0].id];
				this.form.on("formSave", this.onFormSave, this);
				this.form.on("check", this.onFormCheck, this);
				this.list = this.midiModules[this.actions[1].id];
				this.list.on("planExe", this.onPlanExe, this);
				return panel;
			},

			onPlanExe : function(flag) {
				if (flag) {
					this.form.reSetField(false);
				} 
			},

			onLoadData : function() {
				this.list.refresh();
			},

			onFormCheck : function(key) {
				var flag = false;
				var store = this.list.store;
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);
					if (r.get("executePerson") == key) {
						if (r.get("planNumber") > 0) {
							flag = true
							break;
						}
					}
				}
				return flag;
			},

			loadData : function() {
				chis.application.her.script.PlanSetModule.superclass.loadData.call(this);
				this.list.requestData.setId = this.initDataId;
			},

			getLoadRequest : function() {
				return {
					setId : this.initDataId
				};
			},

			doCreate : function() {
				this.form.initDataId = null;
				this.form.doNew();
				this.form.reSetField(true);
				this.list.clear();
			},

			onFormSave : function(setId) {
				this.initDataId = setId;
				this.fireEvent("save");
				this.loadData();
			},

			onWinShow : function() {
				if (this.op != "create") {
					this.list.requestData.setId = this.exContext[this.entryName].id;
				} else {
					this.list.requestData.setId = null;
				}
			},

			getWin : function() {
				var win = this.win;
				if (!win) {
					win = new Ext.Window({
								title : this.title,
								width : this.moduleWidth,
								height : this.height,
								iconCls : 'icon-form',
								closeAction : 'hide',
								shim : true,
								layout : "form",
								plain : true,
								autoScroll : false,
								minimizable : true,
								maximizable : true,
								shadow : false,
								modal : this.modal,
								constrain : true,
								buttonAlign : 'center',
								items : this.initPanel()
							});

					win.on("show", function(win) {
								win.center();
								this.fireEvent("winShow");
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this);
							}, this);
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this);
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
				}
				win.instance = this;
				this.win = win;
				return win;
			}
		})