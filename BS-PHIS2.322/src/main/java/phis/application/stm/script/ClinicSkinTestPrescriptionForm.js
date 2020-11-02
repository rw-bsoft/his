$package("phis.application.stm.script")

$import("phis.script.TableForm")

phis.application.stm.script.ClinicSkinTestPrescriptionForm = function(cfg) {
	phis.application.stm.script.ClinicSkinTestPrescriptionForm.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.stm.script.ClinicSkinTestPrescriptionForm,
		phis.script.TableForm, {
			loadData : function() {
				if (this.loadDataByDefaultValue) {
					this.doNew();
				} else {
					this.doNew(1);
				}
				if (this.loading) {
					return
				}
				if (!this.schema) {
					return
				}
				var loadRequest = this.getLoadRequest();
				if (!this.initDataId && !this.initDataBody && !loadRequest) {
					return;
				}
				if (!this.fireEvent("beforeLoadData", this.entryName,
						this.initDataId || this.initDataBody, loadRequest)) {
					return
				}
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				this.loading = true
				var loadCfg = {
					serviceId : "phis.skintestManageService",
					serviceAction : "loadPatient",
					method : this.loadMethod,
					schema : this.entryName,
					pkey : this.initDataId || this.initDataBody,
					body : loadRequest,
					action : this.op, // 按钮事件
					module : this._mId
					// 增加module的id
				}
				this.fixLoadCfg(loadCfg);
				util.rmi.jsonRequest(loadCfg, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							this.loading = false
							if (code > 300) {
								this.processReturnMsg(code, msg, this.loadData)
								this.fireEvent("exception", code, msg,
										loadRequest, this.initDataId
												|| this.initDataBody); // **
								// 用于一些异常处理
								return
							}
							if (json.body) {
								this.initFormData(json.body)
								this.fireEvent("loadData", this.entryName,
										json.body);
							} else {
								this.initDataId = null;
								// **
								// 没有加载到数据，通常用于以fieldName和fieldValue为条件去加载记录，如果没有返回数据，则为新建操作，此处可做一些新建初始化操作
								this.fireEvent("loadNoData", this);
							}
							if (this.op == 'create') {
								this.op = "update"
							}

						}, this)
			}
		});