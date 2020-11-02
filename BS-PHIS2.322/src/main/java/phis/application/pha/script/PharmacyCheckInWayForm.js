$package("phis.application.pha.script")

$import("phis.script.TableForm")

phis.application.pha.script.PharmacyCheckInWayForm = function(cfg) {
	cfg.repeatInspectionServiceId = "pharmacyManageService";
	cfg.repeatInspectionActionId = "repeatInspection";
	phis.application.pha.script.PharmacyCheckInWayForm.superclass.constructor.apply(
			this, [cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}

Ext.extend(phis.application.pha.script.PharmacyCheckInWayForm,
		phis.script.TableForm, {
			onReady : function() {
				phis.application.pha.script.PharmacyCheckInWayForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				form.findField("RKDH").on("blur", this.onRkdhChange, this);
			},
			// 保存前检验入库方式名称有没重复
			onBeforeSave : function(entryName, op, saveRequest) {
				if(this.op=="create"){
				saveRequest.YFSB=this.mainApp['phis'].pharmacyId;
				}
				var body = {};
				body["mc"] = saveRequest.FSMC;
				body["pkey"] = (saveRequest.RKFS == null || saveRequest.RKFS == "")
						? 0
						: saveRequest.RKFS;
				body["pb"] = "rk";
				body["yfsb"]=saveRequest.YFSB;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.repeatInspectionServiceId,
							serviceAction : this.repeatInspectionActionId,
							body : body
						});
				if (ret.code > 300) {
					var msg = "方式名称已经存在!";
					this.processReturnMsg(ret.code, msg, this.onBeforeSave);
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
			}
		});