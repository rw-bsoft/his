$package("phis.application.sto.script")

$import("phis.application.sto.script.StorehouseMySimpleDetailForm")

phis.application.sto.script.StorehouseCheckOutDetailForm = function(cfg) {
	cfg.showButtonOnTop = false;
	cfg.remoteUrl = "Medicines";
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{KSMC}</td>';
	cfg.queryParams = {
		"tag" : "ks"
	};
	cfg.disabledField=["SQRQ","CKKS","CKBZ"];//如果是查看 界面需要灰掉的标签ID
	cfg.conditionId = "CKFS";// list带过来的需要回填的下拉框
	phis.application.sto.script.StorehouseCheckOutDetailForm.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sto.script.StorehouseCheckOutDetailForm,
		phis.application.sto.script.StorehouseMySimpleDetailForm, {
			// 由于出库方式要根据药库识别(动态)特殊处理,未在框架上找到合适的处理方法,故修改创建dic方法
			createDicField : function(dic) {
				if (dic.id == "phis.dictionary.storehouseDelivery") {
					dic.filter = "['eq',['$','item.properties.XTSB'],['l',"
							+ parseInt(this.mainApp['phis'].storehouseId) + "]]";
				}
				var cls = "util.dictionary.";
				if (!dic.render) {
					cls += "Simple";
				} else {
					cls += dic.render
				}
				cls += "DicFactory"

				$import(cls)
				var factory = eval("(" + cls + ")")
				var field = factory.createDic(dic)
				return field
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'mds',
							// 类里面总数的参数名
							totalProperty : 'count',
							id : 'mdssearch'
						}, [{
									name : 'numKey'
								}, {
									name : 'KSDM'
								}, {
									name : 'KSMC'
								}]);
			},
			// 数据回填
			setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中
				var form = this.form.getForm();
				form.findField("KSMC").setValue(record.get("KSMC"));
				this.data["CKKS"] = record.get("KSDM");
			},
			loadData : function(body) {
				if(body==null){
				return ;}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryActionId,
							body : body
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.loadData);
					return;
				}
				this.doNew();
				this.op="update"
				this.initFormData(r.json.body);
			},
			doNew : function() {
				this.op = "create"
				if (this.data) {
					this.data = {}
				}
				if (!this.schema) {
					return;
				}
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						f.setValue(it.defaultValue)
						if (it.type == "date") {
							f.setValue(Date.getServerDate());
						}
						// @@ 2010-01-07 modified by chinnsii, changed the
						// condition
						// "it.update" to "!=false"
						if (!it.fixed && !it.evalOnServer) {
							f.enable();
						} else {
							f.disable();
						}

						if (it.type == "date") { // ** add by yzh 20100919 **
							if (it.minValue)
								f.setMinValue(it.minValue)
							if (it.maxValue)
								f.setMaxValue(it.maxValue)
						}
						// add by yangl 2012-06-29
						if (it.dic && it.dic.defaultIndex) {
							if (f.store.getCount() == 0)
								return;
							if (isNaN(it.dic.defaultIndex)
									|| f.store.getCount() <= it.dic.defaultIndex)
								it.dic.defaultIndex = 0;
							f.setValue(f.store.getAt(it.dic.defaultIndex)
									.get('key'));
						}
					}
					if (it.id == "CKKS") {
						if (this.ksly == 1) {
							f.enable();
						} else {
							f.disable();
						}
					}
				}
				// this.data["YFSB"]=parseFloat(this.yfsb);
				this.setKeyReadOnly(false)
				this.resetButtons(); // ** add by yzh **
				this.fireEvent("doNew")
				// this.focusFieldAfter(-1, 800)
				this.validate()
				this.fillCondition();
			}
		})