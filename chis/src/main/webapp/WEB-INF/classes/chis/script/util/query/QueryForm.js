/**
 * 公共查询表单页面
 * 
 * @author : yaozh
 */
$package("chis.script.util.query")
$import("chis.script.BizTableFormView")
chis.script.util.query.QueryForm = function(cfg) {
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
	chis.script.util.query.QueryForm.superclass.constructor.apply(this, [cfg]);
	this.colCount = this.colCount || 2;
	this.on("addfield", this.onAddField, this)
};
Ext.extend(chis.script.util.query.QueryForm, chis.script.BizTableFormView, {

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
				this.initCnd = this.initCnd || ['eq', ['s', '1'], ['s', '1']];//['like',['$','a.manaUnitId'],['concat',['substring',['$','%user.manageUnit.id'],0,9],['s','%']]];
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
						if (typeof v == "object") {
							v = v.key;
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
								} else if(it.dic.render == 'LovCombo'){
									var vArray = v.split(',');
									var vs = [];
									for(var ki=0,len=vArray.length;ki<len;ki++){
										vs.push("'"+vArray[ki]+"'");
									}
									cnds = ['in',['$',value],[['s',vs.join(',')]]];
								}else {
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
			}

		})