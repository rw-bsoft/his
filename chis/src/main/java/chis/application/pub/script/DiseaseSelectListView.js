/**
 * @author : yaozh
 */
$package("chis.application.pub.script")
$import("chis.script.BizSelectListView")
chis.application.pub.script.DiseaseSelectListView = function(cfg) {
	this.cfg = cfg;
	chis.application.pub.script.DiseaseSelectListView.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(chis.application.pub.script.DiseaseSelectListView,
		chis.script.BizSelectListView, {
			doCndQuery:function(){
		var initCnd = this.initCnd
		var itid = this.cndFldCombox.getValue()
		var items = this.schema.items
		var it
		for (var i = 0; i < items.length; i++) {
			if (items[i].id == itid) {
				it = items[i]
				break
			}
		}
		if(!it){
			return;
		}
		this.resetFirstPage()
		var f = this.cndField;
		var v = f.getValue()
		var rawV = f.getRawValue();
		var xtype = f.getXType();
		if ((Ext.isEmpty(v) || Ext.isEmpty(rawV)) && (xtype !== "MyRadioGroup" && xtype !== "MyCheckboxGroup")) {
			this.queryCnd = null;
			this.requestData.cnd = initCnd
			this.refresh()
			return
		}
		if(f.getXType() == "datefield"){
			v = v.format("Y-m-d")
		}
		if(f.getXType() == "datetimefield"){
			v = v.format("Y-m-d H:i:s")
		}
		//替换'，解决拼sql语句查询的时候报错
		v=v.replace(/'/g, "''")
		var refAlias = it.refAlias || "a"
		var cnd = ['eq',['$',refAlias + "." + it.id]]
		if(it.dic){
			var expType=this.getCndType(it.type)
			if(it.dic.render == "Tree"){
				var node =  this.cndField.selectedNode;
				if(!node || node.isLeaf()){
					cnd.push([expType,v]);
				}else{
					cnd[0] = 'like'
					cnd.push([expType,v + '%'])
				}
			}
			else{
				cnd.push([expType,v])
			}
		}
		else{
			switch(it.type){
				case 'int':
					cnd.push(['i',v])
					break;
				case 'double':
				case 'bigDecimal':
					cnd.push(['d',v])
					break;
				case 'string':
					cnd[0] = 'like'
					if(it.codeType=="py"){
						v='%'+v.toUpperCase();
					}
					cnd.push(['s',v + '%'])
					break;
				case "date":
					if(v.format){
				       v = v.format("Y-m-d")
				    }
					cnd[1] = ['$', "str(" + refAlias + "." + it.id + ",'yyyy-MM-dd')"]
					cnd.push(['s',v])
					break;
				case 'datetime':
				case 'timestamp':
					if (it.xtype == "datefield") {
						if(v.format){
							v = v.format("Y-m-d")
						}
						cnd[1] = ['$',"str(" + refAlias + "." + it.id + ",'yyyy-MM-dd')"]
						cnd.push(['s', v])
					} else {
						if(v.format){
							v = v.format("Y-m-d H:i:s")
						}
						cnd[1] = ['$',"str(" + refAlias + "." + it.id + ",'yyyy-MM-dd HH24:mi:ss')"]
						cnd.push(['s', v])
					}
					break;
			}
		}
		this.queryCnd = cnd
		if(initCnd){
			cnd = ['and',initCnd,cnd]
		}
		this.requestData.cnd = cnd
		this.refresh()
	}
			
		});
