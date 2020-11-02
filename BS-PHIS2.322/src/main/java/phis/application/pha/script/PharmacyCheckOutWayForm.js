$package("phis.application.pha.script")

$import("phis.script.TableForm")

phis.application.pha.script.PharmacyCheckOutWayForm = function(cfg) {
	cfg.repeatInspectionServiceId = "pharmacyManageService";
	cfg.repeatInspectionActionId = "repeatInspection";
	phis.application.pha.script.PharmacyCheckOutWayForm.superclass.constructor.apply(
			this, [cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}

Ext.extend(phis.application.pha.script.PharmacyCheckOutWayForm,
		phis.script.TableForm, {
			onReady : function() {
				phis.application.pha.script.PharmacyCheckOutWayForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				form.findField("CKDH").on("blur", this.onCkdhChange, this);
			},

			// 保存前检验出库方式名称有没重复
			onBeforeSave : function(entryName, op, saveRequest) {
				if(this.op=="create"){
				saveRequest.YFSB=this.mainApp['phis'].pharmacyId;
				}
				var body = {};
				body["mc"] = saveRequest.FSMC;
				body["pkey"] = (saveRequest.CKFS == null || saveRequest.CKFS == "")
						? 0
						: saveRequest.CKFS;
				body["pb"] = "ck";
				body["yfsb"] = saveRequest.YFSB;
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