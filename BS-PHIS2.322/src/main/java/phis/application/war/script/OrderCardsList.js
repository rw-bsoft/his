$package("phis.application.war.script");

$import("phis.script.SelectList");
phis.application.war.script.OrderCardsList = function(cfg) {
	this.serverParams = {serviceAction:cfg.serviceAction};
	this.CardModule = null;
	this.typeValue = null;
	phis.application.war.script.OrderCardsList.superclass.constructor.apply(
			this, [cfg]);
	
},

Ext.extend(phis.application.war.script.OrderCardsList,
		phis.script.SelectList, {
			CardPrint : function(){//口服卡
				//获得选中的医嘱病人List
				var records = this.getSelectedRecords();
				var arr_zyh = [];
				for(i=0;i < records.length;i++){
					var record = records[i];
					arr_zyh.push(record.data.ZYH);
				}
				//获得医嘱卡片Tab的module
				this.CardModule.sendToCKData(arr_zyh,this.typeValue.typeValue,this.typeValue.orderTypeValue);//this.typeValue.typeValue 卡片类型：固定卡片,执行单
			},
			getCM : function(items) {
				var cm = phis.application.war.script.OrderCardsList.superclass.getCM.call(this,
						items)
				sm = this.sm;
				sm.on("rowselect", function(sm, rowIndex, record) {
							if (this.mutiSelect) {
								this.selects[record.id] = record
							} else {
								this.singleSelect = record
							}
							this.CardPrint();
						}, this)
				sm.on("rowdeselect", function(sm, rowIndex, record) {
							if (this.mutiSelect) {
								delete this.selects[record.id]
							}
							this.CardPrint();
						}, this)
				//return [sm].concat(cm);
				return cm;
			}
});