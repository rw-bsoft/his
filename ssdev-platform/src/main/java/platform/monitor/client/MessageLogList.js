$package("platform.monitor.client")
$import("app.modules.list.SimpleListView")

platform.monitor.client.MessageLogList=function(cfg){
	platform.monitor.client.MessageLogList.superclass.constructor.apply(this,[cfg])
}

Ext.extend(platform.monitor.client.MessageLogList,app.modules.list.SimpleListView,{
	getCM : function(items) {
		var cm = platform.monitor.client.MessageLogList.superclass.getCM.call(this, items)
		if(this.type=="send"){
			var c = {
				header : "点击查看接收信息",
				width : 150,
				dataIndex : "read",
				renderer : function(value, metadata, record, rowIndex, colIndex) {
					var str = '<button style="width:80px;" title="点击查看接收信息" onclick="read()"/>查看接收信息</button>';
					return str;
				}
			}
			cm.push(c)
			var a = this
			read = function() {
				a.read()
			}
		}
		var newcm=[]
		outer:
		for(var i=0 ;i<cm.length;i++){
			if(this.hideCol){
				for(var j=0 ;j<this.hideCol.length;j++){
						if(cm[i].dataIndex==this.hideCol[j])
							continue outer;
				}
			}
			newcm.push(cm[i])
		}
		return newcm
	},
	read:function(){
		var r = this.getSelectedRecord()
		var uuid=r.get("Uuid")
		if(!this.recModule){
			var cls = "platform.monitor.client.MessageLogList"
			$import(cls)
			var cfg = {}		
			cfg.entryName = this.entryName
			cfg.autoLoadSchema = false
			cfg.type="receive"
			cfg.width=600
			cfg.hideCol=["Sender","SendDate","Topic"]
			var module = eval("new " + cls + "(cfg)");
			this.recModule=module
			module.initPanel()
			this.recModule.getWin().setTitle("接收信息")
		}
		this.recModule.requestData.cnd=['and',['eq',['$','Uuid'],['s',uuid]],['notNull',['$','Receiver']]]
		this.recModule.refresh()
		this.recModule.getWin().show()
	},
	fresh:function(ip){
		var cnd
		if(this.type=="send"){
			cnd=['and',this.initCnd,['like',['$','Sender'],['s',ip+'%']]]
		}
		if(this.type=="receive"){
			cnd=['and',this.initCnd,['like',['$','Receiver'],['s',ip+'%']]]
		}
		this.requestData.cnd = cnd
		this.refresh()
	}
})