$package("phis.application.sto.script")
$import("phis.script.SimpleList")

phis.application.sto.script.StorehouseMonthly = function(cfg) {
	cfg.modal=true;
	cfg.width=250;
	cfg.height=300;
	phis.application.sto.script.StorehouseMonthly.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.sto.script.StorehouseMonthly, phis.script.SimpleList,
		{	
			initPanel : function(sc) {
				if (this.mainApp['phis'].storehouseId == null
						|| this.mainApp['phis'].storehouseId == ""
						|| this.mainApp['phis'].storehouseId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药库,请先设置");
					return null;
				}
				//进行是否初始化验证
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryServiceAction
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.initPanel);
					return null;
				}
				var grid = phis.application.sto.script.StorehouseMonthly.superclass.initPanel
						.call(this, sc);
				this.grid = grid;
				return grid;
			},
			doMonthly:function(){
				this.moudle = this.createModule("monthlyForm", this.refForm);
				this.moudle.on("save", this.onSave, this);
				this.moudle.on("close", this.onClose, this);
				var p = this.getWin();
				p.add(this.moudle.initPanel())
				p.show();
				p.center()
			},
			onClose:function(){
			this.getWin().hide();
			this.refresh();
			}
		})