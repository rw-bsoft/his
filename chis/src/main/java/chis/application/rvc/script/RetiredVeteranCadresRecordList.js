$package("chis.application.rvc.script")

$import("chis.script.BizSimpleListView",
		"chis.application.rvc.script.EMPIInfoModuleRVC",
		"chis.script.EHRView",
		"chis.application.rvc.script.RVCRecordWriteOff")

chis.application.rvc.script.RetiredVeteranCadresRecordList = function(cfg) {
	cfg.initCnd = ["eq", ["$", "a.status"], ["s", "0"]];
	chis.application.rvc.script.RetiredVeteranCadresRecordList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.rvc.script.RetiredVeteranCadresRecordList,
		chis.script.BizSimpleListView, {
			getPagingToolbar : function(store) {
				var cfg = {
					pageSize : 25,
					store : store,
					requestData : this.requestData,
					displayInfo : true,
					emptyMsg : "无相关记录"
				};
				var comb = util.dictionary.SimpleDicFactory.createDic({
							id : "chis.dictionary.docStatu",
							forceSelection : true,
							defaultValue : {
								key : "0",
								text : "正常"
							}
						});
				this.statusCmb = comb;
				comb.on("select", function(field) {
							var value = field.getValue();
							this.radioChanged(value);
						}, this);
				comb.setWidth(80);
				var lab = new Ext.form.Label({
							html : "&nbsp;&nbsp;状态:"
						});
				cfg.items = [lab, comb];
				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
				this.pagingToolbar = pagingToolbar;
				return pagingToolbar;
			},

			radioChanged : function(status) {
				var navCnd = this.navCnd;
				var queryCnd = this.queryCnd;
				var statusCnd = ["eq", ["$", "a.status"], ["s", status]];
				this.initCnd = statusCnd;
				var cnd = statusCnd;
				if (navCnd || queryCnd) {
					cnd = ['and', cnd];
					if (navCnd) {
						cnd.push(navCnd);
					}
					if (queryCnd) {
						cnd.push(queryCnd);
					}
				}

				var bts = this.grid.getTopToolbar().items;
				var btn = bts.items[8];
				if (btn) {
					if (status != "0") {
						btn.notReadOnly = false;
					} else {
						btn.notReadOnly = null;
					}
				}
				this.requestData.cnd = cnd;
				this.requestData.pageNo = 1;
				this.refresh();
			},

			doCreateDoc : function() {
				var advancedSearchView = this.midiModules["EMPI.RVCQuery"];
				if (!advancedSearchView) {
					advancedSearchView = new chis.application.rvc.script.EMPIInfoModuleRVC(
							{
								title : "个人基本信息查找",
								modal : true,
								mainApp : this.mainApp
							});
					advancedSearchView.on("onEmpiReturn", this.onEmpiSelected,
							this);
					this.midiModules["EMPI.RVCQuery"] = advancedSearchView;
				}
				var win = advancedSearchView.getWin();
				win.setPosition(250, 100);
				win.show();
			},

			onEmpiSelected : function(empi) {
				this.empiId = empi["empiId"];
				this.showEhrViewWin();
			},

			showEhrViewWin : function() {
				var m = this.midiModules["RVCEhrView"];
				if (!m) {
					m = new chis.script.EHRView({
								closeNav : true,
								initModules : ['R_01','R_02'],
								mainApp : this.mainApp,
								empiId : this.empiId
							});
					this.midiModules["RVCEhrView"] = m;
					m.on("save", this.refresh, this);
				} else {
					m.exContext.ids["empiId"] = this.empiId;
					m.refresh();
				}
				m.getWin().show();
			},

			doModify : function() {
				var r = this.getSelectedRecord();
				this.empiId = r.get("empiId");
				this.showEhrViewWin();
			},
			
			onDblClick : function(){
				this.doModify();
			},

			doWriteOff : function() {
				var record = this.getSelectedRecord();
				if (!record) {
					return;
				}
				var writeOff = this.midiModules["RVC.DocWriteOff"];
				if (!writeOff) {
					writeOff = new chis.application.rvc.script.RVCRecordWriteOff(
							{
								record : record,
								entryName : "chis.application.pub.schemas.EHR_WriteOff",
								title : "离休干部档案注销",
								mainApp : this.mainApp
							});
					writeOff.on("remove", this.onRemove, this);
					writeOff.initPanel();
					this.midiModules["RVC.DocWriteOff"] = writeOff;
				} else {
					writeOff.record = record;
				}
				writeOff.getWin().show();
			},
			
			doVisit : function() {
				var r = this.grid.getSelectionModel().getSelected();
				this.showModule(r.data);
			},
			
			showModule : function(data) {
				var empiId = data.empiId;
				var birthDay = data.birthday;
				var personName = data.personName;
				$import("chis.script.EHRView");
				var module = this.midiModules["RVC_EHRView"];
				if (!module) {
					module = new chis.script.EHRView({
								initModules : ['R_02'],
								empiId : empiId,
								closeNav : true,
								activeTab : 0,
								mainApp : this.mainApp
							});
					this.midiModules["RetiredVeteranCadresRecord_EHRView"] = module;
					module.on("save", this.refresh, this);
				} else {
					module.exContext.ids["empiId"] = empiId;
					module.refresh();
				}
				module.actionName = "RVC_RetiredVeteranCadresRecord";
				module.getWin().show();
			},
			
			onRowClick : function() {
				var r = this.getSelectedRecord();
				var toolBar = this.grid.getTopToolbar();
				if (!r) {
					for (var i = 0; i < this.actions.length; i++) {
						if (this.actions[i].id == "createDoc") {
							continue;
						}
						var btn = toolBar.find("cmd", this.actions[i].id);
						if (!btn || btn.length == 0) {
							continue;
						}
						btn[0].disable();
					}
					return;
				} else {
					for (var i = 0; i < this.actions.length; i++) {
						var btn = toolBar.find("cmd", this.actions[i].id);
						btn[0].enable();
					}
				}
				var status = r.get("status");
				var woBtn = toolBar.find("cmd", "writeOff");// @@ 注销
				// var vBtn = toolBar.find("cmd", "visit");// @@ 随访
				if (status == 1) {
					if (woBtn && woBtn.length > 0) {
						woBtn[0].disable();// @@ 注销
					}
					// if (vBtn && vBtn.length > 0) {
					// vBtn[0].disable();// @@ 随访
					// }
				}
			},

			onRemove : function() {
				this.loadData();
			}
		})