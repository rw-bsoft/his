$package("chis.application.conf.script.admin")
$import("util.Accredit")
$import("chis.script.app.modules.form.TableFormView")
chis.application.conf.script.admin.EHR_CommunityPepBasicSituationForm = function(cfg) {
	cfg.colCount = 3
	cfg.autoFieldWidth = false
	cfg.labelWidth = 106
	cfg.labelAlign  = "right"
	cfg.fldDefaultWidth = 140
	cfg.autoScroll = true;
	chis.application.conf.script.admin.EHR_CommunityPepBasicSituationForm.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(chis.application.conf.script.admin.EHR_CommunityPepBasicSituationForm,
		chis.script.app.modules.form.TableFormView, {
		loadData : function() {
				if (!this.initDataId){
					//this.doNew();
					var result = util.rmi.miniJsonRequestSync({
								serviceId : "chis.simpleQuery",
								schema : this.entryName,
								cnd : ['eq', ['$', 'createUnit'],
										['s',  this.mainApp.deptId]]
							})

					if (result.json.totalCount > 0) {
						this.initDataId = result.json.body[0].RecordID;
						this.op = "update";
					}else{
						this.op = "create";
					}
				}
				chis.application.conf.script.admin.EHR_CommunityPepBasicSituationForm.superclass.loadData.call(this)	
			},doNew : function() {
				chis.application.conf.script.admin.EHR_CommunityPepBasicSituationForm.superclass.doNew.call(this)
//				this.setWidgetStatus()
			},
			createField : function(it) {
			var ac = util.Accredit;
			var defaultWidth = this.fldDefaultWidth || 200
			var cfg = {
				name : it.id,
				fieldLabel : it.alias,
				xtype : it.xtype || "textfield",
				vtype : it.vtype,
				width : defaultWidth,
				value : it.defaultValue,
				enableKeyEvents : it.enableKeyEvents,
				validationEvent : it.validationEvent
			}
			cfg.listeners = {
				specialkey : this.onFieldSpecialkey,
				scope : this
			}
			if (it.inputType) {
				cfg.inputType = it.inputType
			}
			//added by lic 2012-08-07
			if (it.readOnly == true) {
				cfg.readOnly = true
			}
			if (it['not-null']) {
				cfg.allowBlank = false
				cfg.invalidText = "必填字段"
				cfg.fieldLabel = "<span style='color:red'>" + cfg.fieldLabel
						+ "</span>"
			}
			if (it['showRed']) {
				cfg.fieldLabel = "<span style='color:red'>" + cfg.fieldLabel
						+ "</span>"
			}
			if(it['qitTitle']){
				cfg.fieldLabel = "<span title=" + it['qitTitle']+">" + cfg.fieldLabel
						+ "</span>"
			}
			if (it.fixed || it.fixed) {
				cfg.disabled = true
			}
			if (it.pkey && it.generator == 'auto') {
				cfg.disabled = true
			}
			if (it.evalOnServer && ac.canRead(it.acValue)) {
				cfg.disabled = true
			}
			if (this.op == "create" && !ac.canCreate(it.acValue)) {
				cfg.disabled = true
			}
			if (this.op == "update" && !ac.canUpdate(it.acValue)) {
				cfg.disabled = true
			}
			if (it.dic) {
				it.dic.src = this.entryName + "." + it.id
				it.dic.defaultValue = it.defaultValue
				it.dic.width = defaultWidth
				var combox = this.createDicField(it.dic)
				Ext.apply(combox, cfg)
				combox.on("specialkey", this.onFieldSpecialkey, this)
				return combox;
			}
			if (it.length) {
				cfg.maxLength = it.length;
			}
			if (it.xtype) {
				if(it.xtype=='htmleditor' && it.height > 0)
					cfg.height =it.height;
				return cfg;
			}
			switch (it.type) {
				case 'int' :
				case 'double' :
				case 'bigDecimal' :
					cfg.xtype = "numberfield"
					if (it.type == 'int') {
						cfg.decimalPrecision = 0;
						cfg.allowDecimals = false
					} else {
						cfg.decimalPrecision = it.precision || 2;
					}
					if (it.minValue) {
						cfg.minValue = it.minValue;
					}
					if (it.maxValue) {
						cfg.maxValue = it.maxValue;
					}
					break;
				case 'date' :
					cfg.xtype = 'datefield'
					cfg.emptyText = "请选择日期"
					break;
				case 'text' :
					cfg.xtype = "htmleditor"
					cfg.enableSourceEdit = false
					cfg.enableLinks = false
					cfg.width = 300
					break;
				//add by chzhxiang 2011.12.1
				case 'timestamp' :
					cfg.xtype = 'datetimefield'
					cfg.emptyText = "请选择日期时间"
					break;
			}
			return cfg;
		}
		});