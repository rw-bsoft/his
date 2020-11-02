$package("phis.application.ivc.script")

$import("phis.script.TableForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.ivc.script.RefundProcessingForm1 = function(cfg) {
//	cfg.showButtonOnTop = true
	cfg.modal = this.modal = true;
	cfg.labelWidth = 55;
	phis.application.ivc.script.RefundProcessingForm1.superclass.constructor
			.apply(this, [ cfg ])
}

Ext.extend(phis.application.ivc.script.RefundProcessingForm1,
				phis.script.TableForm,
				{
					onReady : function() {
//						phis.application.ivc.script.RefundProcessingForm1.onReady
//								.call(this);
						var form = this.form.getForm();
						var TFFP = form.findField("TFFP");
						if (TFFP) {
							TFFP.on("specialkey", function(f, e) {
								var key = e.getKey();
								if (key == e.ENTER) {
									e.stopEvent();
									if (f.getValue() != "") {
										this.onTFFPEnter(f.getValue());
									}
									// if (f.validate()) {
									// var field = form.findField("BRXZ");
									// if (field) {
									// field.focus();
									// }
									// }
								}else if (key == 40) {
									e.stopEvent();
									var tfList1 = this.opener.tfModule1.tfList1;
//									var grid = this.opener.tfModule1.tfList1.grid;
									var lastIndex = tfList1.grid.getSelectionModel().lastActive;
									tfList1.grid.getStore();
									var n = tfList1.grid.store.getCount();
									if(lastIndex<n){
										tfList1.grid.getSelectionModel().selectRow(lastIndex+1);
										tfList1.onRowClick(tfList1.grid,lastIndex+1,e);
									}else{
										tfList1.grid.getSelectionModel().selectRow(lastIndex);
										tfList1.onRowClick(tfList1.grid,lastIndex,e);
									}
								}else if(key == 38){
									e.stopEvent();
									var tfList1 = this.opener.tfModule1.tfList1;
//									var grid = this.opener.tfModule1.tfList1.grid;
									var lastIndex = tfList1.grid.getSelectionModel().lastActive;
									tfList1.grid.getStore();
									if(lastIndex>0){
										tfList1.grid.getSelectionModel().selectRow(lastIndex-1);
										tfList1.onRowClick(tfList1.grid,lastIndex-1,e);
									}else{
										tfList1.grid.getSelectionModel().selectRow(lastIndex);
										tfList1.onRowClick(tfList1.grid,lastIndex,e);
									}
								}
							}, this);
							TFFP.on("keydown", function(f, e) {
								if (e.getKey() == 32) {
									e.stopEvent();
									var tfList1 = this.opener.tfModule1.tfList1;
									tfList1.onDblClick();
								}
							}, this);
						}
					},
					onTFFPEnter : function(fphm) {
						var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicChargesProcessingService",
							serviceAction : "queryTFFphm",
							body : fphm
						});
						if (r.code > 300) {
							this.processReturnMsg(r.code, r.msg,
									this.onBeforeSave);
							return;
						} else {
							if (!r.json.body) {
								Ext.Msg.alert("提示", "该发票号码不存在!");
								return;
							}
							if (r.json.body.ZFPB == "1") {
								Ext.Msg.alert("提示", "该发票号码已作废!");
								return;
							}
							var form = this.form.getForm();
							form.findField("BRXM").setValue(r.json.body.BRXM);
							form.findField("TFJE").setValue(r.json.body.TFJE);
							form.findField("FPHM").setValue(r.json.body.FPHM);
							this.opener.FPHM = r.json.body.FPHM;
							this.opener.loadData(fphm);
							this.opener.setDetails(r.json.details);
							this.opener.fkxxs = r.json.fkxxs;
							this.opener.MZXX = r.json.MZXX;
//							module.person = r.json.body;
//							module.djs = r.json.djs;
//							var win = module.getWin();
//							module.loadData();
//							win.show();
						}
					}

				});