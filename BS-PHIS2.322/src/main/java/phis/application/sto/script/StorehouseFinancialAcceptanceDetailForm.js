$package("phis.application.sto.script")

$import("phis.script.TableForm")

phis.application.sto.script.StorehouseFinancialAcceptanceDetailForm = function(cfg) {
	cfg.showButtonOnTop = false;
	cfg.remoteUrl="Medicines";
	cfg.remoteTpl='<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{DWMC}</td>';
	cfg.queryParams={"tag":"jhdw"};
	phis.application.sto.script.StorehouseFinancialAcceptanceDetailForm.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.sto.script.StorehouseFinancialAcceptanceDetailForm,
		phis.script.TableForm, {
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
						this.initFormData(json.body);
						if(json.body.YYS==1){
						this.form.getForm().findField("DWMC").disable();
						}else{
						this.form.getForm().findField("DWMC").enable();
						}
						this.fireEvent("loadData", this.entryName, json.body);
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
			}
		})