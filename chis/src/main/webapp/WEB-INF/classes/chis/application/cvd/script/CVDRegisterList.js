$package("chis.application.cvd.script")
$import("chis.script.BizSimpleListView","chis.script.EHRView","chis.application.mpi.script.EMPIInfoModule");
chis.application.cvd.script.CVDRegisterList = function(cfg) {
	this.initCnd=['eq',['$','isDelete'],['s','2']];
	chis.application.cvd.script.CVDRegisterList.superclass.constructor.apply(this, [cfg]);
};
Ext.extend(chis.application.cvd.script.CVDRegisterList, chis.script.BizSimpleListView, {

			doCreateByEmpi : function() {
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
				var currDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				var birthday = data.birthday
				var birth;
				if ((typeof birthday == 'object')
						&& birthday.constructor == Date) {
					birth = birthday;
				} else {
					birth = Date.parseDate(birthday, "Y-m-d");
				}
				currDate.setYear(currDate.getFullYear() - 35);
				if (birth < currDate) {
					this.showModule(empiId, null);
				} else {
					Ext.Msg.alert("消息","未满35周岁不允许新建心血管") 
				}
				
			},
			showModule : function(empiId, inquireId) {
				var m = this.midiModules["ehrView"];
				if (!m) {
					m = new chis.script.EHRView({
								closeNav : true,
								initModules : ['M_01'],
								mainApp : this.mainApp,
								empiId : empiId
							});
					this.midiModules["ehrView"] = m;
					m.exContext.args["selectInquireId"] = inquireId;
					m.on("save", this.refresh, this);
				} else {
					m.exContext.ids["empiId"] = empiId;
					m.exContext.args["selectInquireId"] = inquireId;
					m.refresh();
				}
				m.getWin().show();
			},
			doModify : function(item, e) {
				var r = this.grid.getSelectionModel().getSelected();
				if (r == null) {
					return;
				}
				this.showModule(r.get("empiId"), r.get("inquireId"));
			},
			onDblClick : function(grid, index, e) {
				this.doModify();
			},
			doDelete : function() {
				var r = this.grid.getSelectionModel().getSelected();
				if (r == null) {
					return;
				}
				Ext.Msg.show({
							title : '消息提示',
							msg : '是否确定删除此次心血管评估',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.YESNO,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "yes") {
									util.rmi.jsonRequest({
												serviceId : "chis.cvdService",
												serviceAction : "removeAssessRegister",
												body : {
													pkey : r.get("inquireId"),
													empiId:r.get("empiId")
												},
												method : "execute",
												schema : this.entryName
											}, function(code, msg, json) {
												this.refresh();
											}, this)
								}
							},
							scope : this
						})
			}

		});
