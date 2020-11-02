/**
 * 个人基本信息综合模块
 * 
 * @author tianj
 */
$package("phis.application.war.script");

$import("phis.script.SimpleModule");

phis.application.war.script.MedicalBackApplicationModule = function(cfg) {
	this.exContext = {};
	//this.initDataId = 2012005;// 先写死,以后调这模块的界面传入
	phis.application.war.script.MedicalBackApplicationModule.superclass.constructor
			.apply(this, [cfg]);
			this.on("winShow",this.afterOpen,this);
}

Ext.extend(phis.application.war.script.MedicalBackApplicationModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.mainApp['phis'].wardId == null || this.mainApp['phis'].wardId == ""
						|| this.mainApp['phis'].wardId == undefined) {
					Ext.Msg.alert("提示", "未设置病区,请先设置");
					return null;
				}
				if (this.panel)
					return this.panel;

				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							tbar : this.createButtons(),
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'north',
										height : 76,
										items : [this.getForm()]
									}, {
										layout : "border",
										border : false,
										split : true,
										region : 'center',
										items : [{
													layout : "fit",
													border : false,
													split : true,
													region : 'west',
													width : 500,
													items : this.getLeftList()
												}, {
													layout : "fit",
													border : false,
													split : true,
													region : 'center',
													items : this
															.getRightModule()
												}]
									}]
						});
				this.panel = panel;
				return panel;
			},
			getForm : function() {
				this.form = this.createModule("medicalbkform", this.refForm);
				//this.form.autoFieldWidth = false;
				//this.form.fldDefaultWidth = 110
				return this.form.initPanel();
			},
			getLeftList : function() {
				this.leftList = this.createModule("leftList", this.refLeftList);
				this.leftList.on("recordClick", this.onLeftRecordClick, this);
				return this.leftList.initPanel();
			},
			getRightModule : function() {
				this.rightModule = this.createModule("rightModule",
						this.refRightModule);
				this.rightModule.on("leftLoad", this.onLeftLoad, this)
				return this.rightModule.initPanel();
			},
			afterOpen : function() {
				if (!this.leftList || !this.rightModule.topList || !this.form
						|| !this.rightModule.underList) {
					return;
				}
				this.form.initDataId = this.initDataId;
				this.form.loadData();
				//this.form.loadData(this.initDataId);
				this.leftList.loadData(this.mainApp['phis'].wardId, this.initDataId);
				this.rightModule.topList.loadData(this.initDataId);
				this.rightModule.underList.loadData(this.initDataId);
				// 拖动操作
				var firstGrid = this.rightModule.topList;
				var grid = this.leftList;
				var firstGridDropTargetEl = firstGrid.grid.getView().scroller.dom;
				var secondGridDropTargetEl = grid.grid.getView().scroller.dom;
				var _ctr = this;
				var firstGridDropTarget = new Ext.dd.DropTarget(
						firstGridDropTargetEl, {
							ddGroup : 'firstGridDDGroup',
							notifyDrop : function(ddSource, e, data) {
								var records = ddSource.dragData.selections;
								for (var i = 0; i < records.length; i++) {
									firstGrid.addRecord(records[i].data)
								}
								return true
							}
						});
				var secondGridDropTarget = new Ext.dd.DropTarget(
						secondGridDropTargetEl, {
							ddGroup : 'secondGridDDGroup',
							notifyDrop : function(ddSource, e, data) {
								var records = ddSource.dragData.selections;
								for (var i = 0; i < records.length; i++) {
									firstGrid.store.remove(records[i])
								}
								_ctr.onLeftLoad(2)
//								if (firstGrid.store.getCount() == 0) {
//									_ctr.onLeftLoad(0)
//								}
								return true
							}
						});
				if (this.rightModule.topList.store.getCount() > 0) {
					this.setButtonsState(['confirm'], true);
					this.setButtonsState(['save'], false);
				} else {
					this.setButtonsState(['save'], false);
					this.setButtonsState(['confirm'], false);
				}
			},
			// 改变保存和提交按钮
			onLeftLoad : function(tag) {
				if (tag == 0) {
					this.setButtonsState(['confirm'], false);
					this.setButtonsState(['save'], false);
				} else if (tag == 1) {
					this.setButtonsState(['confirm'], true);
					this.setButtonsState(['save'], false);
				} else {
					this.setButtonsState(['confirm'], false);
					this.setButtonsState(['save'], true);
				}
			},
			// 改变按钮状态
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				if (this.showButtonOnTop) {
					btns = this.panel.getTopToolbar();
				} else {
					btns = this.panel.buttons;
				}

				if (!btns) {
					return;
				}

				if (this.showButtonOnTop) {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns.items.item(m[j]);
						} else {
							btn = btns.find("cmd", m[j]);
							btn = btn[0];
						}
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
						}
					}
				} else {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns[m[j]];
						} else {
							for (var i = 0; i < this.actions.length; i++) {
								if (this.actions[i].id == m[j]) {
									btn = btns[i];
								}
							}
						}
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
						}
					}
				}
			},
			// 双击左边回填右边
			onLeftRecordClick : function(r) {
				this.rightModule.topList.addRecord(r);
			},
			fjxmts : function(record){
//				var datas = this.rightModule.topList.datas;
				var bodys = [];
				for(var j = 0 ; j < record.length ; j ++){
					var data = record[j];
					for(var i = 0 ; i < this.leftList.store.getCount(); i ++){
						var mx = this.leftList.store.getAt(i).data;
						if (data["YPXH"] == mx["YPXH"]
						&& data["YPDJ"] == mx["YPJG"]
						&& data["YPCD"] == mx["YPCD"]
						&& data["ZFBL"] == mx["ZFBL"]
						&& data["YZXH"] == mx["YZID"]) {
							bodys.push({KSDM : mx["LYBQ"],
								YZXH : mx["YZXH"]})
						}
					}
				}
				
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "wardPatientManageService",
					serviceAction : "loadAppendAdviceTYSQ",
					body : bodys
					});
				if (r.code < 300) {
					if(r.json.body){
						if(r.json.body.length>0){
							return "当前退药药品有附加项目，请到退费处理模块进行退费!";
						}
					}
				}
				return false;
			},
			// 保存
			doSave : function() {
				if (!this.rightModule.topList) {
					return;
				}
				var module = this.rightModule.topList
				var count = module.store.getCount();
				var record = [{"ZYH":this.initDataId}];
//				if (count == 0) {
//					return;
//				}
				for (var i = 0; i < count; i++) {
					var r = module.store.getAt(i).data;
					if (r["YPSL"] == 0) {
						continue;
					} else {
						record.push(r);
					}
				}
				//增加有附加项目时提示退附加项目
				var restr = this.fjxmts(record);
				if(restr){
					Ext.Msg.alert("提示", restr);
				}
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.saveActionId,
							body : record
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doSave);
					return;
				}
				this.rightModule.topList.datas = [];
				MyMessageTip.msg("提示", "保存成功", true);
				this.rightModule.topList.addnum = 0;
				this.rightModule.topList.loadData(this.initDataId);
				this.rightModule.underList.loadData(this.initDataId);
			},
			// 提交
			doConfirm : function() {
				if (!this.rightModule.topList) {
					return;
				}
				var module = this.rightModule.topList
				var count = module.store.getCount();
				var record = [];
				if (count == 0) {
					return;
				}
				body = {};
				body["zyh"] = module.store.getAt(0).data["ZYH"];
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.commitActionId,
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doConfirm);
					return;
				}
				MyMessageTip.msg("提示", "提交成功", true);
				this.rightModule.topList.loadData(this.initDataId);
				this.rightModule.underList.loadData(this.initDataId);
				this.leftList.loadData(this.mainApp['phis'].wardId, this.initDataId);
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
			doCancel:function(){
			this.getWin().hide();
			}
		});