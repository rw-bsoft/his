$package("chis.application.ohr.script")

$import("chis.script.BizSimpleListView",
		"chis.application.mpi.script.EMPIInfoModule", "chis.script.EHRView")

chis.application.ohr.script.MedicineManageList = function(cfg) {
	cfg.initCnd = ["eq", ["$", "a.status"], ["s", "0"]];
	chis.application.ohr.script.MedicineManageList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.ohr.script.MedicineManageList,
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
							id : "chis.dictionary.year",
							forceSelection : true,
							defaultValue : {
								key : "2018",
								text : "2018年"
							}
						});
				comb.on("select", this.radioChanged, this);
				comb.setValue("2018");
				comb.setWidth(80);
				var lab = new Ext.form.Label({
							html : "&nbsp;&nbsp;年度:"
						});
	            cfg.items = [lab, comb];
				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
				this.pagingToolbar = pagingToolbar;
				return pagingToolbar;
			},
			
			radioChanged : function(r) {
				var year = r.getValue();
				var statusCnd = ["and",["eq", ["$", "a.year"], ["s", year]],["eq", ["$", "a.status"], ["s", "0"]]];				
				this.initCnd =statusCnd ;
				var cnd = statusCnd;
				debugger;

				this.requestData.cnd = cnd;
				this.requestData.pageNo = 1;
				this.refresh();
			},
			
			refresh : function() {
				chis.application.ohr.script.MedicineManageList.superclass.loadData
						.call(this);
			},

			onDblClick : function(grid, index, e) {
				this.doModify();
			},

			showModule : function() {
//				var module;
//				if (!module) {
//					alert("this.empiId:"+this.empiId)
				var	module = new chis.script.EHRView({
								initModules : ['B_12'],
								empiId : this.empiId,
								closeNav : true,
								activeTab : 0,
								mainApp : this.mainApp
							});
					console.log(this.mainApp)		
					this.midiModules["ChineseMedicineManage_EHRView"] = module;
					module.on("save", this.refresh, this);
//				} else {
//					module.exContext.ids["empiId"] = this.empiId;
//					module.refresh();
//				}
				if(this.id){
					module.exContext.args["id"] = this.id;
				}
				module.actionName = "ChineseMedicineManage";
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
				if (birth.getFullYear() <= currDate.getFullYear()) {
					return true;
				} else {
					return false;
				}
			},

			onEmpiSelected : function(empi) {
				//alert("123"+empi.empiId+empi.personName)
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
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.chineseMedicineManageService",
							serviceAction : "checkHasOldRecord",
							method : "execute",
							empiId : this.empiId
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return
				}
				this.exContext.empiData=empi;
				//alert(Ext.encode(this.exContext.empiData))
				var hasOldRecord = result.json.hasOldRecord;
				if (hasOldRecord) {
					this.showModule();
				} else {
					this.showEhrViewWin();
				}
				
			},

			showEhrViewWin : function() {
				var m = this.midiModules["ehrView"];
				if (!m) {
					m = new chis.script.EHRView({
								closeNav : true,
								initModules : ['B_07', 'B_12'],
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
				this.id = r.get("id");
				this.showModule(r.data);
			},
			onRemove : function() {
				this.loadData();
			},
			doWriteOff : function() {
				var record = this.getSelectedRecord();
				if (!record) {
					return;
				}
				Ext.Msg.show({
							title : '档案注销',
							msg : '档案注销后将无法操作，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.writeOffRecord(record);
								}
							},
							scope : this
						});
			},
			writeOffRecord : function(record) {
				util.rmi.jsonRequest({
							serviceId : "chis.chineseMedicineManageService",
							schema : this.entryName,
							method : "execute",
							serviceAction : "logoutChineseMedicineManage",
							pkey : record.get("id")
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.revertHypertensionRecord,
										[saveData]);
								return
							}
							this.refresh();
						}, this);

			},
			onRowClick : function(grid, index, e) {
				this.selectedIndex = index;
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var btns = this.grid.getTopToolbar();
				var btn = btns.find("cmd", "writeOff");
				btn = btn[0];
				if (r.data.status == "1") {
					btn.disable();
				} else {
					btn.enable();
				}
			},
			onCndFieldSelect : function(item, record, e) {
				var tbar = this.grid.getTopToolbar()
				var tbarItems = tbar.items.items
				var itid = record.data.value
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == itid) {
						it = items[i]
						break
					}
				}
				var field = this.cndField
				field.hide()

				// field.destroy()
				var f = this.midiComponents[it.id]
				if (!f) {
					if (it.dic) {
						it.dic.src = this.entryName + "." + it.id
						it.dic.defaultValue = it.defaultValue
						it.dic.width = this.cndFieldWidth || 200;
						if (it.id == "manaDoctorId") {
							it.dic.parentKey = "%user.manageUnit.id";
						}
						f = this.createDicField(it.dic)
					} else {
						f = this.createNormalField(it)
					}
					this.doAfterCreateQueryField(f, it);
					f.on("specialkey", this.onQueryFieldEnter, this)
					this.midiComponents[it.id] = f
				} else {
					f.show()
					// f.rendered = false
				}
				tbarItems[2] = f
				tbar.doLayout(true)
				this.cndField = f
			}
		});