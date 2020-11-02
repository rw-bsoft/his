/**
 * 
 * @author tianj
 */
$package("phis.application.med.script");

phis.application.med.script.MZBusiListProjectManage = function(cfg) {
	this.width = 960;
	this.height = 600;
	this.modal = true
	phis.application.med.script.MZBusiListProjectManage.superclass.constructor.apply(
			this, [cfg]);
	this.exContext.args = this.exContext.args || {};
	this.on('doSave', this.doSave, this);
}
Ext.extend(phis.application.med.script.MZBusiListProjectManage,
		phis.script.SimpleModule, {
			onBeforeCellEdit : function(it, record, field, value) {
				if (this.param.XZPB != 1) { //不新增 就是 0
					if (this.param.TYPE == 0) { //未收费
						if (it.id == "KSDM") {
							return false;
						}
					} else { //已收费
						if (it.id == "XMMC"
								|| it.id == "YLSL" ) {
							return true;
						}else if(it.id == "MZHM" || it.id == "YSDM"
							|| it.id == "KSDM" || it.id == "YLDJ"){
							return false;
						}
					}
				}
			},
			onReady : function() {

				if (this.param.XZPB != 1) { // 新增判别 不是新增
				} else { // 新增
				// this.formModule.doNew();

				}
				// this.formModule.doNew();
			},
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							height : this.height,
							frame : true,
							layout : 'border',
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'north',
										width : 600,
										height : 104,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : false,
										title : '',
										width : 600,
										region : 'center',
										items : this.getList()
									}],
							tbar : (this.tbar || [])
									.concat(this.createButtons())
						});
				this.form.on("afterrender", this.onReady, this)
				this.panel = panel;
				return panel;
			},
			getForm : function() {
				var formModule = this.createModule("MZListForm",
						this.MZListForm);
				formModule.on("setBake", this.listCreate, this);
				formModule.on("startEditing", this.listStartEditing, this);
				formModule.opener = this;
				formModule.on("loadData",this.thisFormLoadData,this);
				formModule.initDataId = this.param.YJXH;
				this.formModule = formModule;
				return this.form = formModule.initPanel();
			},
			listCreate : function() {
				this.listModule.doFirstCreate();
			},
			listStartEditing : function(){
				this.listModule.StartEditing();
			},
			getList : function() {
				var listModule = this.createModule("MZProjectEditorList",
						this.MZProjectEditorList);
				listModule.opener = this;
				listModule.queryParams = {};
				listModule.queryParams.FYKS = this.mainApp['phis'].MedicalId;
				this.listModule = listModule;
				this.listModule.on("beforeCellEdit", this.onBeforeCellEdit,
						this);
				this.listModule.on("focusZXYS", this.formfocusZXYS,
						this);
				var list = listModule.initPanel();
				return list;
			},
			formfocusZXYS : function(){
				this.formModule.focusZXYS();
			},
			// removeEmptyRecord : function() {
			// var store = this.listModule.grid.getStore();
			// for (var i = 0; i < store.getCount(); i++) {
			// var r = store.getAt(i);
			// if (r.get("FYMC") == null) {
			// store.remove(r);
			// }
			// }
			// this.listModule.grid.getView().refresh();
			// },
			doSave : function() {
				if (this.listModule.grid.activeEditor != null) {
					this.listModule.grid.activeEditor.completeEdit();
				}
				// 判断数据有效性
				// this.removeEmptyRecord();
				var exAdviceRecords = [];
				var store = this.listModule.grid.getStore();
				var n = store.getCount()
				if (n == 0) {
					MyMessageTip.msg("提示", "请先录入医嘱数据!", true);
					this.listModule.doCreate();
					return false;
				}
				var formValue = this.formModule.getSaveData();
				if (!formValue.YSDM) {
					MyMessageTip.msg("提示", "申检医生输入有错 或 必须录入医生工号!", true);
					return;
				}
				if (!formValue.ZXYS) {
					MyMessageTip.msg("提示", "检查医生输入有错 或 必须录入医生工号!", true);
					return;
				}
				if (!formValue.KSDM) {
					MyMessageTip.msg("提示", "申检科室输入有错 或 必须录入申检科室!", true);
					return;
				}
				formValue.ZXKS = this.mainApp['phis'].MedicalId;
				Ext.apply(this.formModule.data, formValue);
				var listValue = this.listModule.getEditorListData();
				if (listValue.length == 0) {
					MyMessageTip.msg("提示", "输入未完成，不能保存!", true);
					return;
				}
				this.panel.el.mask("正在执行操作...");
				phis.script.rmi.jsonRequest({
							serviceId : "medicalTechnicalSectionService",
							serviceAction : "saveMZYJAndProject",
							body : {
								formValue : this.formModule.data,
								listValue : listValue,
								param : this.param
							}
						}, function(code, msg, json) {
							this.panel.el.unmask();
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							this.fireEvent("save");
							this.getWin().hide();
						}, this);
			},
			doClose : function() {
				var win = this.getWin();
				if (win)
					win.hide();
			},
//			createButton : function() {
//				if (this.op == 'read') {
//					return [];
//				}
//				var actions = this.actions;
//				var buttons = [];
//				if (!actions) {
//					return buttons;
//				}
//				var f1 = 112;
//				for (var i = 0; i < actions.length; i++) {
//					var action = actions[i];
//					var btn = {};
//					btn.accessKey = f1 + i;
//					btn.cmd = action.id;
//					btn.text = action.name + "(F" + (i + 1) + ")";
//					btn.iconCls = action.iconCls || action.id;
//					btn.script = action.script;
//					btn.handler = this.doAction;
//					btn.notReadOnly = action.notReadOnly;
//					btn.scope = this;
//					btn.scale = "large";
//					// btn.iconAlign = "top";
//					if (i < actions.length - 1)
//						buttons.push(btn, '-');
//					else
//						buttons.push(btn);
//				}
//				return buttons;
//			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var script = item.script
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
			doNew : function() {
				var toolBar = this.panel.getTopToolbar();
				var btn = toolBar.find("cmd", "add");
				var btn1 = toolBar.find("cmd", "delete");
				var btn2 = toolBar.find("cmd", "clear");
				var btn3 = toolBar.find("cmd", "save");
				btn[0].enable();
				btn1[0].enable();
				btn2[0].enable();
				btn3[0].enable();
				this.op = "create";
				if (this.midiModules["MZListForm"]) {
					this.midiModules["MZListForm"].doNew();
					this.midiModules["MZListForm"].XZPB = 1
				}
				if (this.midiModules["MZProjectEditorList"]) {
					this.midiModules["MZProjectEditorList"].doNew();
				}
			},
			doDelete : function() {
						this.listModule.doRemove();
			},thisFormLoadData : function(){
				var toolBar = this.panel.getTopToolbar();
				var btn3 = toolBar.find("cmd", "delete");
				var btn4 = toolBar.find("cmd", "add");
				var btn5 = toolBar.find("cmd", "save");
				if (this.param.TYPE == 0) { // 如果 还未收过费
					var mzhm = this.form.getForm().findField("JZKH");
					var ysdm = this.form.getForm().findField("YSDM");
					var ksdm = this.form.getForm().findField("KSDM");
					ksdm.setDisabled(true); 
					ysdm.setDisabled(true); 
					mzhm.setDisabled(true); 
					btn3[0].enable();
					btn4[0].enable();
					btn5[0].enable();
				} else {
					var mzhm = this.form.getForm().findField("JZKH");
					var ysdm = this.form.getForm().findField("YSDM");
					var ksdm = this.form.getForm().findField("KSDM");
					ksdm.setDisabled(false); 
					ysdm.setDisabled(false); 
					mzhm.setDisabled(false); 
					btn3[0].disable();
					btn4[0].disable();
					btn5[0].disable();
//						btn2[0].disable();
				}
			},
			loadData : function() {
				this.exContext.args["listRequestData"] = {
					serviceAction : "getMzEditList",
					yjxh : this.param.YJXH,
					jgid : this.mainApp['phisApp'].deptId
				}
				this.exContext.args["formRequestData"] = {
					yjxh : this.param.YJXH,
					jgid : this.mainApp['phisApp'].deptId,
					zxks : this.mainApp['phis'].MedicalId
				};
				if (this.midiModules["MZListForm"]) {
					this.midiModules["MZListForm"].exContext = this.exContext;
					this.midiModules["MZListForm"].XZPB = 0;
					this.midiModules["MZListForm"].param = this.param;
					this.midiModules["MZListForm"].loadData();
				}
				if (this.midiModules["MZProjectEditorList"]) {
					Ext
							.apply(
									this.midiModules["MZProjectEditorList"].requestData,
									this.exContext.args["listRequestData"]);
					this.midiModules["MZProjectEditorList"].loadData();
				}
			},
			doAdd : function() {
				if (this.formModule.data.MZHM||this.formModule.queryData) {
					this.listModule.doCreate();
				}
			},
			doClear : function() {
				this.formModule.queryData = false;
				this.formModule.doNew()
				this.listModule.doNew();
			}
		});