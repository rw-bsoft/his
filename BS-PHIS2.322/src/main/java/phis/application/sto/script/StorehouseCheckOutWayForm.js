$package("phis.application.sto.script")

$import("phis.script.TableForm")

phis.application.sto.script.StorehouseCheckOutWayForm = function(cfg) {
	cfg.repeatInspectionServiceId = "storehouseManageService";
	cfg.repeatInspectionActionId = "repeatInspection_ckfs";
	phis.application.sto.script.StorehouseCheckOutWayForm.superclass.constructor.apply(
			this, [cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}

Ext.extend(phis.application.sto.script.StorehouseCheckOutWayForm,
		phis.script.TableForm, {
			onReady : function() {
				phis.application.sto.script.StorehouseCheckOutWayForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				form.findField("CKDH").on("blur", this.onCkdhChange, this);
			},
			// 保存前检验入库方式名称有没重复,科室领用方式和药房的申领方式是否冲突,
			onBeforeSave : function(entryName, op, saveRequest) {
				if(this.op=="create"){
				saveRequest.XTSB=this.mainApp['phis'].storehouseId;
				}
				if(saveRequest.KSPB==1&&saveRequest.DYFS!=0){
				Ext.Msg.alert("提示", "科室领用和对应方式不能同时维护!");
				return false;
				}
				var body = {};
				body["fsmc"] = saveRequest.FSMC;
				body["ckfs"] = (saveRequest.CKFS == null || saveRequest.CKFS == "")
						? 0
						: saveRequest.CKFS;
				body["yksb"]=saveRequest.XTSB;
				body["op"]=this.op;
				body["kspb"]=saveRequest.KSPB;
				body["dyfs"]=saveRequest.DYFS;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.repeatInspectionServiceId,
							serviceAction : this.repeatInspectionActionId,
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.onBeforeSave);
					return false;
				} else {
					return true;
				}
			},
			// 出库单号验证
			onCkdhChange : function() {
				if (this.op == "create") {
					return 1;
				}
				var form = this.form.getForm();
				var ckdh = form.findField("CKDH").getValue();
				var oldCkdh = this.data["CKDH"];
				if (ckdh < oldCkdh) {
					Ext.Msg.alert("提示", "出库单号不能修改比原来的小");
					form.findField("CKDH").setValue(oldCkdh);
					return 0;
				}
				return 1;
			}
		});