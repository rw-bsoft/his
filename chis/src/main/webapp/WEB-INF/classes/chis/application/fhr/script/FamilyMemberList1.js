
$package("chis.application.fhr.script");

$import("chis.script.BizSimpleListView");

chis.application.fhr.script.FamilyMemberList1 = function(cfg) {
	cfg.westWidth = 150;
	chis.application.fhr.script.FamilyMemberList1.superclass.constructor.apply(
			this, [cfg]);
	this.requestData.queryCndsType = "1";
	this.on("save", this.onSave, this);
	this.flagEmpty = true;
	// this.familyIdIs="";
	this.on("remove",this.onRemove,this);
}

Ext.extend(chis.application.fhr.script.FamilyMemberList1,
		chis.script.BizSimpleListView, {
			loadData : function() {
				if (this.initDataId != this.exContext.args.initDataId) {
					this.initCnd = ['and',
							['eq', ['$', 'a.familyId'],
									['s', this.exContext.args.initDataId]],
							['eq', ['$', 'a.status'], ['s', '0']]];

					this.requestData.cnd = this.initCnd;
					this.initDataId = this.exContext.args.initDataId
					// 清理查询条件
					this.cndFldCombox.setValue();
					this.cndField.setValue();
				}
				chis.application.fhr.script.FamilyMemberList1.superclass.loadData.call(this);

			},
			onStoreLoadData : function(store, records, ops) {
				if (records.length == 0) {
					return
				}
				this.totalCount = store.getTotalCount()
				this.fireEvent("loadData", store)
				if (!this.selectedIndex) {
					this.selectRow(0)
					this.selectedIndex = 0;
				} else {
					this.selectRow(this.selectedIndex);
				}
				if (store.getCount() > 0) {

					this.flagEmpty = false;
				}
			},

			doReadMember : function(item, e) {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				var empiId = r.get("empiId");
				$import("chis.script.EHRView");
				var module = this.midiModules["FamilyMember_EHRView"];
				if (!module) {
					module = new chis.script.EHRView({
								initModules : ['B_011','B_01', 'B_02', 'B_03', 'B_04','B_05'],
								empiId : empiId,
								closeNav : true,
								mainApp : this.mainApp
							});
					this.midiModules["FamilyMember_EHRView"] = module;
					module.on("save", this.refresh, this);
				} else {
					module.actionName = "EHR_HealthRecord";
					module.exContext.ids["empiId"] = empiId;
					module.refresh();
				}
				module.getWin().show();
			},

			onDblClick : function(grid, index, e) {
				this.doReadMember();
			},

			onSave : function(entryName, op, json, rec) {
				this.refresh();
			},
			doAutoMatch : function(item, e) {

				var familyId = this.initDataId;
				// 判断家庭档案户主是否存在，是否为空的档案，如果是，就返回
				var datag = {};
				datag["familyId"] = familyId;
				flagg = this.getOwnerName(datag);

				if (flagg) {
					if (!flagg.flag) {
						Ext.Msg.alert("提示", "该家庭档案没有户主，请先添加户主")
						return;
					}
				}
				var m = this.createModule("grjbxx1",
						"chis.application.hr.HR/HR/B34101")
				if (m.nll) {
					m.nll = {};
				}
				m.data["familyId"] = familyId// m.data是弹出框的（弹出框有5个表单），用来传值用的
				m.on("save", this.onSave, this)
				m.op = "create";
				m.id = "family";
				var win = m.getWin();
				win.add(m.initPanel());
				win.setPosition(250, 100);
				var data = {};
				data["familyId"] = familyId;
//				m.loadData(data);
				win.setPosition(289, 65);
				win.show();
				m.doNew();// 點擊，初始化頁面，清空上次操作
				m.familyData=this.familyData;
				m.setFamilyValue(this.familyData||{});

			},// 删除家庭成员
	doRemove : function() {var _cfg = this;
			var r = this.getSelectedRecord();
			if (r == null) {
				return;
			}
			var data = {};
			data["phrId"] = r["phrId"]
			this.familyIdIs = r["familyId"];
			var empiId = r.get("empiId");
			var vCount = this.store.getCount();

			// 查询家庭签约，删除的人是否是家庭代表
			var dataFCR = {};
			dataFCR["familyId"] = this.initDataId;
			var v = this.isFCRepre(dataFCR);
		
			if (v == empiId && vCount != 1) {
				function callback(id) {
					if (id == "yes") {
						_cfg.fireEvent("changeTo");
						return;
					}
				}
				Ext.MessageBox.confirm("选择框",
						"该成员为家庭签约代表，需要先走变更流程，是否变更签约代表？", callback);
			} else {
				function callback(id) {
					if (id == "yes") {
						_cfg.removeById(data);
						_cfg.fireEvent("winShow1");
					}
				}
				Ext.MessageBox.confirm("选择框", "是否删除成员?", callback);
			}},
			getRemoveRequest:function(r){
				return r.data;
			},
			removeById : function(data) {
				if (!data) {
					return;
				}
				this.processRemove();
			},
			onRemove : function(entryName,op,json,data){
				this.refresh();
			},
			createModule : function(moduleName, moduleId, exCfg) {
				var item = this.midiModules[moduleName]
				if (!item) {
					var moduleCfg = this.loadModuleCfg(moduleId);
					var cfg = {
						showButtonOnTop : true,
						border : false,
						frame : false,
						autoLoadSchema : false,
						isCombined : true,
						exContext : {}
					};
					Ext.apply(cfg, exCfg);
					Ext.apply(cfg, moduleCfg);
					var cls = moduleCfg.script;
					if (!cls) {
						return;
					}
					if (!this.fireEvent("beforeLoadModule", moduleName, cfg)) {
						return;
					}
					$import(cls);
					item = eval("new " + cls + "(cfg)");
					item.setMainApp(this.mainApp);
					this.midiModules[moduleName] = item;
				}
				return item;
			},
			loadModuleCfg : function(id) {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "moduleConfigLocator",
							id : id,
							method : "execute"
						})
				if (result.code != 200) {
					if (result.msg = "NotLogon") {
						this.mainApp.logon(this.loadModuleCfg, this, [id])
					}
					return null;
				}
				return result.json.body;
			},
			getOwnerName : function(data) {
				if (!this.fireEvent("beforeLoadData", this.entryName)) {
					return;
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.basicPersonalInformationService",
							serviceAction : "getOwnerName",
							method : "execute",
							body : data

						}, this)
				var flagg = result.json.body;
				if (result.code != 200) {
					if (result.msg = "NotLogon") {
						this.mainApp.logon(this.loadModuleCfg, this, [id])
					}
					return null;
				}
				return result.json.body;
			},
			doRemoval : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				var module = this.createSimpleModule("EHRConfirmForm",
						"chis.application.fhr.FHR/FHR/B011_10");
				module.on("save", this.onSave, this);
				module.initDataId = r.get("phrId");
				module.initPanel();
				this.showWin(module);
				module.loadData();

			},// 判断是否是签约代表
			isFCRepre : function(data) {

				if (!data) {
					return;
				}
				if (!this.fireEvent("beforeLoadData", this.entryName)) {
					return;
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.familyRecordService1",
							serviceAction : "getFcRepre",
							method : "execute",
							body : data,
							op : this.op
						}, this)

				var resBody = result.json.body;
				return resBody;

			},
			//签约
			doSign : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var familyId = r.get('familyId');
				var dt = {};
				dt["familyId"] = familyId
				// 判断是否有户主，取户主的empiId
				var empiId = this.isOwnerName(dt);
				if (empiId == null) {
					Ext.Msg.alert("提示", "没有找到该家庭的户主相关信息！");
					return;
				}
				//签约之前校验成员基本信息数据完整性
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.familyRecordService",
							serviceAction : "checkpeople",
							method : "execute",
							familyId : familyId
						});
				if(result.json && result.json.tsmsg && result.json.tsmsg.length > 0){
					Ext.Msg.alert("提示",result.json.tsmsg+"请先完善");
					return;
				}
				var cfg = {}
				cfg.empiId = empiId
				cfg.initModules = ['JY_01']
				cfg.closeNav = true
				cfg.mainApp = this.mainApp
				cfg.exContext = this.exContext
				var module = this.midiModules["FamilyRecordList_EHRView"]
				if (!module) {
					$import("chis.script.EHRView")
					module = new chis.script.EHRView(cfg)
					module.exContext.args={initDataId:familyId};	
					module.on("save", this.onSave, this)
					this.midiModules["FamilyRecordList_EHRView"] = module
				} else {
					Ext.apply(module, cfg)
					module.exContext.ids = {}
					module.exContext.ids.empiId = empiId
					module.exContext.args={initDataId:familyId};
					module.refresh()
				}
				module.getWin().show()

			},
			isOwnerName : function(data) {
				var result = util.rmi.miniJsonRequestSync({// 同步方法
					serviceId : "chis.familyRecordService",
					serviceAction : "isOwnerName",
					method : "execute",
					body : data
				})
				if (result.code != 200) {
					Ext.Msg.alert("提示", result.json.msg);
					return null;
				}
				if (result.json.body == null) {
					return;
				}
				return result.json.body.empiId;

			}
		})
