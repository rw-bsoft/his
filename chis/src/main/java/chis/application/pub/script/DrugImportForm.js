$package("chis.application.pub.script");

$import("chis.script.BizTableFormView");

chis.application.pub.script.DrugImportForm = function(cfg) {
	cfg.actions = [{
				id : "select",
				name : "查询",
				iconCls : "common_query"
			}, {
				id : "reset",
				name : "重置",
				iconCls : "common_reset"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}];
	chis.application.pub.script.DrugImportForm.superclass.constructor.apply(this, [cfg]);
	this.colCount = 2;
	this.on("addfield", this.onAddField, this);
};

Ext.extend(chis.application.pub.script.DrugImportForm, chis.script.BizTableFormView, {
			onAddField : function(f, it) {
				if (it.hidden || !it.queryable) {
					return false;
				}
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
						if(it.id == 'mdcUse'){
							continue;
						}
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
				cnd = ['and', cnd, this.initCnd];
				this.fireEvent("select", cnd);
			},

			doCancel : function() {
				this.fireEvent("cancel");
			},

			doReset : function() {
				this.doNew();
			},
			
			getMdcUseValue : function(){
				var form = this.form.getForm();
				var mdcUse = form.findField("mdcUse");
				var mdcUseVal = "";
				if(mdcUse){
					mdcUseVal = mdcUse.getValue();
				}
				return mdcUseVal;
			}
		});