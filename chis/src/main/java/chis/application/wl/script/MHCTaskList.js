/**
 * 列举近期未完成妇保的工作列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.wl.script");
$import("chis.application.wl.script.CommonTaskList", "chis.script.EHRView");
chis.application.wl.script.MHCTaskList = function(cfg) {
	cfg.initCnd = ['in', ['$', 'a.businessType'], ['8', '9']];
	chis.application.wl.script.MHCTaskList.superclass.constructor.apply(this, [cfg]);
};

Ext.extend(chis.application.wl.script.MHCTaskList, chis.application.wl.script.CommonTaskList, {
	getGroupingView : function() {
		return new Ext.grid.GroupingView({
			showGroupName : true,
			enableNoGroups : false,
			hideGroupedColumn : true,
			enableGroupingMenu : false,
			columnsText : "表格字段",
			groupByText : "使用当前字段进行分组",
			showGroupsText : "表格分组",
			groupTextTpl : "姓名：{[values.rs[0].data.personName]}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "联系电话：{[values.rs[0].data.mobileNumber]}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "户籍地址：{[values.rs[0].data.restRegionCode_text]}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "(共{[values.rs.length]}项)",
			getRowClass : this.getRowClass
		});
	},

	getInitModules : function(record) {
		var businessType = record.get("businessType");
		switch (businessType) {
			case "8" :
				return ["G_02"];
			case "9" :
				return ["G_02"];
		}
	},

	getIdsIdName : function() {
		return "pregnantId";
	}
});