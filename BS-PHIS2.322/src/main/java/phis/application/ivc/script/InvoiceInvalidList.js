$package("phis.application.ivc.script")

$import("phis.script.SimpleList")

phis.application.ivc.script.InvoiceInvalidList = function(cfg) {
	phis.application.ivc.script.InvoiceInvalidList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.ivc.script.InvoiceInvalidList, phis.script.SimpleList, {
	
			loadData : function(){
				this.clear(); // ** add by yzh , 2010-06-09 **
				var body={"BRXZ":this.mainApp.BRXZ};
				this.requestData.serviceId = "phis.clinicChargesProcessingService";
				this.requestData.serviceAction = "queryVoidInvoice";
				this.requestData.schema = "phis.application.ivc.schemas.MS_ZFFP";
				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load()
					} else {
						var pt = this.grid.getBottomToolbar()
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor)
					}
				}
				// ** add by yzh **
//				this.resetButtons();
			},
			openFpzf : function(btn, text) {
				if (btn == 'ok') {
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : "clinicChargesProcessingService",
								serviceAction : "queryFphm",
								body : text
							});
					if (r.code > 300) {
						this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
						return;
					} else {
						if (!r.json.body) {
							Ext.Msg.alert("提示", "该发票号码不存在!");
							return;
						}
						if(r.json.body.ZFPB=="1"){
							Ext.Msg.alert("提示", "该发票号码已作废!");
							return;
						}
						// var cfsbs = r.json.body;
						var module = this.midiModules["mzModule"];
						if (!module) {
							module = this.createModule("mzModule", this.fpzfModule);
							module.opener = this;
						}
						module.initDataId = text;
						module.person = r.json.body;
						module.djs = r.json.djs;
						var win = module.getWin();
						module.loadData();
						win.show();
					}
				}
			},
			doFpzf : function() {
				Ext.Msg.show({
							title : '请输入...',
							msg : '请输入需要作废的发票号码.',
							width : 300,
							buttons : Ext.Msg.OKCANCEL,
							prompt : true,
							fn : this.openFpzf,
							scope : this,
							animEl : 'mb3'
						});
			},
			onDblClick : function(grid, index, e) {
				this.doQxzf();
			},
			doQxzf : function() {
				// var lastIndex = this.grid.getSelectionModel().lastActive;
				var record = this.getSelectedRecord();
				if(!record){
					MyMessageTip.msg("提示", "没有可取消的作废发票!", true);
					return;
				}
				var fphm = record.data.FPHM;
				var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : "clinicChargesProcessingService",
								serviceAction : "queryFphm",
								body : fphm
							});
					if (r.code > 300) {
						this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
						return;
					} else {
						if(r.json.body.JZRQ){
							MyMessageTip.msg("提示", "当前作废发票已结账，无法取消作废!", true);
							return;
						}
						var module = this.midiModules["mzModule"];
						if (!module) {
							module = this.createModule("mzModule", this.fpzfModule);
							module.opener = this;
						}
						module.initDataId = fphm;
						module.person = r.json.body;
						module.djs = r.json.djs;
						var win = module.getWin();
						module.loadData();
						win.show();
					}
			}
//			,checkbxlx:function(fphm){
//				//判断报销类型
//				var r = phis.script.rmi.miniJsonRequestSync({
//								serviceId : "clinicChargesProcessingService",
//								serviceAction : "queryFphm",
//								body : fphm
//							});
//					if (r.code > 300) {
//						this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
//						return 0 ;
//					} else {
//						alert(r.json.body.BXID+json.body.BXID.length)
//						if(r.json.body.BXID && r.json.body.BXID.length >0 ){
//							return 1 ;
//						}else{
//							return 0 ;
//						}
//					}
//			}
		});