$package("phis.application.cfg.script")

$import("phis.script.SimpleList")

phis.application.cfg.script.ConfigHsqxYgList = function(cfg) {
	this.height = "250";
	this.closeAction = true;
	this.disablePagingTbr = cfg.disablePagingTbr = true;
	cfg.cnds = ['eq', ['$', 'a.KSDM'], ['i', 0]];
	cfg.initCnd = ['eq', ['$', 'a.KSDM'], ['i', 0]];
	phis.application.cfg.script.ConfigHsqxYgList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.cfg.script.ConfigHsqxYgList, phis.script.SimpleList, {

			doExecute : function() {
				var r = this.getSelectedRecord();
				var data = {};
				if (r != null) {
					var n = this.store.indexOf(r)
					if (n > -1) {
						this.selectedIndex = n
					}
					data["YGID"] = r.get("YGID");
					var hszbz = r.get("HSZBZ");
					if (hszbz == 0) {
						Ext.Msg.confirm("请确认", "是否确定设置[" + r.get("PERSONNAME")
										+ "]为护士长?", function(btn) {
									if (btn == 'yes') {
										data["HSZBZ"] = 1;
										this.updateHSZBZ(data);
									}
								}, this);
						return;
					} else {
						Ext.Msg.confirm("请确认", "是否确定取消[" + r.get("PERSONNAME")
										+ "]为护士长?", function(btn) {
									if (btn == 'yes') {
										data["HSZBZ"] = 0;
										this.updateHSZBZ(data);
									}
								}, this);
						return;
					}
				} else {
					MyMessageTip.msg("提示", '请选择需要维护的记录!', true);
				}
			},
			updateHSZBZ : function(data) {
				this.grid.el.mask("正在启用...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "configHsqxYgService",
							serviceAction : "updateHSZBZ",
							schemaList : "WL_HSQX",
							body : data
						}, function(code, msg, json) {
							this.grid.el.unmask()
							if (code >= 300) {
								MyMessageTip.msg("提示", msg, true);
							} else {
								//MyMessageTip.msg("提示", "设置护士长成功", true);
								this.refresh();
							}
						}, this)
			},
			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var title = r.id;
				if (this.KSDM != 0) {
					MyMessageTip.msg("提示", "该用户已经设置了相应的科室权限,不能删除!", true);
					return;
				}
				if (this.isCompositeKey) {
					title = "";
					for (var i = 0; i < this.schema.keys.length; i++) {
						title += r.get(this.schema.keys[i])
					}
				}
				// add by liyl 2012-06-17 提示信息增加名称显示功能
				if (this.removeByFiled && r.get(this.removeByFiled)) {
					title = r.get(this.removeByFiled);
				}
				Ext.Msg.show({
							title : '确认删除记录[' + title + ']',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.processRemove(r);
								}
							},
							scope : this
						})
			}
		});