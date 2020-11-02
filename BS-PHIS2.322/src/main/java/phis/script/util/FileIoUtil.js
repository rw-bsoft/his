$import("phis.script.report.jquery");

readXmlFile = function(path){
	var xmlDoc='';
	if(!!window.ActiveXObject || "ActiveXObject" in window){
        var ForReading = 1;
        var fso = new ActiveXObject("Scripting.FileSystemObject");
        var ts = fso.OpenTextFile(path, ForReading, true);
        xmlDoc = ts.ReadAll();
//        alert(content);
        ts.Close();
	}else{//非IE
//					xmlDoc=document.implementation.createDocument("","",null);
//					xmlDoc.async=false;  //同步,防止后面程序处理时遇到文件还没加载完成出现的错误,故同步等XML文件加载完再做后面处理
//					alert("xmlDoc load");
//					xmlDoc.load(path); //加载XML
//					alert("xmlDoc load end");
	}
	return xmlDoc;
},
downloadXmlFile = function(filename,content,hideMessage){
	if(!!window.ActiveXObject || "ActiveXObject" in window){
		var fso,tf;
//					var Folder=browseFolder();
		fso = new ActiveXObject("Scripting.FileSystemObject");//创建文件系统对象
//					tf = fso.CreateTextFile(Folder+filename,true);//创建一个文本文件
		tf = fso.OpenTextFile(filename, 2, true);
		tf.write(content);//向文件中写入内容
		tf.Close();
		if(!hideMessage){
			MyMessageTip.msg("提示", "保存完毕!", true);
		}
	}else{//谷歌和火狐，使用“a”标签的download属性
//					var aLink = document.createElement('a');
//					aLink.download = fileName;
//					aLink.href = content;//dataurl格式的字符串
//					var evt = document.createEvent("HTMLEvents");//建立一个事件
//					evt.initEvent("click", false, false);//这是一个单击事件
//					aLink.dispatchEvent(evt);//触发事件
	}
},
loadXmlDoc = function(file){
	try{//IE
		xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
	}catch(e){
		xmlDoc = document.implementation.createDocument("","",null);
	}
	try{
		xmlDoc.async=false;
		xmlDoc.load(file);
	}catch(e){
		var xmlhttp = new window.XMLHttpRequest();
		xmlhttp.open("GET",file,false);
		xmlhttp.send(null);
		xmlDoc = xmlhttp.reponseXML.documentElement;
	}
	return xmlDoc;
},
delXmlFile = function(filename)   
{
   var fso=new ActiveXObject("Scripting.FileSystemObject");
   if(fso.FileExists(filename)){
	   	fso.DeleteFile(filename);
   }else{
   		return false;
   }
}