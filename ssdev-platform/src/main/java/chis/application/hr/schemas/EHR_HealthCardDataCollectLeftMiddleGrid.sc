<?xml version="1.0" encoding="UTF-8"?>
<entry alias="健康卡数据采集联系人信息">
	<item id="personName" alias="姓名" type="string" length="20" />
	<item id="relaCode" alias="与本人关系" type="string" length="2" >
		<dic id="chis.dictionary.relaCode" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="mobileNumber" alias="本人电话" type="string" length="20" width="90"/>
</entry>