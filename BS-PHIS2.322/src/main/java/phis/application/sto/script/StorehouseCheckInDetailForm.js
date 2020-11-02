/**
 * 采购入库详细form
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.application.sto.script.StorehouseMySimpleDetailForm");

phis.application.sto.script.StorehouseCheckInDetailForm = function(cfg) {
	cfg.disabledField=["RKBZ","CGRQ","DWMC","FDJS","PWD"];//如果是查看 界面需要灰掉的标签ID
	cfg.remoteUrl="Medicines";
	cfg.remoteTpl='<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{DWMC}</td>';
	cfg.queryParams={"tag":"jhdw"};
	cfg.conditionId="RKFS";
	phis.application.sto.script.StorehouseCheckInDetailForm.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehouseCheckInDetailForm,
				phis.application.sto.script.StorehouseMySimpleDetailForm, {
	// 由于框架不支持form回填多表查询,故重写
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
				this.loading = true
				this.initDataBody["tag"]="cgrk";
				phis.script.rmi.jsonRequest({
					serviceId : this.serviceId,
					serviceAction : this.loadCheckInActionId,
					body : this.initDataBody
						// 增加module的id
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
						this.fireEvent("loadData", this.entryName, json.body);
					}
					if (this.op == 'create') {
						this.op = "update"
					}

				}, this)// jsonRequest
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
									name : 'DWXH'
								}, {
									name : 'DWMC'
								}]);
			},
			// 数据回填
			setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中
				var form=this.form.getForm();
				form.findField("DWMC").setValue(record.get("DWMC"));
				this.data["DWXH"]=record.get("DWXH");
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
						if (it.type == "datetime") {
							f.setValue(Date.getServerDateTime());
						}
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
				}
				this.setKeyReadOnly(false)
				this.resetButtons(); // ** add by yzh **
				this.fireEvent("doNew")
				//this.focusFieldAfter(2, 500)
				this.validate()
				this.fillCondition();
			}
});