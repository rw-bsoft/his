$package("chis.application.fhr.script")

$import("chis.script.BizTableFormView");

chis.application.fhr.script.FieldMaintainAddForm = function(cfg) {
	cfg.colCount = 2
	chis.application.fhr.script.FieldMaintainAddForm.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(chis.application.fhr.script.FieldMaintainAddForm,
		chis.script.BizTableFormView, {	
			doCancel : function() {
				this.fireEvent("cancel", this)
			},
			doSelect : function() {
				var form = this.form.getForm();
				if (!this.validate()) {
					return;
				}
				if (!this.schema) {
					return;
				}
				var items = this.schema.items;
				this.initCnd = this.initCnd || ['eq', ['s', '1'], ['s', '1']];
				var cnd = ['eq', ['s', '1'], ['s', '1']];
				if (items) {
					var n = items.length;
					for (var i = 0; i < n; i++) {
						var it = items[i];
						var v = "";
						var f = form.findField(it.id);
						if (f) {
							v = f.getValue() || "";
						}
						if (v != null & v != "" & v != undefined) {
							var refAlias = it.refAlias || "a";
							var value = refAlias + "." + it.id;
							var cnds = ['eq', ['$', value]];
							if (it.dic) {
								if (it.dic.render == "Tree") {
									var node = f.selectedNode;
									if (node != null && !node.isLeaf()) {
										cnds[0] = 'like';
										cnds.push(['s', v + '%']);
									} else {
										cnds.push(['s', v]);
									}
								} else {
									cnds.push(['s', v]);
								}
							} else {
								switch (it.type) {
									case 'int' :
										cnds.push(['i', v]);
										break;
									case 'double' :
									case 'bigDecimal' :
										cnds.push(['d', v]);
										break;
									case 'string' :
										cnds[0] = 'like';
										cnds.push(['s', v + '%']);
										break;
									case "date" :
										v = v.format("Y-m-d");
										cnds[1] = [
												'$',
												"str(" + value
														+ ",'yyyy-MM-dd')"];
										cnds.push(['s', v]);
										break;
								}
							}
							cnd = ['and', cnd, cnds];
						}
					}
				}
				this.fireEvent("select", cnd);
			},

			doReset : function() {
				this.doNew();
			}
		})