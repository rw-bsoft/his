$package("chis.application.ag.script")
$import("chis.script.BizTableFormView")

chis.application.ag.script.GenBuildingForm = function(cfg) {
	cfg.width = 300;
	cfg.colCount = 1;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 200;
	cfg.labelWidth = 60;
	cfg.autoLoadSchema = false;
	this.title = "快速生成网格地址";
	this.entryName = "chis.application.ag.schemas.ADMIN_GenBuilding";
	cfg.actions = [{
				id : "save",
				name : "确定"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}]
	chis.application.ag.script.GenBuildingForm.superclass.constructor.apply(this, [cfg])
	this.autoLoadData =false ;
	this.on("winShow", this.onWinShow, this);
	this.on("doNew", this.onDoNew, this);
	this.serviceId = "chis.agService";
	this.serviceAction = "batchResidenceCommunity";
}

Ext.extend(chis.application.ag.script.GenBuildingForm, chis.script.BizTableFormView, {
			onReady : function() {
				chis.application.ag.script.GenBuildingForm.superclass.onReady.call(this);
				this.setKeyReadOnly(false);

				var isFamily = this.form.getForm().findField("isFamily");
				if (isFamily) {
					isFamily.on("expand",function(combo){
						var tree = combo.tree ;
						tree.expandAll();
						},this);
					this.tree = isFamily.tree;
					var root = this.tree.getRootNode();
					root.on("load", function() {
								this.tree.filter
										.filterBy(this.filterTree, this)
							}, this)
					root.on("expand", function(root) {
								var firstNode = root.firstChild;
								firstNode.on("load", function() {
											this.tree.filter.filterBy(
													this.filterTree, this)
										}, this)
								var childNode = root.findChild("id", "country")
								childNode.on("load", function() {
											this.tree.filter.filterBy(
													this.filterTree, this)
										}, this)

							}, this);
				}
			},
			onWinShow : function() {
				this.doNew();
			},
			onDoNew : function() {
				this.tree.filter.filterBy(this.filterTree, this);
			},
			filterTree : function(node) {
				var isFamily = this.selectedNode.attributes["isFamily"]

				var p1 = /^[e-g]\S*2$/ // 农村
				if (p1.exec(isFamily)) {
					if (node.id == "city")
						return false
					if (p1.exec(node.id) && node.id <= isFamily) {
						return false
					}
				}
				var p2 = /^[g-i]\S*1$/ // 社区以下
				if (p2.exec(isFamily)) {
					if (node.id == "country")
						return false
				}
				var p3 = /^[e-i]\S*1$/ // 城市
				if (p3.exec(isFamily)) {
					if (p3.exec(node.id) && node.id <= isFamily)
						return false
					if (p1.exec(node.id)
							&& node.id.substring(0, 1) <= isFamily.substring(0,
									1)) {
						return false
					}
				}
				return true
			},
			doSave : function() {
				if (!this.validate())
					return;
				var form = this.form.getForm();
				var countField = form.findField("count");
				var unitField = form.findField("unit");
				var startField = form.findField("start");
				var isFamilyF = form.findField("isFamily");
				var count = countField.getValue();
				var start = startField.getValue();
				if (start >= 1000000)
					alert("起始数字过大");
				var unit = unitField.getRawValue();
				var isFamily = isFamilyF.getValue();
				var data = {
					count : count,
					start : start,
					unit : unit,
					isFamily : isFamily
				}
				this.saveToServer(data);
			},
			saveToServer : function(saveData) {
				saveData["regionCode"] = this.selectedNode.attributes["key"];
				saveData["parentBottom"] = this.selectedNode.attributes["isBottom"];
				this.form.el.mask("正在执行操作...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.serviceAction,
							method:"execute",
							schema : this.entryName,
							body : saveData
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							this.getWin().hide()
							
							var nodes = json.body;
							if (!nodes || nodes.length == 0)
								return;
							for (var i = 0; i < nodes.length; i++) {
								var node = nodes[i];
								var nodeName = node["regionName"];
								var nodekey = node["regionCode"];
								var isFamily = node["isFamily"];
								var isBottom = node["isBottom"];
								if (!nodekey || nodekey.length == 0) {
									continue;
								}
								var folder ;
								if('n'==isBottom){
									folder = true;
								}else{
									folder= false ;
								}
								
								var node = new Ext.tree.TreeNode({
											id : nodekey,
											expandable : false,
											leaf : folder
										});
								node.attributes["key"] = nodekey;
								node.attributes["text"] = nodeName;
								node.attributes["isFamily"] = isFamily;
								node.attributes["isBottom"] = isBottom;
								node.attributes["parentCode"] = this.selectedNode.attributes["key"];
								node.leaf = !folder ;
								node.setText(nodeName);
								this.selectedNode.leaf = false
								this.selectedNode.appendChild(node);
							}
						this.fireEvent("save");
						}, this)
			}
		})