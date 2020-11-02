/**
 * 健康处方引入公用类（工具）,大字段（blob）引入，共用范围有限
 */
$package("chis.application.her.script.util");

$import("chis.script.BizCombinedModule2");

chis.application.her.script.util.HERQueryModule = function(cfg) {
	chis.application.her.script.util.HERQueryModule.superclass.constructor
			.apply(this, [cfg]);
	this.layOutRegion = "north";
	this.width = this.width || 650;
	this.height = this.height || 500;
	this.itemHeight = this.itemHeight || 92;
	this.on("winShow", this.onWinShow, this);
};

Ext.extend(chis.application.her.script.util.HERQueryModule,
		chis.script.BizCombinedModule2, {
			getFirstItem : function() {
				var module = this.midiModules["QueryForm"];
				if (!module) {
					var cls = "chis.application.her.script.util.HERQueryForm";
					$import(cls);
					var cfg = {
						isCombined : true,
						autoLoadData : false,
						autoLoadSchema : false,
						entryName : this.entryName
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
					var cls = "chis.application.her.script.util.HERQueryList";
					$import(cls);
					var cfg = {
						isCombined : true,
						autoLoadSchema : false,
						enableCnd : false,
						autoLoadData : false,
						mutiSelect : this.mutiSelect,
						selectFirst : false,
						entryName : this.entryName,
						buttonIndex : this.buttonIndex,
						listServiceId : "chis.healthRecipelManageService",
						listAction : "queryHealthRecipel"
					};
					module = eval("new " + cls + "(cfg)");
					module.on("select", this.onRecordSelect, this);
					this.midiModules["QueryList"] = module;
				}
				this.list = module;
				var itemPanel = module.initPanel();
				return itemPanel;
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

			onWinShow : function() {
				if (this.form && this.list) {
					this.form.doReset();
					this.list.store.removeAll();
					this.list.resetFirstPage();
					this.list.clearSelect();
				}
			}
		});