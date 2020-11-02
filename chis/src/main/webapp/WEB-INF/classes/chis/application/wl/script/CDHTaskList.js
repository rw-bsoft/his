/**
 * 列举近期未完成妇保的工作列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.wl.script");
$import("chis.application.wl.script.CommonTaskList", "chis.script.EHRView");
chis.application.wl.script.CDHTaskList = function(cfg) {
	cfg.initCnd = ['and',
			['ge', ['$', 'cast(a.businessType as int)'], ['i', 5]],
			['le', ['$', 'cast(a.businessType as int)'], ['i', 7]]];
	chis.application.wl.script.CDHTaskList.superclass.constructor.apply(this, [cfg]);
};

Ext.extend(chis.application.wl.script.CDHTaskList, chis.application.wl.script.CommonTaskList, {

	getStore : function(items) {
		var o = this.getStoreFields(items);
		var reader = new Ext.data.JsonReader({
					root : "body",
					totalProperty : "totalCount",
					id : o.pkey,
					fields : o.fields
				});
		var url = ClassLoader.serverAppUrl || "";
		var proxy = new Ext.data.HttpProxy({
					url : url + "*.jsonRequest",
					method : "post",
					jsonData : this.requestData
				});
		proxy.on("loadexception", function(proxy, o, response, arg, e) {
					if (response.status == 200) {
						var json = eval("(" + response.responseText + ")");
						if (json) {
							var code = json["x-response-code"];
							var msg = json["x-response-msg"];
							this.processReturnMsg(code, msg, this.refresh);
						}
					} else {
						this.processReturnMsg(404, "ConnectionError",
								this.refresh);
					}
				}, this);
		var store = new Ext.data.GroupingStore({
					reader : reader,
					proxy : proxy,
					sortInfo : {
						field : "planDate",
						direction : "ASC"
					},
					groupField : "empiId"
				});
		store.on("load", this.onStoreLoadData, this);
		store.on("beforeload", this.onStoreBeforeLoad, this);
		return store;
	},

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
					+ "性别：{[values.rs[0].data.sexCode_text]}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "联系电话：{[values.rs[0].data.mobileNumber]}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "户籍地址：{[values.rs[0].data.homeAddress_text]}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "(共{[values.rs.length]}项)",
			getRowClass : this.getRowClass
		});
	},

	getInitModules : function(record) {
		var businessType = record.get("businessType");
		if (businessType == "5") {
			return ["H_03"];
		} else if (businessType == "6") {
			var extend1 = record.get("extend1");
			if (extend1 < 12) {
				return ["H_97"];
			} else if (extend1 < 36) {
				return ["H_98"];
			} else {
				return ["H_99"];
			}
		} else if (businessType == "7") {
			return ["H_10"];
		}
	},

	getModuleName : function(record) {
		var businessType = record.get("businessType");
		if (businessType == "6") {
			var extend1 = record.get("extend1");
			if (extend1 < 12) {
				return businessType + "_1";
			} else if (extend1 < 36) {
				return businessType + "_2";
			} else {
				return businessType + "_3";
			}
		}
		return businessType;
	},

	getIdsIdName : function() {
		return "recordId";
	},

	getArgsIdName : function() {
		return "recordId";
	}
});