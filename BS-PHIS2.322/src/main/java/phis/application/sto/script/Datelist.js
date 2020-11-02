$package("phis.application.sto.script")

$import("phis.script.TableForm")

phis.application.sto.script.Datelist = function(cfg) {
	cfg.autoLoadData = false;
	cfg.showButtonOnTop = false;
	cfg.modal = true;
	phis.application.sto.script.Datelist.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.sto.script.Datelist, phis.script.TableForm, {
			// 确认
			doCommit : function() {
				if(this.form!=undefined){
					var form = this.form.getForm();
				var KSRQ = form.findField("KSRQ").value;// 开始日期
				var JSRQ = form.findField("JSRQ").value;// 开始日期
				if(KSRQ==""||KSRQ==undefined){
					 MyMessageTip.msg("提示","开始时间必填" , true);
					 return;
				}
				if(JSRQ==""||JSRQ==undefined){
					 MyMessageTip.msg("提示","结束时间必填" , true);
					 return;
				}
				}
				var storehouseId = this.mainApp['phis'].storehouseId;
				var url = "http://10.2.202.21:8280/services/medicalbusiness";
				var url1 = "http://10.2.202.21:8280/services/updateDownloadState";
				var payapi = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.SptService",
							serviceAction : "distributionDownload",
							body : {
								url : url,
								storehouseId : storehouseId,
								KSRQ : KSRQ,
								JSRQ : JSRQ
							}
						});
						
						var json=payapi.json;
						var body=json.body;
			     if(payapi.code !=200){
					  MyMessageTip.msg("提示",payapi.msg , true);
					   return;
			    	}else if (body.code){
					  MyMessageTip.msg("提示",body.msg , true);
					   return;
				  }else if (payapi.code == 200) {
					MyMessageTip.msg("提示", "省平台药品信息下载成功!", true);
				    this.getWin().hide();
				    this.opener.doRefreshWin();
				  }
			},
			// 关闭
			doClose : function() {
				this.getWin().hide();
			}
		})