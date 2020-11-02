/**
 * ҩ��ߵʹ���ʾ
 * 
 * @author caijy
 */
$package("phis.application.pha.script");

$import("phis.script.SimpleList");

phis.application.pha.script.PharmacyGDCList = function(cfg) {
	phis.application.pha.script.PharmacyGDCList.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.pha.script.PharmacyGDCList,
		phis.script.SimpleList, {
	loadData:function(){
	var f = this.cndField;
	var v = f.getValue()==null?"":f.getValue();
	this.requestData.PYDM=v.toUpperCase();
	this.requestData.serviceId=this.serviceId;
	this.requestData.serviceAction=this.serviceAction;
	phis.application.pha.script.PharmacyGDCList.superclass.loadData.call(this);
	},
	doQuery:function(){
	this.loadData();
	},
	onRenderer_gc : function(value, metaData, r) {
				if (r.data.KCSL >value) {
					return "<font color='red'>"+value+"</font>";
				}
				return value;
			},
	onRenderer_dc : function(value, metaData, r) {
				if (r.data.KCSL <value) {
					return "<font color='red'>"+value+"</font>";
				}
				return value;
			},
			doPrint:function(){
				var f = this.cndField;
				var v = f.getValue()==null?"":f.getValue();
			var url = "resources/phis.prints.jrxml.PharmacyGDC.print?type=1&PYDM="+v.toUpperCase();
			var LODOP = getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
				LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi.loadXML(
								{
									url : url,
									httpMethod : "get"
								}));
				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
				LODOP.PREVIEW();
			},
			//重写为了去掉高级查询(zww测出的)
		getCndBar : function(items) {
		var fields = [];
		if (!this.enableCnd) {
			return []
		}
		var selected = null;
		var defaultItem = null;
		for (var i = 0; i < items.length; i++) {
			var it = items[i]
			if (!it.queryable || it.queryable == 'false') {
				continue
			}
			if (it.selected == "true") {
				selected = it.id;
				defaultItem = it;
			}
			fields.push({
						// change "i" to "it.id"
						value : it.id,
						text : it.alias
					})
		}
		if (fields.length == 0) {
			return [];
		}
		var store = new Ext.data.JsonStore({
					fields : ['value', 'text'],
					data : fields
				});
		var combox = null;
		if (fields.length > 1) {
			combox = new Ext.form.ComboBox({
						store : store,
						valueField : "value",
						displayField : "text",
						value : selected,
						mode : 'local',
						triggerAction : 'all',
						emptyText : '选择查询字段',
						selectOnFocus : true,
						width : 100
					});
			combox.on("select", this.onCndFieldSelect, this)
			this.cndFldCombox = combox
		} else {
			combox = new Ext.form.Label({
						text : fields[0].text
					});
			this.cndFldCombox = new Ext.form.Hidden({
						value : fields[0].value
					});
		}

		var cndField;
		if (defaultItem) {
			if (defaultItem.dic) {
				defaultItem.dic.src = this.entryName + "." + it.id
				defaultItem.dic.defaultValue = defaultItem.defaultValue
				defaultItem.dic.width = 150
				cndField = this.createDicField(defaultItem.dic)
			} else {
				cndField = this.createNormalField(defaultItem)
			}
		} else {
			cndField = new Ext.form.TextField({
						width : 150,
						selectOnFocus : true,
						name : "dftcndfld"
					})
		}
		this.cndField = cndField
		cndField.on("specialkey", this.onQueryFieldEnter, this)
		var queryBtn = new Ext.Toolbar.Button({
					text : '',
					iconCls : "query",
					notReadOnly : true
				})
		this.queryBtn = queryBtn
		queryBtn.on("click", this.doCndQuery, this);
		return [combox, '-', cndField, '-', queryBtn]
	}
});