$package("chis.script.gis")
$import("app.desktop.Module", "util.widgets.MyRadioGroup","chis.script.util.helper.Helper",
		"util.dictionary.TreeDicFactory")

chis.script.gis.simpleResource = function(cfg) {
	this.width = this.width
	this.height = 500
	this.manageUnitField = cfg.manageUnitField || "manaUnitId"
	this.entryName = "PUB_Resource"
	this.formData = []
	this.season = [{
				value : 1,
				text : "第一季度"
			}, {
				value : 2,
				text : "第二季度"
			}, {
				value : 3,
				text : "第三季度"
			}, {
				value : 4,
				text : "第四季度"
			}]
	this.month = [{
				value : 1,
				text : "一月"
			}, {
				value : 2,
				text : "二月"
			}, {
				value : 3,
				text : "三月"
			}, {
				value : 4,
				text : "四月"
			}, {
				value : 5,
				text : "五月"
			}, {
				value : 6,
				text : "六月"
			}, {
				value : 7,
				text : "七月"
			}, {
				value : 8,
				text : "八月"
			}, {
				value : 9,
				text : "九月"
			}, {
				value : 10,
				text : "十月"
			}, {
				value : 11,
				text : "十一月"
			}, {
				value : 12,
				text : "十二月"
			}]
	this.conditions = []
	this.printurl = chis.script.util.helper.Helper.getUrl()
	chis.script.gis.simpleResource.superclass.constructor.apply(this, [cfg])
}
Ext.extend(chis.script.gis.simpleResource, app.desktop.Module, {
			initPanel : function() {
				this.conditionFormId = "simpleResource_form_" + this.printId
				this.mainFormId = "simpleResource_mainform_" + this.printId
				var panel = new Ext.Panel({
							id : this.mainFormId,
							frame : true,
							autoScroll:true,
							width : this.width,
							height : this.height,
							title : this.title,
							items : this.initConditionFields(this.conditions)
						})
				this.panel = panel
				return panel
			},
			initConditionFields : function(conditions) {
				var item = []
				for (var x = 0; x < conditions.length; x++) {
					var items = []
					var cons = conditions[x];
					var title = "";
					for (var i = 0; i < cons.length; i++) {
						var con = cons[i]
						if (con["title"]) {
							title = con["title"];
						}
						if (con["id"] == "year") {
							items.push(this.createCommonDic("year"))
						} else if (con["id"] == "season") {
							items.push(this.createCommonDic("season"))
						} else if (con["id"] == "month") {
							items.push(this.createCommonDic("month"))
						} else if (con["id"] == "manageUnit") {
							items.push(this.createManageUnitDic(con))
						} else if (con["id"] == "areaGrid") {
							items.push(this.createAreaDic(con))
						}else if(con["key"]!=null){
						items.push({xtype:"textfield",
						name:'key',
						hidden:true,
						value:con["key"]})
						}
					}
					items.push({
								xtype : "textfield",
								fieldLabel : '录入值',
								name : 'text',
								allowBlank : false,
								width : 120
							})
					var panel = new Ext.FormPanel(
							{
								id : title,
								frame : false,
								title : title,
								items : items,
								border : true,
								bodyStyle : {
									margin : 5
								}
							}
							)
					this.formData.push(panel.getForm())
					item.push(panel)
				}
				item.push({
							xtype : "button",
							text : "保存",
							iconCls : "archiveMove_commit",
							scope : this,
							width : 80,
							handler : this.doSetResource
						})
				return item
			},

			saveToServer : function(saveDatas) {

				Ext.getCmp(this.mainFormId).el.mask("正在保存数据...",
						"x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : "chis.ResourceService",
							schema : this.entryName,
							body : saveDatas
						}, function(code, msg, json) {
							Ext.getCmp(this.mainFormId).el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveDatas]);
								return
							}
							Ext.apply(this.data, saveDatas);
							this.getWin().hide()
							// this.fireEvent("save", this.entryName, this.op,
						// json, this.data)
					}, this)// jsonRequest
			},

			doSetResource : function() {
//				var form = Ext.getCmp(this.mainFormId).getForm();
//				if (!form.isValid()) {
//					return
//				}
				var setDatas = [];
				var dd = this.formData;
				for (var j = 0; j < dd.length; j++) {
					var p = dd[j]
					var items = p.items;
					var setData = {};
					for (var i = 0; i < items.getCount(); i++) {
						var f = items.get(i)
						setData[f.getName()] = f.getValue();
					}
					setData["id"] = this.printId;
					setDatas.push(setData)
				}
				this.saveToServer({datas:setDatas})
			},
			resetList : function(navField, node) {
				var m = new app.modules.list.SimpleListView
				m.setMainApp(this.mainApp)
				var p = m.initPanel();
				this.items.push(p)
			},
			createAreaDic : function(con) {
				var parentKey = ""
				var limit = con.lengthLimit || 100 // 字典KEY长度的限制
				if (this.mainApp && this.mainApp.regionCode) {
					parentKey = this.mainApp.regionCode
					if (parentKey.length > limit) {
						parentKey = parentKey.substring(0, limit)
					}
				}
				var dic = util.dictionary.TreeDicFactory.createDic({
							id : "areaGrid",
							parentKey : parentKey == ""
									? "undefined key"
									: parentKey,
							rootVisible : true
						})
				dic.emptyText = "网格地址"
				dic.name = "areaGrid"
				dic.fieldLabel = "网格地址"
				dic.allowBlank = false
				dic.tree.on("load", function(node) {
							node.getOwnerTree().filter.filterBy(function(n) {
										var flag = true
										if (n.id.length > limit) {
											flag = false
										}
										return flag
									}, this)
						}, this)
				return dic
			},
			createManageUnitDic : function(con) {
				var parentKey = ""
				var limit = con.lengthLimit || 100 // 字典KEY长度的限制
				if (this.mainApp && this.mainApp.deptId) {
					parentKey = this.mainApp.deptId
					if (parentKey.length > limit) {
						parentKey = parentKey.substring(0, limit)
					}
				}
				var dic = util.dictionary.TreeDicFactory.createDic({
							id : "manageUnit",
							parentKey : parentKey == ""
									? "undefined key"
									: parentKey,
							rootVisible : true
						})
				dic.text = "填报单位"
				dic.emptyText = "填报单位"
				dic.name = "manageUnit"
				dic.fieldLabel = "填报单位"
				dic.allowBlank = false
				dic.tree.on("load", function(node) {
							node.getOwnerTree().filter.filterBy(function(n) {
										var flag = true
										if (n.id.length > limit) {
											flag = false
										}
										return flag
									}, this)
						}, this)
				return dic
			},
			createCommonDic : function(flag) {
				var fields
				var emptyText = "请选择"
				if (flag == "season") {
					fields = this.season
					emptyText = "季度"
				} else if (flag == "month") {
					fields = this.month
					emptyText = "月份"
				} else if (flag == "year") {
					fields = []
					var currentYear = new Date().getFullYear()
					while (true) {
						fields.push({
									value : currentYear,
									text : currentYear + "年"
								})
						if (currentYear == 2006) {
							break
						}
						currentYear--
					}
					emptyText = "年度"
				} else if (flag == "type") {
					fields = this.preview
					emptyText = "预览方式"
				} else {
					fields = []
					flag = ""
				}
				var store = new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : fields
						});
				var combox = new Ext.form.ComboBox({
							store : store,
							valueField : "value",
							displayField : "text",
							mode : 'local',
							triggerAction : 'all',
							emptyText : emptyText,
							selectOnFocus : true,
							width : 100,
							name : flag,
							fieldLabel : emptyText,
							allowBlank : false
						})
				return combox
			}
		})

simplePrintMask = function(printId) {
	Ext.getCmp("simpleResource_mainform_" + printId).el.unmask()
}