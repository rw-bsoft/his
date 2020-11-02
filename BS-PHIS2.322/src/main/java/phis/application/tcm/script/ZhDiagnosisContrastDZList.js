
$package("phis.application.tcm.script");

$import("phis.script.SimpleList")

phis.application.tcm.script.ZhDiagnosisContrastDZList = function(cfg) {
	phis.application.tcm.script.ZhDiagnosisContrastDZList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.tcm.script.ZhDiagnosisContrastDZList,
		phis.script.SimpleList, {
			loadData : function() {
				this.requestData.pageNo = 1;
				this.requestData.pageSize = 50;
				phis.application.tcm.script.ZhDiagnosisContrastDZList.superclass.loadData
						.call(this)
			},
			doRemove : function(){
				var record = this.getSelectedRecord();
				if(record==undefined){
					return;
				}
				var zhbs = record.data.ZHBS;
				var zhbs_tcm = record.data.ZHBS_TCM;
				Ext.Msg.show({
					title : '删除确认',
					msg : '确认删除？',
					modal : false,
					width : 100,
					buttons : Ext.MessageBox.YESNO,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "yes") {
							var res = util.rmi.miniJsonRequestSync({
							serviceId : "phis.TcmService",
							serviceAction : "removeZhDiagnosisContrastDz",
							body:{
								zhbs:zhbs,
								zhbs_tcm:zhbs_tcm
							}
						});
						if (res.code >= 300) {
							this.processReturnMsg(res.code, res.msg);
							return;
						} 
						MyMessageTip.msg("提示", "删除成功!", true);
						this.refresh();
						this.opener.midiModules["HISList"].refresh();
						this.opener.midiModules["TCMList"].refresh();
						} else {
							return;
						}
					},
					scope : this
				});				
			},
			doSave : function(){
				if(!this.opener.midiModules["HISList"].getSelectedRecord()){
					Ext.Msg.alert("提示","请在中医证候编码HIS列表中选择需对照的数据！");
					return;
				}
				if(!this.opener.midiModules["TCMList"].getSelectedRecord()){
					Ext.Msg.alert("提示","请在中医证候编码TCM列表中选择需对照的数据！");
					return;
				}
				var zhbs = this.opener.midiModules["HISList"].getSelectedRecord().data.ZHBS;
				var zhbs_tcm = this.opener.midiModules["TCMList"].getSelectedRecord().data.ZHBS;
				var zhmc = this.opener.midiModules["HISList"].getSelectedRecord().data.ZHMC;
				var zhdm = this.opener.midiModules["HISList"].getSelectedRecord().data.ZHDM;
				var pydm = this.opener.midiModules["HISList"].getSelectedRecord().data.PYDM;
				var zhmc_tcm = this.opener.midiModules["TCMList"].getSelectedRecord().data.ZHMC;
				var zhdm_tcm = this.opener.midiModules["TCMList"].getSelectedRecord().data.ZHDM;
				var pydm_tcm = this.opener.midiModules["TCMList"].getSelectedRecord().data.PYDM;
				var res = util.rmi.miniJsonRequestSync({
					serviceId : "phis.TcmService",
					serviceAction : "saveZhDiagnosisContrastDz",
					body : {
						zhbs:zhbs,
						zhbs_tcm:zhbs_tcm,
						zhmc:zhmc,
						zhdm:zhdm,
						pydm:pydm,
						zhmc_tcm:zhmc_tcm,
						zhdm_tcm:zhdm_tcm,
						pydm_tcm:pydm_tcm
					}
				});
				if (res.code >= 300) {
					this.processReturnMsg(res.code, res.msg);
					return;
				}
				this.refresh();
				MyMessageTip.msg("提示", "对照成功!", true);
				this.opener.midiModules["HISList"].refresh();
				this.opener.midiModules["TCMList"].refresh();
			},
			doAutomatching : function(item, e){
				var res = util.rmi.miniJsonRequestSync({
					serviceId : "phis.TcmService",
					serviceAction : "auotMatchingZhDiagnosisContrast"
				});
				if (res.code >= 300) {
					this.processReturnMsg(res.code, res.msg);
					return;
				}
				this.refresh();
				MyMessageTip.msg("提示", res.json.body.msg, true);
				this.opener.midiModules["HISList"].refresh();
				this.opener.midiModules["TCMList"].refresh();
			}		
		});