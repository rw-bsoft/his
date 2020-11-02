/**
 * 采购入库-计划单
 * 
 * @author : caijy
 */
$package("phis.application.sto.script")

$import("phis.script.SelectList")

phis.application.sto.script.StorehouseProcurementPlanSelectList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.modal = true
	phis.application.sto.script.StorehouseProcurementPlanSelectList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.sto.script.StorehouseProcurementPlanSelectList,
		phis.script.SelectList, {
			loadData : function() {
				this.requestData.serviceId = this.fullserviceId;
				this.requestData.serviceAction = this.serviceAction;
				phis.application.sto.script.StorehouseProcurementPlanSelectList.superclass.loadData
						.call(this)
			},
			doConfirm : function() {
				var r = this.getSelectedRecords();
				var l = r.length;
				if (l == 0) {
					this.doClose();
					return;
				}
				var dwxh = 0;
				var dwmc = "";
				var xtdw = true;
				var body = new Array();
				var bz="";
				for (var i = 0; i < l; i++) {
					body.push(r[i].data.JHDH);
					if (i == 0) {
						dwxh = r[i].data.DWXH;
						dwmc = r[i].data.DWMC;
						bz=r[i].data.JHBZ
					} else {
						if (r[i].data.DWXH != dwxh) {
							xtdw = false;
						}
						if(bz.length>0&&r[i].data.JHBZ!=null&&r[i].data.JHBZ!=""){
						bz+=","
						}
						bz+=r[i].data.JHBZ;
					}
				}
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryAction,
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return;
				}
				var data = ret.json.body;
				if (data.length == 0) {
					this.doClose();
					return;
				}
				var l = data.length;
				var sfcf = false;
				for (var i = 0; i < l; i++) {
					for (var j = i + 1; j < l; j++) {
						if (data[i].YPXH == data[j].YPXH
								&& data[i].YPCD == data[j].YPCD) {
							sfcf = true;
							break;
						}
					}
					if (sfcf) {
						break;
					}
				}
				if (sfcf) {
					Ext.Msg.show({
						title : '是否继续',
						msg : '计划单中有重复的药品,是否继续?',
						modal : true,
						width : 300,
						buttons : Ext.MessageBox.OKCANCEL,
						multiline : false,
						fn : function(btn, text) {
							if (btn == "ok") {
								if (xtdw) {
									this.fireEvent("confirm", data,bz, dwxh, dwmc);
								} else {
									this.fireEvent("confirm", data,bz);
								}
							}
						},
						scope : this
					})
				} else {
					if (xtdw) {
						this.fireEvent("confirm", data,bz, dwxh, dwmc);
					} else {
						this.fireEvent("confirm", data,bz);
					}
				}
				this.doClose();
			},
			doClose : function() {
				this.getWin().hide();
			}
		});