$package("chis.application.hc.script")

$import("chis.script.BizSimpleListView", "chis.script.EHRView")

chis.application.hc.script.HealthCheckRepeatRecord = function(cfg) {
	cfg.autoLoadData=false;
	cfg.needOwnerBar = true;
//	cfg.serviceId="chis.healthCheckService";
//    cfg.serviceAction="loadRepeatRecord";
	chis.application.hc.script.HealthCheckRepeatRecord.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.hc.script.HealthCheckRepeatRecord, chis.script.BizSimpleListView, {
			createOwnerBar : function() {
				var yearLabel = new Ext.form.Label({
						html : "年度:",
						width : 80
					});
				var yearstore = new Ext.data.ArrayStore({
					fields: ['id', 'year'],
					data: [["2015","2015"],["2016","2016"],["2017","2017"],
						["2018","2018"],["2019","2019"],["2020","2020"]]
					});
				var yearComboBox = new Ext.form.ComboBox({
						id:"hc_repeat_year",
						width : 100,
						fieldLabel: '年度:',
						store: yearstore,
						displayField: 'year',
						valueField: 'id',
						triggerAction: 'all',
						emptyText: this.mainApp.serverDate.substring(0,4),
						allowBlank: false,
						blankText: '请选择',
						editable: false,
						mode: 'local'
					});				 
				var idcardLabel= new Ext.form.Label({
						html : "身份证号:",
						width : 80
					});
				var idcardField= new Ext.form.TextField({
						id:"hc_repeat_idcard",
						width:180,
						disabled:false
					});
				return [yearLabel,yearComboBox,idcardLabel,idcardField]
			},
	getCndBar : function(items) {
		var con={  
            text:'打印',
            handler:this.doPrint,
            iconCls:'print',
            scope : this
        }
        var printbtn = new Ext.Button(con);
        
		var queryBtn = new Ext.Toolbar.SplitButton({
					iconCls : "query",
					menu : new Ext.menu.Menu({
								items : {
									text : "高级查询",
									iconCls : "common_query",
									handler : this.doAdvancedQuery,
									scope : this
								}
							})
				})
		this.queryBtn = queryBtn;
		queryBtn.on("click", this.doCndQuery, this);
		if (this.needOwnerBar) {
			var fields = this.createOwnerBar();
			return [fields, '-', queryBtn,'-',printbtn]
		} else {
			var fields = [];
			if (!this.enableCnd) {
				return []
			}
			for (var i = 0; i < items.length; i++) {
				var it = items[i]
				if (!(it.queryable)) {
					continue
				}
				fields.push({
							value : it.id,
							text : it.alias
						})
			}
			if (fields.length == 0) {
				return fields;
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
						emptyText : '选择查询字段',
						selectOnFocus : true,
						editable : false,
						width : this.queryComboBoxWidth || 120
					});
			combox.on("select", this.onCndFieldSelect, this)
			this.cndFldCombox = combox
			var cndField = new Ext.form.TextField({
						width : this.cndFieldWidth || 200,
						selectOnFocus : true,
						name : "dftcndfld"
					})
			this.cndField = cndField
			return [combox, '-', cndField, '-', queryBtn]
		}

	},			
	getOwnerCnd:function(){
				var year=document.getElementById("hc_repeat_year").value;
				if(!year || year.value=="请选择"){
					MyMessageTip.msg("提示", "请选择年份", true);	
					return;
				}
				var idcard=document.getElementById("hc_repeat_idcard").value;
				var cnd = ['eq', ['$', 'a.year'],['s',year]];
				var cnd1=['like', ['$', 'a.idCard'],['s',"%"+idcard+"%"]];
				cnd = ['and', cnd1, cnd];
				this.initCnd=cnd;
				return cnd;
			},
			onDblClick: function() {
				var r = this.grid.getSelectionModel().getSelected();
				var idcard = r.get("IDCARD");
			}
});