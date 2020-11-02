$package("chis.application.per.script.checkup");

$import("chis.script.BizCombinedModule3");

chis.application.per.script.checkup.CheckupDetailModule = function(cfg) {
	cfg.autoLoadData = false;
	chis.application.per.script.checkup.CheckupDetailModule.superclass.constructor
			.apply(this, [cfg]);
	this.height = 465;
	this.width = 800;
	this.westWidth = 150;
	this.layOutRegion = "north";
	this.itemWidth = 650;
	this.itemHeight = 168;
	// this.on("getFirstKSNode", this.getFirstKSNode, this);
};

Ext.extend(chis.application.per.script.checkup.CheckupDetailModule,
		chis.script.BizCombinedModule3, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var items = this.getPanelItems();
				var panel = new Ext.Panel({
							border : false,
							split : true,
							hideBorders : true,
							frame : true,
							layout : 'border',
							width : this.width,
							height : this.height,
							items : items
						});
				this.panel = panel;
				this.form = this.midiModules[this.actions[0].id];
				this.form.on("save", this.onFormSave, this);
				this.form.on("changeValue", this.changeValue, this);
				this.list = this.midiModules[this.actions[1].id];
				this.list.on("recordSave", this.onFormSave, this);
				this.list.on("save", this.onFormSave, this);
				this.list.on("listDelRow", this.onListDelRow, this);
				this.grid = this.list.grid;
				this.grid.setAutoScroll(true);
				this.grid.setHeight(100);
				return panel;
			},
			getFirstItem : function() {
				var tf = util.dictionary.TreeDicFactory.createDic({
							id : "chis.dictionary.perCombo",
							onlySelectLeaf : "true"
						});
				var tree = tf.tree;
				this.tree = tree;
				tree.animate = false;
				// load for the first time
				tree.getRootNode().on("beforeexpand", function() {
							tree.filter.filterBy(function(n) {
										return false;
									}, this);
							this.moduleLoadData();
						}, this);

				// expand display node
				tree.getLoader().on("load", function(t, n, r) {
							tree.filter.filterBy(this.filterDicTree, this);
						}, this);
				tree.title = " ";
				tree.split = true;
				tree.width = this.westWidth || 150;
				tree.height = 452;

				tree.on("click", function(node, e) {
							this.selectedNode = node;
							var type = node.attributes["type"];
							var item = this.reBulidData(node);
							if (type == "item" || type == "ks") {
								this.setData(item, type);// ----------------------
							}
						}, this);

				tree.on("dbclick", function(node, e) {
						}, this);
				return tree;
			},
			getSecondItem : function() {
				return this.getCombinedModule(this.actions[0].id,
						this.actions[0].ref);
			},
			getThirdItem : function() {
				return this.getCombinedModule(this.actions[1].id,
						this.actions[1].ref);
			},
			reBulidData : function(node) {
				var items = [];
				if (node.hasChildNodes()) {
					var nodes = node.childNodes;
					for (var i = 0; i < nodes.length; i++) {
						var childNode = nodes[i];
						items.push(this.createItem(childNode));
					}
				} else {
					items.push(this.createItem(node));
				}
				return items;
			},
			createItem : function(node) {
				var item = {};
				item["checkupProjectId"] = node.attributes["checkupProjectId"];
				item["checkupProjectName"] = node.attributes["checkupProjectName"];
				item["checkupUnit"] = node.attributes["checkupUnit"];
				item["referenceUpper"] = node.attributes["referenceUpper"];
				item["referenceLower"] = node.attributes["referenceLower"];
				item["projectOffice"] = node.attributes["projectOffice"];
				item["projectOfficeCode"] = node.attributes["projectOfficeCode"];
				item["projectType"] = node.attributes["projectType"];
				item["projectClass"] = node.attributes["projectClass"];
				item["memo"] = node.attributes["memo"];
				// item["mustNot"] = node.attributes["mustNot"];
				return item;
			},
			filterDicTree : function(node) {
				if (!node || !this.checkupType) {
					return true;
				}
				var key = node.attributes["key"];
				if (key != this.checkupType && node.attributes["type"] == "tc") {
					return false;
				} else {
					if (!node.isExpanded()) {
						node.expand();
					}
					return true;
				}
			},
			// 从服务器加载该体检记录对应的检查类型、科室检查、项目明细。
			loadData : function() {
				if (this.ehrStatus == '1') {
					this.form.form.disable();
				} else {
					this.form.form.enable();
				}
				this.list.store.removeAll();
				if(this.list.dicItems){
					this.list.dicItems = null;
				}
				Ext.apply(this.form.exContext, this.exContext);
				Ext.apply(this.list.exContext, this.exContext);
				this.data = {};

				if (!this.exContext.args.initDataId) {
					return;
				}
				var json = util.rmi.miniJsonRequestSync({
							serviceId : 'chis.checkupRecordService',
							serviceAction : 'queryPerDetailInfo',
							method : "execute",
							checkupNo : this.exContext.args.initDataId
						});

				var code = json.code;
				var msg = json.msg;
				if (code > 300) {
					this.processReturnMsg(code, msg);
					return
				}
				if (!json.json || !json.json.body) {
					this.processReturnMsg("500", "未找到该体检记录！");
					return;
				}
				var body = json.json.body;
				this.checkupType = body["checkupType"];
				this.data = body;
				this.checkupType = this.exContext.args.checkupType;
				// tf = util.dictionary.TreeDicFactory.createDic({
				// id : "perCombo",
				// onlySelectLeaf : "true",
				// parentKey:this.checkupType
				// });
				this.moduleLoadData();
				// 加载权限
				this.form.initDataId = this.exContext.args.initDataId;
				var control = body["_actions"];
				if (!control) {
					return;
				}
				Ext.apply(this.form.exContext.control, control);
				this.form.resetButtons();
			},
			moduleLoadData : function() {
				this.tree.filter.filterBy(this.filterDicTree, this);
				// this.fireEvent("getFirstKSNode", this);
				this.getFirstKSNode();
			},
			getFirstKSNode : function() {
				var root = this.tree.getRootNode();
				for (var i = 0; i < root.childNodes.length; i++) {
					var node = root.childNodes[i];
					if (!node.hidden) {
						if (!this.selectFirstNode(node)) {
							node.on("expand", this.selectFirstNode, this);
						}
					}
				}
			},
			selectFirstNode : function(node) {
				var firstPro = node.firstChild;
				if (firstPro) {
					firstPro.select();
					if (firstPro.childNodes.length > 0) {
						this.loadNode(firstPro);
					} else {
						firstPro.on("load", this.loadNode, this);
					}
					return true;
				} else {
					return false;
				}
			},
			loadNode : function(node) {
				this.tree.fireEvent("click", node, this);
			},
			detailSave : function() {
				this.fireEvent("detailSave", this);
			},
			/*
			 * setData1 : function(data) { var projectOfficeCode =
			 * data["projectOfficeCode"]; var summary = data["summary"]; var
			 * items = data["items"]; this.data["itemList"][projectOfficeCode] =
			 * items; this.data["sumList"][projectOfficeCode]["checkupSummary"] =
			 * summary; },
			 */
			// 设置科室检查项、小结
			setData : function(item, type) {
				//
				var form = this.form.form.getForm();
				var summaryField = form.findField("checkupSummary");
				summaryField.reset();
				var checkupDoctor = form.findField("checkupDoctor");
				var inputDoctor = form.findField("inputDoctor");
				// var ifException = form.findField("ifException");
				// ifException.reset();
				var exceptionDesc = form.findField("exceptionDesc");
				exceptionDesc.reset();
				var projectOfficeCode = item[0]["projectOfficeCode"];
				var summary = this.data["sumList"][projectOfficeCode];
				var rpSummary = true;
				if (summary) {
					// 设置科室小结到form
					if (summary.checkupSummary) {
						summaryField.setValue(summary.checkupSummary);
					}
					if (summary.exceptionDesc) {
						exceptionDesc.setValue(summary.exceptionDesc);
					}
					if (summary.checkupDoctor) {
						checkupDoctor.setValue({
							key : summary.checkupDoctor,
							text : summary.checkupDoctor_text
						});
					}
					if (summary.inputDoctor) {
						inputDoctor.setValue({
									key : summary.inputDoctor,
									text : summary.inputDoctor_text
								});
					}
					// if (summary.ifException)
					// ifException.setValue({
					// key : summary.ifException,
					// text : summary.ifException_text
					// });
					if (summary.checkupOffice == item[0].projectOffice
							&& summary.projectOfficeCode == item[0].projectOfficeCode) {
						rpSummary = false;
					}
				} else {
					summary = {};
					inputDoctor.setValue({
								key : this.mainApp.uid,
								text : this.mainApp.uname
							});
					checkupDoctor.setValue({
								key : this.mainApp.uid,
								text : this.mainApp.uname
					});
					exceptionDesc.reset();
				}
				if (rpSummary) {
					summary.checkupOffice = item[0].projectOffice;
					summary.projectOfficeCode = item[0].projectOfficeCode;
				}
				if (this.projectOfficeCode != projectOfficeCode) {
					this.list.store.removeAll();
					var projectItems = this.data["itemList"][projectOfficeCode];
					// 设置列表项
					if (projectItems) {
						var records = [];
						for (var i = 0; i < projectItems.length; i++) {
							var r = projectItems[i];
							var record = new Ext.data.Record(r,
									r.checkupProjectId);
							records.push(record);
						}
						this.list.store.add(records);
					}
					this.projectOfficeCode = projectOfficeCode;
				}
				this.data["sumList"][this.projectOfficeCode] = summary;
				var itemCount = item.length;
				// 如果未保存过体检 或者点击的是具体的项目 或者点击了以保存数据中不存在的可是目录 才加载项目
				if (type != "ks" || !this.data["itemList"][projectOfficeCode]) {
					var newRecords = []
					for (var j = 0; j < itemCount; j++) {
						var subItem = item[j];
						if (subItem.checkupProjectId
								&& subItem.checkupProjectId.length >= 3) {
							// 如果不存在该项目.添加
							var cpId = subItem.checkupProjectId;
							var idx = this.list.store.getById(cpId);
							if (!idx) {
								var recData = {};
								recData["checkupProjectId"] = subItem["checkupProjectId"];
								recData["checkupProjectName"] = subItem["checkupProjectName"];
								recData["checkupUnit"] = subItem["checkupUnit"];
								recData["referenceUpper"] = subItem["referenceUpper"];
								recData["referenceLower"] = subItem["referenceLower"];
								recData["projectOffice"] = subItem["projectOffice"];
								recData["projectOfficeCode"] = subItem["projectOfficeCode"];
								recData["projectType"] = subItem["projectType"];
								recData["projectClass"] = subItem["projectClass"];
								recData["memo"] = subItem["memo"];
								recData["createUnit"] = this.mainApp.deptId;
								recData["createUnit_text"] = this.mainApp.dept;
								recData["createUser"] = this.mainApp.uid;
								recData["createUser_text"] = this.mainApp.uname;
								recData["createDate"] = this.mainApp.serverDate;
								// recData["mustNot"] = subItem["mustNot"];
								var storeRecord = new Ext.data.Record(recData,
										cpId);
								newRecords.push(storeRecord);
							}
						}
					}
					this.list.store.add(newRecords);
					this.saveChanges('loadData');
				}
				// 定位到该项
				if (itemCount == 1) {
					var sr = new Ext.grid.RowSelectionModel(this.grid
							.getSelectionModel());
					sr.deselectRange(0, this.list.store.getCount());
					var index = this.list.store.find('checkupProjectId',
							item[0].checkupProjectId);
					if (index > -1) {
						sr.selectRow(index);
						this.grid.getView().focusRow(index);
					}
				}
			},
			// 从FROM生成科室小结记录.
			getSummaryRecord : function() {
				var form = this.form.form.getForm();
				var summary = form.findField("checkupSummary").getValue();
				// var excDesc = form.findField("exceptionDesc").getValue();
				var doctor = form.findField("inputDoctor").getValue();
				var doctor_text = form.findField("inputDoctor").getRawValue();
				var check = form.findField("checkupDoctor").getValue();
				var check_text = form.findField("checkupDoctor").getRawValue();
				// var ifException = form.findField("ifException").getValue();
				// var ifException_text =
				// form.findField("ifException").getRawValue();
				this.data["sumList"][this.projectOfficeCode].checkupSummary = summary;
				// this.data["sumList"][this.projectOfficeCode].exceptionDesc =
				// excDesc;
				this.data["sumList"][this.projectOfficeCode].checkupDoctor = check;
				this.data["sumList"][this.projectOfficeCode].checkupDoctor_text = check_text;
				this.data["sumList"][this.projectOfficeCode].inputDoctor = doctor;
				this.data["sumList"][this.projectOfficeCode].inputDoctor_text = doctor_text;
				// this.data["sumList"][this.projectOfficeCode].ifException =
				// ifException;
				// this.data["sumList"][this.projectOfficeCode].ifException_text
				// = ifException_text;
				return this.data["sumList"][this.projectOfficeCode];
			},
			saveChanges : function(dataOp) {
				var subItems = [];
				for (var i = 0; i < this.list.store.data.length; i++) {
					var storeItemData = this.list.store.getAt(i).data;
					subItems.push(storeItemData);
					/*
					 * if(dataOp && dataOp == "loadData"){ continue; }
					 * if(storeItemData.mustNot=='1'){ var ifException =
					 * storeItemData.ifException; if(!ifException ||
					 * ifException==""){
					 * Ext.Msg.alert("提示信息",storeItemData.checkupProjectName+"体检结果不能为空！");
					 * return true; } if(typeof(ifException)=="string"){
					 * if(ifException.trim().length == 0){
					 * Ext.Msg.alert("提示信息",storeItemData.checkupProjectName+"体检结果不能为空！");
					 * return true; } } }
					 */
				}
				if (!this.projectOfficeCode) {
					Ext.Msg.alert("提示", "请先为该体检套餐增加体检明细！");
					return true;
				}
				this.data["itemList"][this.projectOfficeCode] = subItems;
				this.data["sumList"][this.projectOfficeCode] = this
						.getSummaryRecord();
				return false;
			},
			onFormSave : function() {
				var rs = this.saveChanges();
				if (rs) {
					return;
				}
				var body = {};
				body["empiId"] = this.exContext.args.empiId;
				body["checkupNo"] = this.exContext.args.checkupNo;
				body["projectOfficeCode"] = this.projectOfficeCode;
				body["phrId"] = this.exContext.args.phrId;
				body["itemList"] = this.data["itemList"][this.projectOfficeCode];
				body["sumList"] = this.data["sumList"][this.projectOfficeCode];
				this.panel.el.mask("正在保存数据...", "x-mask-loading");
				util.rmi.jsonRequest({
							serviceId : 'chis.checkupRecordService',
							serviceAction : 'savePerDetailInfo',
							method : "execute",
							body : body
						}, function(code, msg, json) {
							this.panel.el.unmask();
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							} else {
								this.fireEvent("detailSave",
										json.body.sumupException);
								this.initForm(json);
							}
						}, this);
			},
			initForm : function(json) {
				var ExcDes = json.body.ExcDes;
				this.form.form.getForm().findField("exceptionDesc")
						.setValue(ExcDes);
				this.data["sumList"][this.projectOfficeCode].exceptionDesc = ExcDes;
				// this.list.store.removeAll();
				// for(var i=0;i<ExcDes.length;i++){
				// for(var key in ExcDes[i]){
				// this.data["sumList"][key].exceptionDesc = ExcDes[i][key];
				// if(key == this.projectOfficeCode){
				// this.form.form.getForm().findField("exceptionDesc").setValue(ExcDes[i][key]);
				// }
				// }
				// }
			},

			changeValue : function(key,value) {
				this.form.form.getForm().findField("checkupDoctor")
						.setValue({key:key,text:value});
				this.data["sumList"][this.projectOfficeCode].checkupDoctor = key;
				this.data["sumList"][this.projectOfficeCode].checkupDoctor_text = value;
			},

			onListDelRow : function(delRecord) {
				var delCheckupProjectId = delRecord.data.checkupProjectId;
				var projectItems = this.data["itemList"][this.projectOfficeCode];
				if (projectItems) {
					for (var i = 0; i < projectItems.length; i++) {
						var item = projectItems[i];
						var checkupProjectId = item.checkupProjectId;
						if (checkupProjectId == delCheckupProjectId) {
							this.data["itemList"][this.projectOfficeCode]
									.splice(i, i + 1);
							if (this.data["itemList"][this.projectOfficeCode].length == 0) {
								this.data["itemList"][this.projectOfficeCode] = null;
							}
							break;
						}
					}
				}
			}
		});