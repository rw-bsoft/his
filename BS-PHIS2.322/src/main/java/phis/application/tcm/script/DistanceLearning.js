$package("phis.application.tcm.script")

$import("phis.script.SimpleModule")
phis.application.tcm.script.DistanceLearning = function(cfg) {
	phis.application.tcm.script.DistanceLearning.superclass.constructor.apply(
			this, [cfg]);
}
/***
 * 【中医馆】远程教育
 */
Ext.extend(phis.application.tcm.script.DistanceLearning,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel)
					return this.panel;
				var tagUrl = "";
				//获取系统参数：【中医馆】页面免登陆地址 TCM_URL_LOGIN
				var params = this.loadSystemParams({
					"commons" : ['TCM_URL_LOGIN']
				})
				//获取系统参数：【中医馆】远程教育页面TCM_URL_YCJY
				var params2 = this.loadSystemParams({
					"commons" : ['TCM_URL_YCJY']
				})
				if (params.TCM_URL_LOGIN && params.TCM_URL_LOGIN!="null" && params.TCM_URL_LOGIN!="" 
					&& params2.TCM_URL_YCJY && params2.TCM_URL_YCJY!="null" && params2.TCM_URL_YCJY!="") {
					tagUrl = params.TCM_URL_LOGIN+"?uid="+this.mainApp.uid+"&service="+encodeURIComponent(params2.TCM_URL_YCJY);
				}
				if(tagUrl==""){
					Ext.Msg.alert("提示", "中医馆远程教育无法正常打开，请联系管理员！");
					this.opener.resetTcmSelectItem(0);
					return;
				}
				var panel = new Ext.Panel({
							border : false,
							frame : false,
							html : "<iframe src='"+ tagUrl +"'  width='100%' height='100%' frameborder='no'></iframe>",
							frame : false
						});
				this.panel = panel;
				return panel;
			}
		});