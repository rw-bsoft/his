$package("phis.application.mds.script")

$import("phis.script.TableForm")

phis.application.mds.script.MedicinesDescriptionForm = function(cfg) {
	cfg.showButtonOnTop = false;
	phis.application.mds.script.MedicinesDescriptionForm.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.mds.script.MedicinesDescriptionForm,
		phis.script.TableForm, {
			doNew : function() {
				this.op = "create"
				if (this.data) {
					this.data = {}
				}
				if (!this.schema) {
					return;
				}
				var form = this.form.getForm()
				form.findField("MESS").setValue("");
			},
			// update by caijy at 2015.1.19 for 医生站合理用药信息调阅,只读
			doRead : function() {
				this.loadData();
				var form = this.form.getForm();
				form.reset();
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						f.readOnly = true;
					}
				}
			},
			expansion : function(cfg) {
				cfg.listeners={
            render:function(p){
                p.getEl().on("contextmenu",function(e){//右键事件
                      e.stopEvent()
                })
            }
        }
			}

		})