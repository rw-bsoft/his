/**
 * 个人健康档案列表页面
 * 
 * @author : tianj
 */
$package("chis.application.hmc.script")

$import("chis.script.BizSelectListView", "chis.script.demographicView", 
"util.rmi.jsonRequest")

chis.application.hmc.script.HealthRecordList = function(cfg) {
	cfg.pageSize = 300;
	if(cfg.mainApp.jobId == "chis.22"){
		cfg.entryName = "chis.application.hr.schemas.EHR_HealthRecord_HMC";
	}else{
		cfg.entryName = "chis.application.hr.schemas.EHR_HealthRecord";
	}
//	cfg.autoLoadData = false;
	chis.application.hmc.script.HealthRecordList.superclass.constructor.apply(
			this, [cfg]);
	this.disablePagingTbr = false;
}

Ext.extend(chis.application.hmc.script.HealthRecordList,
		chis.script.BizSelectListView, {
			loadData : function() {
				if(!this.initCnd){
					var centerUnit = this.mainApp.centerUnit;
					var unitCnd = ['like', ['$', 'a.manaUnitId'], ['s', centerUnit+'%']];
					var myCnd = [];
					if(this.mainApp.jobId=="chis.22"){//二级审核角色
						myCnd = ["eq",["$", "a.isOffer"],["s","y"]];
					}else if(this.mainApp.jobId=="chis.23"){//三级审核角色
						myCnd = ["eq",["$", "a.isFirstVerify"],["s","1"]];
					}else{
						this.initCnd = ["in",
							["$", "a.status"],
							['0', '2']];
					}
					if(myCnd!=[]){
						this.initCnd = ["and",["in",
								["$", "a.status"],
								['0', '2']],myCnd,unitCnd];
					}
					this.requestData.cnd = this.initCnd;
					this.requestData.sortInfo = "t1.isFirstVerify,t1.isSecondVerify,t1.zlls,t1.phrId desc";
				}
				chis.application.hmc.script.HealthRecordList.superclass.loadData
						.call(this);
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
				var yearCheckItems = this.getYearCheckItems();
				cfg.items = [lab, comb, yearCheckItems];
				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
				this.pagingToolbar = pagingToolbar;
				return pagingToolbar;
			},
			getYearCheckItems : function() {
				var yearLabel = new Ext.form.Label({
							html : "&nbsp;&nbsp;年度:",
							width : 90
						});
				var yearField = this.createDicField({
							editable : false,
							defaultValue : {
								"key" : new Date().getFullYear(),
								"text" : new Date().getFullYear()
							},
							forceSelection : true,
							width : 60,
							id : "chis.dictionary.years"
						});
				this.yearField = yearField;
				this.yearField.on("select", this.yearFieldSelect, this);
				var checkLabel = new Ext.form.Label({
							html : "&nbsp;&nbsp;体检:",
							width : 80
						});
				var checkField = this.createDicField({
							editable : false,
							defaultValue : {
								"key" : "1",
								"text" : "全部"
							},
							forceSelection : true,
							width : 60,
							id : "chis.dictionary.checkuped"
						});
				this.checkField = checkField;
				this.checkField.on("select", this.checkFieldSelect, this);
				return [yearLabel, yearField, checkLabel, checkField];
			},
			yearFieldSelect : function(combo, record, index) {
				this.requestData.year = this.yearField.getValue();
				this.doCndQuery();
			},
			checkFieldSelect : function(combo, record, index) {
				this.requestData.checkType = this.checkField.getValue();
				this.doCndQuery();
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
				manageField.on("blur", this.manageBlur, this);
				this.manageField = manageField;
				var dateLabel1 = new Ext.form.Label({
							html : "&nbsp;建档日期:",
							width : 80
						});
				var startValue = new Date().getFullYear() + "-01-01";
				var dateField1 = new Ext.form.DateField({
							width : this.cndFieldWidth || 120,
							enableKeyEvents : true,
							emptyText : "建档开始日期",
							value : Date.parseDate(startValue, "Y-m-d"),
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
							emptyText : "建档结束日期",
							value : new Date(),
							name : "createDate2"
						});
				this.dateField2 = dateField2;
				this.dateField1.on("select",this.selectDateField1,this);
				this.dateField2.on("select",this.selectDateField2,this);
				var nameLabel = new Ext.form.Label({
							html : "姓名:",
							width : 60
						});
				var nameField = new Ext.form.TextField({
							width : this.cndFieldWidth || 120,
							enableKeyEvents : true,
							name : "personName"
						});
				this.nameField = nameField;
				var cardLabel = new Ext.form.Label({
							html : "身份证号:",
							width : 80
						});
				var cardField = new Ext.form.TextField({
							width : this.cndFieldWidth || 120,
							enableKeyEvents : true,
							name : "idCard"
						});
				this.cardField = cardField;
				this.nameField.on("keydown", this.KeyUpField, this);
				this.cardField.on("keydown", this.KeyUpField, this);
				var cnd = this.getOwnerCnd([]);
				if (this.requestData.cnd) {
					cnd = ['and', this.requestData.cnd, cnd]
				}
				this.requestData.cnd = cnd;
				return [manageLabel, manageField, dateLabel1, dateField1,
						dateLabel2, dateField2, nameLabel, nameField,
						cardLabel, cardField]
			},
			KeyUpField : function(field, e) {
				if (e.getKey() == e.ENTER) {
					e.stopEvent();
					this.doCndQuery();
					return
				}
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
				if (this.nameField.getValue() != null
						&& this.nameField.getValue() != "") {
					var name = this.nameField.getValue();
					var cnd1 = ['like', ['$', 'b.personName'],
							['s', "%" + name + "%"]];
					if (cnd.length == 0) {
						cnd = cnd1;
					} else {
						cnd = ['and', cnd1, cnd];
					}
				}
				if (this.cardField.getValue() != null
						&& this.cardField.getValue() != "") {
					var card = this.cardField.getValue();
					var cnd1 = ['like', ['$', 'b.idCard'],
							['s', "%" + card + "%"]];
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
									['$', 'a.createDate'],
									[
											'todate',
											[
													's',
													date1.format("Y-m-d")
															+ " 00:00:00"],
											['s', 'yyyy-mm-dd HH24:mi:ss']]],
							[
									'le',
									['$', 'a.createDate'],
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
					MyMessageTip.msg("提示", "请选择建档开始日期！", true);
					return;
				} else if (this.dateField2.getValue() == null
						|| this.dateField2.getValue() == "") {
					MyMessageTip.msg("提示", "请选择建档结束日期！", true);
					return;
				}
				return cnd;
			},
			radioChanged : function(r) {
				var status = r.getValue();
				var navCnd = this.navCnd;
				var queryCnd = this.queryCnd;
				var statusCnd = ["eq", ["$", "a.status"], ["s", status]];
				//yx-2017-05-14查询全部档案-开始 
				if(status==8){
					var str="0"+"','"+1;
					statusCnd =["in",["$", "a.status"],[str]];
				}
				//yx-2017-05-14查询全部档案-结束				
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
						btn.disable();
					} else {
						btn.enable();
					}
				}
				this.requestData.cnd = cnd;
				this.requestData.pageNo = 1;
				this.refresh();
			},
			doAction : function(item, e) {
				var cmd = item.cmd;
				var ref = item.ref;
				if (ref) {
					this.loadRemote(ref, item);
					return;
				}
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1);
				var action = this["do" + cmd];
				if (action) {
					action.apply(this, [item, e]);
				}
			},
			onReady : function() {//增加按钮权限
				if (this.autoLoadData) {
					this.loadData();
				}
				var el = this.grid.el
				if (!el) {
					return
				}
				var actions = this.actions
				if (!actions) {
					return
				}
				var keyMap = new Ext.KeyMap(el)
				keyMap.stopEvent = true

				// index btns
				var btnAccessKeys = {}
				var keys = []
				if (this.showButtonOnTop) {
					var btns = this.grid.getTopToolbar().items || [];
					var n = btns.getCount()
					for (var i = 0; i < n; i++) {
						var btn = btns.item(i)
						var key = btn.accessKey
						var cmd = btn.cmd
						if(this.mainApp.jobId=="chis.24" && (cmd=="verify" || cmd=="cancelVerify" || cmd=="back")){
							btn.disable();
							continue;
						}else if((this.mainApp.jobId=="chis.22" || this.mainApp.jobId=="chis.23") && (cmd=="remark" || cmd=="cancelRemark")){
							btn.disable();
							continue;
						}else if((this.mainApp.jobId=="chis.22" || this.mainApp.jobId=="chis.24") && (cmd=="open" || cmd=="cancelOpen")){
							btn.disable();
							continue;
						}
						if (key) {
							btnAccessKeys[key] = btn
							keys.push(key)
						}
					}
				} else {
					var btns = this.grid.buttons || []
					for (var i = 0; i < btns.length; i++) {
						var btn = btns[i]
						var key = btn.accessKey
						if (key) {
							btnAccessKeys[key] = btn
							keys.push(key)
						}
					}
				}
				this.btnAccessKeys = btnAccessKeys
				// 屏蔽框架自带的快捷键
				// keyMap.on(keys,this.onAccessKey,this)
				keyMap.on(Ext.EventObject.ENTER, this.onEnterKey, this)

			},
			onRemove : function() {
				this.loadData();
			},
			doReadOnly : function(item, e) {
				this.showModule(item, e);
			},
			showModule : function(item, e) {
				var m = this.midiModules["healthRecordModule"];
				if (!m) {
					$import("chis.application.mpi.script.EMPIInfoModule");
					m = new chis.application.mpi.script.EMPIInfoModule({
						entryName : "chis.application.mpi.schemas.MPI_DemographicInfo",
						title : "个人基本信息查询",
						height : 450,
						modal : true,
						mainApp : this.mainApp
					});
					m.on("onEmpiReturn", this.checkRecordExist, this);
					m.on("close", this.active, this);
					this.midiModules["healthRecordModule"] = m;
				}
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
			},
			doModify : function() {
				var r = this.grid.getSelectionModel().getSelected();
				this.record = r.data;
				this.onSelectEMPI(r.data);
			},
			doVerify : function() {
				var records = this.getSelectedRecords();
				var length = records.length;
				if (length == 0) {
					return;
				}
				var jobId = this.mainApp.jobId;
				var phrIds = new Array();
				for (var i = 0; i < length; i++) {
					var r = records[i];
					phrIds.push(r.get("phrId"));
				 	 if (jobId == "chis.22") {// 责任医生角色
					     if((r.get("isFirstVerify")=="1" ) || (r.get("isSecondVerify")=="2" )){
					       MyMessageTip.msg("提示",
					       "存在已审核或已开放的居民档案，无需再次审核或开放；请重新核对查询条件!", true);
					       return;
					     }
				     }else if((jobId == "chis.23")){
				     	  if((r.get("isSecondVerify")=="1" )  || (r.get("isSecondVerify")=="2" )){
					       MyMessageTip.msg("提示",
					       "存在已开放的居民档案，无需再次开放；请重新核对查询条件!", true);
					       return;
				     	  }
				     }
				}
				
				Ext.Msg.confirm("请确认", "是否审核通过选中记录?", function(btn) {
							if (btn == 'yes') {
								var data = {
									"phrIds" : phrIds
								};
								util.rmi.jsonRequest({
											serviceId : this.saveServiceId,
											serviceAction : this.serviceActionVerify,
											method : "execute",
											body : data
										}, function(code, msg, json) {
											if (code >= 300) {
												this
														.processReturnMsg(code,
																msg);
												return;
											}
										 MyMessageTip.msg("通知",
					                         "审核成功!", true);
											this.refresh();
											this.clearSelect();
										}, this)
							}
						}, this);
			},
			doCancelVerify : function() {
				var records = this.getSelectedRecords();
				var length = records.length;
				if (length == 0) {
					return;
				}
				var jobId = this.mainApp.jobId;
				var phrIds = new Array();
				for (var i = 0; i < length; i++) {
					var r = records[i];
					phrIds.push(r.get("phrId"));
					if (jobId == "chis.22") {
					     if(r.get("isSecondVerify")=="2" ){
					       MyMessageTip.msg("提示",
					       "该居民档案已经开放，无法取消审核!请先联系三级审核人员取消开放", true);
					       return;
					     }else if((r.get("isFirstVerify")=="0") || (r.get("isFirstVerify")=="2")){
					     	MyMessageTip.msg("提示",
					       "该居民档案未进行二级审核或已取消审核，无需取消审核", true);
					       return;
					     }
				     }else if((jobId == "chis.23")){
				     	  if(r.get("isSecondVerify")=="2"){
					       MyMessageTip.msg("提示",
					       "该居民档案已经进行开放，无法取消审核!请先进行取消开放!", true);
					       return;
				     	  }
				     }
				}
				Ext.Msg.confirm("请确认", "是否取消审核选中的记录?", function(btn) {
					if (btn == 'yes') {
						var data = {
							"phrIds" : phrIds
						};
						util.rmi.jsonRequest({
									serviceId : this.saveServiceId,
									serviceAction : this.serviceActionCancelVerify,
									method : "execute",
									body : data
								}, function(code, msg, json) {
									if (code >= 300) {
										this.processReturnMsg(code, msg);
										return;
									}
									MyMessageTip.msg("通知",
					                         "取消审核成功!", true);
									this.refresh();
									this.clearSelect();
								}, this)
					}
				}, this);
			},
			doLocate : function() {
				var r = this.grid.getSelectionModel().getSelected();
				if (!r) {
					return;
				}
				if (!r.data.regionCode) {
					Ext.MessageBox.show("用户信息不完整,无法定位");
					return;
				}
				this.parent.on("moduleLoaded", this.onModuleLoaded, this);
				this.parent.listTab.activate(2);
			},
			onModuleLoaded : function() { // added by zhouz
				this.parent.midiModules["gis"].doLocate({
							schema : this.schema,
							data : this.grid.getSelectionModel().getSelected().data
						});
			},
			checkRecordExist : function(r) {
				this.record = r
				this.onSelectEMPI(r);

			},
			onSelectEMPI : function(r) {
				var empiId = r.empiId;
				var birthDay = r.birthday;
				var personName = r.personName;
				var jobId = this.mainApp.jobId;
				var module = this.midiModules["HealthRecord_EHRView"]
				if (!module) {
					$import("chis.script.EHRView");
					var initModules = ['B_01', 'B_02', 'B_03', 'B_04', 'B_05'];
					if (jobId == "chis.04") {
						var initModules = ['B_01', 'B_02', 'B_04', 'B_05'];
					} else if (jobId == "chis.13") {
						var initModules = ['B_01'];
					}
					if (r.incomeSource && r.incomeSource.indexOf("1") > -1) {
						if (jobId != "gp.100" && jobId != "gp.101")
							initModules.push("B_08");
					}
					if (this.checkAge(birthDay) && jobId != "chis.07"
							&& jobId != "chis.08" && jobId != "chis.15"
							&& jobId != "chis.16") {
						initModules.push("B_07");
						initModules.push("B_06");
					}
					if (this.checkHasAssessRegisterModule(birthDay)
							&& jobId != "chis.07" && jobId != "chis.08"
							&& jobId != "chis.15" && jobId != "chis.16") {
						initModules.push("M_01");
					}

					module = new chis.script.EHRView({
								initModules : initModules,
								empiId : empiId,
								closeNav : true,
								mainApp : this.mainApp,
								exContext : {
									"birthDay" : birthDay
								}
							});
					this.midiModules["HealthRecord_EHRView"] = module;
					module.on("save", this.refresh, this);
				} else {
					if (r.incomeSource && r.incomeSource.indexOf('1') > -1) {
						if (module.mainTab.find("mKey", "B_08").length == 0) {
							if (jobId != "gp.100" && jobId != "gp.101") {
								module.activeModules["B_08"] = true;
								module.mainTab.add(module.getModuleCfg("B_08"));
							}
						}
					} else {
						if (module.mainTab.find("mKey", "B_08").length > 0) {
							if (jobId != "gp.100" && jobId != "gp.101") {
								module.activeModules["B_08"] = false;
								module.mainTab.remove(module.mainTab.find(
										"mKey", "B_08")[0]);
							}
						}
					}
					if (!this.checkAge(birthDay) && jobId != "chis.07"
							&& jobId != "chis.08" && jobId != "chis.15"
							&& jobId != "chis.16"
							&& module.activeModules["B_07"]
							&& module.activeModules["B_06"]) {
						module.activeModules["B_07"] = false;
						module.activeModules["B_06"] = false;
						if (module.mainTab.find("mKey", "B_07").length > 0) {
							module.mainTab.remove(module.mainTab.find("mKey",
									"B_07")[0]);
						}
						if (module.mainTab.find("mKey", "B_06").length > 0) {
							module.mainTab.remove(module.mainTab.find("mKey",
									"B_06")[0]);
						}
					} else if (this.checkAge(birthDay) && jobId != "chis.07"
							&& jobId != "chis.08" && jobId != "chis.15"
							&& jobId != "chis.16"
							&& !module.activeModules["B_07"]
							&& !module.activeModules["B_06"]) {
						if (module.mainTab.find("mKey", "B_07").length == 0) {
							module.activeModules["B_07"] = true;
							module.mainTab.add(module.getModuleCfg("B_07"));
						}
						if (module.mainTab.find("mKey", "B_06").length == 0) {
							module.activeModules["B_06"] = true;
							module.mainTab.add(module.getModuleCfg("B_06"));
						}
					}
					if (!this.checkHasAssessRegisterModule(birthDay)
							&& jobId != "chis.07" && jobId != "chis.08"
							&& jobId != "chis.15" && jobId != "chis.16"
							&& module.activeModules["M_01"]) {
						module.activeModules["M_01"] = false;
						if (module.mainTab.find("mKey", "M_01").length > 0) {
							module.mainTab.remove(module.mainTab.find("mKey",
									"M_01")[0]);
						}
					} else if (this.checkHasAssessRegisterModule(birthDay)
							&& jobId != "chis.07" && jobId != "chis.08"
							&& jobId != "chis.15" && jobId != "chis.16"
							&& !module.activeModules["M_01"]) {
						if (module.mainTab.find("mKey", "M_01").length == 0) {
							module.activeModules["M_01"] = true;
							module.mainTab.add(module.getModuleCfg("M_01"));
						}
					}
					module.actionName = "EHR_HealthRecord";
					module.exContext.ids["empiId"] = empiId;
					module.refresh();
				}
				module.getWin().show();
			},
			onDblClick : function(grid, index, e) {
				this.doModify();
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
			checkHasAssessRegisterModule : function(birthDay) {
				var currDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				var birth;
				if ((typeof birthDay == 'object')
						&& birthDay.constructor == Date) {
					birth = birthDay;
				} else {
					birth = Date.parseDate(birthDay, "Y-m-d");
				}
				currDate.setYear(currDate.getFullYear() - 35);
				if (birth < currDate) {
					return true;
				} else {
					return false;
				}
			},
			doMove : function() {
				var cls = "";
			},
			onStoreLoadData : function(store, records, ops) {
				chis.application.hmc.script.HealthRecordList.superclass.onStoreLoadData
						.call(this, store, records, ops);
				var girdcount = 0;
				store.each(function(r) {
					var isFirstVerify = r.get("isFirstVerify");
					var isSecondVerify = r.get("isSecondVerify");
					if ((this.mainApp.jobId=="chis.22" && isFirstVerify!="1") || (this.mainApp.jobId=="chis.23" && isSecondVerify!="2")) {//待审核
						this.grid.getView().getRow(girdcount).style.backgroundColor = '#ffbeba';
					}
					girdcount += 1;
				}, this);
			},
			doBack : function(){
				var records=this.getSelectedRecords();
				var length=records.length;
				if(length==0){
					return;
				}
//				var phrIds=new Array();
//				for(var i=0;i<length;i++){
//					var r=records[i];
//					phrIds.push(r.get("phrId"));
//				}
				
				var r=records[0];
				if (r == null) {
					return;
				}
				var jobId = this.mainApp.jobId;
				 if(jobId == "chis.22"){
				     	  if(r.get("isSecondVerify")=="2" ){
					       MyMessageTip.msg("提示",
					       "存在已开放的居民档案，无法进行退回，请先进行取消开放!", true);
					        return;
				     	   }
				    }else if(jobId == "chis.23"){
					 	   if(r.get("isSecondVerify")=="2" ){
					       MyMessageTip.msg("提示",
					       "存在已开放的居民档案，无法进行退回，请先进行取消开放!", true);
					        return;
				     	   }
				    }else{
				    	MyMessageTip.msg("提示",
					       "当前登录的角色没有权限，请切换用户角色!", true);
					        return;
				    }
				var m = this.getHealthBackForm(r);
				m.opener = this;
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
				var formData = this.castListDataToForm(r, this.schema);
				m.initFormData(formData);
			},
			getHealthBackForm : function(r) {
				var m = this.midiModules["healthBackForm"];
				if (!m) {
					var cfg = {};
					cfg.mainApp=this.mainApp;
					var moduleCfg = this.mainApp.taskManager.loadModuleCfg(this.healthBackFormRef);
					Ext.apply(cfg, moduleCfg.json.body);
					Ext.apply(cfg, moduleCfg.json.body.properties);
					var cls = cfg.script;
					$import(cls);
					m = eval("new " + cls + "(cfg)");
					m.on("save", this.refresh, this);
					m.on("close", this.active, this);
					this.midiModules["healthBackForm"] = m;
				}else {
					m.initDataId = r.get("empiId");
				}
				return m;
			},
			castListDataToForm : function(data, schema) {
				var formData = {};
				var items = schema.items;
				var n = items.length;
				for (var i = 0; i < n; i++) {
					var it = items[i];
					var key = it.id;
					if (it.dic) {
						var dicData = {
							"key" : data[key],
							"text" : data[key + "_text"]
						};
						formData[key] = dicData;
					} else {
						formData[key] = data[key];
					}
				}
				Ext.applyIf(formData, data);
				return formData;
			},
			doOpen : function() {// 2018-09-26 Wangjl 开放按钮方法
				var records = this.getSelectedRecords();
				var length = records.length;
				if (length == 0) {
					return;
				}
				var jobId = this.mainApp.jobId;
				var phrIds = new Array();
				for (var i = 0; i < length; i++) {
					var r = records[i];
					phrIds.push(r.get("phrId"));
					 if((jobId == "chis.23")){
				     	  if(r.get("isSecondVerify")=="2" ){
					       MyMessageTip.msg("提示",
					       "存在已开放的居民档案，无需再次开放!", true);
					        return;
				     	   }
				     	  }else{
					 	    MyMessageTip.msg("提示",
					       "当前登录的角色没有权限，请登录健康档案三级审核角色!", true);
					       return;
				         }
				}  
				Ext.Msg.confirm("请确认", "确认开放选中的档案吗?", function(btn) {
							if (btn == 'yes') {
								var data = {
									"phrIds" : phrIds
								};
								util.rmi.jsonRequest({
											serviceId : this.saveServiceId,
											serviceAction : this.serviceActionOpen,
											method : "execute",
											body : data
										}, function(code, msg, json) {
											if (code >= 300) {
												this
														.processReturnMsg(code,
																msg);
												return;
											}
											 MyMessageTip.msg("通知",
					                         "档案开放成功!", true);
											this.refresh();
											this.clearSelect();
										}, this)
							}
						}, this);
			},
			doCancelOpen : function() {// 2018-09-26 Wangjl 取消开放按钮方法
				var records = this.getSelectedRecords();
				var length = records.length;
				if (length == 0) {
					return;
				}
				var jobId = this.mainApp.jobId;
				var phrIds = new Array();
				for (var i = 0; i < length; i++) {
					var r = records[i];
					phrIds.push(r.get("phrId"));
					 if((jobId == "chis.23")){
				     	  if(r.get("isSecondVerify")!="2" ){
					       MyMessageTip.msg("提示",
					       "存在选中的档案状态为未开放状态，无需取消开放!", true);
					        return;
				     	  }
				     	  }else{
					 	    MyMessageTip.msg("提示",
					       "当前登录的角色没有权限，请登录健康档案三级审核角色!", true);
					       return;
				         }
				}
				Ext.Msg.confirm("请确认", "是否取消开放选中的记录?", function(btn) {
					if (btn == 'yes') {
						var data = {
							"phrIds" : phrIds
						};
						util.rmi.jsonRequest({
									serviceId : this.saveServiceId,
									serviceAction : this.serviceActionCancelOpen,
									method : "execute",
									body : data
								}, function(code, msg, json) {
									if (code >= 300) {
										this.processReturnMsg(code, msg);
										return;
									}
									 MyMessageTip.msg("通知",
					                         "档案取消开放成功!", true);
									this.refresh();
									this.clearSelect();
								}, this)
					}
				}, this);
			}
//			doRemark : function() {
//				var records=this.getSelectedRecords();
//				var length=records.length;
//				if(length==0){
//					return;
//				}
//				var phrIds=new Array();
//				for(var i=0;i<length;i++){
//					var r=records[i];
//					phrIds.push(r.get("phrId"));
//				}
//				Ext.Msg.confirm("请确认", "是否标记选中记录?", function(btn) {
//					if (btn == 'yes') {
//						var data = {
//							"phrIds" : phrIds
//						};
//						util.rmi.jsonRequest({
//									serviceId : this.saveServiceId,
//									serviceAction : this.serviceActionRemark,
//									method:"execute",
//									body : data
//								}, function(code, msg, json) {
//									if (code >= 300) {
//										this.processReturnMsg(code, msg);
//										return;
//									}
//									this.refresh();
//									this.clearSelect();
//								}, this)
//					}
//				}, this);
//			},
//			doCancelRemark : function() {
//				var records=this.getSelectedRecords();
//				var length=records.length;
//				if(length==0){
//					return;
//				}
//				var phrIds=new Array();
//				for(var i=0;i<length;i++){
//					var r=records[i];
//					phrIds.push(r.get("phrId"));
//				}
//				Ext.Msg.confirm("请确认", "是否取消标记选中记录?", function(btn) {
//					if (btn == 'yes') {
//						var data = {
//							"phrIds" : phrIds
//						};
//						util.rmi.jsonRequest({
//									serviceId : this.saveServiceId,
//									serviceAction : this.serviceActionCancelRemark,
//									method:"execute",
//									body : data
//								}, function(code, msg, json) {
//									if (code >= 300) {
//										this.processReturnMsg(code, msg);
//										return;
//									}
//									this.refresh();
//									this.clearSelect();
//								}, this)
//					}
//				}, this);
//			}
		});