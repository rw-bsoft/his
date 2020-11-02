$package("phis.application.reg.script");

$import("phis.script.SimpleList")

phis.application.reg.script.RegCountList = function(cfg) {
	this.serverParams = {
		serviceAction : cfg.queryActionId
	};
	phis.application.reg.script.RegCountList.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(phis.application.reg.script.RegCountList, phis.script.SimpleList, {
	doQuery : function() {
		var beginDate = this.beginDate;
		var endDate = this.endDate;
		if (beginDate != null && endDate != null && beginDate != ""
				&& endDate != "" && beginDate > endDate) {
			Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
			return;
		}
		var tjfs = this.tjfs;
		this.requestData.body = {
			beginDate : beginDate,
			endDate : endDate,
			TJFS : tjfs
		};
		this.store.load({
			callback : function(records, options, success) {
				// this.setHJValue(); // 调用重新合计
			},
			scope : this
				// 作用域为this。必须加上否则this.setHJValue(); 无法调用
				});
		if (tjfs == 1) {
			this.grid.getColumnModel().setColumnHeader(1, "科室名称");
		} else {
			this.grid.getColumnModel().setColumnHeader(1, "性质名称");
		}
	},
	expansion : function(cfg) {
		var label = new Ext.form.Label({
			html : "<div id='rc' align='center' style='color:blue'>共 0 人次</div>"
		})
		cfg.bbar = [];
		cfg.bbar.push(label);
	},
	onStoreLoadData : function(store, records, ops) {
		var store = this.grid.getStore();
		var count = 0;
		for (var i = 0; i < store.getCount(); i++) {
			var record = store.getAt(i);
			if (record.data.RC != "") {
				count += record.data.RC;
			}
		}
		if (document.getElementById("rc")) {
			document.getElementById("rc").innerHTML = "共 " + count + " 人次";
		}
	}
});