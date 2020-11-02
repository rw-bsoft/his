$package("phis.application.cic.script")

$import("phis.script.SimpleModule")
phis.application.cic.script.ClinicalcheckbgModule = function(cfg) {
	this.isModify = false;
	phis.application.cic.script.ClinicalcheckbgModule.superclass.constructor
			.apply(this, [ cfg ]);
}

/**
 * 合理用药
 */
Ext
		.extend(
				phis.application.cic.script.ClinicalcheckbgModule,
				phis.script.SimpleModule,
				{
					initPanel : function() {
						var empiData=this.exContext.empiData;
			         	var res = phis.script.rmi.miniJsonRequestSync({
					              serviceId : "clinicManageService",
					              serviceAction : "getjsdqyjcxx",
					              body : {
						                      "empiid" :empiData.empiId
					                         }
				         });
				         var fsjg={};
				         fsjg.manageUnit=res.json.manageUnit;
						if(this.id=="WAR61"){//住院放射报告结果
		 			       url="http://10.2.202.62:8080/list/reportlist.aspx?ConType=0&inhosptId="+this.exContext.brxx.data.ZYHM+"&sqbh="+fsjg.manageUnit;
		 		         }else if(this.id=="WAR62"){//住院B超报告结果
		 	           	   url="http://10.2.202.62:8080/list/reportlist.aspx?inhosptId="+this.exContext.brxx.data.ZYHM+"&sqbh="+fsjg.manageUnit;
		 	           	}else if(this.id=="WAR63"){//住院放射影像查看
		 	           	   url="http://10.2.202.62:8081/main/webpacs.aspx?+&sqbh="+fsjg.manageUnit+"&sfzh=&mznum=&zynum="+this.exContext.brxx.data.ZYHM+"&studydate=";
		 	           	}else if(this.id=="CIC44"){//门诊放射报告结果
				           url="http://10.2.202.62:8080/list/reportlist.aspx?ConType=0&clinicId="+this.exContext.JZKH+"&sqbh="+fsjg.manageUnit;
		 	           	}else if(this.id=="CIC45"){//门诊B超报告结果
		 	           	   url="http://10.2.202.62:8080/list/reportlist.aspx?ConType=1&clinicId="+this.exContext.JZKH+"&sqbh="+fsjg.manageUnit;
		 	           	}else if(this.id=="CIC46"){//门诊放射影像查看
		 	           	   url="http://10.2.202.62:8081/main/webpacs.aspx?+&sqbh="+fsjg.manageUnit+"&sfzh=&mznum="+this.exContext.JZKH+"&zynum=&studydate=";
		 	           	}else{
		 			        Ext.MessageBox.alert("提示","module的id改变，请工程师修改此页面！")
		 	          	}
		 	          	var encodeurl = this.base64encode(encodeURI(url));
				         if (this.panel)
					     return this.panel;
				         var panel = new Ext.Panel({
							border : false,
							html : "<iframe src='"+url+"'scrolling='yes' frameborder=0 width=100% height=100%></iframe>",
							frame : true,
							autoScroll : true
						});
				this.panel = panel;
				panel.on("afterrender", this.onReady, this)
				return panel;
				 },		
			onReady : function() {
			},
			//加密
		base64encode :function (str) {
			var base64EncodeChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
			var base64DecodeChars = new Array(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1,
			63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1,
			0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
			20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31,
			32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
			50, 51, -1, -1, -1, -1, -1);		
	    	var out, i, len;
	    	var c1, c2, c3;
	    	len = str.length;
	    	i = 0;
		    out = "";
		    while(i < len) {
		        c1 = str.charCodeAt(i++) & 0xff;
		        if(i == len)
		        {
		            out += base64EncodeChars.charAt(c1 >> 2);
		            out += base64EncodeChars.charAt((c1 & 0x3) << 4);
		            out += "==";
		            break;
		        }
		        c2 = str.charCodeAt(i++);
		        if(i == len)
		        {
		            out += base64EncodeChars.charAt(c1 >> 2);
		            out += base64EncodeChars.charAt(((c1 & 0x3)<< 4) | ((c2 & 0xF0) >> 4));
		            out += base64EncodeChars.charAt((c2 & 0xF) << 2);
		            out += "=";
		            break;
		        }
		        c3 = str.charCodeAt(i++);
		        out += base64EncodeChars.charAt(c1 >> 2);
		        out += base64EncodeChars.charAt(((c1 & 0x3)<< 4) | ((c2 & 0xF0) >> 4));
		        out += base64EncodeChars.charAt(((c2 & 0xF) << 2) | ((c3 & 0xC0) >>6));
		        out += base64EncodeChars.charAt(c3 & 0x3F);
		    }
		    return out;	
	    }  		
    });	