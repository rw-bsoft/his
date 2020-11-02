
$package("phis.application.tcm.script");

$import("phis.script.SimpleList")

phis.application.tcm.script.DiagnosisContrastDZList = function(cfg) {
	phis.application.tcm.script.DiagnosisContrastDZList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.tcm.script.DiagnosisContrastDZList,
		phis.script.SimpleList, {
			loadData : function() {
				this.requestData.pageNo = 1;
				this.requestData.pageSize = 50;
				phis.application.tcm.script.DiagnosisContrastDZList.superclass.loadData
						.call(this)
			},
			doRemove : function(){
				var record = this.getSelectedRecord();
				if(record==undefined){
					return;
				}
				var jbxh = record.data.JBXH;
				var jbxh_tcm = record.data.JBXH_TCM;
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
							serviceAction : "removeDiagnosisContrastDz",
							body:{
								jbxh:jbxh,
								jbxh_tcm:jbxh_tcm
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
					Ext.Msg.alert("提示","请在西医疾病编码HIS列表中选择需对照的数据！");
					return;
				}
				if(!this.opener.midiModules["TCMList"].getSelectedRecord()){
					Ext.Msg.alert("提示","请在西医疾病编码TCM列表中选择需对照的数据！");
					return;
				}
				var jbxh = this.opener.midiModules["HISList"].getSelectedRecord().data.JBXH;
				var jbxh_tcm = this.opener.midiModules["TCMList"].getSelectedRecord().data.JBXH;
				var jbmc = this.opener.midiModules["HISList"].getSelectedRecord().data.JBMC;
				var icd10 = this.opener.midiModules["HISList"].getSelectedRecord().data.ICD10;
				var pydm = this.opener.midiModules["HISList"].getSelectedRecord().data.PYDM;
				var jbmc_tcm = this.opener.midiModules["TCMList"].getSelectedRecord().data.JBMC;
				var icd10_tcm = this.opener.midiModules["TCMList"].getSelectedRecord().data.ICD10;
				var pydm_tcm = this.opener.midiModules["TCMList"].getSelectedRecord().data.PYDM;
				var res = util.rmi.miniJsonRequestSync({
					serviceId : "phis.TcmService",
					serviceAction : "saveDiagnosisContrastDz",
					body : {
						jbxh:jbxh,
						jbxh_tcm:jbxh_tcm,
						jbmc:jbmc,
						icd10:icd10,
						pydm:pydm,
						jbmc_tcm:jbmc_tcm,
						icd10_tcm:icd10_tcm,
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
					serviceAction : "auotMatchingDiagnosisContrast"
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