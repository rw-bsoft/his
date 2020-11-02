/**
 * 个人健康档案列表页面
 * 
 * @author : tianj
 */
$package("chis.application.hr.script")

$import("chis.script.BizSimpleListView", "chis.script.demographicView")

chis.application.hr.script.HealthRecordList = function(cfg) {
	//this.initCnd = cfg.cnds || ["eq", ["$", "a.status"], ["s", "0"]];
	this.initCnd = ['in', ['$', 'a.status'], ['0', '2']]
	//this.needOwnerBar = true;
	chis.application.hr.script.HealthRecordList.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(chis.application.hr.script.HealthRecordList,
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

			doWriteOff : function() {

				var r = this.getSelectedRecord();
				if (this.store.getCount() == 0) {
					return;
				}
                if(this.mainApp.jobId!='chis.14' && r.get("manaDoctorId")!=this.mainApp.uid){
                	alert("只有防保科长或现责任医生能注销档案！")
                	return;
                }
				var cfg = {
					title : "健康档案注销",
					phrId : r.get("phrId"),
					personName : r.get("personName"),
					empiId : r.get("empiId"),
					mainApp : this.mainApp
				};
				var module = this.midiModules["HealthRecordLogoutForm"];
				if (!module) {
					$import("chis.application.hr.script.HealthRecordLogoutForm");
					module = new chis.application.hr.script.HealthRecordLogoutForm(cfg);
					module.on("remove", this.onRemove, this);
					module.initPanel();
					this.midiModules["HealthRecordLogoutForm"] = module;
				} else {
					Ext.apply(module, cfg);
				}
				module.getWin().show();
			},

			onRemove : function() {
				this.loadData();
			},

			doShowModule : function(item, e) {
				this.showModule(item, e);
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
			doShowBackReason : function() {//责任医生查看档案退回原因
				var r = this.grid.getSelectionModel().getSelected();
				this.record = r.data;
				var empiId = this.record.empiId;
				var phrId = this.record.phrId;
				var jobId = this.mainApp.jobId;
				if(jobId == "chis.01"){//责任医生角色
					util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : "queryBackRecord",
							method:"execute",
							body : {phrId:phrId}
						}, function(code, msg, json) {
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							if(json.data.length>0){
								var backReason = json.data[0].backReason;
								Ext.Msg.alert("退回原因", backReason);
							}
						}, this)
				}
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
			doGraphic : function() {
				var r = this.grid.getSelectionModel().getSelected();
				if (!r) {
					return;
				}
				var demograhpic = this.midiModules["demographic"];
				if (!demograhpic) {
					demographic = new chis.script.demographicView({
								empiId : r.data.empiId,
								birthday : r.data.birthday,
								list : this
							});
					this.midiModules["demographic"] = demographic;
				} else {
					demographic.exContext.ids.empiId = r.data.empiId;
					demographic.exContext.ids.birthday = r.data.birthday;
					demograhpic.reloadUrl();
				}
				demographic.getWin().show();
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
					var initModules = ['B_011','B_04', 'B_05'];
					if (jobId == "chis.04") {
						var initModules = ['B_01', 'B_02', 'B_04', 'B_05'];
					} else if (jobId == "chis.13") {
						var initModules = ['B_01'];
					}
					if (r.incomeSource && r.incomeSource.indexOf("1") > -1) {
						if (jobId != "gp.100" && jobId != "gp.101")
							initModules.push("B_08");
					}
					//if (this.checkAge(birthDay) && jobId != "chis.07"
					//		&& jobId != "chis.08" && jobId != "chis.15"
					//		&& jobId != "chis.16") {
					//	initModules.push("B_07");
						//initModules.push("B_06");
					//}
					//if (this.checkHasAssessRegisterModule(birthDay)
					//		&& jobId != "chis.07" && jobId != "chis.08"
					//		&& jobId != "chis.15" && jobId != "chis.16") {
				    //     initModules.push("B_12");
					//}

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
					//if (!this.checkHasAssessRegisterModule(birthDay)
					//		&& jobId != "chis.07" && jobId != "chis.08"
					//		&& jobId != "chis.15" && jobId != "chis.16"
					//		&& module.activeModules["M_01"]) {
					 //       module.activeModules["B_12"] = false;
					//	if (module.mainTab.find("mKey", "B_12").length > 0) {
					//		module.mainTab.remove(module.mainTab.find("mKey",
					//				"B_12")[0]);
					//	}
					//} else if (this.checkHasAssessRegisterModule(birthDay)
					//		&& jobId != "chis.07" && jobId != "chis.08"
					//		&& jobId != "chis.15" && jobId != "chis.16"
					//	&& !module.activeModules["B_12"]) {
					//	if (module.mainTab.find("mKey", "B_12").length == 0) {
					//		module.activeModules["B_12"] = true;
					//		module.mainTab.add(module.getModuleCfg("B_12"));
					//	}
					//}
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
			doZlls : function() {
				var r = this.grid.getSelectionModel().getSelected();
				if(!r){
					alert("请选择一条居民记录");
					return ;
				}
				var idcardnow=r.data.idCard;
				if( idcardnow.length != 18 ){
					alert("身份证号不是18位");
					return ;
				}
//				//调用大数据健康档案浏览器接口服务，跳转html页面  zhaojian 2017-11-01
				var params_array= [{name:"idcard",value:idcardnow.replace(/(^\s*)|(\s*$)/g, "")},{name:"sys_organ_code",value:this.mainApp.deptId.replace(/(^\s*)|(\s*$)/g, "")},{name:"sys_code",value:"jkda"},{name : "opeCode",value : this.mainApp.uid},{name : "opeName",value : this.mainApp.uname}];
				util.rmi.jsonRequest({
					serviceId : "chis.desedeService",
					schema : "",
					serviceAction : "getDesInfo",
					method : "execute",
					params : JSON.stringify(params_array)
				}, function(code, msg, json) {
					if (msg == "Success") {
						this.openBHRView(json,"getPersonInfo");                  		
					} else {
						Ext.Msg.alert("提示", "操作失败");
						return false;
					}
				}, this)
			},
			showColor : function(value, metaData, r, row, col) {
				var nowdate = new Date();
		        nowdate.setMonth(nowdate.getMonth()+1);
		        var y = nowdate.getFullYear();
		        var m = nowdate.getMonth()+1;
		        var d = nowdate.getDate();
		        var nextMonthDate = y+'-'+((m+"").length==1?("0"+m):m)+'-'+((d+"").length==1?("0"+d):d);
		        nowdate.setMonth(nowdate.getMonth()-1);
		        y = nowdate.getFullYear();
		        m = nowdate.getMonth()+1;
		        d = nowdate.getDate();
		        var currentDate = y+'-'+((m+"").length==1?("0"+m):m)+'-'+((d+"").length==1?("0"+d):d);
				//客户端电脑时间与签约结束时间对比判定
				if(value=="已签约" && r.data.sceEndDate.length>0 && nextMonthDate>=r.data.sceEndDate && currentDate<=r.data.sceEndDate){
					return "<font style='color:red'>即将到期</font>";
				}else if(value=="已签约" && r.data.sceEndDate.length>0 && currentDate>r.data.sceEndDate){
					return "<font style='color:red'>已到期</font>";
				}
				return value;
			}
		});