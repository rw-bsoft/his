$package("phis.application.cfg.script")

/**
 * 病人性质维护的自负比例界面list zhangyq 2012.5.25
 */
$import("phis.script.EditorList")

phis.application.cfg.script.ConfigConceitProportionEditorList = function(cfg) {
	cfg.disablePagingTbr = true;//不分页
	cfg.autoLoadData = false;
	cfg.listServiceId = "configPatientPropertiesService";
	this.serviceId = "configPatientPropertiesService";
	this.saveActionId = "saveOwnExpenseProportion";
	this.serverParams = {};
	this.serverParams.serviceAction = "queryOwnExpenseProportion";
	phis.application.cfg.script.ConfigConceitProportionEditorList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.cfg.script.ConfigConceitProportionEditorList,
		phis.script.EditorList, {
			doSave : function(item, e) {
				if (!this.mainApp.BRXZ) {
					MyMessageTip.msg("提示", "请先选择病人性质", true);
					return;
				}
				var store = this.grid.getStore();
				var n = store.getCount()
				var datas = [];
				for (var i = 0; i < n; i++) {
					var data = {};
					var r = store.getAt(i)
					if (!r.get('ZFBL')) {
						MyMessageTip.msg("提示", '自费比例不能为空!', true);
						return
					}
					if (r.get('ZFBL') <= 100) {
						data['SFXM'] = r.get('SFXM');
						data['BRXZ'] = r.get('BRXZ');
						data['ZFBL'] = r.get('ZFBL');
						if(r.get('ZFBL')<0){
							data['ZFBL'] = "0";
						}
						datas.push(data);
					}
					this.brxz = r.get('BRXZ');
				}
				this.grid.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.saveActionId,
							schema : this.entryName,
							method : "execute",
							body : datas,
							brxz : this.brxz
						}, function(code, msg, json) {

							this.grid.el.unmask()
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							var store = this.grid.getStore();
							var n = store.getCount()
							for (var i = 0; i < n; i++) {
								var r = store.getAt(i)
								if (r.dirty) {
									r.commit();
								}
							}
						}, this)
			}
		})