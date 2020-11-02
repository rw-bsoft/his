$package("chis.application.dc.script")
$import("util.Accredit", "app.modules.common", "chis.application.dc.script.VaccinationList",
		"chis.application.dc.script.RabiesRecordForm", "chis.script.BizModule")
chis.application.dc.script.RabiesTabModule = function(cfg) {
	cfg.autoLoadData = false;
	cfg.autoLoadSchema= true;
	chis.application.dc.script.RabiesTabModule.superclass.constructor.apply(this, [cfg]);
}
Ext.extend(chis.application.dc.script.RabiesTabModule, chis.script.BizModule, {
			initPanel : function(sc) {
				var panel = new Ext.Panel({
							border : false,
							frame : false,
							layout : 'anchor',
							width : 900,
							height : 500,
							autoWidth : true,
							items : [{
										layout : "fit",
										border : false,
										frame : false,
										split : true,
										region : 'north',
										width : 900,
										anchor : '100% 50%',
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										frame : false,
										split : true,
										region : 'center',
										width : 900,
										anchor : '100% 50%',
										items : this.getList()
									}]
						});
				return panel;
			},
//			getCfg : function(ref, id) {
//				var cfg = {
//					phrId : this.exContext.ids.phrId,
//					empiId : this.exContext.ids.empiId,
//					autoLoadData : false,
//					showButtonOnTop : true,
//					autoLoadSchema : false,
//					readOnly : this.readOnly,
//					isCombined : true,
//					exContext : this.exContext,
//					mainApp : this.mainApp
//				};
//				var result = util.rmi.miniJsonRequestSync({
//							serviceId : "moduleConfigLocator",
//							id : ref
//						});
//				if (result.code == 200) {
//					Ext.apply(cfg, result.json.body);
//				}
//				if (result.code > 300) {
//					this.processReturnMsg(result.code, result.msg, this.doSave);
//					return;
//				}
//				return cfg
//			},
			getForm : function() {
				var id = this.actions[0].id
//				var cfg = this.getCfg(this.actions[0].ref, id)
				var cfg = this.loadModuleCfg(this.actions[0].ref)
				cfg.phrId = this.exContext.ids.phrId,
				cfg.empiId =this.exContext.ids.empiId
				cfg.autoLoadData = false
				cfg.showButtonOnTop = true
				cfg.autoLoadSchema = false
				cfg.readOnly = this.readOnly
				cfg.isCombined = true
				cfg.exContext =this.exContext,
				cfg.mainApp = this.mainApp
				cfg.autoLoadSchema = true
				var cls = cfg.script
				cfg.exContext = this.exContext
				if (!cls) {
					return
				}
				var form = this.midiModules[id]
				if (!form) {
					form = eval("new " + cls + "(cfg)");
					this.midiModules[id] = form;
				}
				form.on("close", this.onClose, this);
				form.on("add", this.onAdd, this);
				form.on("save", this.onFormSave, this);
				var panel = form.initPanel();
				panel.title = null;
				panel.border = false;
				panel.frame = false;
				return panel;
			},
			onAdd : function() {
				this.fireEvent("add", this);
			},
			onClose : function(rabiesId) {
				this.fireEvent("close", rabiesId);
			},
			getList : function() {
				var id = this.actions[1].id
//				var cfg = this.getCfg(this.actions[1].ref, id)
				var cfg = this.loadModuleCfg(this.actions[1].ref)
				cfg.phrId =this.exContext.ids.phrId,
				cfg.empiId =this.exContext.ids.empiId
				cfg.autoLoadData = false
				cfg.showButtonOnTop = true
				cfg.autoLoadSchema = false
				cfg.readOnly = this.readOnly
				cfg.isCombined = true
				cfg.exContext =this.exContext,
				cfg.mainApp = this.mainApp
				cfg.autoLoadSchema = true
				var cls = cfg.script
				cfg.autoLoadSchema = true
				if (!cls) {
					return;
				}
				var list = this.midiModules[id]
				if (!list) {
					list = eval("new " + cls + "(cfg)");
					this.midiModules[id] = list;
				}
				var panel = list.initPanel();
				panel.title = null;
				return panel;
			},
			doNew : function() {
				this.midiModules["RabiesForm"].doNew();
				this.midiModules["VaccinationList"].exContext.args.rabiesID = "";
				this.midiModules["VaccinationList"].doNew();
			},
			loadData : function() {
				var form = this.midiModules["RabiesForm"];
			
				if (form) {
					form.exContext.args.rabiesId = this.exContext.args.rabiesId;
					form.exContext.ids.empiId = this.exContext.ids.empiId;
					form.exContext.ids.phrId = this.exContext.ids.phrId;
					form.loadData();
				}
				var list = this.midiModules["VaccinationList"];
				if (list) {
					list.exContext.args.rabiesID = this.exContext.args.rabiesId;
					list.exContext.ids.empiId = this.exContext.ids.empiId;
					list.exContext.ids.phrId = this.exContext.ids.phrId;
					list.requestData.cnd = ["eq", ["$", "rabiesId"],
							["s", this.exContext.args.rabiesId]];
					if (list.loadData && list.autoLoadData == false&&this.exContext.args.rabiesId)
						list.loadData();
				}
			},
			loadInitData : function() {
				this.midiModules["RabiesForm"].loadInitData();
				this.midiModules["VaccinationList"].exContext.args.rabiesID = "";
				this.midiModules["VaccinationList"].doNew();
			},
			onFormSave : function(entryName, op, json, data) {
				this.fireEvent("save", entryName, op, json, data);

			},
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width,
								height : 570,
								iconCls : 'icon-form',
								closeAction : 'hide',
								shim : true,
								layout : "fit",
								plain : true,
								minimizable : true,
								maximizable : true,
								constrain : true,
								shadow : false,
								buttonAlign : 'center',
								items : this.initPanel()
							})
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this)
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					this.win = win
				}
				win.instance = this;
				return win;
			}
		});