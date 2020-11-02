$package("phis.application.tcm.script")

$import("phis.script.SimpleModule")
$styleSheet("phis.resources.css.app.biz.cic_css")
$styleSheet("phis.resources.css.app.biz.style")
phis.application.tcm.script.UploadHerbalMedicine = function(cfg) {
	this.isModify = false;
	this.width = 800;
	this.height = 500;
	phis.application.tcm.script.UploadHerbalMedicine.superclass.constructor.apply(
			this, [cfg]);
}

/***
 * 【中医馆】草药数据上传
 */
Ext.extend(phis.application.tcm.script.UploadHerbalMedicine,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel)
					return this.panel;
				this.form=new Ext.form.TextArea();
				this.panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : 500,
							height : 500,
							items : [{
										title : '',
										layout : "fit",
										split : true,
										region : 'center',
										items : this.form
									}],
							bbar:this.createButtons()
						});
				this.panel.on("afterrender", this.onReady, this)
				return this.panel;
			},
			onReady : function() {
			},
			doCommit : function() {
				var alertString = "开始上传草药信息到省中医馆平台，这个过程可能需要较长一段时间，请耐心等待...";
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "TcmService",
					serviceAction : "HerbalMedicineCount",
					body : {
							}
						});
						if(r.code == 200){
							if(r.json.body.infocount>0){
								alertString = "开始上传草药信息到省中医馆平台，这个过程大概需要"+parseInt(parseInt(r.json.body.infocount)*5/60)+"分钟时间，请耐心等待...";
							}
						}
						if(this.form.value==undefined){
							this.form.setValue(alertString);
						}
						else{
							this.form.setValue(this.form.value+"\r\n"+alertString);
						}
					/********************获取病人信息，并推送给省中医馆平台************************************/
					var r = phis.script.rmi.miniJsonRequestAsync({
					serviceId : "TcmService",
					serviceAction : "UploadHerbalMedicine",
					body : {
								/*jgid : this.mainApp['phisApp'].deptId,
								brid : this.exContext.ids.brid,
								jzxh : this.exContext.ids.clinicId*/
							}
						}, function(code, msg, json) {
							//debugger;
					if (code > 300) {
						this.processReturnMsg(code, msg, this.onBeforeSave);
						return;
					} else {
						if (this.form.value == undefined) {
							this.form.setValue("上传信息结束：" + json.body.msg);
						} else {
							this.form.setValue(this.form.value + "\r\n"
									+ json.body.msg);
						}
						this.form.setValue(this.form.value
										+ "\r\n"
										+ "----------------------------------------------------------------------------------------------------------------");
					}
				}, this);
			}
		});