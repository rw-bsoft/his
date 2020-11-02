
$package("phis.application.tcm.script");

$import("phis.script.SimpleList")

phis.application.tcm.script.ZyDiagnosisContrastDZList = function(cfg) {
	phis.application.tcm.script.ZyDiagnosisContrastDZList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.tcm.script.ZyDiagnosisContrastDZList,
		phis.script.SimpleList, {
			loadData : function() {
				this.requestData.pageNo = 1;
				this.requestData.pageSize = 50;
				phis.application.tcm.script.ZyDiagnosisContrastDZList.superclass.loadData
						.call(this)
			},
			doRemove : function(){
				var record = this.getSelectedRecord();
				if(record==undefined){
					return;
				}
				var jbbs = record.data.JBBS;
				var jbbs_tcm = record.data.JBBS_TCM;
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
							serviceAction : "removeZyDiagnosisContrastDz",
							body:{
								jbbs:jbbs,
								jbbs_tcm:jbbs_tcm
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
					Ext.Msg.alert("提示","请在中医疾病编码HIS列表中选择需对照的数据！");
					return;
				}
				if(!this.opener.midiModules["TCMList"].getSelectedRecord()){
					Ext.Msg.alert("提示","请在中医疾病编码TCM列表中选择需对照的数据！");
					return;
				}
				var jbbs = this.opener.midiModules["HISList"].getSelectedRecord().data.JBBS;
				var jbbs_tcm = this.opener.midiModules["TCMList"].getSelectedRecord().data.JBBS;
				var jbmc = this.opener.midiModules["HISList"].getSelectedRecord().data.JBMC;
				var jbdm = this.opener.midiModules["HISList"].getSelectedRecord().data.JBDM;
				var pydm = this.opener.midiModules["HISList"].getSelectedRecord().data.PYDM;
				var jbmc_tcm = this.opener.midiModules["TCMList"].getSelectedRecord().data.JBMC;
				var jbdm_tcm = this.opener.midiModules["TCMList"].getSelectedRecord().data.JBDM;
				var pydm_tcm = this.opener.midiModules["TCMList"].getSelectedRecord().data.PYDM;
				var res = util.rmi.miniJsonRequestSync({
					serviceId : "phis.TcmService",
					serviceAction : "saveZyDiagnosisContrastDz",
					body : {
						jbbs:jbbs,
						jbbs_tcm:jbbs_tcm,
						jbmc:jbmc,
						jbdm:jbdm,
						pydm:pydm,
						jbmc_tcm:jbmc_tcm,
						jbdm_tcm:jbdm_tcm,
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
					serviceAction : "auotMatchingZyDiagnosisContrast"
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