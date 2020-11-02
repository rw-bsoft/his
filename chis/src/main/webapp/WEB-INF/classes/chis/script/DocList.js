$package("chis.script")

$import("app.modules.list.SimpleListView")

chis.script.DocList = function(cfg){
	chis.script.DocList.superclass.constructor.apply(this,[cfg])
}
Ext.extend(chis.script.DocList, app.modules.list.SimpleListView,{
	doTest:function(){
		alert(this.title)
	},
	doMakeRandData:function(){
		Ext.Msg.show({
		   title: '生成随机数据',
		   msg: '请输入需要生成的随机数据数量',
		   width: 300,
		   value:500,
		   modal:false,
		   buttons: Ext.MessageBox.OKCANCEL,
		   multiline: false,
		   fn: function(btn, text){
		   	 if(btn == "CANCEL" || text.length == 0){
		   	 	return
		   	 }
		   	 var amount = parseFloat(text)
		   	 if(isNaN(amount)){
		   	 	return;
		   	 }
		   	 var cfg = {
		   	 	serviceId:"chis.makeRandData",
		   	    method:"execute",
		   	 	entryName:this.entryName,
		   	 	size:amount
		   	 }
		   	 this.sendToServer(cfg)
		   },
		   scope:this,
		   prompt:true
		});		
	},
	sendToServer:function(cfg){
		this.grid.el.mask("正在生成随机数据...")
		util.rmi.jsonRequest(
			cfg,
			function(code,msg,json){
				this.grid.el.unmask()
				if(code < 300){
					this.refresh()
				}
				else{
					alert(msg)
				}
			},
		this)	
	}	
});