$package("phis.application.sto.script")

$import("phis.script.TableForm")

phis.application.sto.script.StorehouseCheckInWayForm = function(cfg) {
	cfg.repeatInspectionServiceId = "storehouseManageService";
	cfg.repeatInspectionActionId = "repeatInspection";
	phis.application.sto.script.StorehouseCheckInWayForm.superclass.constructor.apply(
			this, [cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}

Ext.extend(phis.application.sto.script.StorehouseCheckInWayForm,
		phis.script.TableForm, {
			onReady : function() {
				phis.application.sto.script.StorehouseCheckInWayForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				form.findField("RKDH").on("blur", this.onRkdhChange, this);
				form.findField("YSDH").on("blur", this.onYsdhChange, this);
			},
			// 保存前检验入库方式名称有没重复
			onBeforeSave : function(entryName, op, saveRequest) {
				if(this.op=="create"){
				saveRequest.XTSB=this.mainApp['phis'].storehouseId;
				}
				var body = {};
				body["mc"] = saveRequest.FSMC;
				body["pkey"] = (saveRequest.RKFS == null || saveRequest.RKFS == "")
						? 0
						: saveRequest.RKFS;
				body["pb"] = "rk";
				body["yksb"]=saveRequest.XTSB;
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
			// 入库单号验证
			onRkdhChange : function() {
				if (this.op == "create") {
					return 1;
				}
				var form = this.form.getForm();
				var rkdh = form.findField("RKDH").getValue();
				var oldRkdh = this.data["RKDH"];
				if (rkdh < oldRkdh) {
					Ext.Msg.alert("提示", "入库单号不能修改比原来的小");
					form.findField("RKDH").setValue(oldRkdh);
					return 0;
				}
				return 1;
			},
			// 验收单号验证
			onYsdhChange : function() {
				if (this.op == "create") {
					return 1;
				}
				var form = this.form.getForm();
				var ysdh = form.findField("YSDH").getValue();
				var oldYsdh = this.data["YSDH"];
				if (ysdh < oldYsdh) {
					Ext.Msg.alert("提示", "验收单号不能修改比原来的小");
					form.findField("YSDH").setValue(oldYsdh);
					return 0;
				}
				return 1;
			}
		});