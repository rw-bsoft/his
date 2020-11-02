/**
 * 药库模块,打开详情界面Module
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.script.SimpleModule");

phis.application.sto.script.StorehouseMySimpleDetailModule = function(cfg) {
	cfg.width = 1020;
	cfg.height = 550;
	this.modal = this.modal = true;
	this.title = "药品出库单";// module的标题
	this.refForm = "";// module上面form的id
	this.refList = "";// module下面list的id
	this.serviceId = "storehouseManageService"
	phis.application.sto.script.StorehouseMySimpleDetailModule.superclass.constructor
			.apply(this, [cfg]);
	this.on('doSave', this.doSave, this);
	this.on("beforeclose", this.doClose, this);
}
Ext.extend(phis.application.sto.script.StorehouseMySimpleDetailModule,
		phis.script.SimpleModule, {
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
										title : this.title,
										region : 'north',
										//width : 960,
										height : this.formHeigh||90,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										//width : 960,
										items : this.getList()
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				this.panel = panel;
				return panel;
			},
			doNew : function() {
				this.changeButtonState("new")
				this.form.op = "create";
				this.panel.items.items[0].setTitle(this.title);
				this.form.selectValue = this.selectValue;
				this.form.doNew();
				this.list.op = "create";
				this.list.doNew();
				this.list.doCreate();
			},
			getForm : function() {
				this.form = this.createModule("form", this.refForm);
				this.form.on("loadData", this.afterLoad, this);
				this.form.on("createRow", this.onCreateRow, this);
				return this.form.initPanel();
			},
			getList : function() {
				this.list = this.createModule("list", this.refList);
				return this.list.initPanel();
			},
			// 保存方法,需要重写
			doSave : function() {
				var body = {};
				var d01 = this.getFormData();
				if (d01 == null) {
					return;
				}
				var d02 = this.getListData();
				if (d02 == null || d02.length == 0) {
					return;
				}
				body["d01"] = d01;
				body["d02"] = d02;
				this.panel.el.mask("正在保存...", "x-mask-loading");
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.saveActionId,
							body : body,
							op : this.op
						});
				if (r.code > 300) {
					if (r.code == 700) {
						MyMessageTip.msg("提示", r.msg, true);
						this.panel.el.unmask();
						this.fireEvent("winClose", this);
						this.fireEvent("save", this);
						return;
					}
					this.processReturnMsg(r.code, r.msg, this.doSave);
					this.panel.el.unmask();
					return;
				}
				this.list.doInit();
				this.fireEvent("winClose", this);
				this.fireEvent("save", this);
				this.panel.el.unmask();
				MyMessageTip.msg("提示", "保存成功!", true);
			},
			doCancel : function() {
				this.fireEvent("winClose", this);
			},
			doClose : function() {
				if (this.isRead) {
					if(this.unlock){
					this.unlock();
					}
					return true;
				}
				if (this.list.editRecords && this.list.editRecords.length > 0) {
					Ext.Msg.show({
								title : "提示",
								msg : "有已修改未保存的数据,确定不保存直接关闭?",
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										this.list.editRecords = [];
										this.fireEvent("winClose", this);
									}
								},
								scope : this
							});
					return false;
				}
				if(this.unlock){
					this.unlock();
					}
				return true;
			},
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
			// 修改
			loadData : function(initDataBody) {
				this.changeButtonState("update")
				this.doLoad(initDataBody);
			},
			// 查看
			doRead : function(initDataBody) {
				this.changeButtonState("read");
				this.doLoad(initDataBody);
			},
			// 提交按钮打开界面,除了确定按钮其他按钮全屏蔽掉
			doOpneCommit : function(initDataBody) {
				this.initDataBody = initDataBody;
				this.changeButtonState("commit");
				this.doLoad(initDataBody);
			},
			// 提交出库单 需要重写
			doCommit : function() {
				var body = {};
				var d01 = this.getFormData();
				if (d01 == null) {
					return;
				}
				var d02 = this.getListData();
				if (d02 == null || d02.length == 0) {
					return;
				}
				body["d01"] = d01;
				body["d02"] = d02;
				this.panel.el.mask("正在保存...", "x-mask-loading");
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.commitActionId,
							body : body
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.doSave);
					this.fireEvent("commit", this);
					this.panel.el.unmask();
					return;
				}
				this.list.doInit();
				this.fireEvent("winClose", this);
				this.fireEvent("commit", this);
				this.panel.el.unmask();
				MyMessageTip.msg("提示", "确认成功!", true);
			},
			// 改变按钮状态
			changeButtonState : function(state) {
				var actions = this.actions;
				this.form.isRead = false;
				this.list.isRead = false;
				if (state == "read" || state == "commit") {
					this.form.isRead = true;
					this.list.isRead = true;
					this.list.setButtonsState(["create", "remove"], false);
				} else {
					this.list.setButtonsState(["create", "remove"], true);
				}
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					if (action.id == "cancel" || action.id == "print") {
						continue;
					}
					if (state == "read") {
						this.setButtonsState([action.id], false);
					} else if (state == "update" || state == "new") {
						if (action.id == "commit") {
							this.setButtonsState([action.id], false);
							continue;
						}
						this.setButtonsState([action.id], true);
					} else if (state == "commit") {
						if (action.id == "commit") {
							this.setButtonsState([action.id], true);
							continue;
						}
						this.setButtonsState([action.id], false);
					} else {
						this.setButtonsState([action.id], true);
					}
				}

			},
			// 改变按钮状态
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
			// 修改,查看,提交数据回填 需重写
			doLoad : function(initDataBody) {

			},
			afterLoad : function(entryName, body) {
				this.panel.items.items[0].setTitle("NO: " + body.CKDH);
			},
			getListData : function() {
				var ck02 = new Array();
				var count = this.list.store.getCount();
				for (var i = 0; i < count; i++) {
					if (this.list.store.getAt(i).data["YPXH"] != ''
							&& this.list.store.getAt(i).data["YPXH"] != null
							&& this.list.store.getAt(i).data["YPXH"] != 0
							&& this.list.store.getAt(i).data["YPCD"] != ''
							&& this.list.store.getAt(i).data["YPCD"] != 0
							&& this.list.store.getAt(i).data["YPCD"] != null
							&& this.list.store.getAt(i).data["YPSL"] != ''
							&& this.list.store.getAt(i).data["YPSL"] != 0
							&& this.list.store.getAt(i).data["YPSL"] != null) {
						if (this.list.store.getAt(i).data.LSJE > 99999999.99) {
							MyMessageTip.msg("提示",
									"第" + (i + 1) + "行零售金额超过最大值", true);
							this.panel.el.unmask();
							return;
						}
						if (this.list.store.getAt(i).data.JHJE > 99999999.99) {
							MyMessageTip.msg("提示",
									"第" + (i + 1) + "行进货金额超过最大值", true);
							this.panel.el.unmask();
							return;
						}
						ck02.push(this.list.store.getAt(i).data);
					}
				}
				return ck02;
			},
			getFormData : function() {
				return this.form.getFormData();
			},
			// 备注回车,新增一行
			onCreateRow : function() {
				this.list.doCreateRow();
			},
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
						id : this.id,
						title : this.title || this.name,
						width : this.width,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : "hide",
						constrainHeader : true,
						constrain : true,
						minimizable : true,
						maximizable : true,
						shadow : false,
						modal : this.modal || false
							// add by huangpf.
						})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("add", function() {
								this.win.doLayout()
							}, this)
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() { // ** add by yzh 2010-06-24 **
								this.fireEvent("hide", this)
							}, this)
					this.win = win
				}
				return win;
			}
		});