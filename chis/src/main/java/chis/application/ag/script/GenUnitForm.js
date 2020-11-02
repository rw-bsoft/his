$package("chis.application.ag.script")
$import("chis.script.BizTableFormView")

chis.application.ag.script.GenUnitForm = function(cfg) {
	cfg.width = 300;
	cfg.colCount = 2;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 50;
	cfg.labelWidth = 60
	cfg.autoLoadSchema = false;
	this.title = "快速生成户"
	this.entryName = "chis.application.ag.schemas.ADMIN_GenUnit";
	cfg.actions = [{
				id : "save",
				name : "确定"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}]
	chis.application.ag.script.GenUnitForm.superclass.constructor.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this)
	this.serviceId = "chis.agService"
	this.serviceAction = "batchUnit"
}

Ext.extend(chis.application.ag.script.GenUnitForm, chis.script.BizTableFormView, {
			onReady : function() {
				chis.application.ag.script.GenUnitForm.superclass.onReady.call(this);
				this.setKeyReadOnly(false);
				var codeStyle = this.form.getForm().findField("codeStyle");
				if (codeStyle)
					codeStyle.on("select", this.selectCodeStyle, this);
			},
			selectCodeStyle : function(field) {
				var codeStyle = field.getValue();
				if (!codeStyle)
					return;
				var form = this.form.getForm();
				var familyCount = form.findField("familyCount");
				var startFamily = form.findField("startFamily");
				if (codeStyle == "03") {
					familyCount.setValue("1");
					startFamily.setValue("1");
					familyCount.setReadOnly(true);
					startFamily.setReadOnly(true);
				} else {
                    familyCount.setValue("");
					familyCount.setReadOnly(false);
					startFamily.setReadOnly(false);
				}
			},
			onWinShow : function() {
				this.doNew();
				var codeStyle = this.form.getForm().findField("codeStyle");
				this.selectCodeStyle(codeStyle);
			},
			setParentNode : function(parentNode) {
				this.parentNode = parentNode;
			},
			saveToServer : function(saveData) {
				var layerCount = saveData["layerCount"];
				var familyCount = saveData["familyCount"];
				if (familyCount * layerCount > 999) {
					alert("每单元总户数不能超过999户");
					return;
				}

				this.form.el.mask("正在执行操作...", "x-mask-loading")
				saveData["parentCode"] = this.parentNode.attributes["key"];
				saveData["parentBottom"] = this.parentNode.attributes["isBottom"];
				util.rmi.jsonRequest({
							serviceId : this.serviceId,
							schema : this.entryName,
							method:"execute",
							body : saveData,
							serviceAction : this.serviceAction
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							this.getWin().hide()
							this.fireEvent("save");
							if (json["errmsg"]) {
								alert(json["errmsg"])
							}
							var nodes = json.body;
							if (!nodes || nodes.length == 0)
								return;
							for (var i = 0; i < nodes.length; i++) {
								var node = new Ext.tree.TreeNode({
											id : regionCode
										});
								var regionCode = nodes[i]["regionCode"];
								if (!regionCode || regionCode.length == 0) {
									continue;
								}
								var regionName = nodes[i]["regionName"];
								node.attributes["key"] = regionCode;
								node.attributes["text"] = regionName;
								node.attributes["isFamily"] = nodes[i]["isFamily"];
								node.attributes["isBottom"] = nodes[i]["isBottom"];
								node.attributes["parentCode"] = this.parentNode.attributes["key"];
								node.setText(nodes[i]["regionName"]);
								node.leaf = true;
								this.parentNode.leaf = false;
								this.parentNode.appendChild(node);
							}
						}, this)// jsonRequest
			}
		})