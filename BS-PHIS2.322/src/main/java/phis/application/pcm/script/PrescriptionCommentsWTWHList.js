$package("phis.application.pcm.script")
$import("phis.script.SimpleList")

phis.application.pcm.script.PrescriptionCommentsWTWHList = function(cfg) {
	phis.application.pcm.script.PrescriptionCommentsWTWHList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.pcm.script.PrescriptionCommentsWTWHList,
		phis.script.SimpleList, {
			onRenderer : function(value, metaData, r) {
				if (r.data.ZFPB == 1) {
					return "<img src='" + ClassLoader.appRootOffsetPath
							+ "resources/phis/resources/images/(00,04).png'/>"
				}
				return value;
			},
			doXz : function() {
				this.form = this.createModule("form", this.refForm);
				this.form.on("save", this.onSave, this);
				var win = this.form.getWin();
				win.add(this.form.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.form.op = "create";
					this.form.doNew();
				}
			},
			doXg : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				this.form = this.createModule("form", this.refForm);
				this.form.on("save", this.onSave, this);
				var win = this.form.getWin();
				win.add(this.form.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.form.initDataId = r.get("WTXH");
					this.form.op = "update";
					this.form.loadData();
				}
			},
			doSc : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				this.grid.el.mask("正在删除数据...", "x-mask-loading")
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.saveServiceId,
							serviceAction : this.serviceAction,
							body : {
								"WTXH" : r.get("WTXH"),
								"WTDM" : r.get("WTDM"),
								"TAG" : "sc"
							}
						});
				this.grid.el.unmask()
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return;
				}
				MyMessageTip.msg("提示", "删除成功", true);
				this.refresh();
			},
			doZx : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				if(r.get("ZFPB")==1){
				MyMessageTip.msg("提示", "已注销问题,不能重复注销", true);
				this.refresh();
				return;
				}
				this.grid.el.mask("正在注销数据...", "x-mask-loading")
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.saveServiceId,
							serviceAction : this.serviceAction,
							body : {
								"WTXH" : r.get("WTXH"),
								"WTDM" : r.get("WTDM"),
								"TAG" : "zx"
							}
						});
				this.grid.el.unmask()
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return;
				}
				MyMessageTip.msg("提示", "注销成功", true);
				this.refresh();
			},
			doQxzx : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				if(r.get("ZFPB")==0){
				MyMessageTip.msg("提示", "问题未注销,不能取消注销", true);
				this.refresh();
				return;
				}
				this.grid.el.mask("正在取消注销数据...", "x-mask-loading")
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.saveServiceId,
							serviceAction : this.serviceAction,
							body : {
								"WTXH" : r.get("WTXH"),
								"WTDM" : r.get("WTDM"),
								"TAG" : "qxzx"
							}
						});
				this.grid.el.unmask()
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return;
				}
				MyMessageTip.msg("提示", "取消注销成功", true);
				this.refresh();
			}

		})