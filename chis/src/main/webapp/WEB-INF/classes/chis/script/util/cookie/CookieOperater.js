$package("chis.script.util.cookie")

chis.script.util.cookie.CookieOperater = {
	expireDays:100,	//过期时间100天
	setCookie:function(key,value){
		var date=new Date()
		date.setTime(date.getTime()+this.expireDays*24*3600*1000)
		document.cookie=key+"="+escape(value)+";expires="+date.toGMTString();
	},
	getCookie:function(key,user){
		var strCookies=document.cookie
		var arrCookies=strCookies.split(";")
		for(var i=0;i<arrCookies.length;i++){
			var arrCookie=arrCookies[i].split("=")
			if(this.lefttrim(arrCookie[0]) == key){
				return unescape(arrCookie[1])
			}
		}
	},
	lefttrim:function(str){
		var index = 0
		for(var i=0;i<str.length;i++){
			if(str[i] != " "){
				index = i
				break
			}
		}
		return str.substring(index)
	}
}