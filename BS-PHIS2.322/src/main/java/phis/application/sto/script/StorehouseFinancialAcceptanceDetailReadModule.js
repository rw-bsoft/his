/**
 * 财务验收明细查看
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.script.SimpleModule");

phis.application.sto.script.StorehouseFinancialAcceptanceDetailReadModule= function(cfg) {
	this.width = 1024;
	this.height = 550;
	cfg.listIsUpdate = true;
	phis.application.sto.script.StorehouseFinancialAcceptanceDetailReadModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehouseFinancialAcceptanceDetailReadModule,
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
			doCancel : function() {
				this.fireEvent("winClose", this);
				this.getWin().hide();
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
			doRead : function(initDataBody) {
				this.form.op = "update";
				this.form.initDataBody = initDataBody;
				this.form.loadData();
				this.list.op = "create";
				var cnd = [
						'and',
						['eq', ['$', 'RKFS'], ['i', initDataBody.RKFS]],
						[
								'and',
								['eq', ['$', 'RKDH'], ['i', initDataBody.RKDH]],
								['eq', ['$', 'XTSB'], ['d', initDataBody.XTSB]]]];
				if (initDataBody.YSDH != null && initDataBody.YSDH != undefined
						&& initDataBody.YSDH != 0) {
					cnd = ['and', cnd,
							['eq', ['$', 'YSDH'], ['i', initDataBody.YSDH]]]
				}
				this.list.requestData.cnd = cnd;
				this.list.loadData();
			},
			afterLoad : function(entryName, body) {
				this.panel.items.items[0].setTitle("NO: " + body.RKDH);
			},
			doPrint : function() {
				var module = this.createModule("storehouseinprint",
						this.refStorehouseListPrint)
				var data = this.form.getFormData();
				module.xtsb = data.XTSB;
				module.rkfs = data.RKFS;
				module.rkdh = data.RKDH;
				module.pwd = data.PWD;
				module.fdjs = data.FDJS;
				module.initPanel();
				module.doPrint();
			}
		});