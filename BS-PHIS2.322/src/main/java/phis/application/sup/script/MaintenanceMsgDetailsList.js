$package("phis.application.sup.script")
$import("phis.script.SimpleList")

phis.application.sup.script.MaintenanceMsgDetailsList = function(cfg) {
	var dat = new Date().format('Y-m-d');
	var dateFromValue = dat.substring(0, dat.lastIndexOf("-")) + "-01";
	this.dateFromValue = dateFromValue;
	cfg.cnds = [
			'and',
			[
					'and',
					[
							'and',
							['ge', ['$', "str(a.SXRQ,'yyyy-mm-dd')"],
									['s', dateFromValue]],
							['le', ['$', "str(a.SXRQ,'yyyy-mm-dd')"],
									['s', dat]]],
					['eq', ['$', 'a.KFXH'],
							['$', '%user.properties.treasuryId']]],
			['eq', ['$', 'a.WXZT'], ['i', 1]]];
	phis.application.sup.script.MaintenanceMsgDetailsList.superclass.constructor
			.apply(this, [cfg])
}
var inputValueDet = 1;
Ext.extend(phis.application.sup.script.MaintenanceMsgDetailsList,
		phis.script.SimpleList, {
			init : function() {
				this.addEvents({
							"gridInit" : true,
							"beforeLoadData" : true,
							"loadData" : true,
							"loadSchema" : true
						})
				this.requestData = {
					serviceId : this.listServiceId,
					schema : this.entryName,
					ksType : 1,
					method : 'execute',
					dicValue : this.dicValue,
					cnd : this.cnds,
					pageSize : this.pageSize > 0 ? this.pageSize : 0,
					pageNo : 1
				}
				if (this.serverParams) {
					Ext.apply(this.requestData, this.serverParams)
				}
				if (this.autoLoadSchema) {
					this.getSchema();
				}
			},
			expansion : function(cfg) {
				// 顶部工具栏
				var label = new Ext.form.Label({
							text : "维修时间："
						});
				this.dateField = new Ext.form.DateField({
							name : 'storeDate',
							value : this.dateFromValue,
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d'
						});
				this.dateFieldEnd = new Ext.form.DateField({
							name : 'storeDate',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d'
						});
				// 状态
				var filelable = new Ext.form.Label({
							text : "报告状态："
						})
				this.statusRadio = new Ext.form.RadioGroup({
					height : 20,
					width : 120,
					id : 'bgzt',
					name : 'bgzt', // 后台返回的JSON格式，直接赋值
					value : "1",
					items : [{
								boxLabel : '未审核',
								name : 'bgzt',
								inputValue : 1
							}, {
								boxLabel : '已审核',
								name : 'bgzt',
								inputValue : 2
							}],
					listeners : {
						change : function(group, newValue, oldValue) {
							inputValueDet = newValue.inputValue;
							if (newValue.inputValue == 1) {
								this.requestData.cnd = [
										'and',
										[
												'and',
												[
														'and',
														[
																'ge',
																['$',
																		"str(a.SXRQ,'yyyy-mm-dd')"],
																[
																		's',
																		this.dateField.value]],
														[
																'le',
																['$',
																		"str(a.SXRQ,'yyyy-mm-dd')"],
																[
																		's',
																		this.dateFieldEnd.value]]],
												[
														'eq',
														['$', 'a.KFXH'],
														[
																'i',
																this.mainApp['phis'].treasuryId]]],
										['eq', ['$', 'a.WXZT'],
												['i', newValue.inputValue]]];
							} else {
								this.requestData.cnd = [
										'and',
										[
												'and',
												[
														'and',
														[
																'ge',
																['$',
																		"str(a.SXRQ,'yyyy-mm-dd')"],
																[
																		's',
																		this.dateField.value]],
														[
																'le',
																['$',
																		"str(a.SXRQ,'yyyy-mm-dd')"],
																[
																		's',
																		this.dateFieldEnd.value]]],
												[
														'eq',
														['$', 'a.KFXH'],
														[
																'i',
																this.mainApp['phis'].treasuryId]]],
										['eq', ['$', 'a.WXZT'],
												['i', newValue.inputValue]]];
							}
							this.loadData();
						},
						scope : this
					}
				});
				var tbar = cfg.tbar;
				cfg.tbar = [];
				cfg.tbar.push([label, this.dateField, "至", this.dateFieldEnd,
						'-', filelable, this.statusRadio, '-', tbar]);
			},
			doRefresh : function() {
				this.clear();
				var startDate = "";// 开始时间
				var endDate = ""; // 结束时间
				if (this.dateField) {
					startDate = new Date(this.dateField.getValue())
							.format("Y-m-d");
				}
				if (this.dateFieldEnd) {
					endDate = new Date(this.dateFieldEnd.getValue())
							.format("Y-m-d");
				}
				if (inputValueDet == 1) {
					this.requestData.cnd = [
							'and',
							[
									'and',
									[
											'and',
											[
													'ge',
													['$',
															"str(a.SXRQ,'yyyy-mm-dd')"],
													['s', startDate]],
											[
													'le',
													['$',
															"str(a.SXRQ,'yyyy-mm-dd')"],
													['s', endDate]]],
									[
											'eq',
											['$', 'a.KFXH'],
											[
													'i',
													this.mainApp['phis'].treasuryId]]],
							['eq', ['$', 'a.WXZT'], ['i', inputValueDet]]];
				} else {
					this.requestData.cnd = [
							'and',
							[
									'and',
									[
											'and',
											[
													'ge',
													['$',
															"str(a.SXRQ,'yyyy-mm-dd')"],
													['s', startDate]],
											[
													'le',
													['$',
															"str(a.SXRQ,'yyyy-mm-dd')"],
													['s', endDate]]],
									[
											'eq',
											['$', 'a.KFXH'],
											[
													'i',
													this.mainApp['phis'].treasuryId]]],
							['eq', ['$', 'a.WXZT'], ['i', inputValueDet]]];
				}
				this.loadData();
			},
			doModify : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					MyMessageTip.msg("提示", "请选择要维修的物资！", true);
					return;
				}
				if (r.get("WXZT") != 1) {
					MyMessageTip.msg("提示", "已审核的维修报告单不能修改！", true);
					return;
				}

				this.serviceUpForm = this.createModule("serviceUpForm",
						"phis.application.sup.SUP/SUP/SUP510104");
				this.serviceUpForm.on("save", this.onSave, this);
				this.serviceUpForm.on("refresh", this.doRefresh, this);

				this.serviceUpForm.WXXH = r.get("WXXH");
				this.serviceUpForm.WZXH = r.get("WZXH");
				this.serviceUpForm.initPanel();
				var win = this.serviceUpForm.getWin();
				win.add(this.serviceUpForm.initPanel());
				win.show();
				win.center();
				if (!win.hidden) {
					this.serviceUpForm.loadData();
				}
			},
			doAudit : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					MyMessageTip.msg("提示", "请选择要审核的物资！", true);
					return;
				}
				if (r.get("SHGH")) {
					MyMessageTip.msg("提示", "已经审核！", true);
					return;
				}
				phis.script.rmi.jsonRequest({
							serviceId : "repairRequestrService",
							serviceAction : "auditWXGLform",
							body : r.data.WXXH,
							op : 2
						}, function(code, msg, json) {
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							MyMessageTip.msg("提示", "审核成功！", true);
							this.doRefresh();
						}, this);
			}
		})