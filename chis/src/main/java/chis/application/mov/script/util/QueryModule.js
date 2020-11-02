/**
 * 公共查询整体页面
 * 
 * @author : yaozh
 */
$package("chis.application.mov.script.util")
$import("chis.script.BizCombinedModule2")
chis.application.mov.script.util.QueryModule = function(cfg) {
	chis.application.mov.script.util.QueryModule.superclass.constructor.apply(
			this, [cfg]);
	this.layOutRegion = "north"
	this.width = 650
	this.height = 550
	this.itemHeight = 170
	this.on("winShow", this.onWinShow, this);
};
Ext.extend(chis.application.mov.script.util.QueryModule,
		chis.script.BizCombinedModule2, {

			getFirstItem : function() {
				var module = this.midiModules["QueryForm"];
				if (!module) {
					var cls = "chis.application.mov.script.util.QueryForm";
					$import(cls);
					var cfg = {
						isCombined : true,
						autoLoadData : false,
						autoLoadSchema : false,
						colCount : this.selectFormColCount || 2,
						entryName : this.entryName,
						mainApp : this.mainApp
					};
				
					module = eval("new " + cls + "(cfg)");
					module.on("cancel", this.onCancel, this);
					module.on("select", this.onSelect, this);
					this.midiModules["QueryForm"] = module;
				}
				this.form = module;
				var itemPanel = module.initPanel();
				return itemPanel;
			},

			getSecondItem : function() {
				var module = this.midiModules["QueryList"];
				if (!module) {
					var cls = "chis.application.mov.script.util.QueryList";
					$import(cls);
					var cfg = {
						isCombined : true,
						autoLoadSchema : false,
						enableCnd : false,
						autoLoadData : false,
						mutiSelect : this.mutiSelect,
						selectFirst : false,
						entryName : this.entryName,
						buttonIndex : this.buttonIndex
					};
					module = eval("new " + cls + "(cfg)");
					module.on("select", this.onRecordSelect, this);
					this.midiModules["QueryList"] = module;
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
					this.fireEvent("recordSelected", selectedRecord);
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
			keyManageFunc : function(keyCode, keyName) {
				var m = this.form;
				if (m) {
					if (!m.btnAccessKeys[keyCode]) {
						m = this.list;
					}
					if (m.btnAccessKeys) {
						var btn = m.btnAccessKeys[keyCode];
						if (btn && btn.disabled) {
							return;
						}
					}
					m.doAction(m.btnAccessKeys[keyCode]);
				}
			},
			onWinShow : function() {
				if (this.form && this.list) {
					this.form.doReset();
					this.list.store.removeAll();
					this.list.resetFirstPage();
					this.list.clearSelect();
				}
			}

		})