$package("chis.script")

chis.script.ExcelFileUploader = function(filter) {
	this.fileFilter = filter
	chis.script.ExcelFileUploader.superclass.constructor.call(this)
	this.init();
	this.on("uploadEnd", this.onUploadEnd);
}

Ext.extend(chis.script.ExcelFileUploader, Ext.util.Observable, {
			init : function() {
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

				this.addEvents({
							"uploadSuccess" : true,
							"uploadException" : true
						})
			},

			setUpdateFileId : function(v) {
				this.updateFileId = v
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
								var form = this.form.getForm();
								form.findField("fileId")
										.setValue(this.updateFileId);
								form.findField("file").setValue("");
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

			onUploadEnd : function() {
			},

			getUrl : function() {
				return '';
			},

			haveProgress : function() {
				return false;
			},

			getProgress : function() {
				// 发请求到后台，获得变量
				// 更新进度条
				// ....各自业务自己实现
			},

			doUpload : function() {
				var form = this.form
				if (!this.checkFileType()) {
					this.win.setTitle("上传文件:请选择允许的文件类型：" + this.fileFilter
							+ " 文件")
					return;
				}
				// 是否需要有进度条
				if (this.haveProgress()) {
					// 获取进度条
					this.getProgress();
				}
				// 上传文件
				var url = this.getUrl();
				var con = new Ext.data.Connection();
				this.win.el.mask("正在上传请稍候...", "x-mask-loading")
				con.request({
							url : url || "excelUploadForm",
							method : "post",
							isUpload : true,
							callback : complete,
							scope : this,
							form : form.getForm().el
						})

				function complete(ops, sucess, response) {
					Ext.Msg.alert('提示', '网格地址导入成功');
					this.win.el.unmask()
					if (sucess) {
						var json;
						try {
							eval("json=" + response.responseText)
						} catch (e) {
							this.fireEvent("uploadException", 501,
									"unknowResponseForm")
						}
						if (json) {
							if (json.code == 200) {
								Ext.Msg.alert('提示', '网格地址导入成功');
								this.fireEvent("upFieldEnd");
								this.win.hide();
							} else {
								Ext.Msg.alert('错误提示',json.msg);
								this.win.hide();
							}
						}
					} else {
						this.fireEvent("uploadException", 500, "unknowError")
					}
					this.fireEvent("uploadEnd");
					this.win.hide();
				}
			},

			checkFileType : function() {
				var f = this.form.items.item(1).getValue()
				if (!f) {
					return false;
				}
				var filter = this.fileFilter
				if (!filter) {
					return true;
				}

				var type = f.substring(f.lastIndexOf(".") + 1)

				type = type.toLowerCase();
				if (typeof filter == "string") {
					return type == filter.toLowerCase();
				}

				if (typeof filter == "object" && filter.length > 0) {
					for (var i = 0; i < filter.length; i++) {

						if (type == filter[i].toLowerCase()) {
							return true;
						}
					}
				}

			},
			close : function() {
				if (this.win) {
					this.win.close();
				}

			},
			destory : function() {
			}
		})