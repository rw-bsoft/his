$package("phis.application.war.script")

$import("phis.script.EditorList")

phis.application.war.script.WardRemainInList_In = function(cfg) {
	/*
	 * cfg.remoteUrl = 'MedicineClinic'; cfg.minListWidth = 400; //cfg.remoteTpl =
	 * this.getRemoteTpl(); cfg.autoLoadData = false; cfg.listServiceId =
	 * "remainService"; cfg.selectOnFocus = true; cfg.disablePagingTbr = true;
	 * cfg.sortable = false; cfg.enableHdMenu = false; cfg.showButtonOnTop =
	 * false; this.removeRecords = []; this.serviceId =
	 * "wardPatientManageService";
	 */
	phis.application.war.script.WardRemainInList_In.superclass.constructor.apply(this,
			[cfg])
	this.on("beforeCellEdit", this.beforeGridEdit, this);
	// this.on("loadData", this.myLoadData, this);
	// this.on("beforeSearchQuery",this.beforeSearchQuery,this);
}
Ext.extend(phis.application.war.script.WardRemainInList_In, phis.script.EditorList, {
			loadData : function() {
				if(!this.mainApp['phis'].wardId){
					MyMessageTip.msg("提示", "当前不存在病区，请先选择病区信息!", true);
					this.opener.opener.closeCurrentTab();
					return;
				}
			//	this.clear();
				this.requestData.serviceId = "phis."+this.listServiceId;
				this.requestData.serviceAction = "queryInList";
				this.requestData.warId = this.mainApp['phis'].wardId;
				phis.application.war.script.WardRemainInList_In.superclass.loadData
						.call(this);
			},

			beforeGridEdit : function(it, record, field, value) {

				if (it.id == 'FPCW') {
					var module = this.createModule("getWAR9703", "phis.application.war.WAR/WAR/WAR9703");
					module.operater = this;
					this.module = module;

					var hhks=record.get("HHKS");
					if(hhks==null||hhks==''){
						
						Ext.MessageBox.alert("没有可分配的床位!");
						return false;
					}
				
					module.hhks =hhks;
					module.zyh = record.get("ZYH")
					// module.WZXH = record.get("WZXH")
					this.rr = this.getSelectedRecord()
					if (this.rr == null) {
						return false;
					}
					this.win1 = module.getWin();
					this.win1.add(module.initPanel());
					module.loadData();
					this.win1.show();
					return false;
				}

			},
			doSure : function() {
				Ext.Msg.show({
							title : '提示',
							msg : '确认对该病人做转入操作吗?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									var rr = this.getSelectedRecord();
									var val = rr.data.FPCW;
									if (val == '' || val == null) {
										Ext.MessageBox.alert("提示", "请选择床位!");
										return;
									} else {
										// 判断新床位是否已有病人
										var c = phis.script.rmi.miniJsonRequestSync({
													serviceId : this.listServiceId,
													serviceAction : "queryInfo",
													body : {
														fpcw : val
													}
												})
										if (c.json.count > 0) {
											Ext.Msg.alert("提示", "分配床位已有病人，转科分配床位失败!");
											return;
										}
										
										//判断病人是否满足新床位性别限制
										var c1 = phis.script.rmi.miniJsonRequestSync({
											serviceId : this.listServiceId,
											serviceAction : "checkBedSexLimit",
											body : {
												brch : val,
												zyh : rr.data.ZYH,
												ksdm : rr.data.HHKS,
												wardId : this.mainApp['phis'].wardId
											}
										});
										if (c1.json.illegal) {
											Ext.Msg.alert("提示", "该病人性别不匹配分配床位的性别限制，转科分配床位失败!");
											return;
										}
										
										var c2 = phis.script.rmi.miniJsonRequestSync({
												serviceId : this.listServiceId,
												serviceAction : "saveConfirm",
												body : {
														al_zyh : rr.data.ZYH,
														as_cwhm_Old : rr.data.HQCH,//床位号码(原来)
														as_cwhm_New : val,//床位号码新
														al_zwks_Old : rr.data.HQKS,//换前科室
														al_zwks_New : rr.data.HHKS,//换后科室
														ll_bqdm_Old:  rr.data.HQBQ,//换前病区
														ll_bqdm_New:  rr.data.HHBQ //换后病区
														

													}
												})
										 if (c2.json.mes!=null) {
											Ext.Msg.alert("提示", c2.json.mes);
										    this.loadData();
											return;
										}
										
									}

								}
							},
							scope : this
						})
			},

			doCre : function() {

				var ch = this.fpcw;
				this.rr.set("FPCW", ch);
			},doClose:function(){
				
				
			}

		});
