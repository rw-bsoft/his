/**
 * 简单诊断录入
 * 
 * @type
 */
phis.application.cic.script.ClinicHealthEducationInputModule = {
	/**
	 * 设置健康教育处方
	 * 
	 * @param {}
	 *            obj
	 * @param {}
	 *            record
	 */
	setHealthEduInfo : function(obj, record) {
		if(!this.JKCFRecords) {
			this.JKCFRecords = {};
		}
		var oldData = this.JKCFRecords[record.get("DIAGNOSEID")];
		if(!oldData) {
			oldData = {};
		}
		var data = {};
		data['recordId'] = record.get("RECORDID");
		data['recipeName'] = record.get("RECIPENAME");
		data['diagnoseName'] = record.get("DIAGNOSENAME")
		data['diagnoseId'] = record.get("DIAGNOSEID")
		data['ICD10'] = record.get("ICD10")
		// data['healthTeach'] = record.get("healthTeach")
		data['recordId'] = record.get("RECORDID")
		var content = Ext.getCmp("JKJY").getValue();
		content = content + record.get("HEALTHTEACH");
		Ext.apply(oldData,data);
		this.JKCFRecords[record.get("DIAGNOSEID")] = oldData;
		Ext.getCmp("JKJY").setValue(content);
		obj.setValue("");
		obj.collapse();
		obj.triggerBlur();
		Ext.getCmp("JKJY").focus(100);
	},
	/**
	 * 创建健康教育快捷录入字段
	 * 
	 * @return {}
	 */
	createHealthEduField : function() {
		var mds_reader = this.getHealthEduReader();
		// store远程url
		// var url = "http://127.0.0.1:8080/BS-PHIS/" + this.remoteUrl
		var url = ClassLoader.serverAppUrl || "";
		var comboJsonData = {
			serviceId : "phis.searchService",
			serviceAction : "loadDicData",
			method : "execute",
			className : "HealthEducation"
			// ,pageSize : this.pageSize || 25,
			// pageNo : 1
		}
		var proxy = new Ext.data.HttpProxy({
					url : url + '*.jsonRequest',
					method : 'POST',
					jsonData : comboJsonData
				});
		var mdsstore = new Ext.data.Store({
					proxy : proxy,
					reader : mds_reader
				});
		proxy.on("loadexception", function(proxy, o, response, arg, e) {
					if (response.status == 200) {
						var json = eval("(" + response.responseText + ")")
						if (json) {
							var code = json["code"]
							var msg = json["msg"]
							MyMessageTip.msg("提示", msg, true)
						}
					} else {
						MyMessageTip.msg("提示", "貌似网络不是很给力~请重新尝试!", true)
					}
				}, this)
		// this.remoteDicStore = mdsstore;
		Ext.apply(mdsstore.baseParams, this.queryParams);
		var resultTpl = new Ext.XTemplate(
				'<tpl for=".">',
				'<div class="search-item">',
				'<table cellpadding="0" cellspacing="0" border="0" class="search-item-table">',
				'<tr>' + this.healthEduTpl + '</tr>', '</table>', '</div>',
				'</tpl>');
		var _ctx = this;
		var remoteField = new Ext.form.ComboBox({
					name : "healthEdu",
					width : 140,
					store : mdsstore,
					selectOnFocus : true,
					selectedClass : this.selectedClass||'x-remoteField-selected',
					typeAhead : false,
					loadingText : '搜索中...',
					pageSize : 10,
					hideTrigger : true,
					minListWidth : this.minListWidth || 360,
					tpl : resultTpl,
					minChars : 2,
					enableKeyEvents : true,
					// style : "border-style:none;background-image:none;",
					lazyInit : false,
					// hiddenName : ("ZDXH_" + r.JLBH),
					// hiddenValue : r.ZDXH,
					itemSelector : 'div.search-item',
					// renderTo : ("DIV_ZDLR_" + r.JLBH),
					onSelect : function(record) { // override default onSelect
						// to do
						this.bySelect = true;
						_ctx.setHealthEduInfo(this, record);
						// this.hasFocus = false;// add by yangl 2013.9.4
						// 解决新增行搜索时重复调用setBack问题
					}
				});
		remoteField.render("div_healthEduInput")
		remoteField.on("focus", function() {
					remoteField.innerList.setStyle('overflow-y', 'hidden');
				}, this);
		remoteField.on("keyup", function(obj, e) {// 实现数字键导航
					var key = e.getKey();
					if (key == e.ENTER && !obj.isExpanded()) {
						// 是否是字母
						if (key == e.ENTER) {
							if (!obj.isExpanded()) {
								// 是否是字母
								var patrn = /^[a-zA-Z.]+$/;
								if (patrn.exec(obj.getValue())) {
									// 防止查询不出数据或者按回车速度过快导致上次输入结果直接被调入
									obj.getStore().removeAll();
									obj.lastQuery = "";
									if (obj.doQuery(obj.getValue(), true) !== false) {
										e.stopEvent();
										return;
									}
								}
							}
							_ctx.focusFieldAfter(obj.index);
							return;
						}
						var patrn = /^[a-zA-Z.]+$/;
						if (patrn.exec(obj.getValue())) {
							// 防止查询不出数据或者按回车速度过快导致上次输入结果直接被调入
							obj.getStore().removeAll();
							obj.lastQuery = "";
							if (obj.doQuery(obj.getValue(), true) !== false) {
								e.stopEvent();
								return;
							}
						}
					}
					if ((key >= 48 && key <= 57) || (key >= 96 && key <= 105)) {
						var searchTypeValue = _ctx.cookie
								.getCookie(_ctx.mainApp.uid + "_searchType");
						if (searchTypeValue != 'BHDM') {
							if (obj.isExpanded()) {
								if (key == 48 || key == 96)
									key = key + 10;
								key = key < 59 ? key - 49 : key - 97;
								var record = this.getStore().getAt(key);
								obj.bySelect = true;
								_ctx.setHealthEduInfo(obj, record);
							}
						}
					}
					// 支持翻页
					if (key == 37) {
						obj.pageTb.movePrevious();
					} else if (key == 39) {
						obj.pageTb.moveNext();
					}
					// 删除事件 8
					if (key == 8) {
						if (obj.getValue().trim().length == 0) {
							if (obj.isExpanded()) {
								obj.collapse();
							}
						}
					}
				})
		if (remoteField.store) {
			remoteField.store.load = function(options) {
				Ext.apply(comboJsonData, options.params);
				Ext.apply(comboJsonData, mdsstore.baseParams);
				options = Ext.apply({}, options);
				this.storeOptions(options);
				if (this.sortInfo && this.remoteSort) {
					var pn = this.paramNames;
					options.params = Ext.apply({}, options.params);
					options.params[pn.sort] = this.sortInfo.field;
					options.params[pn.dir] = this.sortInfo.direction;
				}
				try {
					return this.execute('read', null, options); // <-- null
					// represents
					// rs. No rs for
					// load actions.
				} catch (e) {
					this.handleException(e);
					return false;
				}
			}
		}
		remoteField.isSearchField = true;
		return remoteField;
	},
	getHealthEduReader : function() {
		return new Ext.data.JsonReader({
					root : 'healthEdu',
					totalProperty : 'count'
				}, [{
							name : 'numKey'
						}, {
							name : 'DIAGNOSEID'
						}, {
							name : 'RECIPENAME'
						}, {
							name : 'DIAGNOSENAME'

						}, {
							name : 'ICD10'

						}, {
							name : 'HEALTHTEACH'

						}, {
							name : 'RECIPENAMEPY'

						}]);
	}

}