$import("util.helper.Helper")
function getLod(object){
    var LODOP=object;
    var downloadurl = util.helper.Helper.getUrl();
    downloadurl+="resources/component/lodop/install_lodop32.exe";
     if ((LODOP==null)||(typeof(LODOP.VERSION)=="undefined")) {
     	Ext.Msg.show({
		   title: '信息提示',
		   msg: '打印控件尚未安装,点击["确定"]按钮下载安装,然后重启浏览器进入！',
		   modal:true,
		   width: 300,
		   buttons: Ext.MessageBox.OKCANCEL,
		   multiline: false,
		   fn: function(btn, text){
		   	 if(btn == "ok"){
		   	 	window.open(downloadurl,"","height="+(screen.height-100)+", width="+(screen.width-10)+", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
		   	 }
		   },
		   scope:this
		})	
		return LODOP; 
     }else if (LODOP.VERSION<"6.1.9.8") {
		  Ext.Msg.show({
					title : '信息提示',
					msg : '打印控件需要升级,点击["确定"]按钮下载最新版本,然后重启浏览器进入！',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							window
									.open(
											downloadurl,
											"",
											"height="
													+ (screen.height - 100)
													+ ", width="
													+ (screen.width - 10)
													+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
						}
					},
					scope : this
				})
		return null;
     };
     //=====如下空白位置适合调用统一功能:=====	 
     LODOP.SET_LICENSES("创业软件股份有限公司","F8B97CB7AB0D3E19ECD8D05CDC69A847","","");
     //=======================================
     return LODOP; 
}
//当前lodop版本6.161
function getLodop(){
	if(!this.LODOP){
		var LODOP;
		if (navigator.appVersion.indexOf("MSIE")>=0){
			try{ 
				LODOP=new ActiveXObject("Lodop.LodopX"); 
			 }catch(err){ 
	 		 } 
		} else{ 
			LODOP=document.createElement("embed");   
			LODOP.setAttribute("id",'lodopOcx'); 
			LODOP.setAttribute("width",0); 
			LODOP.setAttribute("height",0); 
			LODOP.type="application/x-print-lodop";   
			document.documentElement.appendChild(LODOP); 
		}; 
		if(!Ext.isIE) {
			if (LODOP.PREVIEW) {// add by yangl 解决打印卡死问题
				var func = LODOP.PREVIEW;
				LODOP.PREVIEW = function() {
					LODOP.SET_SHOW_MODE("NP_NO_RESULT",true);
					Ext.callback(func, LODOP)
				}
				LODOP.SYNC_PREVIEW = func;// 同步预览方式可能界面会卡死，不建议使用
			}
			if (LODOP.PRINT) {// add by yangl 解决打印卡死问题
				var printFunc = LODOP.PRINT;
				LODOP.PRINT = function() {
					LODOP.SET_SHOW_MODE("NP_NO_RESULT",true);
					Ext.callback(printFunc, LODOP)
				}
				LODOP.SYNC_PRINT = printFunc; // 同步方式，分单打印
			}
		}
		this.LODOP=LODOP
	}
	return getLod(this.LODOP);
}
