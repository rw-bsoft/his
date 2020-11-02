/**
 * 报损管理修改界面
 * 
 * @author gaof
 */
$package("phis.application.sup.script");

$import("phis.script.SimpleModule");

phis.application.sup.script.FaultyDetailModule = function(cfg) {
	this.width = 1024;
	this.height = 500;
	cfg.listIsUpdate = true;
	phis.application.sup.script.FaultyDetailModule.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.sup.script.FaultyDetailModule, phis.script.SimpleModule,
		{
			sreachValue : {
				"bsfs" : 1,
				"ksdm" : 0
			},
			initPanel : function() {
				if (this.panel) {

					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'north',
										width : 960,
										height : 95,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										width : 960,
										items : this.getList()
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				this.panel = panel;

				return panel;
			},
			getForm : function() {
				this.form = this.createModule("faultyConform", this.refForm);
				this.form.initPanel();
				/*
				 * var lzfsCombo = this.form.form.getForm().findField("LZFS");
				 * lzfsCombo.getStore().on("load", function(store, records,
				 * options) { lzfsCombo.setValue(records[0].get("key")); });
				 */
				return this.form.form;
			},
			getList : function() {
				this.list = this.createModule("faultyDetaillist", this.refList);
				this.list.zblb = this.zblb;
				this.list.grid = this.list.initPanel();
				this.list.opener = this;
				this.list.num = 0;
				/*
				 * this.list.grid.getStore().on("add",function(){
				 * this.form.form.getForm().findField("BSFS").disable();
				 * this.form.form.getForm().findField("BSKS").disable();
				 * },this);
				 */
				this.list.grid.getStore().on("remove",
						function(store, record, index) {
							if (!Ext.isEmpty(record.get("JLXH"))) {
								this.list.delIds.push(record.get("JLXH"));
							}
							if (store.getCount() == 0) {
								this.form.form.getForm().findField("BSFS")
										.enable();
								this.form.form.getForm().findField("BSKS")
										.enable();
							}
						}, this);
				/*
				 * this.list.grid.getColumnModel().setHidden(
				 * this.list.grid.getColumnModel().getIndexById("KTSL"), true);
				 */
				return this.list.grid;
			},
			doNew : function() {
				this.list.faultyForm = this.form.form;
				/*
				 * this.list.remoteDicStore.baseParams = { "zblb" : this.zblb }
				 */
				this.form.form.getForm().findField("BSFS").enable();
				// this.form.form.getForm().reset();
				this.form.doNew();
				this.list.clear();
				this.list.editRecords = [];
				// this.list.doCreate();
				this.changeButtonState("blank");
				var bsrqField = this.form.form.getForm().findField("BSRQ");
				this.form.form.getForm().findField("BSKS").setValue("");
				this.form.form.getForm().findField("BSKS").disable();
				Ext.defer(function() {
							bsrqField.setValue(new Date());
						}, 1000);
			},
			listIsEmpty : function() {
				var comb = this.form.form.getForm().findField("BSFS");
				var bsks = this.form.form.getForm().findField("BSKS");
				if (Ext.isEmpty(comb.getValue())) {
					Ext.MessageBox.alert("警告", "请选择报损方式");
					return false;
				}
				var bsfsStore = comb.getStore();
				if (comb.getValue() == 1 && Ext.isEmpty(bsks.getValue())) {
					Ext.MessageBox.alert("警告", "请选择报损科室");
					return false;
				}
				return true;
			},
			doAdd : function() {
				var winclose = false;
				this.beforeClose(winclose);
				this.doNew();
			},
			// 保存
			doSave : function(winclose) {
				this.panel.el.mask("正在保存...", "x-mask-loading");
				var body = this.getSendData();
				body["WL_BS01"].ZBLB = Ext.getCmp("faultyStautsRadio")
						.getValue().inputValue;
				body["op"] = this.op;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "saveOrUpdaterFaulty",
							body : body
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					this.panel.el.unmask();
					return false;
				} else {
					this.panel.el.unmask();
					this.DJXH = r.json.DJXH
					this.op = "update";
					this.list.num = 0;
				    this.fireEvent("save", this);
				    this.fireEvent("winClose", this);
				}
				this.list.num = 0;
				return true;
			},
			saveData : function() {
				var body = {};
				body["WL_BS02"] = [];
				var count = this.list.store.getCount();
				for (var i = 0; i < count; i++) {
					if ((Number(this.list.store.getAt(i).data["WZSL"])) > (Number(this.list.store
							.getAt(i).data["TJSL"]))) {
						Ext.Msg.alert("提示", "第" + (i + 1) + "行报损数量大于推荐数量");
						this.panel.el.unmask();
						return false;
					}
					if (body["WL_BS02"].length < 1) {
						this.panel.el.unmask();
						Ext.Msg.alert("提示", "没有明细信息reer,保存失败");
						return false;
					}
					body["WL_BS02"].push(data);
				}
				body["WL_BS01"] = this.form.getFormData();
				if (!body["WL_BS01"]) {
					this.panel.el.unmask();
					return false;
				}
				body["WL_BS01"].ZBLB = Ext.getCmp("faultyStautsRadio")
						.getValue().inputValue;
				return body;
			},
			// 保存前module的验证
			validationModule : function() {
				var body = this.form.getFormData();
				if (body.BSFS == 1 && Ext.isEmpty(body.BSKS)) {
					Ext.Msg.alert("提示", "请选择报损科室!");
					return false;
				}
				if (this.list.store.getCount() == 0) {
					Ext.Msg.alert("提示", "没有明细信息,保存失败!");
					return false;
				}
				for (var j = 0; j < this.list.store.getCount(); j++) {
					var r = this.list.store.getAt(j);
					if (r.get("WZSL") == 0) {
						Ext.Msg.alert("提示", "第" + (j + 1) + "行报损数量不能为空！");
						return false;
					}
				}
				var i = Ext.each(this.list.store.getRange(),
						function(record, i) {
							if (Number(record.get("WZSL")) > Number(record
									.get("TJSL"))) {
								return false;
							}

						});
				if (!Ext.isEmpty(i)) {
					Ext.Msg.alert("提示", "第" + (i + 1) + "行报损数量大于推荐数量");
					return false;
				}
				return true;
			},

			// 提交前module的验证
			validationVerify : function() {
				if(this.list.num==1){
					Ext.Msg.alert("提示", "明细信息已改变，请保存后再审核!");
					return false;
				}
				var body = this.form.getFormData();
				if (body.BSFS == 1 && Ext.isEmpty(body.BSKS)) {
					Ext.Msg.alert("提示", "请选择报损科室!");
					return false;
				}
				if (this.list.store.getCount() == 0) {
					Ext.Msg.alert("提示", "没有明细信息,保存失败!"
									+ this.list.store.getCount());
					return false;
				}

				var i = Ext.each(this.list.store.getRange(),
						function(record, i) {
							if (Number(record.get("WZSL")) > Number(record
									.get("TJSL"))) {
								return false;
							}

						});
				if (!Ext.isEmpty(i)) {
					Ext.Msg.alert("提示", "第" + (i + 1) + "行报损数量大于推荐数量");
					return false;
				}
				return true;
			},

			getSendData : function() {
				var body = {};
				body["WL_BS01"] = this.form.getFormData();
				body["WL_BS02"] = [];
				var i = Ext.each(this.list.store.getRange(),
						function(record, i) {
							var data=Ext.apply({},record.data);
							data.SBBH=undefined;
							body["WL_BS02"].push(data);
						});

				return body;
			},
			doVerify : function() {
				// 盘点状态
				if (this.mainApp['phis'].treasuryPdzt == 1) {
					Ext.Msg.alert("提示", "库房正处于盘点状态，无法进行操作");
					return;
				}
				if (this.form.getFormData().DJZT != 0) {
					Ext.Msg.alert("提示", "非新增状态，不能审核");
					return;
				}
				// 判断单据状态是否异步改变
				var body = {}
				body["DJXH"] = this.form.getFormData().DJXH;
				var r1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "getDjztByDjxh",
							body : body,
							op : this.op
						});
				if (r1.json.djzt != this.form.getFormData().DJZT) {
					Ext.Msg.alert("提示", "单据状态已改变，不能审核");
					return;
				}
				body = this.getSendData();
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "verify",
							body : body,
							op : this.op
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				}else{
					this.fireEvent("winClose", this);
					this.fireEvent("save", this);
				}
			},
			doCancelVerify : function() {
				if (this.form.getFormData().DJZT != 1) {
					Ext.Msg.alert("提示", "非审核状态，不能弃审");
					return;
				}

				// 判断单据状态是否异步改变
				var body = {}
				body["DJXH"] = this.form.getFormData().DJXH;
				var r1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "getDjztByDjxh",
							body : body,
							op : this.op
						});
				if (r1.json.djzt != this.form.getFormData().DJZT) {
					Ext.Msg.alert("提示", "单据状态已改变，不能审核");
					return;
				}

				body["WL_BS01"] = this.form.getFormData();
				if (!body["WL_BS01"]) {
					return;
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "cancelVerify",
							body : body,
							op : this.op
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					/*
					 * this.loadData(this.initDataBody);
					 * this.changeButtonState("new"); //
					 * this.fireEvent("winClose", this);
					 */this.fireEvent("save", this);
					 this.fireEvent("winClose", this);
				}
				// this.op = "update";
			},
			// 记账
			doCommit : function() {
				// 盘点状态
				if (this.mainApp['phis'].treasuryPdzt == 1) {
					Ext.Msg.alert("提示", "库房正处于盘点状态，无法进行操作");
					return;
				}
				if (this.form.getFormData().DJZT != 1) {
					Ext.Msg.alert("提示", "非审核状态，不能记账");
					return;
				}
				// 判断单据状态是否异步改变
				var body = {}
				body["DJXH"] = this.form.getFormData().DJXH;
				var r1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "getDjztByDjxh",
							body : body,
							op : this.op
						});
				if (r1.json.djzt != this.form.getFormData().DJZT) {
					Ext.Msg.alert("提示", "单据状态已改变，不能审核");
					return;
				}

				if (this.mainApp['phis'].treasuryPdzt == 1) {
					Ext.Msg.alert("提示", "库房正处于盘点状态，无法进行记账");
					return;
				}

				var body = this.getSendData();

				/*
				 * if (this.form.getFormData().THDJ == 0) { //
				 * 把入库明细中每条记录的单价，更新物资厂家(WL_WZCJ)对应物资序号 var r =
				 * phis.script.rmi.miniJsonRequestSync({ serviceId :
				 * this.serviceId, serviceAction : "updateWzcj", body : body, op :
				 * this.op }); if (r.code > 300) { this.processReturnMsg(r.code,
				 * r.msg, this.onBeforeSave); this.panel.el.unmask(); return
				 * false; } }
				 */

				// 增加库存,记账
				// body["WL_RK01"] = this.form.getFormData();
				// body["ZBLB"] = this.zblb;
				var r1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "saveCommit",
							body : body,
							op : this.op
						});
				if (r1.code > 300) {
					this.processReturnMsg(r1.code, r1.msg, this.onBeforeSave);
					this.panel.el.unmask();
					return false;
				} else {
					this.fireEvent("winClose", this);
					this.fireEvent("save", this);
				}
			},
			doClose : function() {
				// this.beforeClose();
				this.fireEvent("winClose", this);
				return true;
			},
			beforeClose : function(winclose) {
				var data = this.form.getFormData();
				if (data.DJZT == 0 && this.list.editRecords
						&& this.list.editRecords.length > 0) {
					if (confirm('数据已经修改，是否保存?')) {
						return this.doSave(winclose)
					} else {
						return true;
					}
				}
				return true;
			},
			// 修改
			loadData : function(initDataBody) {
				this.form.list = this.list;
				/*
				 * this.list.remoteDicStore.baseParams = { "zblb" : this.zblb }
				 */
				this.listIsUpdate = true;
				this.doLoad(initDataBody, true);
			},
			// 改变按钮状态
			changeButtonState : function(state) {
				var actions = this.actions;
				for (var i = 0; i < actions.length-1; i++) {
					var action = actions[i];
					this.setButtonsState([action.id], false);
				}
				/*
				 * var bsfs=this.form.form.getForm().findField("BSFS"); var
				 * bsks=this.form.form.getForm().findField("BSKS");
				 * Ext.defer(function(){ bsfs.disable(); bsks.disable();
				 * },1000);
				 */
				if (state == "blank") {
					this.setButtonsState(["create", "save", "comeIn", "print",
									"close"], true);
					this.list.setButtonsState(["remove"], true);
					/*
					 * bsfs.enable(); bsks.enable();
					 */
				}
				if (state == "new") {
					this.setButtonsState(["create", "save", "verify", "comeIn",
									"print", "close"], true);
					this.list.setButtonsState(["remove"], true);
				}
				if (state == "verified") {
					this.setButtonsState(["create", "cancelVerify", "commit",
									"print", "close"], true);
					this.list.setButtonsState(["remove"], false);
					// this.form.isRead = true;
				}
				if (state == "commited") {
					this.setButtonsState(["create", "print", "close"], true);
					this.list.setButtonsState(["remove"], false);
				}

			},
			// 设置按钮状态
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				btns = this.panel.getTopToolbar();
				if (!btns) {
					return;
				}
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
			},
			// 修改,查看,提交数据回填
			doLoad : function(initDataBody) {
				this.form.initDataId = initDataBody.DJXH;
				// this.form.initDataBody = initDataBody;
				// this.form.requestData.cnd = ['eq', ['$', 'DJXH'],
				// ['i', initDataBody.DJXH]];
				this.form.loadData();
				this.list.requestData.cnd = ['eq', ['$', 'DJXH'],
						['i', initDataBody.DJXH]];
				this.list.loadData();
			},
			// 按钮条
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
					var action;
					if (cmd == "ComeIn") {
						action = this["do" + cmd];
					} else {
						action = this["do" + cmd].createSequence(
								this.changeButtonStatus, this);

					}
					if (cmd == "Save") {
						action = Ext.createInterceptor(action,
								this.validationModule, this);
					}
					if (cmd == "Verify") {
						action = Ext.createInterceptor(action,
								this.validationVerify, this);
					}
					// this.doSave=this.doSave.createInterceptor(this.validationModule,this);
					// this.doVerify=this.doVerify.createInterceptor(this.validationModule,this);
					if (action) {
						action.apply(this, [item, e])
					}
				}
			},
			doComeIn : function() {
				var bsks = this.form.form.getForm().findField("BSKS")
						.getValue();
				var bsfs = this.form.form.getForm().findField("BSFS")
						.getValue();
				if (Ext.isEmpty(bsfs)) {
					MyMessageTip.msg("提示", "请选择报损方式!", true);
					return false;
				}
				if (bsfs == "1" && Ext.isEmpty(bsks)) {
					Ext.MessageBox.alert("警告", "请选择报损科室!");
					return false;
				}
				if (Ext.isEmpty(this.form.getFormData())) {
					Ext.MessageBox.alert("警告", "先填写必填项!");
					return false;
				}
				// 账簿类别
				var cfg = {};
				/*
				 * if(this.form.getFormData().ZBLB!=0){
				 * cfg.zblb=this.form.getFormData().ZBLB; }else{ }
				 */
				cfg.zblb = Ext.getCmp("faultyStautsRadio").getValue().inputValue;
				this.faultyComInList = this.createModule("KSBSComeInList",
						this.comeInList, cfg);
				this.faultyComInList.BSKSStore = this.form.form.getForm()
						.findField("BSKS").getStore();
				this.faultyComInList.BSFSStore = this.form.form.getForm()
						.findField("BSFS").getStore();
				this.faultyComInList.BSKS = this.form.form.getForm()
						.findField("BSKS").getValue();
				this.faultyComInList.BSFS = this.form.form.getForm()
						.findField("BSFS").getValue();
				this.faultyComInList.on("save", this.onSave, this);
				this.faultyComInList.on("winClose", this.onClose, this);
				this.faultyComInList.on("hide", this.onSetList, this);
				this.faultyComInList.edlist = this.list;
				var win = this.faultyComInList.getWin();
				var panel = this.faultyComInList.initPanel();
				if(this.faultyComInList.cndField){
				this.faultyComInList.cndField.setValue("");
				this.faultyComInList.requestData.cnd=null;
				}
				win.add(panel);
				win.show()
				win.center()
				this.faultyComInList.refresh();
			},
			onSetList:function(){
				this.form.newlist = this.list;
			},
			changeButtonStatus : function(item, e) {
				if (item.cmd == "comeIn" || item.cmd == "close") {
					return;
				}
				var body = {}
				body["DJXH"] = this.form.getFormData().DJXH;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "getDjztByDjxh",
							body : body,
							op : this.op
						});

				if (item.cmd == "create") {
					this.changeButtonState("blank");
				} else if (item.cmd == "save" || item.cmd == "verify"
						|| item.cmd == "cancelVerify" || item.cmd == "commit") {
					var body = {}
					body["DJXH"] = this.DJXH;
					if (Ext.isEmpty(this.DJXH)) {
						this.changeButtonState("blank");
						return;
					}
					var r = phis.script.rmi.miniJsonRequestSync({// 获得单据状态
						serviceId : this.serviceId,
						serviceAction : "buttonbarStaus",
						body : body,
						op : this.op
					});
					if (r.json.error) {
						this.changeButtonState("blank");
					}
					var type = r.json.type;
					this.changeButtonState(type);
					this.loadData({
								"DJXH" : this.DJXH
							});
				}
			},
            doPrint : function() {
                var module = this.createModule("faultyManagerPrint",
                        this.refFaultyManagerPrint)
                var r = this.form.getFormData()
                if (r == null) {
                    MyMessageTip.msg("提示", "打印失败：无效的报损单信息!", true);
                    return;
                }
//                if(r.DJZT==0){
//                    MyMessageTip.msg("提示", "打印失败：未审核的单据不能打印", true);
//                    return;
//                }
                module.djxh=r.DJXH;
                module.initPanel();
                module.doPrint();
            }
		});