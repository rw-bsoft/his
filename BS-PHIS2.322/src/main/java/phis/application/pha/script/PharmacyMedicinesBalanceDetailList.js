$package("phis.application.pha.script")
$import("phis.script.SimpleList")

phis.application.pha.script.PharmacyMedicinesBalanceDetailList = function(cfg) {
	cfg.modal = true;
	cfg.autoLoadData = false;
	cfg.serviceId = "pharmacyManageService";
	cfg.serviceAction = "queryBalanceDetailInfo";
	cfg.initializationServiceAction = "balanceDetailInit";
	cfg.entryName = "phis.application.pha.schemas.YF_YPDZ_MX";
	phis.application.pha.script.PharmacyMedicinesBalanceDetailList.superclass.constructor
		.apply(this, [cfg]);
}

Ext.extend(phis.application.pha.script.PharmacyMedicinesBalanceDetailList, 
		phis.script.SimpleList, {
			loadData : function(data) {
				this.grid.remove();
				this.resetFirstPage();
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : this.initializationServiceAction,
					body : data
				});
				var ret_data = ret.json.body;
				this.initLabel(ret_data);
				Ext.apply(data, ret_data);
				this.requestData.body = data;
				this.requestData.serviceId=this.fullserviceId;
				this.requestData.serviceAction=this.serviceAction;
				phis.application.pha.script.PharmacyMedicinesBalanceDetailList.superclass.loadData.call(this);
			},
			doCancel : function() {
				this.getWin().hide();
			},
			getCndBar : function(cfg) {
				this.ypmcLabel = new Ext.form.Label({
				});
				this.ypggLabel = new Ext.form.Label({
				});
				this.ypdwLabel = new Ext.form.Label({
				});
				return [this.ypmcLabel, '-', this.ypggLabel, '-', this.ypdwLabel, '->']
			},
			initLabel : function(data) {
				if(!this.ypmcLabel && !this.ypggLabel && !this.ypdwLabel){
					return;
				}
				this.ypmcLabel.setText("药品名称：" + data.YPMC);
				this.ypggLabel.setText("规格：" + data.YFGG);
				this.ypdwLabel.setText("单位：" + data.YFDW);
			}
		});