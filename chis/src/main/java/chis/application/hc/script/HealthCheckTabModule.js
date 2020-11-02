$package("chis.application.hc.script")
$import("util.Accredit", "app.modules.common", "app.modules.combined.TabModule")
chis.application.hc.script.HealthCheckTabModule = function(cfg) {
	cfg.colCount = 4;
	cfg.fldDefaultWidth = 150
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	Ext.apply(this, app.modules.common)
	chis.application.hc.script.HealthCheckTabModule.superclass.constructor.apply(this,
			[cfg])
	this.empiId = cfg.empiId;
	this.phrId = cfg.phrId;
}
Ext.extend(chis.application.hc.script.HealthCheckTabModule, app.modules.combined.TabModule,
		{
			initPanel : function() {
				if (this.tab) {
					return this.tab;
				}
				var tabItems = []
				var actions = this.actions
				for (var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					tabItems.push({
								layout : "fit",
								title : ac.title,
								exCfg : ac,
								name : ac.id
							})
				}
				var tab = new Ext.TabPanel({
							title : " ",
							border : false,
							width : this.width,
							activeTab : 0,
							frame : true,
							autoHeight : true,
							defaults : {
								border : false,
								autoHeight : true,
								autoWidth : true
							},
							items : tabItems
						})
				tab.on("tabchange", this.onTabChange, this);
				if (!this.activateId)
					tab.activate(0)
				this.tab = tab
				return tab;
			},
			// onBeforeload : function(entry,exContext) {
			// exContext.healthCheck = this.healthCheck
			// },
			onTabChange : function(tabPanel, newTab, curTab) {
				this.activateId = newTab.name
				if (newTab.__inited) {
					var module = this.midiModules[newTab.name]
					// module.loadData();
					return;
				}
				var exCfg = newTab.exCfg
				var cfg = {
					showButtonOnTop : true,
					autoLoadSchema : false,
					autoLoadData : false,
					isCombined : true,
					phrId : this.phrId,
					empiId : this.empiId,
					readOnly : this.readOnly,
					mainApp : this.mainApp
				}
				Ext.apply(cfg, exCfg);
				cfg.exContext = {};
				var ref = exCfg.ref
				if (ref) {
					var moduleCfg = this.mainApp.taskManager.loadModuleCfg(ref);
					Ext.apply(cfg, moduleCfg.json.body);
					Ext.apply(cfg, moduleCfg.json.body.properties);
				}
				var cls = cfg.script
				if (!cls) {
					return;
				}

				if (!this.fireEvent("beforeload", cfg)) {
					return;
				}
				$require(cls, [function() {
									var m = eval("new " + cls + "(cfg)")
									this.midiModules[newTab.name] = m;
									var p = m.initPanel();
									m.on("save", this.onSave, this)
									m.on("doCreate", this.onCreate, this)
									m.on("ready", this.ready, this)
									// m.on("beforeSave",
									// this.onBeforeSave,this)
									newTab.add(p);
									newTab.__inited = true
									m.inited = false;
									m.healthCheck = this.healthCheck;
									m.checkDate = this.checkDate;
									m.createUser = this.createUser;
									m.createUser_text = this.createUser_text;
									m.loadData();
								}, this])
			},
			onCreate : function() {
				this.fireEvent("doCreate", this)
			},
			ready : function() {
				this.fireEvent("ready");
			},
			// onBeforeSave :function(entry,op,data){
			// if(entry=="HC_HealthCheck"){
			//					
			// Ext.apply(data,this.getData());
			// }
			// },
			// getData:function(){
			// m = this.midiModules[this.actions[1].id]
			// var form = m.form.getForm()
			// if (!m.validate()) {
			// return
			// }
			// if (!m.schema) {
			// return
			// }
			// var values = {};
			// var items = m.schema.items
			// Ext.apply(m.data, m.exContext)
			//
			// if (items) {
			// var n = items.length
			// for (var i = 0; i < n; i++) {
			// var it = items[i]
			// var v = m.data[it.id] // ** modify by yzh 2010-08-04
			// if (v == undefined) {
			// v = it.defaultValue
			// }
			// if (v != null && typeof v == "object") {
			// v = v.key
			// }
			// var f = form.findField(it.id)
			// if (f) {
			// v = f.getValue()
			// // add by huangpf
			// if (f.getXType() == "treeField") {
			// var rawVal = f.getRawValue();
			// if (rawVal == null || rawVal == "")
			// v = "";
			// }
			// // end
			// }
			//
			// if (v == null || v === "") {
			// if (!it.pkey && it["not-null"] && !it.ref) {
			// Ext.Msg.alert("提示", it.alias + "不能为空")
			// return;
			// }
			// }
			// values[it.id] = v;
			// }
			// }
			// return values;
			// },
			loadData : function() {
				for (var i = 0; i < this.actions.length; i++) {
					var module = this.midiModules[this.actions[i].id]
					if (module) {
						module.inited = false;
						module.readOnly = this.readOnly;
						module.empiId = this.empiId;
						module.phrId = this.phrId;
						module.healthCheck = this.healthCheck;
						module.checkDate = this.checkDate;
						module.createUser = this.createUser;
						module.createUser_text = this.createUser_text;
						module.loadData();
					}
				}
				// var actModule = this.midiModules[this.activateId]
				// if (actModule)
				// actModule.loadData();
			},
			doNew : function() {
				for (var i = 0; i < this.actions.length; i++) {
					var module = this.midiModules[this.actions[i].id]
					if (module) {
						module.inited = false;
						module.readOnly = this.readOnly;
						module.empiId = this.empiId
						module.phrId = this.phrId
						module.healthCheck = "";
						if (this.actions[i].id == "action5"
								|| this.actions[i].id == "action7")
							module.loadData()
						else {
							module.doNew();
						}
					}
				}
			},
			onSave : function(entryName, op, json, data) {
				this.fireEvent("save", entryName, op, json, data, this);
			}
		});