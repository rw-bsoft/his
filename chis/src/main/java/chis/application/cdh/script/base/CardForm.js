/**
 * 卡管理表单
 * 
 * @author yaozh
 */
$package("chis.application.cdh.script.base")
$import("chis.script.BizTableFormView");
chis.application.cdh.script.base.CardForm = function(cfg) {
	cfg.width = 300;
	cfg.colCount = 1;
	cfg.actions = [{
				id : "save",
				name : "确定"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}];
	chis.application.cdh.script.base.CardForm.superclass.constructor.apply(this, [cfg]);
	this.on("winShow", this.onWinShow, this)
}

Ext.extend(chis.application.cdh.script.base.CardForm, chis.script.BizTableFormView, {
			onWinShow:function(){
				this.form.getForm().findField("cardNo").setValue(this.cardNo)
			}
			,
			doSave : function() {
				var values = this.getFormData();
				if (!values) {
					return;
				}
				this.form.el.mask("正在校验数据...", "x-mask-loading")
				var data = util.rmi.miniJsonRequestSync({
							serviceId : "chis.empiService",
							serviceAction : "checkCardExist",
							method:"execute",
							body : values
						})
				this.form.el.unmask();
				if (data.code != 200) {
					this.processReturnMsg(data.code, data.msg);
					return;
				} else {
					var body = data.json.result;
					if (!body) {
						this.fireEvent("saveCard", values);
						this.doCancel();
					} else {
						Ext.Msg.alert("提示信息","该卡已经存在!");
						return;
					}
				}

			}

		})