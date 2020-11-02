$package("phis.application.phsa.script")

$import("phis.script.SimpleList", "phis.script.ux.GroupSummary",
		"phis.script.ux.RowExpander", "util.dictionary.TreeDicFactory")

phis.application.phsa.script.PHSAInfectiousDiseaseList = function(cfg) {
	this.serverParams = {
		serviceAction : cfg.queryActionId
	};
	cfg.disablePagingTbr = true;
	cfg.width = "900";
	phis.application.phsa.script.PHSAInfectiousDiseaseList.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.phsa.script.PHSAInfectiousDiseaseList,
		phis.script.SimpleList, {
			/**
			 * 扩展顶部工具栏 添加开始统计时间和结束统计时间
			 * 
			 * @param cfg
			 */
			expansion : function(cfg) {
				// 顶部工具栏
				var label = new Ext.form.Label({
							html : "&nbsp;&nbsp;统计日期："
						});
				this.zhi = new Ext.form.Label({
							html : "&nbsp;&nbsp;至&nbsp;&nbsp;"
						});
				this.beginDate = new phis.script.widgets.DateTimeField({
							name : 'beginDate',
							value : new Date().getFirstDateOfMonth(),
							width : 146,
							allowBlank : false,
							hideLabel : true,
							altFormats : 'Y-m-d h:i:s',
							format : 'Y-m-d h:i:s'
						});
				this.endDate = new phis.script.widgets.DateTimeField({
							name : 'endDate',
							value : new Date(),
							width : 146,
							allowBlank : false,
							hideLabel : true,
							altFormats : 'Y-m-d h:i:s',
							format : 'Y-m-d h:i:s'
						});
				var manaLabel = new Ext.form.Label({
							html : "&nbsp;&nbsp;管辖机构："
						})
				// this.manageUnit = this.createDicField({
				// "src" : "",
				// "width" : 160,
				// "id" : "chis.@manageUnit",
				// "render" : "Tree",
				// "defaultValue" : this.mainApp['phisApp'].deptId
				// });
				var manageCfg = {
					id : "chis.@manageUnit",
					width : 160,
					defaultValue : {
						"key" : this.mainApp.deptId,
						"text" : this.mainApp.dept
					},
					filter : ['le', ['len', ['$', 'item.getKey()']], '9'],
					rootVisible : false
				};
				if (this.mainApp.jobType != "T13"
						&& this.mainApp.jobType != "Tsystem") {
					manageCfg.parentKey = this.mainApp.deptId;
					manageCfg.rootVisible = true
				}
				this.manageUnit = util.dictionary.TreeDicFactory
						.createDic(manageCfg);
				// this.manageUnit.setValue();
				var tbar = cfg.tbar;

				this.requestData.body = {
					beginDate : this.beginDate.getValue(),
					endDate : this.endDate.getValue(),
					manageUnit : this.manageUnit.getValue()
				};
				delete cfg.tbar;
				cfg.tbar = [];
				cfg.tbar.push([label, this.beginDate, this.zhi, this.endDate,
						manaLabel, this.manageUnit, tbar]);
			},

			/**
			 * 刷新按钮实现
			 */
			doRefresh : function() {
				var beginDate = new Date(this.beginDate.getValue())
						.format("Y-m-d h:i:s");
				var endDate = new Date(this.endDate.getValue())
						.format("Y-m-d h:i:s");
				if (!this.flag) {
					if (beginDate != null && endDate != null && beginDate != ""
							&& endDate != "" && beginDate > endDate) {
						Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
						return;
					}
				}
				this.requestData.body = {
					beginDate : this.beginDate.getValue(),
					endDate : this.endDate.getValue(),
					manageUnit : this.manageUnit.getValue()
				};
				this.loadData();
			},
			onDblClick : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				var manageUnit = r.get("GXJG");
				if(manageUnit.length==12||manageUnit.length==9){
					return;
				}
				var manageUnitName = r.get("GXJG_text");
				this.manageUnit.setValue({
							"key" : manageUnit,
							"text" : manageUnitName
						});
				this.doRefresh();
			}

		});