/**
 * 药房模块,左边list模版
 * 
 * @author caijy
 */
$package("phis.application.pha.script")
$import("phis.script.SimpleList")

phis.application.pha.script.PharmacyMySimpleLeftList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	this.cnds = ['eq', ['$', '1'], ['i', 1]];// 页面查询的条件,需要重写
	cfg.winState = "center";
	cfg.modal = this.modal = true;
	cfg.gridDDGroup = "firstGridDDGroup";
	this.imgUrl = 'images/bogus.png';// 页面记录图片地址
	this.selectItemId = "";// 下拉框的id
	this.conditionText = "出库方式";// 条件名称
	this.dicFitle = "";// 下拉框的条件
	this.serviceId = "pharmacyManageService";
	this.verificationDeleteActionId = "";// 验证记录是否已经被删除的actionid
	this.removeActionId = "";// 删除的方法
	this.addRef = "";// 新增修改界面id
	this.commitRef = "";// 提交界面id
	this.isfirst = 0;// 控制下拉框防止重复刷新
	cfg.autoLoadData = false;
	phis.application.pha.script.PharmacyMySimpleLeftList.superclass.constructor.apply(this,
			[cfg])
	this.on("beforeclose", this.doCancel, this);
}
Ext.extend(phis.application.pha.script.PharmacyMySimpleLeftList, phis.script.SimpleList, {
			// 生成条件组件
			getCndBar : function(items) {
				this.selectBox = this.getSelectBox();
				var filelable = new Ext.form.Label({
							text : this.conditionText
						})
				this.selectBox.on("select", this.onSelect, this)
				var combox = this.selectBox;
				var _ctr = this;
				this.selectBox.store.on("load", function() {
							if (this.getCount() == 0 || _ctr.isfirst == 1)
								return;
							combox.setValue(this.getAt(0).get('key'))
							_ctr.onSelect(null, this.getAt(0))
							_ctr.isfirst = 1;
						});
				return [filelable, '-', this.selectBox];
			},
			// 页面打开时记录前增加未确认图标
			onRenderer : function(value, metaData, r) {
				return "<img src=" + this.imgUrl + "/>";
			},
			// 生成条件下拉框
			getSelectBox : function() {
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == this.selectItemId) {
						it = items[i]
						break
					}
				}
				it.dic.src = this.entryName + "." + it.id;
				it.dic.defaultValue = it.defaultValue;
				it.dic.width = 150;
				if (this.getDicFitle() != null) {
					it.dic.filter = this.getDicFitle();
				}
				f = this.createDicField(it.dic);
				f.on("specialkey", this.onQueryFieldEnter, this);
				return f;
			},
			// 获取条件下拉框的数据, 需要重写
			getDicFitle : function() {
				return null;
			},
			// 选中入库方式抛出事件,刷新两边界面
			onSelect : function(item, record, e) {
				this.fireEvent("select", record);
			},
			// 刷新按钮
			doRefresh : function() {
				var record = {};
				var data = {};
				data["key"] = this.selectValue;
				record["data"] = data;
				this.fireEvent("select", record);
			},
			// 刷新页面 必须重写
			doRefreshWin : function() {
				
			},
			// 新增
			doAdd : function() {
				if (!this.selectValue) {
					Ext.Msg.alert("提示", "请先选择" + cfg.conditionText);
					return;
				}
				this.module = this.createModule("module", this.addRef);
				this.module.on("save", this.onCommit, this);
				this.module.on("winClose", this.onClose, this);
				this.module.selectValue = this.selectValue;
				this.module.initPanel();
				var win = this.getWin();
				win.add(this.module.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.module.op = "create";
					this.module.doNew();
				}

			},
			onClose : function() {
				this.getWin().hide();
			},
			// 关闭界面
			doCancel : function() {
				if (this.module) {
					return this.module.doClose();
				}
			},
			// 提交
			doCommit : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var initDataBody = this.getInitDataBody(r);
//				initDataBody["YFSB"] = r.data.YFSB;
//				initDataBody["CKFS"] = r.data.CKFS;
//				initDataBody["CKDH"] = r.data.CKDH;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.verificationDeleteActionId,
							body : initDataBody
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doCommit);
					this.doRefresh();
					return;
				}
				this.module = this.createModule("module", this.commitRef);
				this.module.on("commit", this.onCommit, this);
				this.module.on("save", this.onCommit, this);
				this.module.on("winClose", this.onClose, this);
				this.module.selectValue = this.selectValue;
				var win = this.getWin();
				win.add(this.module.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.module.op = "commit";
					this.module.doOpneCommit(initDataBody);
				}
			},
			// 提交完后刷新界面
			onCommit : function() {
				var record = {};
				var data = {};
				data["key"] = this.selectValue;
				record["data"] = data;
				this.fireEvent("select", record);
			},
			// 打开修改界面
			doUpd : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var initDataBody = this.getInitDataBody(r);
//				initDataBody["YFSB"] = r.data.YFSB;
//				initDataBody["CKFS"] = r.data.CKFS;
//				initDataBody["CKDH"] = r.data.CKDH;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.verificationDeleteActionId,
							body : initDataBody
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doUpd);
					this.doRefresh();
					return;
				}
				this.module = this.createModule("module", this.addRef);
				this.module.on("save", this.onCommit, this);
				this.module.on("winClose", this.onClose, this);
				this.module.selectValue = this.selectValue;
				var win = this.getWin();
				win.add(this.module.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.module.op = "update";
					this.module.loadData(initDataBody);
				}

			},
			// 双击修改操作
			onDblClick : function(grid, index, e) {
				var item = {};
				item.text = "修改";
				item.cmd = "upd";
				this.doAction(item, e)

			},
			processRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (!this.fireEvent("beforeRemove", this.entryName, r)) {
					return;
				}
				var body = this.getInitDataBody(r);
//				body["YFSB"] = r.data.YFSB;
//				body["CKFS"] = r.data.CKFS;
//				body["CKDH"] = r.data.CKDH;
				var record = {};
				var data = {};
				data["key"] = this.selectValue;
				record["data"] = data;
				this.mask("在正删除数据...");
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.removeActionId,
							body : body
						}, function(code, msg, json) {
							this.unmask()
							if (code < 300) {
								this.store.remove(r)
								this.fireEvent("remove", this.entryName,
										'remove', json, r.data);
								this.fireEvent("select", record)
							} else {
								this.processReturnMsg(code, msg, this.doRemove)
								this.fireEvent("select", record)
							}
						}, this)
			},
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
						id : this.id,
						title : this.title||this.name,
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
		})