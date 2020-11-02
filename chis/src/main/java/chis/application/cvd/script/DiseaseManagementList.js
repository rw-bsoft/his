$package("chis.application.cvd.script")
$import("chis.script.BizSimpleListView","chis.script.EHRView","chis.application.mpi.script.EMPIInfoModule")
chis.application.cvd.script.DiseaseManagementList = function(cfg) {
	cfg.selectFirst = false;
	this.initCnd = cfg.cnds || ['and',["eq", ["$", "c.manaUnitId"], ["s",cfg.mainApp.deptId]],["eq", ["$", "c.manaDoctorId"], ["s", cfg.mainApp.uid]]];
	chis.application.cvd.script.DiseaseManagementList.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(chis.application.cvd.script.DiseaseManagementList,
		chis.script.BizSimpleListView, {
		loadData : function() {
			chis.application.cvd.script.DiseaseManagementList.superclass.loadData
					.call(this);
			var recordNum = this.store.getCount();
			if (recordNum == 0) {
				var bts = this.grid.getTopToolbar().items;
				for (var i = 0; i < bts.getCount(); i++) {
					if (i > 5) {
						if(i!=9){
						bts.items[i].disable();
						}
					}
				}
			}
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
							id : "chis.dictionary.checkStatu",
							forceSelection : true,
							defaultValue : {
								key : "0",
								text : "全部"
							}
						});
				comb.on("select", this.radioChanged, this);
				comb.setValue(0);
				comb.setWidth(80);
				var lab = new Ext.form.Label({
							html : "&nbsp;&nbsp;&nbsp;核实状态:&nbsp;"
						});
				cfg.items = [lab, comb];
				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
				this.pagingToolbar = pagingToolbar;
				return pagingToolbar;
			},
			radioChanged : function(r) {
				var status = r.getValue();
				var statusCnd=['and',["eq", ["$", "c.manaUnitId"], ["s",this.mainApp.deptId]],["eq", ["$", "c.manaDoctorId"], ["s", this.mainApp.uid]]];
				if(status!=0){
					statusCnd=['and',["eq", ["$", "a.hszt"], ["i",status]],["eq", ["$", "c.manaUnitId"], ["s",this.mainApp.deptId]],["eq", ["$", "c.manaDoctorId"], ["s", this.mainApp.uid]]];
				}
				this.requestData.cnd = statusCnd;
				this.refresh();
			},
			
			doCreateByEmpi : function() {
				debugger;
				var advancedSearchView = this.midiModules["EMPI.ExpertQuery"];
				if (!advancedSearchView) {
					advancedSearchView = new chis.application.mpi.script.EMPIInfoModule({
								title : "个人基本信息查找",
								modal : true,
								mainApp : this.mainApp
							});
					advancedSearchView.on("onEmpiReturn", this.onEmpiSelected,
							this);
					this.midiModules["EMPI.ExpertQuery"] = advancedSearchView;
				}
				var win = advancedSearchView.getWin();
				win.setPosition(250, 100);
				win.show();
			},
			onEmpiSelected : function(data) {
				var empiId = data.empiId;
//				var currDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
//				var birthday = data.birthday
//				var birth;
//				if ((typeof birthday == 'object')
//						&& birthday.constructor == Date) {
//					birth = birthday;
//				} else {
//					birth = Date.parseDate(birthday, "Y-m-d");
//				}
//				currDate.setYear(currDate.getFullYear() - 35);
//				if (birth < currDate) {
//					this.showModule(empiId, null);
//				} else {
//					Ext.Msg.alert("消息","未满35周岁不允许新建心血管") 
//				}
				this.showModule(empiId, null);
				
			},
			showModule : function(empiId, recordId,r) {
				debugger;
				var m = this.midiModules["ehrView"];
				if (!m) {
					m = new chis.script.EHRView({
								closeNav : true,
								initModules : ['M_02'],
								mainApp : this.mainApp,
								empiId : empiId
							});
					this.midiModules["ehrView"] = m;
					m.exContext.args["selectRecordId"] = recordId;
					m.on("save", this.refresh, this);
				} else {
					m.exContext.ids["empiId"] = empiId;
					m.exContext.args["selectRecordId"] = recordId;
					m.exContext.args['selectData']=r;
					m.refresh();
				}
				m.getWin().show();
			},
			doModify : function(item, e) {
				debugger;
				var r = this.grid.getSelectionModel().getSelected();
				if (r == null) {
					return;
				}
				this.showModule(r.get("empiId"), r.get("recordId"),r);
			},
			onDblClick : function(grid, index, e) {
				this.doModify();
			},
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return
				}
				debugger;
				var hszt = r.get("hszt");
				var bts = this.grid.getTopToolbar().items;
				if (!bts) {
					return;
				}
				//控制删除和核实按钮
				if (hszt==2) {
						bts.items[7].disable();
						bts.items[8].disable();
				} else {
						bts.items[7].enable();
						bts.items[8].enable();
				}
				bts.items[6].enable();
			}
});