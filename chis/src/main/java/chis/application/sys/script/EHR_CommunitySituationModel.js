$package("chis.application.conf.script.admin")
$import("util.Accredit", "chis.script.app.modules.combined.TabModule")
chis.application.conf.script.admin.EHR_CommunitySituationModel = function(cfg) {
	chis.application.conf.script.admin.EHR_CommunitySituationModel.superclass.constructor.apply(this, [cfg])
}
Ext.extend(chis.application.conf.script.admin.EHR_CommunitySituationModel, chis.script.app.modules.combined.TabModule, {
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
								frame : true,
								title : ac.title,
								exCfg : ac,
								name : ac.id
							})
				}
				var tab = new Ext.TabPanel({
							title : " ",
							border : false,
							// width : 800,
							// height : 435,
							activeTab : 0,
							deferredRender : false,
							frame : false,
							//autoHeight : true,
							defaults : {
								border : false
								//,autoHeight : true
								//,autoWidth : true
							},
							items : tabItems
						})
				tab.on("tabchange", this.onTabChange, this);
				if (!this.activateId)
					tab.activate(0)
				this.tab = tab
				return tab;
			},
			onTabChange : function(tabPanel, newTab, curTab) {
				this.activateId = newTab.name
				if (newTab.__inited) {
					var module = this.midiModules[newTab.name]
					module.sexCode = this.sexCode;
					module.empiId = this.empiId
					module.premaritalId = this.premaritalId;
					module.loadData();
					return;
				}
				var exCfg = newTab.exCfg
				var cfg = {
					showButtonOnTop : true,
					autoLoadSchema : false,
					isCombined : true,
					readOnly : this.readOnly,
					mainApp : this.mainApp
				}
				Ext.apply(cfg, exCfg);
				cfg.exContext = {};
				var ref = exCfg.ref
				if (ref) {
					var result = util.rmi.miniJsonRequestSync({
								serviceId : "moduleConfigLocator",
								id : ref
							})
					if (result.code == 200) {
						Ext.apply(cfg, result.json.body)
					}
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
							m.sexCode = this.sexCode;
							m.premaritalId = this.premaritalId;
							m.on("save", this.onFormSave, this)
							m.on("formSave", function(){
								this.fireEvent("moduleSave")
							}, this)
							m.on("setEmpiId", function(empiId) {
										this.empiId = empiId;
									}, this);
							m.on("confirm", function() {
										this.fireEvent("confirm");
									}, this);
							m.on("syn", function(column, value, sexCode) {
										this.fireEvent("syn", column, value,
												sexCode);
									}, this);
							m.on("checkCard", function(type, cardNo, sexCode) {
										this.fireEvent("checkCard", type, cardNo,
												sexCode);
									}, this);
							m.on("chgabsent", function(absent) {
												this.absent = absent;
												var premaritalId;
												var jobtitleId = this.mainApp.jobtitleId;
												var actSize = this.actions.length;
												if (this.premaritalId) {
														premaritalId = this.premaritalId;
													} else if (!this.fromList) {
														premaritalId = this["PMC_PremaritalRecord.premaritalId"]
													}
												for (var i = 0; i < actSize; i++) {
													if (i > 0) {
														var tab = this.tab.items.itemAt(i);
														if (!premaritalId||absent == "1")
															tab.disable()
														else if((jobtitleId=="17"&&this.sexCode=="1")||
																(jobtitleId=="18"&&this.sexCode=="2")||
																jobtitleId=="16"||jobtitleId=="system")
															tab.enable()
													}
												}
									}, this);
							m.on("chgStatus", function(status) {
												this.status = status;
												var premaritalId;
												var actSize = this.actions.length;
												if (this.premaritalId) {
														premaritalId = this.premaritalId;
													} else if (!this.fromList) {
														premaritalId = this["PMC_PremaritalRecord.premaritalId"]
													}
												for (var i = 0; i < actSize; i++) {
													if (i > 0) {
														var tab = this.tab.items.itemAt(i);
														if (!premaritalId||status == "1")
															tab.disable()
														else if(status == "2")
															tab.enable()
													}
												}
									}, this);			
							var p = m.initPanel();
							newTab.add(p);
							newTab.__inited = true;
							this.tab.doLayout();
							m.loadData();
						}, this])
			}
});