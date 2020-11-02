$package("phis.application.pha.script")
$import("phis.script.TableForm")

phis.application.pha.script.PharmacyMedicinesManageForm = function(cfg) {
	phis.application.pha.script.PharmacyMedicinesManageForm.superclass.constructor
			.apply(this, [cfg]);
			this.on("beforeSave",this.onBeforeSave,this);
}
Ext.extend(phis.application.pha.script.PharmacyMedicinesManageForm,
		phis.script.TableForm, {
			onReady : function() {
				phis.application.pha.script.PharmacyMedicinesManageForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				form.findField("YFBZ").on("blur", this.onYFBZChange, this);
				form.findField("YFGC").on("blur", this.onYFGCChange, this);
				form.findField("YFDC").on("blur", this.onYFGCChange, this);
			},
			loadData : function() {
				if (this.loading) {
					return
				}
				if (!this.schema) {
					return
				}
				if (!this.initDataId && !this.initDataBody) {
					return;
				}
				if (!this.fireEvent("beforeLoadData", this.entryName,
						this.initDataId, this.initDataBody)) {
					return
				}
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				var body = {};
				body["ypxh"] = this.initDataBody["YPXH"];
				body["yfsb"] = this.initDataBody["YFSB"];
				this.loading = true
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.queryServiceAction,
							body : body
						}, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							this.loading = false
							if (code > 300) {
								this.processReturnMsg(code, msg, this.loadData)
								return
							}
							if (json.body) {
								this.doNew()
								this.initFormData(json.body)
								this.fireEvent("loadData", this.entryName,
										json.body);
							}
							if (this.op == 'create') {
								this.op = "update"
							}

						}, this)// jsonRequest
			},
			onYFBZChange : function() {
				var form = this.form.getForm();
				var yfbz = form.findField("YFBZ").getValue();
				var zxbz = this.data["ZXBZ"];
				if (yfbz == this.data["YFBZ"]) {
					return true;
				} else {
					var body = {};
					body["ypxh"] = this.data["YPXH"];
					body["yfsb"] = this.data["YFSB"];
					body["jgid"] = this.data["JGID"];
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.ValidationPackageServiceAction,
								body : body
							});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg,
								this.onBeforeSave);
						form.findField("YFBZ").setValue(this.data["YFBZ"]);
						return false;
					}
					if (zxbz % yfbz != 0) {
						Ext.Msg
								.alert("提示", "药房包装必须能被最小包装整除,当前药品的最小包装为:"
												+ zxbz);
						form.findField("YFBZ").setValue(this.data["YFBZ"]);
						return false;
					}
				}
				return true;
			},
			onYFGCChange : function() {
				var form = this.form.getForm();
				var yfgc = form.findField("YFGC").getValue();
				var yfdc = form.findField("YFDC").getValue();
				if (parseFloat(yfdc) > parseFloat(yfgc)) {
					MyMessageTip.msg("提示", "药房低储不能高于药房高储!", true);
					return false;
				}
				return true;
			},
			onBeforeSave:function(){
			if(!this.onYFBZChange()||!this.onYFGCChange()) {
			return false;}
			return true;
			},
			//修改药房包装更新库存
			doSave:function(){
			var form = this.form.getForm();
			var oldYfbz=this.data["YFBZ"];
			var yfbz=form.findField("YFBZ").getValue();
			if(oldYfbz==yfbz){
			phis.application.pha.script.PharmacyMedicinesManageForm.superclass.doSave.call(this);
			}else{
			if (this.saving) {
			return
			}
			var values = this.getFormData();
			if (!values) {
			return;
			}
			if (!this.fireEvent("beforeSave", this.entryName, this.op, values)) {
			return;
			}
			values["OLDYFBZ"]=oldYfbz;
			Ext.apply(this.data, values);
			this.saving = true;
			this.form.el.mask("正在保存数据...", "x-mask-loading");
			var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.saveServiceAction,
								body : values
							});
					this.form.el.unmask();
					this.saving = false;
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg,
								this.doSave);
						return ;
					}
					this.fireEvent("save", this.entryName, this.op, ret.json,
							this.data);
			}
			
			},
			doCancel:function(){
			this.fireEvent("close");
			}
		});
