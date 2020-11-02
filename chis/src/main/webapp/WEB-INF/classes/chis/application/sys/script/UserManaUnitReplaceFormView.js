$package("chis.application.conf.script.admin")
$import("chis.script.app.modules.form.TableFormView", "util.dictionary.DictionaryLoader",
		"util.widgets.TreeField")

chis.application.conf.script.admin.UserManaUnitReplaceFormView = function(cfg) {
	cfg.colCount = 1;
	cfg.width = 400
	cfg.fldDefaultWidth = 250
	cfg.autoLoadData = false;
	cfg.title = "所属单位发生变化,请为原机构的相关档案选择接管医生!"
	cfg.actions = [{
				id : "save",
				name : "确定"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}]
	cfg.autoFieldWidth = false
	cfg.entryName = "SystemUsersReplace"
	chis.application.conf.script.admin.UserManaUnitReplaceFormView.superclass.constructor.apply(
			this, [cfg])
	this.on("changeDic", this.onChangeDic, this)
	//this.on("winShow",this.onWinShow,this)
}

Ext.extend(chis.application.conf.script.admin.UserManaUnitReplaceFormView,
		chis.script.app.modules.form.TableFormView, {
			onChangeDic : function(items) {
				
				var size = items.length
				//var item
				for (var i = 0; i < size; i++) {
					if (items[i].id == 'newDoctor') {
						this.docItem = items[i]
					}
				}
				if (this.jobTitle == '01' || this.jobTitle == '02'
						|| this.jobTitle == '05') {
					this.docItem.dic.id = 'user01'
				} else if (this.jobTitle == '07') {
					this.docItem.dic.id = 'user02'
				} else if (this.jobTitle == '08') {
					this.docItem.dic.id = 'user03'
				}
				this.docItem.dic.parentKey=this.manaUnitId;
			},
			doSave : function() {
				var newDoctorF = this.form.getForm().findField("newDoctor")
				var newManaUnitIdF = this.form.getForm().findField("newManaUnitId")
				var data = {
					newDoctor : newDoctorF.getValue(),
					newManaUnitId : newManaUnitIdF.getValue(),
					oldJobTitle:this.jobTitle
				}
				this.fireEvent("save", this.entryName, this.op, null,data)
				if(this.win){
					this.win.close()
				}
			},
			getManaUnit : function(key) {
				var cnd = [];
				if(this.jobTitle == "01" || this.jobTitle=="05"){
					cnd = [
								"and",
									["eq", ["$", "userId"], ["s", key]],
									['or',
										["eq", ["$", "jobId"],
											["s", "01"]],
										["eq", ["$", "jobId"],
											["s", "05"]]		
									]
							]
				}else{
					cnd = [
								"and",
									["eq", ["$", "userId"], ["s", key]],
									["eq", ["$", "jobId"],
											["s", this.jobTitle]]	
							]
				}
				
				util.rmi.jsonRequest({
							serviceId : 'chis.simpleQuery',
							schema : "SYS_UserProp",
							cnd : cnd
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							if (json.body && json.body.length > 0) {
								var body = json.body[0];
								alert(Ext.encode(body))
								var combox = this.form.getForm()
										.findField("newManaUnitId");
								if (body) {
									var manageUnit = body.manaUnitId;
									var manageUnitText = body.manaUnitId_text;
									if (manageUnit) {
										combox.setValue({
													key : manageUnit,
													text : manageUnitText
												})
									} else {
										combox.setValue({
													key : "",
													text : ""
												})
									}
								}
							}
						}, this)
			},
			onReady : function() {
				var form = this.form.getForm();
				var newDoctorF = this.form.getForm().findField("newDoctor");
				newDoctorF.on("select",function(combo,r,idx){//根据所选择的医生，获取对应的管辖机构.
					this.getManaUnit(newDoctorF.getValue())
				},this)
				
				newDoctorF.on("beforeselect",function(comb, node){//根据所选择的医生，获取对应的管辖机构.
					if(node.attributes["key"]==this.logonName){
						Ext.Msg.alert("提示","请选择另外一名医生!");
						return false ;
					}
				},this)
				
				
			},
			filterNewManaUnitId : function(node, comb) {
				if (!node || !this.jobTitle)
					return false;

				var keySize = node.attributes["key"].length;
				var key = node.attributes["key"];
				var mp = node.attributes["isMunicipality"];
				alert(key+"-"+this.logonName)
				
				if(key == this.logonName)
					return false ;
				if (this.jobTitle == "13") {
					if (mp)
						return true;
					else if (keySize <= 4 || keySize == 6)
						return true;
					else
						return false;
				}
				if (this.jobTitle == "11" || this.jobTitle == "09") { // 市CDC,市妇保
					if (mp)
						return true;
					else if (keySize <= 4)
						return true;
					else
						return false;
				} else if (this.jobTitle == "12" || this.jobTitle == "10") {// ,区CDC,区妇保
					if (keySize == 6)
						return true;
					else
						return false;
				} else if (this.jobTitle == "04" || this.jobTitle == "06"
						|| this.jobTitle == "07" || this.jobTitle == "08"
						|| this.jobTitle == "14" || this.jobTitle == "15"
						|| this.jobTitle == "16") { // 中心主任,儿保医生,妇保医生,网络管理员,防保科长
					if (keySize == 9)
						return true;
					else
						return false;
				} else if (this.jobTitle == "05" || this.jobTitle == "01"
						|| this.jobTitle == "02" || this.jobTitle == "03") { // 团队长,责任医生,责任护士,公卫医生
					if (keySize >= 11) {
						return true;
					} else
						return false;
				}
			},

			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width,
								autoHeight : true,
								iconCls : 'icon-form',
								closeAction : 'close',
								shim : true,
								layout : "fit",
								plain : true,
								autoScroll : false,
								minimizable : true,
								maximizable : true,
								constrain : true,
								shadow : false,
								buttonAlign : 'center',
								modal : true
							})
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this)
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					this.win = win
				}
				win.instance = this;
				return win;
			}
		})