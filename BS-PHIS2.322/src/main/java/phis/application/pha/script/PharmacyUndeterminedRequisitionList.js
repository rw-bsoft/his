$package("phis.application.pha.script")
$import("phis.application.pha.script.PharmacyMySimpleLeftList")

phis.application.pha.script.PharmacyUndeterminedRequisitionList = function(cfg) {
	cfg.conditionText = "目标药房";// 条件名称
	cfg.selectItemId = "MBYF";// 下拉框的id
	phis.application.pha.script.PharmacyUndeterminedRequisitionList.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeRemove", this.onBeforeRemove, this);
}
Ext.extend(phis.application.pha.script.PharmacyUndeterminedRequisitionList,
		phis.application.pha.script.PharmacyMySimpleLeftList, {
			expansion : function(cfg) {
				var bar = cfg.tbar;
				cfg.tbar = {
					enableOverflow : true,
					items : bar
				}
			},
			// 刷新页面
			doRefreshWin : function() {
				if (this.selectValue) {
					this.requestData.cnd = [
							'and',
							['eq', ['$', 'a.MBYF'], ['l', this.selectValue]],
							['eq', ['$', 'a.RKBZ'], ['i', 0]],
							['eq', ['$', 'a.SQYF'],
									['l', this.mainApp['phis'].pharmacyId]],
							['eq', ['$', 'a.TYPB'], ['i', 0]]];
					this.requestData.serviceId = this.fullserviceId;
					this.requestData.serviceAction = this.queryActionId;
					this.refresh();
				}
			},
			// 修改确认删除时 获取唯一识别
			getInitDataBody : function(r) {
				var initDataBody = {};
				initDataBody["SQYF"] = r.data.SQYF;
				initDataBody["SQDH"] = r.data.SQDH;
				return initDataBody;
			},
			// 页面打开时记录前增加未确认图标
			onRenderer : function(value, metaData, r) {
				if (r.data.TJBZ == 0) {
					return "<img src='" + ClassLoader.appRootOffsetPath
									+ "resources/phis/resources/images/add.gif'/>"
					//return "<img src='images/add.gif'/>";
				}
				if (r.data.TJBZ == 1 && r.data.CKBZ == 0) {
					return "<img src='" + ClassLoader.appRootOffsetPath
									+ "resources/phis/resources/images/i_warn.gif'/>"
					//return "<img src='images/i_warn.gif'/>";
				}
				if (r.data.TJBZ == 1 && r.data.CKBZ == 1) {
					return "<img src='" + ClassLoader.appRootOffsetPath
									+ "resources/phis/resources/images/grid.png'/>"
					//return "<img src='images/grid.png'/>";
				}

			},
			// 打开确认界面时判断是否已经提交记录,如果已提交记录并未出库记录 则不能修改
			doCommit : function() {
				phis.application.pha.script.PharmacyUndeterminedRequisitionList.superclass.doCommit
						.call(this);
				var r = this.getSelectedRecord()
				if (r.data.TJBZ == 1 && r.data.CKBZ == 0) {
					this.module.changeButtonState("read");
				} else if (r.data.TJBZ == 0) {
					this.module.changeButtonState("update");
				}
			},
			// 打开修改界面时判断是否已经提交记录,如果已提交记录并未出库记录 则不能修改
			doUpd : function() {
				phis.application.pha.script.PharmacyUndeterminedRequisitionList.superclass.doUpd
						.call(this);
				/** 2013-07-22 gejj 修改2169bug 与蔡建勇沟通过添加后无影响**/
				this.module.on("commit", this.onCommit, this);
				/** end**/
				var r = this.getSelectedRecord()
				if (r.data.TJBZ == 1 && r.data.CKBZ == 0) {
					this.module.changeButtonState("read");
				}
				if (r.data.TJBZ == 1 && r.data.CKBZ == 1) {
					this.module.changeButtonState("commit");
				}
			},
			// 删除前判断,是否提交的记录,已提交不能删除
			onBeforeRemove : function(scamel, r) {
				if (r.data.TJBZ == 1) {
					MyMessageTip.msg("提示", "已提交记录不能删除!", true);
					return false;
				}
			},
			// 提交
			doSubmit : function() {
				var re = this.getSelectedRecord();
				if (re == null) {
					return;
				}
				if (re.data.TJBZ == 1) {
					MyMessageTip.msg("提示", "记录已提交记录不需要重复提交!", true);
					return false;
				}
				var data = {};
				data["key"] = this.selectValue;
				var record = {};
				record["data"] = data;
				Ext.Msg.show({
							title : '确认提交记录',
							msg : '确定提交调拨申请单' + re.data.SQDH + '吗?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									var body = {
										"SQDH" : re.data.SQDH,
										"SQYF" : re.data.SQYF
									};
									var r = phis.script.rmi.miniJsonRequestSync({
												serviceId : this.serviceId,
												serviceAction : this.submitActionId,
												body : body
											});
									if (r.code > 300) {
										this.processReturnMsg(r.code, r.msg,
												this.doSave);
										this.fireEvent("select", record)
										return;
									}
									this.fireEvent("select", record)
								}
							},
							scope : this
						})
			},
			getDicFitle : function() {
				return "['and',['ne',['$','item.properties.YFSB'],['l',"
						+ this.mainApp['phis'].pharmacyId
						+ "]],['eq',['$','item.properties.JGID'],['s',"
						+ this.mainApp['phisApp'].deptId
						+ "]],['ne',['$','item.properties.ZXBZ'],['i', 1]]]";
			}
		})