$package("phis.application.sto.script")

$import("phis.script.SimpleModule");

phis.application.sto.script.StorehouseFinancialAcceptanceDetailModule = function(cfg) {
	this.width = 1024;
	this.height = 550;
	phis.application.sto.script.StorehouseFinancialAcceptanceDetailModule.superclass.constructor
			.apply(this, [cfg]);
	this.on("beforeclose", this.doClose, this);
}
Ext.extend(phis.application.sto.script.StorehouseFinancialAcceptanceDetailModule,
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
										title : '药品入库单',
										region : 'north',
										width : 960,
										height : 90,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
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
				this.form = this.createModule("form", this.refForm);
				this.form.on("loadData", this.afterLoad, this);
				return this.form.initPanel();
			},
			getList : function() {
				this.list = this.createModule("list", this.refList);
				return this.list.initPanel();
			},
			doSave : function() {
				var formData = this.form.getFormData();
				if (formData == null) {
					return;
				}
				// 验证当月是否月结,入库单是否已经全部验收
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.verificationActionId,
							body : formData
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.doSave);
					this.fireEvent("save", this);
					return;
				}
				//var _ctr = this;
				
					var body = {};
					body["YK_RK01"] = formData;
					if(formData.PWD!=0){
					MyMessageTip.msg("提示", "货到票未到,无法审核!", true);
						return;
					}
					var listData = this.list.getSelectedRecords();
					if (listData.length == 0) {
						MyMessageTip.msg("提示", "未选择任何明细!", true);
						return;
					}
					body["YK_RK02"] = [];
					var length = listData.length;
					/**
					 * 2013-07-17 gejj已知this.list.getSelectedRecords方法中加入判断id是否为"indexOf"
					 * 修改描述 : 注释以下三行代码
					 * 解决2158bug
					 */
//					if (Ext.isIE) {
//						length = length - 1;
//					}
					/** end**/
					for (var i = 0; i < length; i++) {
						var rkmx = listData[i].data;
						if (rkmx.FPHM == null || rkmx.FPHM == undefined
								|| rkmx.FPHM == "") {
							MyMessageTip.msg("提示", "发票号码不能为空", true);
							return;
						}
						if (rkmx.FPHM.length > 10) {
							MyMessageTip.msg("提示", "发票号码太长,最大长度是10", true);
							return;
						}
						if (rkmx.FKJE == 0
								|| rkmx.FKJE == ""
								|| rkmx.FKJE == undefined
								|| parseFloat(rkmx.FKJE) > parseFloat(rkmx.JHHJ)) {
							MyMessageTip.msg("提示", "请正确输入付款金额!", true);
							return;
						}
						if (rkmx.JHJG == 0) {
							MyMessageTip.msg("提示", "进货价格不能为0!", true);
							return;
						}
						body["YK_RK02"].push(rkmx);
					}
					this.panel.el.mask("正在保存...", "x-mask-loading");
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.saveActionId,
								body : body
							});
					this.panel.el.unmask();
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg, this.doSave);
						this.fireEvent("save", this);
						return;
					}
					MyMessageTip.msg("提示", "验收成功!", true);
					this.fireEvent("save", this);
					this.fireEvent("winClose", this);
			},
			doCancel : function() {
				this.fireEvent("winClose", this);
			},
			// doClose : function() {
			// var _ctr = this;
			// if (this.list.editRecords && this.list.editRecords.length > 0) {
			// Ext.Msg.show({
			// title : "提示",
			// msg : "有已修改未保存的数据,确定不保存直接关闭?",
			// modal : true,
			// width : 300,
			// buttons : Ext.MessageBox.OKCANCEL,
			// multiline : false,
			// fn : function(btn, text) {
			// if (btn == "ok") {
			// this.list.editRecords = [];
			// this.fireEvent("winClose", this);
			// }
			// },
			// scope : this
			// });
			// return false;
			// }
			// return true;
			// },
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
			doLoad : function(initDataBody) {
				this.form.op = "update";
				this.form.initDataBody = initDataBody;
				this.form.loadData();
				this.list.op = "create";
				this.list.requestData.cnd = [
						'and',
						['eq', ['$', 'YSDH'], ['i', 0]],
						[
								'and',
								['eq', ['$', 'RKFS'], ['i', initDataBody.RKFS]],
								[
										'and',
										['eq', ['$', 'RKDH'],
												['i', initDataBody.RKDH]],
										['eq', ['$', 'XTSB'],
												['d', initDataBody.XTSB]]]]];
				this.list.clearSelect();
				this.list.loadData();

			},
			afterLoad : function(entryName, body) {
				this.panel.items.items[0].setTitle("NO: " + body.RKDH);
			}
		});