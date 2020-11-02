$package("chis.application.fhr.script")

$import("chis.script.BizSimpleListView","chis.script.EHRView")

chis.application.fhr.script.PremaritalCheckFemaleListView = function(cfg) {
	this.initCnd = ["or",["eq", ["$", "a.status"], ["s", "0"]],["eq", ["$", "a.status"], ["s", "2"]]]
	this.entryName = "chis.application.fhr.schemas.PER_XCQYFW"
	chis.application.fhr.script.PremaritalCheckFemaleListView.superclass.constructor
			.apply(this, [cfg]);
    this.serviceId="chis.premaritalCheckXcqyfwService";
    this.serviceAction="listPremaritalCheckFemale";
}

Ext.extend(chis.application.fhr.script.PremaritalCheckFemaleListView,
		chis.script.BizSimpleListView, {
			radioChanged : function(r) {
				var status = r.getValue()
				var navCnd = this.navCnd
				var queryCnd = this.queryCnd
				var statusCnd = ['eq', ['$', 'a.status'], ['s', status]]
				this.initCnd = statusCnd;
				var cnd = statusCnd;
				if (navCnd || queryCnd) {
					cnd = ['and', cnd];
					if (navCnd) {
						cnd.push(navCnd)
					}
					if (queryCnd) {
						cnd.push(queryCnd)
					}
				}
				this.requestData.cnd = cnd
				this.requestData.pageNo = 1
				this.refresh()
			},

			doCheck : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				Ext.Msg.show({
					title : '确认注销记录[' + r.id + ']',
					msg : r.get("personName") + '的档案将注销，是否确定要注销该档案?',
					modal : true,
					width : 300,
					buttons : {"yes":"确认注销","no":"恢复","cancel":"取消"},//Ext.MessageBox.YESNOCANCEL,/*���ӻָ���ť*/
					multiline : false,
					fn : function(btn, text) {
						if (btn == "yes") {
							Ext.Msg.show({
										title : '注销核实[' + r.id + ']',
										msg : '档案注销后将无法操作，是否继续?',
										modal : true,
										width : 300,
										buttons : Ext.MessageBox.YESNO,
										multiline : false,
										fn : function(btn, text) {
											if (btn == "yes") {
												this
														.checkDiabetesRecordLogout();
											}
										},
										scope : this
									})
						}
						if(btn=="no"){
						    Ext.Msg.show({
										title : '确认恢复[' + r.id + ']',
										msg : '将恢复档案，是否继续?',
										modal : true,
										width : 300,
										buttons : Ext.MessageBox.YESNO,
										multiline : false,
										fn : function(btn, text) {
											if (btn == "yes") {
												/**写恢复计划代码**/
												this.setDiabetesRecordNormal();
											}
										},
										scope : this
									})
						}
					},
					scope : this
				})
			},
			 
			saveDataToServer : function(saveData) {
				saveData["status"] = 1
				this.mask("正在执行操作...")
				util.rmi.jsonRequest({
							serviceId : "recordLogoutAndRevertService",
							op : "update",
							schema : this.entryName,
							serviceAction : "checkSubRecordLogout",
							body : saveData
						}, function(code, msg, json) {
							this.unmask()
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return
							}
							if (code < 300) {
								this.refresh();
							}
						}, this)// jsonRequest
			},
			doCreateByEmpi : function() {
				var advancedSearchView = this.midiModules["DiabetesRecordListView_EMPIInfoTabView"];
				if (!advancedSearchView) {
					$import("chis.application.mpi.script.EMPIInfoModule")
					advancedSearchView = new chis.application.mpi.script.EMPIInfoModule({
								title : "个人基本信息查找",
								mainApp : this.mainApp,
								modal : true
							});
					advancedSearchView.on("onEmpiReturn", this.onEmpiSelected,
							this)
					this.midiModules["DiabetesRecordListView_EMPIInfoTabView"] = advancedSearchView;
				}
				var win = advancedSearchView.getWin();
				win.setPosition(250, 100);
				win.show();
			},
			onEmpiSelected : function(data) {
				
				this.empiId=data.empiId;
				var cfg = {};
				cfg.closeNav = true;
				cfg.initModules = ['QY_01'];
				cfg.mainApp = this.mainApp;
				cfg.activeTab = 0;//判断进入界面后显不显示第一个form
				cfg.needInitFirstPanel = true
				var module = this.midiModules["QYFYView_EHRView"]
				if (!module) {
					module = new chis.script.EHRView(cfg)
					module.exContext.ids["empiId"] = data.empiId;
					module.exContext.ids["phrId"] = this.phrId;
					module.on("save", this.onSave, this)
					this.midiModules["QYFYView_EHRView"] = module
				} else {
					module=null;
					module = new chis.script.EHRView(cfg)
					module.exContext.ids["empiId"] = data.empiId;
					module.exContext.ids["phrId"] = this.phrId;
					module.on("save", this.onSave, this)
					this.midiModules["QYFYView_EHRView"] = module
				}
				module.actionName = "PER_XCQYFW";
				 
				module.getWin().show()
			},
			doModify : function() {
				if (this.store.getCount() == 0) {
					return
				}
				var r = this.grid.getSelectionModel().getSelected()
				var data = {}
				data.empiId = r.get("empiId")
				this.onEmpiSelected(data)
			},
			onDblClick : function(grid, index, e) {
				var r = grid.getSelectionModel().getSelected()
				var data = {}
				data.empiId = r.get("empiId")
				this.onEmpiSelected(data)
			},
			loadModuleCfg : function(id) {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "moduleConfigLocator",
							id : id
						})
				if (result.code != 200) {
					if (result.msg = "NotLogon") {
						this.mainApp.logon(this.loadModuleCfg, this, [id])
					}
					return null;
				}
				return result.json.body;
			},
			 
			onStoreLoadData : function(store, records, ops) {
				chis.application.fhr.script.PremaritalCheckFemaleListView.superclass.onStoreLoadData
						.call(this, store, records, ops);
				this.onRowClick()
				var girdcount = 0;
				store.each(function(r) {
					var needVisit = r.get("needDoVisit");
					if (needVisit) {
						this.grid.getView().getRow(girdcount).style.backgroundColor = '#ffbeba';
					}
					girdcount += 1;
				}, this);
			}
		});