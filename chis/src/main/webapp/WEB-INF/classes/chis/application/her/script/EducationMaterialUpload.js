$package("chis.application.her.script");

$import("chis.script.ExcelFileUploader", "util.rmi.jsonRequest");

chis.application.her.script.EducationMaterialUpload = function(cfg) {
	this.dirType = cfg.dirType
	chis.application.her.script.EducationMaterialUpload.superclass.constructor
			.apply(this, [cfg]);
	Ext.apply(this, app.modules.common);
	this.fileFilter = cfg.fileFilter;
	this.modal = true;
};

Ext.extend(chis.application.her.script.EducationMaterialUpload,
		chis.script.ExcelFileUploader, {
			init : function() {
				var fileTypeCombox = util.dictionary.SimpleDicFactory
						.createDic({
									id : 'chis.dictionary.fileType',
									width : 200,
									editable : false
								});
				fileTypeCombox.name = 'type';
				fileTypeCombox.fieldLabel = '资料分类';
				fileTypeCombox.labelStyle = "width:60px;float:left;margin-top:5px;";
				fileTypeCombox.allowBlank = false;
				fileTypeCombox.blankText = "资料分类不能为空";
				this.fileTypeCombox = fileTypeCombox;

				var fileObjectTypeCombox = util.dictionary.SimpleDicFactory
						.createDic({
									id : 'chis.dictionary.objectType',
									width : 200,
									editable : false
								});
				fileObjectTypeCombox.name = 'type';
				fileObjectTypeCombox.fieldLabel = '对象分类';
				fileObjectTypeCombox.labelStyle = "width:60px;float:left;margin-top:5px;";
				fileObjectTypeCombox.allowBlank = false;
				fileObjectTypeCombox.blankText = "对象分类不能为空";
				this.fileObjectTypeCombox = fileObjectTypeCombox;

				this.form = new Ext.FormPanel({
							frame : true,
							labelWidth : 130,
							labelAlign : 'top',
							defaultType : 'textfield',
							shadow : true,
							items : [{
										fieldLabel : '文件保存路径',
										name : 'dirType',
										inputType : 'hidden',
										value : this.dirType
									}, fileTypeCombox, fileObjectTypeCombox, {
										fieldLabel : '请选择要上传的文件',
										name : 'file',
										inputType : 'file',
										defaults : {
											width : '95%'
										},
										autoCreate : {
											tag : 'input',
											type : 'text',
											size : '40',
											autocomplete : 'off'
										}
									}, {
										fieldLabel : '文件编码',
										name : 'fileId',
										inputType : 'hidden'
									}]
						});

				this.addEvents({
							"uploadSuccess" : true,
							"uploadException" : true
						});
			},

			show : function(renderTo, xy) {
				var win = this.win;
				if (!win) {
					win = new Ext.Window({
								title : "文件上传",
								id : "x-single-file-upload-"
										+ (new Date()).getTime(),
								layout : "form",
								width : 360,
								height : 200,
								closeAction : "hide",
								shadow : false,
								modal : this.modal || false,
								items : this.form,
								buttonAlign : 'center',
								buttons : [{
											text : '开始上传',
											handler : this.doUpload,
											scope : this
										}]
							});
					win.on("show", function() {
								var form = this.form.getForm();
								this.fileTypeCombox.validate();
								this.fileObjectTypeCombox.validate();

								form.findField("fileId")
										.setValue(this.updateFileId);
								form.findField("file").setValue("");
							}, this);
					this.win = win;
				}
				if (xy) {
					win.setPosition(xy[0], xy[1]);
				}
				if (renderTo) {
					win.render(renderTo);
				}
				if (win.isVisible()) {
					win.hide(); // for refresh bug
				}
				win.doLayout();
				win.show();
			},

			checkFileType : function() {

				var f = this.form.items.item(3).getValue();
				if (!f) {
					return false;
				}
				var filter = this.fileFilter;
				if (!filter) {
					return true;
				}

				var type = f.substring(f.lastIndexOf(".") + 1);

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

			checkFileName : function() {
				var f = this.form.items.item(2).getValue();
				if (!f) {
					return false;
				}
				this.fileName = f.substring(f.lastIndexOf("/") + 1);

				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.educationMaterialService",
							serviceAction : "checkFileName",
							method : "execute",
							body : {
								fileName : this.fileName,
								setId : this.setId,
								exeId : this.exeId,
								recordId : this.recordId
							}
						});
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return
				}
				return result.json.exist;
			},

			doUpload : function() {
				var form = this.form;
				if (!this.checkFileType()) {
					this.win.setTitle("文件上传:请选择允许的文件类型："
							+ this.fileFilter.toString() + " 文件");
					return;
				}
				var items = this.form.items;
				var fileTypeVal = items.item(1).getValue();
				var fileObjectTypeVal = items.item(2).getValue();
				if (!fileTypeVal || !fileObjectTypeVal) {
					Ext.Msg.alert("提示", "资料分类和对象分类不能为空！");
					return;
				}
				var url = this.getUrl();
				var con = new Ext.data.Connection();
				this.win.el.mask("正在上传请稍候...", "x-mask-loading");

				if (this.checkFileName()) {
					this.win.el.unmask();
					this.win.setTitle("文件上传:" + this.fileName
							+ "文件已存在，请重命名后再上传");
					return;
				}
				con.request({
							url : "*.uploadForm",
							method : "post",
							isUpload : true,
							callback : complete,
							scope : this,
							form : form.getForm().el
						});
				function complete(ops, sucess, response) {
					this.win.el.unmask();
					if (sucess) {
						var json;
						try {
							eval("json=" + response.responseText);
						} catch (e) {
							this.fireEvent("uploadException", 501,
									"unknowResponseForm");
						}
						if (json) {
							if (json['code'] == 200) {
								var body = json.body;
								var fileName = body.file;
								var path = body.path;
								this.saveDataToDb(fileName, path);
							} else {
								Ext.Msg.alert('错误提示', json['msg']);
								this.win.hide();
							}
						}
					} else {
						this.fireEvent("uploadException", 500, "unknowError");
					}
					this.fireEvent("uploadEnd");
					this.win.hide();
				}
			},

			saveDataToDb : function(fileName, path) {
				var items = this.form.items;
				var fileTypeVal = items.item(1).getValue();
				var fileObjectTypeVal = items.item(2).getValue();
				var curUserUnit = this.mainApp.deptId;
				var curUserName = this.mainApp.uid;
				util.rmi.jsonRequest({
							serviceId : "chis.educationMaterialService",
							serviceAction : "saveEcucationMaterialInfo",
							method : "execute",
							body : {
								"setId" : this.setId,
								"exeId" : this.exeId,
								"recordId" : this.recordId,
								"fileType" : fileTypeVal,
								"fileObjectType" : fileObjectTypeVal,
								"fileName" : fileName,
								"fileSite" : path,
								"createUnit" : curUserUnit,
								"createUser" : curUserName,
								"lastModifyUser" : curUserName,
								"lastModifyUnit" : curUserUnit
							}
						}, function(code, msg, json) {
							if (code < 300) {
								Ext.Msg.alert('提示', '附件上传成功');
								this.fireEvent("upFieldSucess", this.fileName);
								this.win.hide();
							} else {
								Ext.Msg.alert('错误提示', msg);
								this.win.hide();
							}
						}, this)
			}
		});