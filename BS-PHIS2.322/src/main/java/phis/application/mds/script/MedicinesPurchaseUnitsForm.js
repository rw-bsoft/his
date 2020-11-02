$package("phis.application.mds.script") 
$import("phis.script.TableForm")

phis.application.mds.script.MedicinesPurchaseUnitsForm = function(cfg) {
	cfg.serviceId = "medicinesManageService"; 
	cfg.repeatInspectionActionId = "inspectionPurchaseUnits";
	phis.application.mds.script.MedicinesPurchaseUnitsForm.superclass.constructor.apply(this, [cfg])
	this.on("beforeSave", this.onBeforeSave, this);
} 
Ext.extend(phis.application.mds.script.MedicinesPurchaseUnitsForm,
		phis.script.TableForm, {
			// 保存前检验
			onBeforeSave : function(entryName, op, saveRequest) {
				var p1 = /^[+]{0,1}(\d){1,3}[ ]?([-]?(\d){1,12})+$/; 
				var p2=/^[+]{0,1}(\d){1,3}[ ]?([-]?((\d)|[ ]){1,12})+$/;
				if(!p1.test(saveRequest.LXDH)&&!p2.test(saveRequest.LXDH)){
				MyMessageTip.msg("提示", "联系电话输入格式不正确", true);
				return false; 
				}
				var body = {};  
				body["op"] = this.op;
				if(this.op=="update"){
				body["dwxh"]=saveRequest.DWXH;
				body["zfpb"]=saveRequest.ZFPB;
				}
				body["dwmc"] = saveRequest.DWMC;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.repeatInspectionActionId,
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code,ret.msg, this.onBeforeSave);
					return false;
				}
				return true;
			}
		});