$package("phis.application.pha.script")
$import("phis.application.pha.script.PharmacyMySimpleLeftList")

phis.application.pha.script.PharmacyUndeterminedInventoryBackList = function(cfg) {
	cfg.conditionText = "目标药房";// 条件名称
	cfg.selectItemId = "MBYF";// 下拉框的id
	phis.application.pha.script.PharmacyUndeterminedInventoryBackList.superclass.constructor
			.apply(this, [cfg])
//	this.on("beforeRemove", this.onBeforeRemove, this);
}
Ext.extend(phis.application.pha.script.PharmacyUndeterminedInventoryBackList,
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
							['eq', ['$', 'a.TYPB'], ['i', 1]]];
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
				if (r.data.TJBZ == 1 && r.data.CKBZ == 1) {
					return "<img src='" + ClassLoader.appRootOffsetPath
									+ "resources/phis/resources/images/grid.png'/>"
					//return "<img src='images/grid.png'/>";
				}
			},
			getDicFitle : function() {
				return "['and',['ne',['$','item.properties.YFSB'],['l',"
						+ this.mainApp['phis'].pharmacyId
						+ "]],['eq',['$','item.properties.JGID'],['s',"
						+ this.mainApp['phisApp'].deptId
						+ "]],['ne',['$','item.properties.ZXBZ'],['i', 1]]]";
			}
		})