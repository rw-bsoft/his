$package("phis.application.pha.script")
$import("phis.application.pha.script.PharmacyMySimpleLeftList")

phis.application.pha.script.PharmacyUndeterminedDeployInventoryList = function(cfg) {
	cfg.conditionText = "申请药房";// 条件名称
	cfg.selectItemId = "SQYF";// 下拉框的id
	phis.application.pha.script.PharmacyUndeterminedDeployInventoryList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.pha.script.PharmacyUndeterminedDeployInventoryList,
		phis.application.pha.script.PharmacyMySimpleLeftList, {
			// 刷新页面
			doRefreshWin : function() {
				if (this.selectValue) {
					this.requestData.cnd = [
							'and',
							['eq', ['$', 'a.SQYF'], ['l', this.selectValue]],
							['eq', ['$', 'a.CKBZ'], ['i', 0]],
							['eq', ['$', 'a.MBYF'],
									['l', this.mainApp['phis'].pharmacyId]],
							[
									'or',
									['and', ['eq', ['$', 'a.TJBZ'], ['i', 1]],
											['eq', ['$', 'a.RKBZ'], ['i', 0]],
											['eq', ['$', 'a.TYPB'], ['i', 0]]],
									['and', ['eq', ['$', 'a.TJBZ'], ['i', 1]],
											['eq', ['$', 'a.RKBZ'], ['i', 1]],
											['eq', ['$', 'a.TYPB'], ['i', 1]]]]];

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
				return "<img src='" + ClassLoader.appRootOffsetPath
									+ "resources/phis/resources/images/grid.png'/>"
				//return "<img src='images/grid.png'/>";
			},
			// 双击修改操作
			onDblClick : function(grid, index, e) {
				var item = {};
				item.text = "确认";
				item.cmd = "commit";
				this.doAction(item, e)
			},
			getDicFitle : function() {
				return "['and',['ne',['$','item.properties.YFSB'],['l',"
						+ this.mainApp['phis'].pharmacyId
						+ "]],['eq',['$','item.properties.JGID'],['s',"
						+ this.mainApp['phisApp'].deptId
						+ "]],['ne',['$','item.properties.ZXBZ'],['i', 1]]]";
			},
			doBack : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				body={};
				body["SQDH"]=r.data.SQDH;
				body["SQYF"]=r.data.SQYF;
				Ext.Msg.show({
							title : '确认退回记录[' + r.data.SQDH + ']',
							msg : '是否确认退回?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									var ret = phis.script.rmi.miniJsonRequestSync({
												serviceId : this.serviceId,
												serviceAction : this.backActionId,
												body : body
											});
									if (ret.code > 300) {
										this.processReturnMsg(ret.code,
												ret.msg, this.doBack);
									}
									this.doRefresh();
								}
							},
							scope : this
						})
			}
		})