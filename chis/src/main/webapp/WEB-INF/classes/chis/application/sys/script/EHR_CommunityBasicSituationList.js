$package("chis.application.conf.script.admin")

$import("chis.script.app.modules.list.SimpleListView")
chis.application.conf.script.admin.EHR_CommunityBasicSituationList = function(cfg) {
	
	cfg.xy = [150, 100]
	cfg.autoLoadData = true
	cfg.autoLoadSchema = true
	this.fileFilter = ["xls","xlsx"];
	chis.application.conf.script.admin.EHR_CommunityBasicSituationList.superclass.constructor.apply(this, [cfg]);
	this.on("uploadException",this.uploadException,this);
	this.on("uploadSuccess",this.uploadSuccess,this);
}
Ext.extend(chis.application.conf.script.admin.EHR_CommunityBasicSituationList, chis.script.app.modules.list.SimpleListView, {
doReportExcel : function(){
	window.open("*.excelUploadForm?serviceId=chis.communityBasicSituationService&serviceAction=reportExcel");
},
uploadException:function(){
	if(this.json.exceptionCode||this.json['x-response-code']){
			if(this.json['x-response-code']==401){
				this.win.setTitle("上传失败:用户未登陆或登录已过期")
				//this.processReturnMsg("", "", this.doUpload)
				return;
			}
			if (this.json.exceptionCode == 402) {
				this.win.setTitle("上传失败:用户空间已满或无权限")
				return
			}
			if (this.json.exceptionCode == 403) {
				this.win.setTitle("上传失败:单文件大小限制或其他错误")
				return
			}
			if (this.json.exceptionCode == 406) {
				this.win.setTitle("上传失败:文件格式错误;错误行号"+this.json.errorRow)
				return 	
			}
			if (this.json.exceptionCode == 407) {
				this.win.setTitle("上传失败:检测到非数值;第"+this.json.errorRow+"行;第"+this.json.errorCell+"列")
				return 	
			}
			if (this.json.exceptionCode == 408) {
				this.win.setTitle("上传失败:保存至数据库失败,请重试")
				return 	
			}
			if (this.json.exceptionCode == 409) {
				this.win.setTitle("上传失败:管辖机构不能为空;第"+this.json.errorRow+"行;第"+this.json.errorCell+"列")
				return 	
			}
			this.win.setTitle("上传失败:unknowError")
			return 	
	    } 
},
uploadSuccess:function(){
 			   if(this.json["x-response-code"] == 200){
					Ext.Msg.alert("提示消息","成功上传"
					    +(this.json.count||"0")+"条,更新"+(this.json.update||"0")+"条",
					    function(){
						chis.application.conf.script.admin.EHR_CommunityBasicSituationList.superclass.refresh.call(this);
					},this);
				} 
				var form = this.form.getForm()
				form.findField("fileId").setValue("")
				form.findField("file").setValue("")
				this.win.hide();
},
doAction:function(item, e){
	var cmd = item.cmd
	if (cmd == "create") {
 		this.doBegin();
 	}else{
 		var script = item.script
 		cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
		if (script) {
			$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
		} else {
			var action = this["do" + cmd]
			if (action) {
				action.apply(this, [item, e])
			}
		}
 	}
}

,doBegin : function() {
				if(!this.form){
					this.form = new Ext.FormPanel({
							frame : true,
							labelWidth : 75,
							labelAlign : 'top',
							defaults : {
								width : '95%'
							},
							defaultType : 'textfield',
							shadow : true,
							items : [{
										fieldLabel : '文件编码',
										name : 'fileId',
										inputType : 'hidden'
									}, {
										fieldLabel : '请选择要上传的文件',
										name : 'file',
										inputType : 'file',
										cls : 'x-form-fileupload'
									}]
						})
				}
				this.show()
			},
			show : function(renderTo, xy) {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
								title : "文件上传",
								id : "x-single-file-upload-"
										+ (new Date()).getTime(),
								layout : "form",
								width : 300,
								height : 130,
								closeAction : "hide",
								shadow : false,
								items : this.form,
								buttonAlign : 'center',
								buttons : [{
											text : '开始上传',
											handler : this.doUpload,
											scope : this
										}]
							})
					win.on("show", function() {
								var form = this.form.getForm()
								form.findField("fileId")
										.setValue(this.updateFileId)
								form.findField("file").setValue("")
								this.win.setTitle("上传文件:请选择允许的文件类型")
							}, this)
					this.win = win
				}
				if (xy) {
					win.setPosition(xy[0], xy[1])
				}
				if (renderTo) {
					win.render(renderTo)
				}
				if (win.isVisible()) {
					win.hide() // for refresh bug
				}
				win.doLayout()
				win.show()
			},
			doUpload : function() {
				var form = this.form
				if (!this.checkFileType()) {
					this.win.setTitle("上传文件:请选择Excel表格文件上传!")
					return;
				}
				var con = new Ext.data.Connection();
				this.win.el.mask("正在上传请稍候...", "x-mask-loading")
				con.request({	
							url : "*.excelUploadForm?serviceId=chis.communityBasicSituationService&serviceAction=uploadCommunityBasFile",
							method : "post",
							isUpload : true,
							callback : complete,
							scope : this,
							form : form.getForm().el
						})
				function complete(ops, sucess, response) {
					this.win.el.unmask()
					var json;
					if (sucess) { //成功
						try {
							eval("json=" + response.responseText)
						} catch (e) {
							this.fireEvent("uploadException", 501,
									"unknowResponseForm")
						}
						 
						this.json = json; 
						if(json.exceptionCode||(json['x-response-code']!=200)){
							this.fireEvent("uploadException", json['x-response-code'], "unknowError")
						}else{
					  	    this.fireEvent("uploadSuccess", 201, id);
						}
					} 
				}// func complete
			},// func doUpload
			checkFileType : function() {
				var f = this.form.items.item(1).getValue()
				if (!f) {
					return false;
				}
				var type = f.substring(f.lastIndexOf(".") + 1)
				type = type.toLowerCase();
				for(t in this.fileFilter){
				  if(this.fileFilter[t]==type){
				  	return true;
				  }
				}
				return false;
			},
			close : function() {
				if (this.win) {
					this.win.close();
				}

			}
			,
			doModifyData:function(){
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.cvdService",
							schema : "CVD_AssessRegister",
							serviceAction:"modifyData",
							body:""
						})
			}
}); 