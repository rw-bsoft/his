/**
 * 病区退药-退药处理左边LIST
 * 
 * @author : caijy
 */
$package("phis.application.pcm.script")

$import("phis.script.SelectList")

phis.application.pcm.script.PrescriptionCommentsRightModuleBhlList = function(cfg) {
	cfg.initCnd = ['ne',['$','ZFPB'],['i',1]]
	cfg.modal=true;
	phis.application.pcm.script.PrescriptionCommentsRightModuleBhlList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.pcm.script.PrescriptionCommentsRightModuleBhlList,
		phis.script.SelectList, {
			doNew : function() {
				this.clearSelect();
				this.loadData();
			},
			doClose:function(){
			this.getWin().hide();
			},
			doQd:function(){
			var records=this.getSelectedRecords();
			if(records.length==0){
			MyMessageTip.msg("提示","未选择任何记录",true);
			return;
			}
			var length=records.length;
			var wtdms="";
			for(var i=0;i<length;i++){
			var r=records[i];
			wtdms+=r.get("WTDM");
			if(i!=length-1){
			wtdms+=",";
			}
			}
			this.fireEvent("wtqr",wtdms);
			this.doClose();
			}
		});