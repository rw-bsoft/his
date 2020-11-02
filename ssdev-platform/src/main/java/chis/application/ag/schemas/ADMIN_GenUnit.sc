<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.ag.schemas.ADMIN_GenUnit" alias="批量生成幢">
  <item id="layerCount"  alias="楼层数" type="int" not-null="true" />
  <item id="familyCount"  alias="每层户数" type="int" not-null="true"/>
  <item id="startLayer"  alias="起始层号" type="int"  defaultValue="1"/>
  <item id="startFamily"  alias="起始户号" type="int" defaultValue="1"/>
  <item id="codeStyle"	 alias="编码样式" type="string" length="2" colspan="2" defaultValue="01" not-null="true">
  	<dic>
  		<item key="01" text="数字(101、102..)"/>
  		<item key="02" text="字母(10A、10B..)"/>
        <item key="03" text="门牌号(1号、2号..)"/>
  	</dic>
  </item>	
</entry>
  	