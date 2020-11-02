/**
 * 药品养护
 */
$package("phis.application.pha.script");

$import("phis.script.SimpleList", "phis.script.widgets.Spinner",
		"phis.script.widgets.Strategy");

phis.application.pha.script.PharmacyMaintainList = function(cfg) {
	cfg.modal = true;
	cfg.width = 1020
	phis.application.pha.script.PharmacyMaintainList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.pha.script.PharmacyMaintainList,
		phis.script.SimpleList, {
			// 页面初始化
			initPanel : function(sc) {
				// console.debug(this.mainApp)
				if (this.mainApp.pharmacyId == null
						|| this.mainApp.pharmacyId == ""
						|| this.mainApp.pharmacyId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药房,请先设置");
					return null;
				}
				// 进行是否初始化验证
				// var ret = phis.script.rmi.miniJsonRequestSync({
				// serviceId : this.serviceId,
				// serviceAction : this.initializationServiceActionID
				// });
				// if (ret.code > 300) {
				// this.processReturnMsg(ret.code, ret.msg, this.initPanel);
				// return null;
				// }
				return phis.application.pha.script.PharmacyMaintainList.superclass.initPanel
						.call(this, sc);
			},
			// 加载数据
			loadData : function() {
				var cnd = ["eq", ['$', 'a.XTSB'],
						['l', this.mainApp.pharmacyId]];
				var simple = this.simple;
				if (simple) {
					var stroeDate = simple.getValue();
					cnd = ['and', cnd,
							['eq', ['$', "str(YHRQ,'yyyy-MM')"], ['s', stroeDate]]]
				}
				this.requestData.cnd = cnd;
				phis.application.pha.script.PharmacyMaintainList.superclass.loadData
						.call(this);
			},
			// 生成时间框
			getCndBar : function(items) {
				this.simple = new Ext.ux.form.Spinner({
							fieldLabel : '年份',
							name : 'storeDate',
							value : new Date().format('Y-m'),
							strategy : {
								xtype : "month"
							}
						})
				return [this.simple];
			},
			// 刷新
			doRefresh : function() {
				this.refresh();
			},
			// 新增
			doAdd : function() {
				this.module = this.createModule("module", this.addRef);
				this.module.on("save", this.onSave, this);
				this.module.on("close", this.onClose, this);
				var win = this.module.getWin();
				win.add(this.module.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.module.doNew();
				}
			},
			// 加载数据时判断状态
			onRenderer : function(value, metaData, r) {
				if (r.get("YSGH") != null && r.get("YSGH") != ""
						&& r.get("YSGH") != undefined) {
					return "已确认";
				}
				return "未确认";
			},
			onClose : function() {
				this.getWin().hide();
			},
			// 双击事件
			onDblClick : function(grid, index, e) {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var body = {
					"YHDH" : r.get("YHDH"),
					"XTSB" : r.get("XTSB")
				}
				this.module = this.createModule("module", this.addRef);
				this.module.on("save", this.onSave, this);
				this.module.on("close", this.onClose, this);
				var win = this.module.getWin();
				win.add(this.module.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					if (r.get("YSGH") != null && r.get("YSGH") != "") {
						this.module.doRead(body);
					} else {
						this.module.loadData(body);
					}
				}
			},
			// 删除
			doRemove : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				if (r.get("YSGH") != null && r.get("YSGH") != "") {
					MyMessageTip.msg("提示", "已确认单子不能删除!", true);
					return;
				}
				var body = {
					"YHDH" : r.get("YHDH"),
					"XTSB" : r.get("XTSB")
				};
				this.grid.el.mask("正在删除...", "x-mask-loading");
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.removeActionId,
							body : body
						});
				this.grid.el.unmask();
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.initPanel);
					this.refresh();
					return;
				}
				MyMessageTip.msg("提示", "删除成功!", true);
				this.refresh();
			}
		});