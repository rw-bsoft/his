$package("phis.application.cfg.script")

$import("phis.script.SimpleList")

phis.application.cfg.script.ConfigDepartmentEJKFMforKSList = function(cfg) {
	this.height = "250";
	this.closeAction = true;
	cfg.autoLoadData = false;
	this.disablePagingTbr = cfg.disablePagingTbr = true;
	cfg.gridDDGroup = "firstGridKSSelectGroup";
	phis.application.cfg.script.ConfigDepartmentEJKFMforKSList.superclass.constructor
			.apply(this, [ cfg ]);
}

Ext.extend(phis.application.cfg.script.ConfigDepartmentEJKFMforKSList,
		phis.script.SimpleList, {
			onRenderer : function(value, metaData, r) {
				var MRZ = r.get("MRZ");
				var src = (MRZ == 1) ? "yes" : "no";
				return "<img src='images/" + src + ".png'/>";
			},
			doSave : function() {
				if (!this.KFXH) {
					return;
				}
				if (this.KFXH) {
					var store = this.grid.getStore();
					var n = store.getCount()
					var data = []
					for ( var i = 0; i < n; i++) {
						var r = store.getAt(i)
						r.data['KFXH'] = this.KFXH;
						r.data['JGID'] = this.mainApp['phisApp'].deptId;
						data.push(r.data)
					}
					this.grid.el.mask("正在保存数据...", "x-mask-loading")
					phis.script.rmi.jsonRequest({
						serviceId : "configHsqxYgService",
						serviceAction : "saveKFDZ",
						KFXH : this.KFXH,
						body : data
					}, function(code, msg, json) {

						this.grid.el.unmask()
						if (code >= 300) {
							this.processReturnMsg(code, msg);
							return;
						}
						this.loadData();
						this.departmentList.KFXH = this.KFXH;
						this.departmentList.loadData();
					}, this)
				} else {
					MyMessageTip.msg("提示", '请选择需要维护科室权限的员工!', true);
					return;
				}
			},
			doExecute : function() {
				var r = this.getSelectedRecord();
				var data = {};
				if (r != null) {

					var n = this.store.indexOf(r)
					if (n > -1) {
						this.selectedIndex = n
					}
					if (r.get("YGID")) {
						data["YGID"] = r.get("YGID");
						data["KSDM"] = r.get("KSDM");
						this.grid.el.mask("正在启用...", "x-mask-loading")
						phis.script.rmi.jsonRequest({
							serviceId : "configHsqxYgService",
							serviceAction : "updateMRKS",
							schemaList : "WL_HSQX",
							body : data
						}, function(code, msg, json) {
							this.grid.el.unmask()
							if (code >= 300) {
								MyMessageTip.msg("提示", msg, true);
							} else {
								MyMessageTip.msg("提示", "默认成功", true);
								this.refresh();
							}
						}, this)
					} else {
						MyMessageTip.msg("提示", '请选择需要维护科室权限的员工!', true);
						return;
					}
				} else {
					MyMessageTip.msg("提示", '请选择需要默认的记录!', true);
				}
			}
		});