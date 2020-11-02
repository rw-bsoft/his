$package("chis.application.fhr.script")

$import("chis.script.BizSimpleListView", "chis.script.PrintWin2","util.Base64")

chis.application.fhr.script.FamilyRecordList = function(cfg) {
	
	cfg.initCnd = ['eq', ['$', 'status'], ['s', '0']];
	var jobId = cfg.mainApp.jobId;
	// 按钮权限的过滤
	this.needOwnerBar = false;
	chis.application.fhr.script.FamilyRecordList.superclass.constructor.apply(
			this, [cfg]);

}

Ext.extend(chis.application.fhr.script.FamilyRecordList,
		chis.script.BizSimpleListView, {
			createButtons : function() {
				var actions = this.actions
				var buttons = []
				if (!actions) {
					return buttons
				}
				var f1 = 112
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					if (action.hide) {
						continue
					}
					var btn = {
						accessKey : f1 + i + this.buttonIndex,
						text : action.name + "(F" + (i + 1 + this.buttonIndex)
								+ ")",
						ref : action.ref,
						target : action.target,
						cmd : action.delegate || action.id,
						iconCls : action.iconCls || action.id,
						enableToggle : (action.toggle),
						script : action.script,
						handler : this.doAction,
						prop : {},
						scope : this
					}
					Ext.apply(btn.prop, action);
					Ext.apply(btn.prop, action.properties);
					buttons.push(btn)
				}
				return buttons

			},
			// 重写原因:户主姓名是个虚拟字段，form中是字典，list上的查询是文本。
			initPanel : function(sc) {
				var p = chis.application.fhr.script.FamilyRecordList.superclass.initPanel
						.call(this, sc);
				var mySchema = {};
				Ext.apply(mySchema, this.schema);
				var myItems = [];
				for (var i = 0; i < this.schema.items.length; i++) {
					var item = this.schema.items[i];
					if (item.id == 'ownerName') {
						var oItem = {};
						Ext.apply(oItem, item);
						oItem.dic = null;
						myItems[i] = oItem;
						continue;
					}
					myItems[i] = item;
				}
				mySchema.items = myItems;
				this.schema = mySchema;
				return p;
			},

			onStoreLoadData : function(store, records, ops) {
				chis.application.fhr.script.FamilyRecordList.superclass.onStoreLoadData
						.call(this, store, records, ops);

				this.onRowClick()
			},
			onSave : function(entryName, op, json, rec) {

				this.refresh();
			},

			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return
				}

				var bts = this.grid.getTopToolbar().items;
				if (bts.items[7]) {
					var manaDoctorId = r.get("manaDoctorId");
					if (this.mainApp.uid == manaDoctorId) {
						bts.items[7].enable();
					}
					// else {
					// bts.items[7].disable();
					// }
				}
			},
			doRemove : function() {

				var r = this.getSelectedRecord();
				if (!r) {
					return
				}
				var data = {};
				data["familyId"] = r.id;
				data['empiId'] = r.get("empiId") || '';
				this.removeByFamilyId(data);

			},

			doPrint : function() {
				var cm = this.grid.getColumnModel()
				var pWin = this.midiModules["printView2"]
				var cfg = {
					title : this.title,
					requestData : this.requestData,
					cm : cm
				}
				if (pWin) {
					Ext.apply(pWin, cfg)
					pWin.getWin().show()
					return
				}
				pWin = new chis.script.PrintWin2(cfg)
				this.midiModules["printView2"] = pWin
				pWin.getWin().show()
			},
			getSystem : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.systemManageTypeService",
							serviceAction : "getConfig",
							method : "execute"
						})
				if (result.code != 200) {
					Ext.Msg.alert("提示", msg);
					return null;
				}
				return result.json.body;
			},
			getModuleId : function(){
				var result = this.getSystem();
				var part="part";
				if (result) {
					part = result.areaGridType.key;
				}
				if (part == null) {
					part = "part";
				}
				var moduleId = "chis.application.fhr.FHR/FHR/B011";
				if ("part" == part) {
					moduleId = "chis.application.fhr.FHR/FHR/B0110";
				}else{
					moduleId = "chis.application.fhr.FHR/FHR/B011";
				}
				return moduleId;
			},
			doCreateFHR : function(){
				var module = this.createSimpleModule(
						"FamilyRecordModule", this.getModuleId());
				module.initPanel();
				module.on("save", this.afterSave, this);
				module.initDataId = null;
				Ext.apply(module.exContext, this.exContext);
				module.exContext.control = {};
				var win = module.getWin();
				var x = (document.body.clientWidth - win.getWidth()) / 2;
				var y = (document.body.clientHeight - 400) / 2;
				win.setTitle(module.title)
				win.setPosition(x, y);
				win.show();
				module.doNew();
			},
			doModify : function(){
				debugger;
			  	this.url="replace=true&uid=0310581X&pass=ls1&role=chis.system&cls=chis.application.wl.script.FromhisList&entryName=chis.application.wl.schemas.MDC_FromHis&navField=Status&refConfirmModule=chis.application.wl.WL/WL/R04_2&empiId=32012419610708241200000000000000";
		        var base64 = new util.Base64();
		        this.url = base64.base64encode(base64.utf16to8(this.url));
				console.log(this.url);
			  //  alert(this.url);
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				var module = this.createSimpleModule(
						"FamilyRecordModule", this.getModuleId());
				module.initPanel();
				module.on("save", this.afterSave, this);
				module.initDataId = null;
				Ext.apply(module.exContext, this.exContext);
				module.initDataId = r.id;
				module.exContext[this.entryName] = r;
				module.exContext.control = {};
				var win = module.getWin();
				var x = (document.body.clientWidth - win.getWidth()) / 2;
				var y = (document.body.clientHeight - 400) / 2;
				win.setTitle(module.title)
				win.setPosition(x, y);
				win.show();
				module.loadData();
			},
			afterSave : function(){
				this.refresh();
			},
			onDblClick : function(grid,index,e){
				this.doModify();
			},
			loadRemote : function(ref, btn) {

				var result = this.getSystem();

				if (result) {
					r = result.areaGridType.key;
				}
				if (r == null) {
					r = "part";
				}
				if ("part" == r) {

					if (cmd != "sign") {
						ref = "chis.application.fhr.FHR/FHR/B0110";
					}
					// else {
					// ref = "chis.application.fhr.FHR/FHR/B011";
					// }

				} else {
					if (cmd != "sign") {
						ref = "chis.application.fhr.FHR/FHR/B011";
					}

				}
				var r = this.getSelectedRecord()
				var cmd = btn.cmd
				if (cmd == "sign") {
					if (r == null) {
						return
					}
				}
				chis.application.fhr.script.FamilyRecordList.superclass.loadRemote
						.call(this, ref, btn)
			},

			openModule : function(cmd, r, xy) {

				var module = this.midiModules[cmd]// 缓存
				module.on("winShow2", this.winS2, this);
				module.on("save", this.onSave, this);
				module.on("close",this.refresh,this);
				if (module) {

					var win = module.getWin()
					var x = (document.body.clientWidth - win.getWidth()) / 2;
					var y = (document.body.clientHeight - 400) / 2;
					win.setTitle(module.title)
					win.setPosition(x, y);
					win.show()
					if (cmd == "sign") {
						module.activeModuleId = 3;
					} else {
						module.activeModuleId = 0;
					}
					this.fireEvent("openModule", module);

					if (!win.hidden) {
						switch (cmd) {
							case "create" :
								module.doNew()
								break;
							// case "sign" :
							case "update" :
								module.loadData()
						}
					}
				}
			},
			removeByFamilyId : function(data) {

				if (!this.fireEvent("beforeLoadData", this.entryName)) {
					return;
				}

				util.rmi.jsonRequest({
							serviceId : "chis.familyRecordService",
							serviceAction : "isFamilyNumber",
							method : "execute",
							body : data

						}, function(code, msg, json) {

							// this.form.el.unmask();
							this.saving = false;
							var resBody = json.body;

							if (code > 300) {
								Ext.Msg.alert("提示", msg);
								return;
							}
							if (code == 200) {
								Ext.Msg.confirm("确认删除记录[" + data["familyId"]
												+ "]", "删除操作将无法操作，是否继续？",
										function(btn) {// 先提示是否删除
											if (btn == 'yes') {
												util.rmi.jsonRequest({
													serviceId : "chis.familyRecordService",
													serviceAction : "removeFamilyInfo",
													method : "execute",
													body : data

												}, function(code, msg, json) {

												}, this);
												this.refresh();
											}
										}, this);

							}

							return resBody;

						}, this)
			},
			winS2 : function() {

				this.onSave();
			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var ref = item.ref

				if (ref) {
					this.loadRemote(ref, item)
					return;
				}
				var script = item.script
				if (cmd == "create") {
					if (!script) {
						script = this.createCls
					}
					this.loadModule(script, this.entryName, item)
					return
				}
				if (cmd == "update" || cmd == "read") {
					var r = this.getSelectedRecord()
					if (r == null) {
						return
					}
					if (!script) {
						script = this.updateCls
					}
					this.loadModule(script, this.entryName, item, r)
					return
				}
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
				if (script) {
					$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
				} else {
					var action = this["do" + cmd]
					if (action) {
						action.apply(this, [item, e])
					}
				}
			},
			doSign : function() {
				var r = this.grid.getSelectionModel().getSelected()
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