/**
 * 医保药品对照module
 * 
 * @author caijy
 */
$package("phis.application.yb.script");

$import("phis.script.SimpleModule");

phis.application.yb.script.YpdzModule = function(cfg) {
	phis.application.yb.script.YpdzModule.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.yb.script.YpdzModule, phis.script.SimpleModule, {
			initPanel : function(sc) {
				if (this.panel) {
					return this.panel;
				}
				var schema = sc
				this.schema = schema;
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										width : 800,
										items : this.getLList()
									}],
							tbar : this.createButtons()
						});
				this.panel = panel;
				return panel;
			},
			// 本地人员信息
			getLList : function() {
				this.leftList = this.createModule("leftList", this.refLeftList);
				return this.leftList.initPanel();
			},
			// 刷新,只有点刷新或保存按钮才会清除缓存数据
			doSx : function() {
				this.leftList.updateRecord = {};
				this.leftList.loadData();
			},
			// 将药品对应的医保信息重置,缓存 并不会存到数据库,只有点保存后才会生效
			doCz : function() {
				var r = this.leftList.getSelectedRecord();
				if (!r || !r.get("YPXH") || r.get("YPXH") == null
						|| r.get("YPXH") == "") {
					return;
				}
				r.set("YYZBM", "");
				this.leftList.updateRecord[r.get("YPXH")] = r.data;
			},
			// 保存
			doSave : function() {
				var ed = this.leftList.grid.activeEditor;
				if (!ed) {
					ed = this.leftList.grid.lastActiveEditor;
				}
				if (ed) {
					ed.completeEdit();
				}
				this.panel.el.mask("正在保存...", "x-mask-loading");
				var records = this.leftList.getUpdateRecord();
				if (records.length == 0) {
					MyMessageTip.msg("提示", "没有修改数据", true);
					this.panel.el.unmask();
					return;
				}
				var datas = [];
				for (var i = 0; i < records.length; i++) {
					var r = records[i];
					var data = {};
					if (r.YPXH) {
						data["YPXH"] = r.YPXH;
						data["YPCD"] = r.YPCD;
						data["YYZBM"] = r.YYZBM;
						datas[i] = data;
					}
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.saveActionId,
							bodys : datas
						});
				this.panel.el.unmask();
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.doSave);
					return;
				}
				MyMessageTip.msg("提示", "保存成功", true);
				this.doSx();
			}

		});