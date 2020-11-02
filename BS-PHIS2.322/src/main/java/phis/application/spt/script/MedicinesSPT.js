$package("phis.application.spt.script")

$import("phis.script.SimpleList")
phis.application.spt.script.MedicinesSPT = function(cfg) {
	phis.application.spt.script.MedicinesSPT.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(phis.application.spt.script.MedicinesSPT, phis.script.SimpleList, {
	initPanel : function(sc) {
		if (this.mainApp['phis'].storehouseId == null
				|| this.mainApp['phis'].storehouseId == ""
				|| this.mainApp['phis'].storehouseId == undefined) {
			Ext.Msg.alert("提示", "未设置登录药库,请先设置");
			return null;
		}
		this.tbar = this.initConditionFields();
		var grid = phis.application.spt.script.MedicinesSPT.superclass.initPanel
				.apply(this, sc);
		this.grid = grid;
		return grid;
	},
	initConditionFields : function() {
		var items = [];
		var date = new Date().format('Y-m-d');
		var beginDate = date.substring(0, date.lastIndexOf("-")) + "-01";
		items.push(new Ext.form.Label({
					text : '选择日期：'
				}));
		items.push(new Ext.form.DateField({
					name : 'beginDate',
					value : beginDate,
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '日期'
				}));
		return items
	},
	doDownloads : function() {
		var date = this.tbar[1].value;
		var url = "http://10.2.202.21:8280/services/medicalbusiness"
		var payapi = phis.script.rmi.miniJsonRequestSync({
					serviceId : "phis.SptService",
					serviceAction : "medicinesDownload",
					body : {
						url : url,
						date : date
					}
				});
		var json = payapi.json;
		var body = json.body;
		if (payapi.code != 200) {
			MyMessageTip.msg("提示", payapi.msg, true);
			return;
		} else if (body.code) {
			MyMessageTip.msg("提示", body.msg, true);
			return;
		} else {
			MyMessageTip.msg("提示", "药品省平台药品信息成功!", true);
		}
		this.refresh();
	},
	doGysdownloads : function() {
		var url = "http://10.2.202.21:8280/services/medicalbusiness"
		var payapi = phis.script.rmi.miniJsonRequestSync({
					serviceId : "phis.SptService",
					serviceAction : "factoryDownload",
					body : {
						url : url
					}
				});
		var json = payapi.json;
		var body = json.body;
		if (payapi.code != 200) {
			MyMessageTip.msg("提示", payapi.msg, true);
			return;
		} else if (body.code) {
			MyMessageTip.msg("提示", body.msg, true);
			return;
		} else if (payapi.code == 200) {
			MyMessageTip.msg("提示", "省平台供应商信息下载成功!", true);
		}
		this.refresh();
	}
});