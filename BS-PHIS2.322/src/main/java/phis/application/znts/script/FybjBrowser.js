$package("phis.application.znts.script")

$import("phis.script.SimpleModule")
phis.application.znts.script.FybjBrowser = function(cfg) {
	phis.application.znts.script.FybjBrowser.superclass.constructor.apply(
			this, [cfg]);
}
/*******************************************************************************
 * 【溧水】妇幼保健(东软)页面内嵌HIS zhaojian 2018-10-29
 */
Ext.extend(phis.application.znts.script.FybjBrowser,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel)
					return this.panel;
				var tagUrl="";
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "phis.ZntsService",
					serviceAction : "getFybjUrlPage",
					body : {
						timeout : 2000
					}
				});
				if(r.code == 200 && r.json.body.tagUrl){
					if(r.json.body.tagUrl=="" || r.json.body.idcard=="" || r.json.body.phonenum==""){
						Ext.Msg.alert("提示", "妇幼保健浏览器无法正常打开，请联系管理员！");
						return;
					}
					tagUrl = r.json.body.tagUrl+"?idcard="+r.json.body.idcard+"&phonenum="+r.json.body.phonenum;
					window.open (tagUrl,'new','toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no, status=no')
				}else{
					Ext.Msg.alert("提示", r.json.body.msg);
				}
				if(tagUrl==""){
					Ext.Msg.alert("提示", "妇幼保健浏览器无法正常打开，请联系管理员！");
					return;
				}
				//var panel = new Ext.Panel({
				//			border : false,
				//			frame : false,
				//			html : "<iframe src='"+tagUrl+"'  width='100%' height='100%' frameborder='no'></iframe>",
				//			frame : false
				//		});
				//this.panel = panel;
				//return panel;
			}
		});