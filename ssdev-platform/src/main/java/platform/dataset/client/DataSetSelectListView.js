$package("platform.dataset.client")

$import("app.modules.list.SelectListView")

platform.dataset.client.DataSetSelectListView = function(cfg){
	this.initCnd = ['and',['eq',['$','a.Status'],['i','1']],['eq',['$','b.Status'],['s','1']]]
	platform.dataset.client.DataSetSelectListView.superclass.constructor.apply(this,[cfg]);
}

Ext.extend(platform.dataset.client.DataSetSelectListView, app.modules.list.SelectListView, {
	doZip:function(){
		var dataSets=[];
		var records=this.getSelectedRecords();
		for(var i=0;i<records.length;i++){
			var record=records[i]
			var r={
				Status:record.get("Status"),
				DataSetId:record.get("DataSetId"),
				StandardIdentify:record.get("StandardIdentify"),
				CustomIdentify:record.get("CustomIdentify"),
				DName:record.get("DName"),
				DataStandardId:record.get("DataStandardId"),
				Parent:record.get("Parent")
			}
			dataSets.push(r)
		}
		var url ="*.download?serviceId=downloadDataSetZip&method=download&body="+encodeURI(encodeURI(Ext.encode({dataSet:dataSets})))
		var printWin = window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
	},
	doZipList:function(){
		var url ="*.download?serviceId=downloadDataSetZip&method=batchDownload&body="+encodeURI(encodeURI(Ext.encode(this.requestData)))
		var printWin = window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
	},
	doCndQuery:function(){
		var initCnd = this.initCnd
		var index = this.cndFldCombox.getValue()
		var it = this.schema.items[index]
		if(!it){
			return;
		}
		this.resetFirstPage()
		var f = this.cndField;
		var v = f.getValue()

		if(v == null || v == ""){
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
		var refAlias = it.refAlias || "a"
		var cnd = ['eq',['$',refAlias + "." + it.id]]
		if(it.dic){
			if(it.dic.render == "Tree"){
				var node =  this.cndField.selectedNode
				if(!node.isLeaf()){
					var keys=[node.attributes.key]
					keys.push()
					var url=it.dic.id+".dic?sliceType=0&parentKey="+node.attributes.key
					var r = util.rmi.miniRequestSync(url)
					var items=r.json.items
					for(var i=0;i<items.length;i++){
						keys.push(items[i].key)
					}
					cnd[0] = 'in'
					cnd.push(keys)
				}
				else{
					cnd.push(['s',v])
				}
			}
			else{
				cnd.push(['s',v])
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
					if(v.format){
				       v = v.format("Y-m-d H:i:s")
				    }
					cnd[1] = ['$', "str(" + refAlias + "." + it.id + ",'yyyy-MM-dd')"]
					cnd.push(['s',v])
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
})