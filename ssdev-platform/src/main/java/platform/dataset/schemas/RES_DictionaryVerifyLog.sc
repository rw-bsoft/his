<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="RES_DictionaryVerifyLog" alias="字典审核记录" sortInfo="VDATE desc">
  <item id="ID" alias="ID" type="int" not-null="1" generator="auto" pkey="true" display="0"/>
  <item id="DICID" alias="字典" type="string" length="200" width="350" queryable="true"/>
  <item id="VERSION" alias="数据标准" type="string" length="50" width="200" queryable="true">
  	<dic id="platform.dataset.dic.resDataStandard"/>
  </item>
  <item id="USERID" alias="审核用户" type="string" length="50" queryable="true">
  	<dic id="platform.reg.dictionary.user02" render="Tree"/>
  </item>
  <item id="VDATE" alias="审核时间" type="date" />
  <item id="VFLAG" alias="审核标记" type="int" queryable="true">
  	<dic>
		<item key="1" text="通过" />
		<item key="2" text="拒绝" />
	</dic>
  </item>
</entry>
