$package("chis.application.idr.script")
$import("chis.script.BizSimpleListView")

chis.application.idr.script.IDR_ReportListView = function(cfg) {
	this.initCnd = ["eq", ["$", "a.status"], ["s", "0"]]
	chis.application.idr.script.IDR_ReportListView.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(chis.application.idr.script.IDR_ReportListView,
		chis.script.BizSimpleListView, {
			onDblClick : function() {
				this.doModify();
				//this.openPHISIDRModule();
			},
			doCreateByEmpi : function() {
				var advancedSearchView = this.midiModules["IDR_Report_EMPIInfoModule"];
				if (!advancedSearchView) {
					$import("chis.application.mpi.script.EMPIInfoModule");
					advancedSearchView = new chis.application.mpi.script.EMPIInfoModule(
							{
								title : "个人基本信息查找",
								mainApp : this.mainApp,
								modal : true
							});
					advancedSearchView.on("onEmpiReturn", this.onEmpiSelected,
							this);
					this.midiModules["IDR_Report_EMPIInfoModule"] = advancedSearchView;
				}
				var win = advancedSearchView.getWin();
				win.setPosition(250, 100);
				win.show();
			},
			onEmpiSelected : function(data) {
				this.empiId = data.empiId;
				this.birthday = data.birthday;
				this.createIDR = true;
				// 检查健康档案是否和当前登录人为同一机构
				this.grid.el.mask("正在查询数据..")
				util.rmi.jsonRequest({
							serviceId : "chis.simpleLoad",
							method : "execute",
							schema : "chis.application.hr.schemas.EHR_HealthRecord",
							body : {
								fieldName : "empiId",
								fieldValue : this.empiId
							}
						}, function(code, msg, json) {
							this.grid.el.unmask()
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}

							if (this.mainApp.jobtitleId == "chis.system") {
								this.showEHRViewMule();
								return;
							}

							if (!json.body) {
								this.showEHRViewMule();
								return;
							}

							var docManageUnitId = json.body.manaUnitId.key;
							var userManaUnitId = this.mainApp.deptId;
							if (docManageUnitId.substring(0, 9) == userManaUnitId) {
								this.showEHRViewMule();
							} else {
								Ext.Msg.alert("提示", "无法对非本辖区人员进行操作!")
							}
						}, this)

			},
			showEHRViewMule : function() {
				var cfg = {};
				cfg.initModules = ['DCIDR_01'];
				cfg.closeNav = true;
				cfg.mainApp = this.mainApp;
				cfg.empiId = this.empiId;
				var module = this.midiModules["IDR_ReportListViewView_EHRView"];
				if (!module) {
					$import('chis.script.EHRView');
					module = new chis.script.EHRView(cfg);
					this.midiModules["IDR_ReportListViewView_EHRView"] = module
					module.on("save", this.refreshList, this);
				} else {
					module.ehrNavTreePanel.collapse(true);
					module.exContext.ids["empiId"] = this.empiId;
					module.exContext.ids["phrId"] = this.phrId;
					//module.refresh();
				}
				module.exContext.args={};
				module.exContext.args.birthday = this.birthday;
				module.exContext.args.recordId = this.recordId;
				module.exContext.args.createIDR = this.createIDR;
				module.getWin().show();
			},
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
				comb.on("select", this.radioChanged, this);
				comb.setValue("01");
				comb.setWidth(80);
				var lab = new Ext.form.Label({
							html : "&nbsp;&nbsp;状态:"
						});
				cfg.items = [lab, comb];
				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
				this.pagingToolbar = pagingToolbar;
				return pagingToolbar;
			},

			radioChanged : function(r) {
				var status = r.getValue();
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
				this.requestData.cnd = cnd;
				this.requestData.pageNo = 1;
				this.refresh();
			},
			doModify : function(item, e) {
				var r = this.grid.getSelectionModel().getSelected()
				this.initDataId = r.get("RecordID");
				this.empiId = r.get("empiId");
				this.phrId = r.get("phrId");
				this.recordId = r.get("RecordID");
				this.createIDR = false;
				this.showEHRViewMule();
			},
			refreshList : function(entryName, op, json, data) {
				if (entryName == this.entryName) {
					this.refresh();
				}
			},
			openPHISIDRModule : function(){
				var r = this.grid.getSelectionModel().getSelected()
				this.initDataId = r.get("RecordID");
				this.empiId = r.get("empiId");
				this.phrId = r.get("phrId");
				this.recordId = r.get("RecordID");
				this.phisIDRModule="chis.application.diseasecontrol.DISEASECONTROL/IDR/DCIDR_03_PHIS";
				var module = this.createSimpleModule("TPHQModule",this.phisIDRModule);
				module.exContext = {};
				module.exContext.ids={};
				module.exContext.args={};
				module.exContext.ids["empiId"] = this.empiId;
				module.exContext.ids["phrId"] = this.phrId;
				module.exContext.args.birthday = this.birthday;
				module.exContext.args.recordId = this.recordId;
				module.exContext.args.createIDR = this.createIDR;
				this.showWin(module);
				module.loadData();
			}
		});