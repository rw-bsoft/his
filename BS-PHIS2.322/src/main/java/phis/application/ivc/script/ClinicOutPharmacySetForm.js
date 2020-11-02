$package("phis.application.ivc.script")

$import("phis.script.TableForm")

phis.application.ivc.script.ClinicOutPharmacySetForm = function(cfg) {
	cfg.loadServiceId = "outPharmacyLoad";
	cfg.saveServiceId = "outPharmacySave";
	cfg.autoLoadData = false;
	phis.application.ivc.script.ClinicOutPharmacySetForm.superclass.constructor.apply(
			this, [cfg])
	
}

Ext.extend(phis.application.ivc.script.ClinicOutPharmacySetForm,
		phis.script.TableForm, {
			onWinShow : function() {
					this.onCyLoad();
			},
			onReady : function() {
				var cy = this.form.getForm().findField("CF_XYF_CODE");
				cy.getStore().on("load", this.onCyLoad, this);
				this.on("winShow", this.onWinShow, this)
			},
			onCyLoad : function() {
				this.initDataId = "CF_%_CODE";
				this.loadData();
			},
			initFormData : function(data) {
				Ext.apply(this.data, data)
				this.initDataId = this.data[this.schema.pkey]
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						f.focus();
						var v = data[it.id]
						this.afterinitFormData(f, v);
					}
				}
				this.setKeyReadOnly(true)
				this.focusFieldAfter(-1, 800)
			},
			afterinitFormData : function(f, v) {
				var count = f.getStore().getCount();
				//alert(count + "@@@" + f.id)
				if (count > 0) {
					if (v != undefined) {
						f.setValue(v)
						if(f.isExpanded()){
							f.collapse();
						}
					}
				} else {
					var this1 = this;
					setTimeout(function() {
								this1.afterinitFormData(f, v);
							}, "100");
				}
			}
		})
