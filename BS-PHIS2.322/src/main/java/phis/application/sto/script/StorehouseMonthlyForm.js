$package("phis.application.sto.script")
$import("phis.script.TableForm")

phis.application.sto.script.StorehouseMonthlyForm = function(cfg) {
	phis.application.sto.script.StorehouseMonthlyForm.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.sto.script.StorehouseMonthlyForm, phis.script.TableForm,
		{
			createField : function(it) {
				var cfg = {
					name : it.id,
					fieldLabel : it.alias,
					xtype : "uxspinner",
					strategy : {
						xtype : "month"
					},
					width : 200,
					height : this.defaultHeight || it.height,
					value : new Date().format('Y-m')
				}
				return cfg;
			},
			doCancel : function() {
				this.fireEvent("close", this);
			},
			doMonthly : function() {
				var time = this.form.getForm().findField("CWYF").getValue();
				var ym = time.split("-");
				if (ym.length < 2) {
					Ext.Msg.alert("提示", "财务月份输入有误!");
					return;
				}
				var body = {};
				body["year"] = ym[0];
				body["month"] = ym[1];
				body["op"] = 1;
				this.form.el.mask("正在保存...", "x-mask-loading")
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.serviceActionId,
							body : body
						});
				if (r.code == 9001 || r.code == 9002) {
					this.form.el.unmask();
					Ext.Msg.show({
						title : '',
						msg : r.msg,
						modal : true,
						width : 300,
						buttons : Ext.MessageBox.OKCANCEL,
						multiline : false,
						fn : function(btn, text) {
							if (btn == "ok") {
								if (r.code == 9001) {
									body["op"] = 2;
								} else if (r.code == 9002) {
									body["op"] = 3;
								}
								this.form.el.mask("正在取消...", "x-mask-loading")
								var ret = phis.script.rmi.miniJsonRequestSync({
											serviceId : this.serviceId,
											serviceAction : this.serviceActionId,
											body : body
										});
								this.form.el.unmask();
								if (ret.code > 200) {
									this.processReturnMsg(ret.code, ret.msg,
											this.doMonthly);
									return;
								}
								MyMessageTip.msg("提示", ret.msg, true);
								this.fireEvent("save", this);
							}
						},
						scope : this
					})
				} else if (r.code == 200) {
					this.form.el.unmask();
					this.fireEvent("save", this);
					MyMessageTip.msg("提示", "保存成功", true);
				} else {
					this.form.el.unmask();
					this.fireEvent("save", this);
					this.processReturnMsg(r.code, r.msg, this.doMonthly);
					return;
				}
			},
			//重写防止那日期的默认值清空了
			doNew:function(){
			}
		})