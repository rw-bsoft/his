$package("chis.application.pub.script");

$import("chis.script.BizCombinedModule2");

chis.application.pub.script.DrugImportModule = function(cfg) {
	chis.application.pub.script.DrugImportModule.superclass.constructor.apply(this, [cfg]);
	this.layOutRegion = "north";
	this.width = this.width || 650;
	this.height = this.height || 550;
	this.itemHeight = this.itemHeight || 170;
	this.on("winShow", this.onWinShow, this);
};

Ext.extend(chis.application.pub.script.DrugImportModule, chis.script.BizCombinedModule2, {
			getFirstItem : function() {
				var module = this.midiModules["DrugQueryForm"];
				if (!module) {
					var cls = "chis.application.pub.script.DrugImportForm";
					$import(cls);
					var cfg = {
						isCombined : true,
						autoLoadData : false,
						autoLoadSchema : false,
						entryName : this.formEntryName
					};
					module = eval("new " + cls + "(cfg)");
					module.on("cancel", this.onCancel, this);
					module.on("select", this.onSelect, this);
					this.midiModules["DrugQueryForm"] = module;
				}
				this.form = module;
				var itemPanel = module.initPanel();
				return itemPanel;
			},

			getSecondItem : function() {
				var module = this.midiModules["DrugQueryList"];
				if (!module) {
					var cls = "chis.application.pub.script.DrugImportList";
					$import(cls);
					var cfg = {
						isCombined : true,
						autoLoadSchema : false,
						enableCnd : false,
						autoLoadData : false,
						mutiSelect : this.mutiSelect,
						selectFirst : false,
						entryName : this.listEntryName,
						buttonIndex : this.buttonIndex
					};
					module = eval("new " + cls + "(cfg)");
					module.on("select", this.onRecordSelect, this);
					this.midiModules["DrugQueryList"] = module;
				}
				this.list = module;
				var itemPanel = module.initPanel();
				return itemPanel;
			},

			onCancel : function() {
				this.list.clearSelect();
				var win = this.getWin();
				if (win) {
					win.hide();
				}
			},

			onSelect : function(cnd) {
				this.list.clearSelect();
				this.list.requestData.cnd = cnd;
				this.list.requestData.queryCndsType = this.queryCndsType || "1";
				this.refresh();
			},

			refresh : function() {
				this.list.resetFirstPage();
				this.fireEvent("refresh");
				this.list.loadData();
			},

			onRecordSelect : function(selectedRecord) {
				if (selectedRecord) {
					var mdcUse = this.form.getMdcUseValue();
					this.fireEvent("recordSelected", selectedRecord,mdcUse);
					var win = this.getWin();
					if (win) {
						win.hide();
					}
				}
			},

			getWin : function() {
				var win = this.win;
				var closeAction = "hide";
				if (!win) {
					win = new Ext.Window({
								title : this.title,
								width : this.width || 800,
								height : this.height || 450,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								closeAction : closeAction,
								constrainHeader : true,
								constrain : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								modal : true,
								items : this.initPanel()
							});
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					win.on("show", function() {
								this.win.doLayout();
								this.fireEvent("winShow", this);
							}, this);
					win.on("hide", function() {
								this.fireEvent("close", this);
							}, this);

					this.win = win;
				}
				win.instance = this;
				return win;
			},

			onWinShow : function() {
				if (this.form && this.list) {
					this.form.doReset();
					this.list.store.removeAll();
					this.list.resetFirstPage();
					this.list.clearSelect();
				}
			}
		});