$package("phis.application.war.script")
$import("phis.script.SimpleList", "phis.script.rmi.jsonRequest")

phis.application.war.script.MedicalConditionList = function(cfg) {
	cfg.showRowNumber = false;
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	cfg.autoLoadSchema = false;
	phis.application.war.script.MedicalConditionList.superclass.constructor.apply(
			this, [cfg]);
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.war.script.MedicalConditionList,
		phis.script.SimpleList, {
			onWinShow : function() {
				this.requestData.serviceId = "phis.medicalRecordsQueryService";
				this.requestData.serviceAction = "listAllConditions";
				this.loadData();
			},
			onDblClick : function() {
				var record=this.getSelectedRecord();
				if(!record){
					return;
				}
				this.fireEvent("listDblClick",record,this.schema);
			},

			

			clickImgToLogout : function(grid, rowIndex, colIndex) {
				this.store = grid.getStore();
				var record = this.store.getAt(rowIndex);
				if (record == null) {
					return;
				}
				Ext.Msg.show({
					title : '确认删除查询条件[' + record.get("TJMC") + ']',
					msg : '删除操作将无法恢复，是否继续?',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							phis.script.rmi.jsonRequest({
										serviceId : "medicalRecordsQueryService",
										serviceAction : "removeMedicalCondition",
										pkey : record.id
									}, function(code, msg, json) {
										if (code < 300) {
											this.store.remove(record);
										} else {
											Ext.MessageBox.alert("错误", msg)
										}
									}, this)
						}
					},
					scope : this
				})

			},

			getCM : function(items) {
				var cm = []
				var ac = util.Accredit;
				var expands = []
				if (this.showRowNumber) {
					cm.push(new Ext.grid.RowNumberer())
				}
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if ((it.display <= 0 || it.display == 2)
							|| !ac.canRead(it.acValue)) {
						continue
					}
					if (it.expand) {
						var expand = {
							id : it.dic ? it.id + "_text" : it.id,
							alias : it.alias,
							xtype : it.xtype
						}
						expands.push(expand)
						continue
					}
					if (!this.fireEvent("onGetCM", it)) { // **
						// fire一个事件，在此处可以进行其他判断，比如跳过某个字段
						continue;
					}
					var width = parseInt(it.width || 80)
					// if(width < 80){width = 80}
					var c = {
						header : it.alias,
						width : width,
						sortable : this.sortable,// add by yangl 增加是否启用排序参数
						dataIndex : it.dic ? it.id + "_text" : it.id
					}
					if (!this.isCompositeKey && it.pkey == "true") {
						c.id = it.id
					}
					if (it.summaryType) {
						c.summaryType = it.summaryType;
					}
					switch (it.type) {
						case 'int' :
						case 'double' :
						case 'bigDecimal' :
							if (!it.dic) {
								c.css = "color:#00AA00;font-weight:bold;"
								c.align = "right"
								if (it.type == 'double'
										|| it.type == 'bigDecimal') {
									c.precision = it.precision;
									c.nullToValue = it.nullToValue;
									c.renderer = function(value, metaData, r,
											row, col, store) {
										if (value == null && this.nullToValue) {
											value = parseFloat(this.nullToValue)
											var retValue = this.precision
													? value
															.toFixed(this.precision)
													: value;
											try {
												r.set(this.id, retValue);
											} catch (e) {
												// 防止新增行报错
											}
											return retValue;
										}
										if (value != null) {
											value = parseFloat(value);
											var retValue = this.precision
													? value
															.toFixed(this.precision)
													: value;
											return retValue;
										}
									}
								}
							}
							break
						case 'timestamp' :
							// c.renderer = Ext.util.Format.dateRenderer('Y-m-d
							// HH:m:s')
					}
					if (it.renderer) {
						var func
						func = eval("this." + it.renderer)
						if (typeof func == 'function') {
							c.renderer = func
						}
					}
					if (it.summaryType) {
						c.summaryType = it.summaryType;
						if (it.summaryRenderer) {
							var func = eval("this." + it.summaryRenderer)
							if (typeof func == 'function') {
								c.summaryRenderer = func
							}
						}
					}
					if (this.fireEvent("addfield", c, it)) {
						cm.push(c)
					}
				}
				if (expands.length > 0) {
					this.rowExpander = this.getExpander(expands)
					cm = [this.rowExpander].concat(cm)
					this.array.push(this.rowExpander)// add by taoy
				}
				this.clm = {
					xtype : 'actioncolumn',
					width : 35,
					header : "删除",
					items : [{
								icon : 'resources/phis/resources/images/no.png',
								tooltip : '删除',
								handler : this.clickImgToLogout
							}]
				}
				cm.push(this.clm);
				return cm
			}

		})