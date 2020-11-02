$package("chis.application.index.script")
$import("app.desktop.Module", "util.rmi.miniJsonRequestSync")
chis.application.index.script.RecordGrid = function(cfg) {
	chis.application.index.script.RecordGrid.superclass.constructor.apply(this, [cfg])
}

Ext.extend(chis.application.index.script.RecordGrid, app.desktop.Module, {
	initPanel : function() {
		if (this.panel) {
			return this.panel
		}
		var body = this.getRecordData();
		var data = this.formatData(body);
		var addCount = body.addCount;
		// var addCount = 333;
		// var a = 100;
		// var data = [['健康档案', a, '家庭档案', a], ['老年人档案', a, '儿童档案', a],
		// ['高血压档案', a, '孕产妇档案', a], ['糖尿病档案', a, '精神病档案', a]];
		var cm = this.initCM();
		var store = new Ext.data.SimpleStore({
					data : data,
					fields : ["name1", "data1", "name2", "data2"]
				});
		var panel = new Ext.grid.GridPanel({
					title : " 本月个档新增数： " + addCount + "份",
					height : 150,
					width : 600,
					viewConfig : {
						forceFit : true
					},
					cm : cm,
					store : store
				});
		this.panel = panel;
		return panel;
	},

	getRecordData : function() {
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.statService",
					serviceAction : "getAllRecordCount",
					method:"execute",
					body : {
						deptId : this.mainApp.deptId
					}
				})
		if (result.code > 300) {
			this.processReturnMsg(result.code, result.msg);
			return
		}
		return result.json.body;
	},

	formatData : function(body) {
		var data = [
				['健康档案', body.healthRecord, '家庭档案', body.familyRecord],
				['老年人档案', body.oldPeopleRecord, '儿童档案', body.childRecord],
				['高血压档案', body.hypertensionRecord, '孕产妇档案', body.pregnantRecord],
				['糖尿病档案', body.diabetesRecord, '精神病档案', body.psychosisRecord]];
		return data;
	},

	initCM : function() {
		var cm = new Ext.grid.ColumnModel([{
					header : " 档案名称 ",
					dataIndex : "name1",
					width : 2
				}, {
					header : " 份数 ",
					dataIndex : "data1",
					width : 1
				}, {
					header : " 档案名称 ",
					dataIndex : "name2",
					width : 2
				}, {
					header : " 份数 ",
					dataIndex : "data2",
					width : 1
				}]);
		return cm;
	}
})