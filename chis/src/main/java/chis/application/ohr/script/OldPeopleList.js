$package("chis.application.ohr.script")

$import("chis.script.BizSimpleListView",
		"chis.application.mpi.script.EMPIInfoModule", "chis.script.EHRView",
		"chis.application.ohr.script.OldpeopleRecordWriteOff")

chis.application.ohr.script.OldPeopleList = function(cfg) {
	cfg.initCnd = ["eq", ["$", "a.status"], ["s", "0"]];
	//this.needOwnerBar = true;
	chis.application.ohr.script.OldPeopleList.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(chis.application.ohr.script.OldPeopleList,
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
				var yearCheckItems = this.getYearCheckItems();
				cfg.items = [lab, comb, yearCheckItems];
				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
				this.pagingToolbar = pagingToolbar;
				return pagingToolbar;
			},
			createOwnerBar : function() {
				var manageLabel = new Ext.form.Label({
							html : "管辖机构:",
							width : 80

						});
				var manageField = this.createDicField({
							'id' : 'chis.@manageUnit',
							'showWholeText' : 'true',
							'includeParentMinLen' : '6',
							'render' : 'Tree',
							defaultValue : {
								"key" : this.mainApp.deptId,
								"text" : this.mainApp.dept
							},
							'parentKey' : this.mainApp.deptId,
							rootVisible : true,
							width : 120
						});
				this.manageField = manageField;
				var dateLabel1 = new Ext.form.Label({
							html : "&nbsp;出生日期:",
							width : 80
						});
				var dateField1 = new Ext.form.DateField({
							width : this.cndFieldWidth || 120,
							enableKeyEvents : true,
							emptyText : "出生开始日期",
							name : "createDate1"
						});
				this.dateField1 = dateField1;
				var dateLabel2 = new Ext.form.Label({
							html : "&nbsp;->&nbsp;",
							width : 30
						});
				var dateField2 = new Ext.form.DateField({
							width : this.cndFieldWidth || 120,
							enableKeyEvents : true,
							emptyText : "出生结束日期",
							name : "createDate2"
						});
				this.dateField2 = dateField2;
				this.dateField1.on("select", this.selectDateField1, this);
				this.dateField2.on("select", this.selectDateField2, this);
				var cnd = this.getOwnerCnd([]);
				if (this.requestData.cnd) {
					cnd = ['and', this.requestData.cnd, cnd]
				}
				this.requestData.cnd = cnd;
				return [manageLabel, manageField, dateLabel1, dateField1,
						dateLabel2, dateField2]
			},
			getOwnerCnd : function(cnd) {
				if (this.manageField.getValue() != null
						&& this.manageField.getValue() != "") {
					var manageUnit = this.manageField.getValue();
					if (typeof manageUnit != "string") {
						manageUnit = manageUnit.key;
					}
					var cnd1 = ['like', ['$', 'a.manaUnitId'],
							['s', manageUnit + "%"]];
					if (cnd.length == 0) {
						cnd = cnd1;
					} else {
						cnd = ['and', cnd1, cnd];
					}
				}
				if (this.dateField1.getValue() != null
						&& this.dateField2.getValue() != null
						&& this.dateField1.getValue() != ""
						&& this.dateField2.getValue() != "") {
					var date1 = this.dateField1.getValue();
					var date2 = this.dateField2.getValue();
					var cnd2 = [
							'and',
							[
									'ge',
									['$', 'b.birthday'],
									[
											'todate',
											[
													's',
													date1.format("Y-m-d")
															+ " 00:00:00"],
											['s', 'yyyy-mm-dd HH24:mi:ss']]],
							[
									'le',
									['$', 'b.birthday'],
									[
											'todate',
											[
													's',
													date2.format("Y-m-d")
															+ " 23:59:59"],
											['s', 'yyyy-mm-dd HH24:mi:ss']]]];
					if (cnd.length == 0) {
						cnd = cnd2;
					} else {
						cnd = ['and', cnd2, cnd];
					}
				} else if ((this.dateField1.getValue() == null || this.dateField1
						.getValue() == "")
						&& (this.dateField2.getValue() == null || this.dateField2
								.getValue() == "")) {

				} else if (this.dateField1.getValue() == null
						|| this.dateField1.getValue() == "") {
					MyMessageTip.msg("提示", "请选择出生开始日期！", true);
					return;
				} else if (this.dateField2.getValue() == null
						|| this.dateField2.getValue() == "") {
					MyMessageTip.msg("提示", "请选择出生结束日期！", true);
					return;
				}
				return cnd;
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

			refresh : function() {
				chis.application.ohr.script.OldPeopleList.superclass.loadData
						.call(this);
			},

			onDblClick : function(grid, index, e) {
				this.doModify();
			},

			onStoreLoadData : function(store, records, ops) {
				chis.application.ohr.script.OldPeopleList.superclass.onStoreLoadData
						.call(this, store, records, ops);
				this.onRowClick();

				var girdcount = 0;
				store.each(function(r) {
					var needVisit = r.get("needDoVisit");
					if (needVisit) {
						this.grid.getView().getRow(girdcount).style.backgroundColor = '#ffbeba';
					}
					girdcount += 1;
				}, this);
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

			doVisit : function() {
				var r = this.grid.getSelectionModel().getSelected();
				this.showModule(r.data);
			},

			showModule : function(data) {
				var empiId = data.empiId;
				var birthDay = data.birthday;
				var personName = data.personName;
				$import("chis.script.EHRView");
				var module = this.midiModules["OldPeopleRecord_EHRView"];
				if (!module) {
					module = new chis.script.EHRView({
								initModules : ['B_06'],
								empiId : empiId,
								closeNav : true,
								activeTab : 0,
								mainApp : this.mainApp
							});
					this.midiModules["OldPeopleRecord_EHRView"] = module;
					module.on("save", this.refresh, this);
				} else {
					module.exContext.ids["empiId"] = empiId;
					module.refresh();
				}
				module.actionName = "MDC_OldPeopleRecord";
				module.getWin().show();
			},

			doCreateDoc : function() {
				var advancedSearchView = this.midiModules["EMPI.ExpertQuery"];
				if (!advancedSearchView) {
					advancedSearchView = new chis.application.mpi.script.EMPIInfoModule(
							{
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

			checkAge : function(birthDay) {
				var currDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				var birth;
				if ((typeof birthDay == 'object')
						&& birthDay.constructor == Date) {
					birth = birthDay;
				} else {
					birth = Date.parseDate(birthDay, "Y-m-d");
				}
				currDate.setYear(currDate.getFullYear()
						- this.mainApp.exContext.oldPeopleAge);
				if (birth <= currDate) {
					return true;
				} else {
					return false;
				}
			},

			onEmpiSelected : function(empi) {
				var empiId = empi.empiId;
				var birthDay = empi.birthday;
				var personName = empi.personName;
				if (!this.mainApp.exContext.oldPeopleAge) {
					Ext.Msg.show({
								title : '提示信息',
								msg : '请先配置老年人年龄',
								modal : true,
								minWidth : 300,
								maxWidth : 600,
								buttons : Ext.MessageBox.OK,
								multiline : false,
								scope : this
							});
					return;
				}
				if (!this.checkAge(birthDay)) {
					Ext.Msg.show({
								title : '提示信息',
								msg : '年龄小于'
										+ this.mainApp.exContext.oldPeopleAge
										+ '岁不允许建立老年人档案',
								modal : true,
								minWidth : 300,
								maxWidth : 600,
								buttons : Ext.MessageBox.OK,
								multiline : false,
								scope : this
							});
					return;
				}

				this.empiId = empi["empiId"];
				this.showEhrViewWin();
			},

			showEhrViewWin : function() {
				var m = this.midiModules["ehrView"];
				if (!m) {
					m = new chis.script.EHRView({
								closeNav : true,
								initModules : ['B_07', 'B_06', 'B_11'],
								mainApp : this.mainApp,
								empiId : this.empiId
							});
					this.midiModules["ehrView"] = m;
					m.on("save", this.refresh, this);
				} else {
					m.exContext.ids["empiId"] = this.empiId;
					m.refresh();
				}
				m.actionName = "MDC_OldPeopleRecord";
				m.getWin().show();
			},

			doModify : function() {
				var r = this.getSelectedRecord();
				this.empiId = r.get("empiId");
				this.showEhrViewWin();
			},

			doWriteOff : function() {
				var record = this.getSelectedRecord();
				if (!record) {
					return;
				}
			if(!(this.mainApp.jobId=='chis.14'|| this.mainApp.jobId=='chis.system')){
                	if(record.get("manaDoctorId")!=this.mainApp.uid){
                		alert("只有防保科长或现责任医生能注销档案！")
                		return;
                	}
                }
				var writeOff = this.midiModules["MDC.DocWriteOff"];
				if (!writeOff) {
					writeOff = new chis.application.ohr.script.OldpeopleRecordWriteOff(
							{
								record : record,
								entryName : "chis.application.pub.schemas.EHR_WriteOff",
								title : "老年人档案注销",
								mainApp : this.mainApp
							});
					writeOff.on("remove", this.onRemove, this);
					writeOff.initPanel();
					this.midiModules["MDC.DocWriteOff"] = writeOff;
				} else {
					writeOff.record = record;
				}
				writeOff.getWin().show();
			},

			onRemove : function() {
				this.loadData();
			}
		});