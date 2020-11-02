/**
 * 药品调价新增修改界面
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.script.SimpleModule");

phis.application.sto.script.StorehouseMedicinesPriceAdjustDetailModule = function(cfg) {
	this.width = 1020;
	this.height = 550;
	cfg.modal = this.modal = true;
	phis.application.sto.script.StorehouseMedicinesPriceAdjustDetailModule.superclass.constructor
			.apply(this, [cfg]);
	this.on("beforeclose", this.doClose, this);
}
Ext.extend(phis.application.sto.script.StorehouseMedicinesPriceAdjustDetailModule,
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
										title : '药品调价单',
										region : 'north',
										height : 100,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getList()
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				this.panel = panel;

				return panel;
			},
			doNew : function() {
				this.panel.items.items[0].setTitle("药品调价单");
				this.form.priceAdjustWayValue = this.priceAdjustWayValue;
				this.form.doNew();
				this.list.doNew();
				this.list.doCreate();
			},
			getForm : function() {
				this.form = this.createModule("form", this.refForm);
				this.form.on("loadData",this.afterLoad,this);
				return this.form.initPanel();
			},
			getList : function() {
				this.list = this.createModule("list", this.refList);
				this.list.on("loadData",this.onLoadData,this);
				return this.list.initPanel();
			},
			doCancel : function() {
				this.getWin().hide();
			},
			doClose : function() {
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
										this.getWin().hide();
									}
								},
								scope : this
							});
					return false;
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
				this.form.op = "update";
				this.form.initDataBody = initDataBody;
				this.form.loadData();
				this.list.op = "create";
				this.list.requestData.cnd = [
						'and',
						['eq', ['$', 'a.TJFS'], ['i', initDataBody.TJFS]],
						[
								'and',
								['eq', ['$', 'a.TJDH'],
										['i', initDataBody.TJDH]],
								['eq', ['$', 'a.JGID'],
										['s', initDataBody.JGID]]]];
				this.list.loadData();
			},
			// 保存
			doSave : function() {
				var ed=this.list.grid.activeEditor;
				if (!ed) {
						ed = this.list.grid.lastActiveEditor;
					}
					if(ed){
					ed.completeEdit();
					}
				var body = {};
				body["YK_TJ02"] = [];
				var count = this.list.store.getCount();
				var sfkz = this.getSfkz();
				for (var i = 0; i < count; i++) {
					if (this.list.store.getAt(i).data["YPXH"] != ''
							&& this.list.store.getAt(i).data["YPXH"] != null
							&& this.list.store.getAt(i).data["YPXH"] != 0
							&& this.list.store.getAt(i).data["YPCD"] != ''
							&& this.list.store.getAt(i).data["YPCD"] != 0
							&& this.list.store.getAt(i).data["YPCD"] != null) {
						if (this.list.store.getAt(i).data["XLSJ"] == null
								|| this.list.store.getAt(i).data["XLSJ"] == ""
								|| this.list.store.getAt(i).data["XLSJ"] == undefined) {
							MyMessageTip.msg("提示", "新零售价不能为空!", true);
							return;
						}
						if (this.list.store.getAt(i).data["XJHJ"] == null
								|| this.list.store.getAt(i).data["XJHJ"] == ""
								|| this.list.store.getAt(i).data["XJHJ"] == undefined) {
							MyMessageTip.msg("提示", "新进货价不能为空!", true);
							return;
						}
						// 如果中心控制价格,判断下限制价格有无被修改
						if (sfkz > 0) {
							var body_jgkz = {};
							body_jgkz["YPXH"] = this.list.store.getAt(i).data["YPXH"];
							body_jgkz["YPCD"] = this.list.store.getAt(i).data["YPCD"];
							body_jgkz["LSJG"] = this.list.store.getAt(i).data["XLSJ"];
							body_jgkz["JHJG"] = this.list.store.getAt(i).data["XJHJ"];
							body_jgkz["ROW"] = i + 1;
							body_jgkz["TAG"] = "tj";
							var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.queryPriceChangesServiceId,
								serviceAction : this.queryPriceChangesServiceAction,
								body : body_jgkz
							});
							if (ret.code > 300) {
								this.processReturnMsg(ret.code, ret.msg,
										this.doSave);
								return;
							}
						}
						body["YK_TJ02"].push(this.list.store.getAt(i).data);
					}
				}
				if (body["YK_TJ02"].length < 1) {
					return;
				}
				body["YK_TJ01"] = this.form.getFormData();
				if (!body["YK_TJ01"]) {
					return;
				}
				this.panel.el.mask("正在保存数据...", "x-mask-loading");
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.savePriceAdjustActionId,
							body : body,
							op : this.op
						});
				this.panel.el.unmask();
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					this.list.doInit();
					this.getWin().hide();
					this.fireEvent("save", this);
				}
				this.op = "update";
			},
			// 获取是否需要控制价格
			getSfkz : function() {
				if (this.sfkz == undefined) {
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.queryControlPricesServiceAction
							});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg, this.getSfkz);
						return null;
					}
					this.sfkz = ret.json.sfkz;
				}
				return this.sfkz;
			},
			afterLoad:function(entryName,body){
				this.panel.items.items[0].setTitle("NO: "+body.TJDH);
			},
			onLoadData : function(store) {
				this.list.doCreate();
			},
			beforeClose : function(winclose) {
				 if (this.list.editRecords && this.list.editRecords.length > 0) {
					 if (confirm('数据已经修改，是否保存?')) {
					 	return this.doSave(winclose)
					 } else {
					 	return true;
					 }
				 }
			}
		});